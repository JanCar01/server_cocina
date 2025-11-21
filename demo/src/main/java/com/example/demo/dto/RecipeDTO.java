package com.example.demo.dto; 

import java.util.List;

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
    } 
    
    public static class IngredientDTO { 
        public String ingredient; 
        public Double amount; 
        public String unit; 
    } 
}