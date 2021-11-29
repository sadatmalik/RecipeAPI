package com.sadatmalik.recipeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RecipeapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipeapiApplication.class, args);
	}

}
