package com.example.demo.service;

import com.example.demo.dto.RecipeDTO;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;


import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository repo;
    private final UserService userService;
    private final UserRepository userRepository;
    
    public RecipeService(RecipeRepository repo, UserService userService, UserRepository userRepository) {
        this.repo = repo;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // 🔹 Crear una nueva receta
    public Recipe saveRecipe(String username, RecipeDTO req) {
        User user = userService.getUser(username);

        Recipe recipe = new Recipe();
        recipe.setUser(user);
        if (req.id != null) recipe.setId(req.id);
        recipe.setName(req.name);
        recipe.setDescription(req.description);

        List<RecipeStep> steps = new ArrayList<>();
        List<RecipeIngredient> ingredients = new ArrayList<>();

        // --- Steps ---
        if (req.steps != null) {
            for (RecipeDTO.StepDTO s : req.steps) {
                RecipeStep step = new RecipeStep();
                step.setStepOrder(s.order);
                step.setStepText(s.text);
                step.setRecipe(recipe);
                steps.add(step);
            }
        }

        // --- Ingredients ---
        if (req.ingredients != null) {
            for (RecipeDTO.IngredientDTO ing : req.ingredients) {
                RecipeIngredient i = new RecipeIngredient();
                i.setIngredient(ing.ingredient);
                i.setAmount(ing.amount);
                i.setUnit(ing.unit);
                i.setRecipe(recipe);
                ingredients.add(i);
            }
        }

        recipe.setSteps(steps);
        recipe.setIngredients(ingredients);

        return repo.save(recipe);
    }

    // 🔹 Obtener todas las recetas de un usuario
    public List<RecipeDTO> getRecipesForUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        List<Recipe> recipes = repo.findByUser(user);

        return recipes.stream()
            .map(RecipeDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public RecipeDTO toDto(Recipe recipe) {
        RecipeDTO dto = new RecipeDTO();

        dto.id = recipe.getId();
        dto.username = recipe.getUser().getUsername();
        dto.name = recipe.getName();
        dto.description = recipe.getDescription();

        System.out.println("👣 Recipe " + dto.id + " tiene " +
            (recipe.getSteps() == null ? "null" : recipe.getSteps().size() + " steps"));

        dto.ingredients = recipe.getIngredients()
            .stream()
            .map(ing -> {
                RecipeDTO.IngredientDTO i = new RecipeDTO.IngredientDTO();
                i.ingredient = ing.getIngredient();
                i.amount = ing.getAmount();
                i.unit = ing.getUnit();
                return i;
            })
            .toList();

        dto.steps = recipe.getSteps()
            .stream()
            .map(step -> {
                RecipeDTO.StepDTO s = new RecipeDTO.StepDTO();
                s.order = step.getStepOrder();
                s.text = step.getStepText();
                return s;
            })
            .toList();

        return dto;
    }

    // 🔹 Eliminar receta de un usuario
    public void delete(Long id, String username) {
        Recipe r = repo.findById(id).orElseThrow();
        if (!r.getUser().getUsername().equals(username))
            throw new RuntimeException("No autorizado");

        repo.delete(r);
    }

    // 🔹 Actualizar receta existente
    public Recipe updateRecipe(String username, Long id, RecipeDTO req) {
        User user = userService.getUser(username);

        Recipe recipe = repo.findById(id).orElseGet(() -> {
            Recipe r = new Recipe();
            r.setId(id);
            r.setUser(user);
            return r;
        });

        if (!recipe.getUser().getId().equals(user.getId()))
            throw new RuntimeException("No autorizado");

        recipe.setName(req.name);
        recipe.setDescription(req.description);

        // --- Actualizar steps ---
        recipe.getSteps().clear();
        if (req.steps != null) {
            for (RecipeDTO.StepDTO s : req.steps) {
                RecipeStep step = new RecipeStep();
                step.setStepOrder(s.order);
                step.setStepText(s.text);
                step.setRecipe(recipe);
                recipe.getSteps().add(step);
            }
        }

        // --- Actualizar ingredientes ---
        recipe.getIngredients().clear();
        if (req.ingredients != null) {
            for (RecipeDTO.IngredientDTO ing : req.ingredients) {
                RecipeIngredient i = new RecipeIngredient();
                i.setIngredient(ing.ingredient);
                i.setAmount(ing.amount);
                i.setUnit(ing.unit);
                i.setRecipe(recipe);
                recipe.getIngredients().add(i);
            }
        }

        return repo.save(recipe);
    }

    // 🔹 Crear o actualizar según existencia
    public void saveOrUpdate(String username, RecipeDTO dto) {
        if (dto.id != null && repo.existsById(dto.id)) {
            updateRecipe(username, dto.id, dto);
        } else {
            saveRecipe(username, dto);
        }
    }
}
