package com.sadatmalik.recipeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
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

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private CustomUserDetails user;

    @NotNull
    private int rating;

    private String description;

    public void validate() throws IllegalStateException {
        if (rating <= 0 || rating > 10) {
            throw new IllegalStateException("Must include a Rating must be between 0 and 10.");
        }
    }

    public String getAuthor() {
        return user.getUsername();
    }
}