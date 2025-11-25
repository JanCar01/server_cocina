package com.example.demo.controller;

import com.example.demo.dto.RecipeDTO;
import com.example.demo.model.Recipe;
import com.example.demo.service.RecipeService;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    /**
     * 🔼 Guarda una nueva receta asociada a un usuario
     */
    @PostMapping("/save")
    public Recipe save(@RequestBody RecipeDTO req, Authentication auth) {
        String email = auth.getName();
        return service.saveRecipe(email, req);
    }

    /**
     * 🔽 Devuelve todas las recetas de un usuario
     */
    @GetMapping("/list")
    public List<RecipeDTO> list(Authentication auth) {
        String email = auth.getName(); // ← viene del token
        return service.getRecipesForUser(email);
    }

    /**
     * ❌ Elimina una receta
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        service.delete(id, email);
    }


    /**
     * ✏️ Actualiza una receta existente
     */
    @PutMapping("/update/{id}")
    public Recipe update(@PathVariable Long id, @RequestBody RecipeDTO req, Authentication auth) {
        String email = auth.getName();
        return service.updateRecipe(email, id, req);
    }
}
