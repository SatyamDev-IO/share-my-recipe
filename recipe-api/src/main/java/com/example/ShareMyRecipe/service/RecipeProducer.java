package com.example.ShareMyRecipe.service;

import com.example.ShareMyRecipe.dto.RecipeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RecipeProducer {
    @Autowired
    private KafkaTemplate<String, RecipeDTO> kafkaTemplate;

    private static final String TOPIC = "recipe-topic";
    private static final Logger log = LoggerFactory.getLogger(RecipeProducer.class);

    public void sendRecipe(RecipeDTO recipeDTO) {
        String eventId = UUID.randomUUID().toString();
        kafkaTemplate.send(TOPIC, eventId, recipeDTO);
        log.info("Sent recipe event: {} with eventId: {}", recipeDTO.getTitle(), eventId);
    }
}
