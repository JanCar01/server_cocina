package com.example.demo.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RecipeInputDTO {
    public Long id;

    @JsonProperty("title")
    public String name;

    @JsonProperty("duration")
    public String description;

    public List<InputIngredientDTO> ingredients;
    public List<InputStepDTO> steps;

    public static class InputIngredientDTO {
        public String ingredient;
        public Double amount;
        public String unit;
    }

    public static class InputStepDTO {
        public int order;
        public String text;
    }
}
