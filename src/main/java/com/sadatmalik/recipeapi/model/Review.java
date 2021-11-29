package com.sadatmalik.recipeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties("author") //this will ignore 'author' when converting json bytes to object for testing
public class Review implements Serializable {

    private static final long serialVersionUID = 3052491181108747090L;

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