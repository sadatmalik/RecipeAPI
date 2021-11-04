package com.sadatmalik.recipeapi.repositories;

import com.sadatmalik.recipeapi.model.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<CustomUserDetails, Long> {

    CustomUserDetails findByUsername(String username);
}