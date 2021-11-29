package com.sadatmalik.recipeapi;

import com.sadatmalik.recipeapi.model.*;
import com.sadatmalik.recipeapi.repositories.RecipeRepo;
import com.sadatmalik.recipeapi.repositories.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
@Profile("test")
public class RecipeapiMainTest implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(RecipeapiMainTest.class);

    @Autowired
    private RecipeRepo recipeRepo;

    @Autowired
    private UserRepo userRepo;

    // TODO -- autowiring this so it's picked up from security config throws a circular dependency exception?
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("STARTING WITH TEST DATABASE SETUP");

        if (recipeRepo.findAll().isEmpty()) {

            String encodedPassword = passwordEncoder.encode("password");
            String encodedAdminPassword = passwordEncoder.encode("admin");

            // reviewers
            CustomUserDetails idfk = CustomUserDetails.builder().username("idfk").password(encodedPassword)
                    .userMeta(UserMeta.builder().name("IDFK").email("idfk@hotmail.com").build())
                    .authorities(Set.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .build();
            CustomUserDetails ben = CustomUserDetails.builder().username("ben").password(encodedPassword)
                    .userMeta(UserMeta.builder().name("Benjamin").email("ben@gmail.com").build())
                    .authorities(Set.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .build();

            userRepo.save(idfk);
            userRepo.save(ben);

            // recipe contributors
            CustomUserDetails maliksa = CustomUserDetails.builder().username("maliksa").password(encodedPassword)
                    .userMeta(UserMeta.builder().name("Sadat Malik").email("sadat@me.com").build())
                    .authorities(Set.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .build();
            CustomUserDetails gherkin = CustomUserDetails.builder().username("gherkin").password(encodedPassword)
                    .userMeta(UserMeta.builder().name("Pickle Jones").email("pj@duncan.com").build())
                    .authorities(Set.of(Role.builder().role(Role.Roles.ROLE_USER).build()))
                    .build();

            userRepo.save(maliksa);
            userRepo.save(gherkin);

            // admin user
            CustomUserDetails admin = CustomUserDetails.builder().username("ADMIN").password(encodedAdminPassword)
                    .userMeta(UserMeta.builder().name("Administrator").email("administrator@admin.com").build())
                    .authorities(Set.of(Role.builder().role(Role.Roles.ROLE_ADMIN).build()))
                    .build();

            userRepo.save(admin);

            Ingredient ingredient = Ingredient.builder().name("flour").state("dry").amount("2 cups").build();
            Step step1 = Step.builder().description("put flour in bowl").stepNumber(1).build();
            Step step2 = Step.builder().description("eat it?").stepNumber(2).build();

            Review review = Review.builder().description("tasted pretty bad").rating(2).user(idfk).build();

            Recipe recipe1 = Recipe.builder()
                    .name("test recipe")
                    .difficultyRating(10)
                    .minutesToMake(2)
                    .ingredients(Set.of(ingredient))
                    .steps(Set.of(step1, step2))
                    .reviews(Set.of(review))
                    .user(maliksa)
                    .build();

            recipeRepo.save(recipe1);

            ingredient.setId(null);
            Recipe recipe2 = Recipe.builder()
                    .steps(Set.of(Step.builder().description("test").build()))
                    .ingredients(Set.of(Ingredient.builder().name("test ing").amount("1").state("dry").build()))
                    .name("another test recipe")
                    .difficultyRating(10)
                    .minutesToMake(2)
                    .user(gherkin)
                    .build();
            recipeRepo.save(recipe2);

            Recipe recipe3 = Recipe.builder()
                    .steps(Set.of(Step.builder().description("test 2").build()))
                    .ingredients(Set.of(Ingredient.builder().name("test ing 2").amount("2").state("wet").build()))
                    .name("another another test recipe")
                    .difficultyRating(5)
                    .minutesToMake(2)
                    .user(maliksa)
                    .build();

            recipeRepo.save(recipe3);

            Recipe recipe4 = Recipe.builder()
                    .name("chocolate and potato chips")
                    .difficultyRating(10)
                    .minutesToMake(1)
                    .ingredients(Set.of(
                            Ingredient.builder().name("potato chips").amount("1 bag").build(),
                            Ingredient.builder().name("chocolate").amount("1 bar").build()))
                    .steps(Set.of(
                            Step.builder().stepNumber(1).description("eat both items together").build()))
                    .reviews(Set.of(
                            Review.builder().user(ben).rating(10).description("this stuff is so good").build()
                    ))
                    .user(gherkin)
                    .build();

            recipeRepo.save(recipe4);
            LOGGER.info("FINISHED TEST DATABASE SETUP");
        } else {
            LOGGER.info("DATABASE ALREADY CONTAINS ENTRIES - NOT ADDING ADDITIONAL TEST ENTRIES ");
        }
    }
}