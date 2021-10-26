package com.sadatmalik.recipeapi.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private int rating;

    private String description;

    public void validate() throws IllegalStateException {
        if (rating <= 0 || rating > 10) {
            throw new IllegalStateException("Must include a Rating must be between 0 and 10.");
        }

        if (username == null || username.equals("")) {
            throw new IllegalStateException("Username cannot be null.");
        }
    }
}