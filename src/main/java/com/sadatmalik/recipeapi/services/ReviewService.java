package com.sadatmalik.recipeapi.services;

import com.sadatmalik.recipeapi.exceptions.NoSuchRecipeException;
import com.sadatmalik.recipeapi.exceptions.NoSuchReviewException;
import com.sadatmalik.recipeapi.exceptions.UserException;
import com.sadatmalik.recipeapi.model.Recipe;
import com.sadatmalik.recipeapi.model.Review;
import com.sadatmalik.recipeapi.repositories.ReviewRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);
    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    RecipeService recipeService;

    @Cacheable(value = "reviewCache", key = "#id", sync = true)
    public Review getReviewById(Long id) throws NoSuchReviewException {
        Optional<Review> review = reviewRepo.findById(id);

        if (review.isEmpty()) {
            throw new NoSuchReviewException("The review with ID " + id + " could not be found.");
        }

        LOGGER.info("Returning review from DB: " + review.get());

        return review.get();
    }

    @Cacheable(value = "reviewListCache", key = "'recipeid:' + #recipeId", sync = true)
    public ArrayList<Review> getReviewByRecipeId(Long recipeId) throws NoSuchRecipeException, NoSuchReviewException {
        Recipe recipe = recipeService.getRecipeById(recipeId);

        ArrayList<Review> reviews = new ArrayList<>(recipe.getReviews());

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("There are no reviews for this recipe.");
        }

        LOGGER.info("Looked up reviews for recipeId '" + recipeId + "': " + reviews);

        return reviews;
    }

    @Cacheable(value = "reviewListCache", key = "'username:' + #username", sync = true)
    public ArrayList<Review> getReviewByUsername(String username) throws NoSuchReviewException {
        ArrayList<Review> reviews = reviewRepo.findByUser_Username(username);

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("No reviews could be found for username " + username);
        }

        LOGGER.info("Returning reviews for username '" + username + "' from DB: " + reviews);

        return reviews;
    }

    @CachePut(value = "recipeCache", key = "#result.id")
    public Recipe postNewReview(Review review, Long recipeId) throws NoSuchRecipeException, UserException {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        if (recipe.getUser().getUsername().equals(review.getUser().getUsername())) {
            throw new UserException("Nice try - reviewing your own handy work, eh? Sorry, that's not allowed :-)");
        }
        recipe.getReviews().add(review);
        recipeService.updateRecipe(recipe, false);
        return recipe;
    }

    @Caching(evict = {
            @CacheEvict(value = "reviewCache", key = "#result.id"),
            @CacheEvict(value = "reviewListCache", allEntries = true)
    })
    public Review deleteReviewById(Long id) throws NoSuchReviewException {
        Review review = getReviewById(id);

        if (null == review) {
            throw new NoSuchReviewException("The review you are trying to delete does not exist.");
        }
        reviewRepo.deleteById(id);

        return review;
    }

    @Caching(put = @CachePut(value = "reviewCache", key = "#result.id"),
            evict = @CacheEvict(value = "reviewListCache", allEntries = true))
    public Review updateReviewById(Review reviewToUpdate) throws NoSuchReviewException {
        try {
            Review review = getReviewById(reviewToUpdate.getId());
        } catch (NoSuchReviewException e) {
            throw new NoSuchReviewException("The review you are trying to update. Maybe you meant to create one? If not," +
                    "please double check the ID you passed in.");
        }

        Review updatedReview = reviewRepo.save(reviewToUpdate);

        return updatedReview;
    }
}