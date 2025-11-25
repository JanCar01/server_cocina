package com.example.demo.controller;

import com.example.demo.dto.RecipeDTO;
import com.example.demo.dto.SyncRequestDTO;
import com.example.demo.model.*;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.service.RecipeService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeSyncController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final RecipeRepository recipeRepository;

    public RecipeSyncController(RecipeService recipeService, UserService userService, RecipeRepository recipeRepository) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.recipeRepository = recipeRepository;
    }

    /**
     * 🔼 Recibe recetas desde Flutter (push)
     */
    @PostMapping("/syncPush")
    public String syncPush(@RequestBody SyncRequestDTO data) {

        // 1️⃣ Obtener usuario autenticado desde JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userService.getUserByEmail(email);

        System.out.println("🔐 Usuario autenticado = " + email);
        System.out.println("📥 Recetas recibidas = " + data.recipes.size());

        // 2️⃣ Procesar eliminaciones
        if (data.deletedIds != null) {
            for (Long id : data.deletedIds) {
                recipeRepository.findById(id).ifPresent(recipe -> {
                    if (recipe.getUser().getId().equals(user.getId())) {
                        recipeRepository.delete(recipe);
                        System.out.println("🗑️ Eliminada receta id " + id);
                    }
                });
            }
        }

        // 3️⃣ Guardar o actualizar recetas
        for (RecipeDTO dto : data.recipes) {

            Recipe recipe = recipeRepository.findById(dto.id).orElse(new Recipe());

            recipe.setId(dto.id);
            recipe.setUser(user);
            recipe.setName(dto.name);
            recipe.setDescription(dto.description);

            recipe.setIngredients(new ArrayList<>());
            recipe.setSteps(new ArrayList<>());

            if (dto.ingredients != null) {
                for (RecipeDTO.IngredientDTO ing : dto.ingredients) {
                    RecipeIngredient i = new RecipeIngredient();
                    i.setIngredient(ing.ingredient);
                    i.setAmount(ing.amount);
                    i.setUnit(ing.unit);
                    i.setRecipe(recipe);
                    recipe.getIngredients().add(i);
                }
            }

            if (dto.steps != null) {
                for (RecipeDTO.StepDTO s : dto.steps) {
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



    /**
     * 🔽 Devuelve todas las recetas del usuario
     */
    @GetMapping("/syncPull")
    public List<RecipeDTO> syncPull(@RequestParam String username) {
        return recipeService.getRecipesForUser(username);
    }
}
