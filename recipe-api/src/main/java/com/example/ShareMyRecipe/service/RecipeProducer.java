package com.example.ShareMyRecipe.service;

import com.example.ShareMyRecipe.dto.RecipeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RecipeProducer {
    @Autowired
    private KafkaTemplate<String, RecipeDTO> kafkaTemplate;

    private static final String TOPIC = "recipe-topic";

    public void sendRecipe(RecipeDTO recipeDTO) {
        kafkaTemplate.send(TOPIC, recipeDTO);
        System.out.println("Sent to Kafka: " + recipeDTO.getTitle());
    }
}
