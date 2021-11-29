package com.sadatmalik.recipeapi.services;

import com.sadatmalik.recipeapi.exceptions.NoSuchRecipeException;
import com.sadatmalik.recipeapi.model.Ingredient;
import com.sadatmalik.recipeapi.model.Recipe;
import com.sadatmalik.recipeapi.model.Step;
import com.sadatmalik.recipeapi.repositories.RecipeRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe) throws IllegalStateException {
        recipe.validate();
        recipe = recipeRepo.save(recipe);
        recipe.generateLocationURI();
        recipe.calculateAverageRating();
        return recipe;
    }

    @Cacheable(value = "recipeCache", key = "#id", sync = true)
    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);

        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe with ID " + id + " could be found.");
        }

        Recipe recipe = recipeOptional.get();

        // initializing these collections to enable caching retrieval
        Collection<Ingredient> ingredients = recipe.getIngredients();
        Hibernate.initialize(ingredients);

        Collection<Step> steps = recipe.getSteps();
        Hibernate.initialize(steps);

        recipe.generateLocationURI();
        recipe.calculateAverageRating();
        return recipe;
    }

    public ArrayList<Recipe> getRecipesByUser(String username) throws NoSuchRecipeException {
        ArrayList<Recipe> matchingRecipes = recipeRepo.findByUser_Username(username);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that username.");
        }

        for (Recipe r : matchingRecipes) {
            r.generateLocationURI();
            r.calculateAverageRating();
        }
        return matchingRecipes;
    }

    public ArrayList<Recipe> getRecipesByName(String name) throws NoSuchRecipeException {
        ArrayList<Recipe> matchingRecipes = recipeRepo.findByNameContaining(name);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name.");
        }

        for (Recipe r : matchingRecipes) {
            r.generateLocationURI();
            r.calculateAverageRating();
        }
        return matchingRecipes;
    }

    public ArrayList<Recipe> getAllRecipes() throws NoSuchRecipeException {
        ArrayList<Recipe> recipes = new ArrayList<>(recipeRepo.findAll());

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes yet :( feel free to add one though");
        }
        for (Recipe r : recipes) {
            r.generateLocationURI();
            r.calculateAverageRating();
        }
        return recipes;
    }

    @Transactional
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(e.getMessage() + " Could not delete.");
        }
    }

    @Transactional
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            recipe.validate();
            Recipe savedRecipe = recipeRepo.save(recipe);
            savedRecipe.generateLocationURI();
            savedRecipe.calculateAverageRating();
            return savedRecipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("The recipe you passed in did not have an ID found in the database." +
                    " Double check that it is correct. Or maybe you meant to POST a recipe not PATCH one.");
        }
    }
}