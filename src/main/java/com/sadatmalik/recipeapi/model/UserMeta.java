package com.sadatmalik.recipeapi.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_meta")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMeta implements Serializable {

    private static final long serialVersionUID = -6995888536280734198L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;
}