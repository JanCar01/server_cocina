package com.example.demo.controller;

import com.example.demo.dto.RecipeDTO;
import com.example.demo.model.Recipe;
import com.example.demo.service.RecipeService;
import org.springframework.web.bind.annotation.*;

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
    public Recipe save(@RequestBody RecipeDTO req) {
        return service.saveRecipe(req.username, req); // ✅ ahora incluye username
    }

    /**
     * 🔽 Devuelve todas las recetas de un usuario
     */
    @GetMapping("/list")
    public List<RecipeDTO> list(@RequestParam String username) {
        return service.getRecipesForUser(username);
    }

    /**
     * ❌ Elimina una receta
     */
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id, @RequestParam String username) {
        service.delete(id, username);
    }

    /**
     * ✏️ Actualiza una receta existente
     */
    @PutMapping("/update/{id}")
    public Recipe update(@PathVariable Long id, @RequestBody RecipeDTO req) {
        return service.updateRecipe(req.username, id, req); // ✅ ahora incluye username
    }
}
