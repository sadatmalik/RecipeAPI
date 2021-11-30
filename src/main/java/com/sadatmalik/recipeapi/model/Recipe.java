package com.sadatmalik.recipeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("author") //this will ignore 'author' when converting json bytes to object for testing
public class Recipe implements Serializable {

    private static final long serialVersionUID = -7949601140835616356L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private CustomUserDetails user;

    @Column(nullable = false)
    private Integer minutesToMake;

    @Column(nullable = false)
    private Integer difficultyRating;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Step> steps = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Review> reviews;

    @Transient
    @JsonIgnore
    private URI locationURI;

    @Transient
    private Double averageRating;

    public void setDifficultyRating(int difficultyRating) {
        if (difficultyRating < 0 || difficultyRating > 10) {
            throw new IllegalStateException("Difficulty rating must be between 0 and 10.");
        }
        this.difficultyRating = difficultyRating;
    }

    public void validate() throws IllegalStateException {
        if (ingredients.size() == 0) {
            throw new IllegalStateException("You have to have at least one ingredient for you recipe!");
        } else if (steps.size() == 0) {
            throw new IllegalStateException("You have to include at least one step for your recipe!");
        }
        // name
        if (name == null || name.equals("")) {
            throw new IllegalStateException("Your recipe must have a name!");
        }

        // minutes to make
        if (minutesToMake == null || minutesToMake <= 0) {
            throw new IllegalStateException("Your recipe must have minutesToMake > 0");
        }

        // difficulty rating
        if (difficultyRating == null || difficultyRating < 1 || difficultyRating > 10) {
            throw new IllegalStateException("Your recipe must have a difficulty rating between 1 and 10");
        }
    }

    public void generateLocationURI() {
        try {
            locationURI = new URI(
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/recipes/")
                            .path(String.valueOf(id))
                            .toUriString());
        } catch (URISyntaxException e) {
            //Exception should stop here.
        }
    }

    public void calculateAverageRating() {
        averageRating = 0.0;

        if (reviews != null && !reviews.isEmpty()) {
            for (Review review : reviews) {
                averageRating += review.getRating();
            }
            averageRating /= reviews.size();
        }
    }

    public String getAuthor() {
        return user.getUsername();
    }

    // Calls hibernate.initialize on collections to enable caching retrieval
    public void initialize() {
        Hibernate.initialize(this.ingredients);
        Hibernate.initialize(this.steps);
    }
}