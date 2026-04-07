package com.example.RecipeWorkerService.service;

import com.example.RecipeWorkerService.dto.RecipeDTO;
import com.example.RecipeWorkerService.entity.Recipe;
import com.example.RecipeWorkerService.entity.User;
import com.example.RecipeWorkerService.repository.RecipeRepository;
import com.example.RecipeWorkerService.repository.UserRepository;
import enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RecipeConsumer {
    @Autowired
    UserRepository _userRepository;
    @Autowired
    RecipeRepository _recipeRepository;

    @KafkaListener(topics = "recipe-topic", groupId = "recipe-group")
    public void consume(RecipeDTO recipeDTO) {

        System.out.println("Received from Kafka: " + recipeDTO.getTitle());

        User chef = _userRepository.findById(recipeDTO.getChefId())
                .orElseThrow(() -> new RuntimeException("Chef not found"));

        Recipe recipe = new Recipe();
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setSummary(recipeDTO.getSummary());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setSteps(recipeDTO.getSteps());

        recipe.setChef(chef);
        recipe.setStatus(Status.DRAFT);
        recipe.setPublishedAt(null);

        _recipeRepository.save(recipe);

        System.out.println("Recipe saved to DB!");
    }
}
