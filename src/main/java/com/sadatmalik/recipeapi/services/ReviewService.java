package com.sadatmalik.recipeapi.services;

import com.sadatmalik.recipeapi.exceptions.NoSuchRecipeException;
import com.sadatmalik.recipeapi.exceptions.NoSuchReviewException;
import com.sadatmalik.recipeapi.exceptions.UserException;
import com.sadatmalik.recipeapi.model.Recipe;
import com.sadatmalik.recipeapi.model.Review;
import com.sadatmalik.recipeapi.repositories.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    RecipeService recipeService;

    public Review getReviewById(Long id) throws NoSuchReviewException {
        Optional<Review> review = reviewRepo.findById(id);

        if (review.isEmpty()) {
            throw new NoSuchReviewException("The review with ID " + id + " could not be found.");
        }
        return review.get();
    }

    public ArrayList<Review> getReviewByRecipeId(Long recipeId) throws NoSuchRecipeException, NoSuchReviewException {
        Recipe recipe = recipeService.getRecipeById(recipeId);

        ArrayList<Review> reviews = new ArrayList<>(recipe.getReviews());

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("There are no reviews for this recipe.");
        }
        return reviews;
    }

    public ArrayList<Review> getReviewByUsername(String username) throws NoSuchReviewException {
        ArrayList<Review> reviews = reviewRepo.findByUsername(username);

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("No reviews could be found for username " + username);
        }

        return reviews;
    }

    public Recipe postNewReview(Review review, Long recipeId) throws NoSuchRecipeException, UserException {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        if (recipe.getUsername().equals(review.getUsername())) {
            throw new UserException("Nice try - reviewing your own handy work, eh? Sorry, that's not allowed :-)");
        }
        recipe.getReviews().add(review);
        recipeService.updateRecipe(recipe, false);
        return recipe;
    }

    public Review deleteReviewById(Long id) throws NoSuchReviewException {
        Review review = getReviewById(id);

        if (null == review) {
            throw new NoSuchReviewException("The review you are trying to delete does not exist.");
        }
        reviewRepo.deleteById(id);
        return review;
    }

    public Review updateReviewById(Review reviewToUpdate) throws NoSuchReviewException {
        try {
            Review review = getReviewById(reviewToUpdate.getId());
        } catch (NoSuchReviewException e) {
            throw new NoSuchReviewException("The review you are trying to update. Maybe you meant to create one? If not," +
                    "please double check the ID you passed in.");
        }
        reviewRepo.save(reviewToUpdate);
        return reviewToUpdate;
    }
}