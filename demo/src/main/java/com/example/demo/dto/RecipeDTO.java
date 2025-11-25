package com.example.demo.dto;

import com.example.demo.model.Recipe;
import com.example.demo.model.RecipeIngredient;
import com.example.demo.model.RecipeStep;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeDTO {

    public Long id;
    public String username;
    public String name;
    public String description;

    public List<StepDTO> steps;
    public List<IngredientDTO> ingredients;

    public static class StepDTO {
        public int order;
        public String text;

        public static StepDTO fromEntity(RecipeStep step) {
            StepDTO dto = new StepDTO();
            dto.order = step.getStepOrder();
            dto.text = step.getStepText(); // ← corregido
            return dto;
        }
    }


    public static class IngredientDTO {
        public String ingredient;
        public Double amount;
        public String unit;

        public static IngredientDTO fromEntity(RecipeIngredient ing) {
            IngredientDTO dto = new IngredientDTO();
            dto.ingredient = ing.getIngredient();
            dto.amount = ing.getAmount();
            dto.unit = ing.getUnit();
            return dto;
        }
    }

    public static RecipeDTO fromEntity(Recipe r) {
        RecipeDTO dto = new RecipeDTO();
        dto.id = r.getId();
        dto.name = r.getName();
        dto.description = r.getDescription();
        dto.username = r.getUser().getUsername();

        // 👇 Convertimos steps
        dto.steps = r.getSteps().stream()
                .map(StepDTO::fromEntity)
                .collect(Collectors.toList());

        // 👇 Convertimos ingredients
        dto.ingredients = r.getIngredients().stream()
                .map(IngredientDTO::fromEntity)
                .collect(Collectors.toList());

        return dto;
    }
}
