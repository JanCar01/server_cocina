package com.example.demo.controller;

import com.example.demo.dto.RecipeDTO;
import com.example.demo.dto.SyncRequestDTO;
import com.example.demo.dto.RecipeInputDTO;

import com.example.demo.model.*;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.service.RecipeService;
import com.example.demo.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeSyncController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final RecipeRepository recipeRepository;

    public RecipeSyncController(
            RecipeService recipeService,
            UserService userService,
            RecipeRepository recipeRepository
    ) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.recipeRepository = recipeRepository;
    }


    // ====================================================
    // 🔼 SYNC PUSH DESDE FLUTTER
    // ====================================================
    @PostMapping("/syncPush")
    public String syncPush(@RequestBody SyncRequestDTO data) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.getUserByEmail(email);

        System.out.println("🔐 Usuario autenticado = " + email);
        System.out.println("📥 Recetas recibidas = " + data.recipes.size());

        // --- 1️⃣ ELIMINACIONES ---
        if (data.deletedIds != null) {
            for (Long id : data.deletedIds) {
                recipeRepository.findById(id).ifPresent(recipe -> {
                    if (recipe.getUser().getId().equals(user.getId())) {
                        recipeRepository.delete(recipe);
                        System.out.println("🗑️ Eliminada " + id);
                    }
                });
            }
        }

        // --- 2️⃣ GUARDAR / ACTUALIZAR RECETAS ---
        for (RecipeInputDTO dto : data.recipes) {

            Recipe recipe = recipeRepository.findById(dto.id).orElse(new Recipe());

            recipe.setId(dto.id);
            recipe.setUser(user);
            recipe.setName(dto.name);
            recipe.setDescription(dto.description);

            recipe.getIngredients().clear();
            recipe.getSteps().clear();

            // INGREDIENTES
            if (dto.ingredients != null) {
                for (RecipeInputDTO.InputIngredientDTO ing : dto.ingredients) {
                    RecipeIngredient i = new RecipeIngredient();
                    i.setIngredient(ing.ingredient);
                    i.setAmount(ing.amount);
                    i.setUnit(ing.unit);
                    i.setRecipe(recipe);
                    recipe.getIngredients().add(i);
                }
            }

            // PASOS
            if (dto.steps != null) {
                for (RecipeInputDTO.InputStepDTO s : dto.steps) {
                    RecipeStep step = new RecipeStep();
                    step.setStepOrder(s.order);
                    step.setStepText(s.text);
                    step.setRecipe(recipe);
                    recipe.getSteps().add(step);
                }
            }

            recipeRepository.save(recipe);
        }

        return "OK";
    }



    // ====================================================
    // 🔽 SYNC PULL HACIA FLUTTER
    // ====================================================
    @GetMapping("/syncPull")
    public List<RecipeDTO> syncPull(@RequestParam String username) {
        return recipeService.getRecipesForUser(username);
    }
}
