package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Recipe {

    @Id
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
        mappedBy = "recipe",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER           // 👈 AÑADIR ESTO
    )
    private List<RecipeStep> steps;

    @OneToMany(
        mappedBy = "recipe",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER           // 👈 opcional pero recomendable para tu caso
    )
    private List<RecipeIngredient> ingredients;
    
    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
    }
    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStep> steps) {
        this.steps = steps;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
    
}
