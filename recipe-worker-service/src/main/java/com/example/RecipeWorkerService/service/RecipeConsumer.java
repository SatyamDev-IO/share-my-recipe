package com.example.RecipeWorkerService.service;

import com.example.RecipeWorkerService.dto.RecipeDTO;
import com.example.RecipeWorkerService.entity.ProcessedEvent;
import com.example.RecipeWorkerService.entity.Recipe;
import com.example.RecipeWorkerService.entity.User;
import com.example.RecipeWorkerService.repository.ProcessedEventRepository;
import com.example.RecipeWorkerService.repository.RecipeRepository;
import com.example.RecipeWorkerService.repository.UserRepository;
import com.example.RecipeWorkerService.enums.Status;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeConsumer {

    private static final Logger log = LoggerFactory.getLogger(RecipeConsumer.class);
    @Autowired
    UserRepository _userRepository;
    @Autowired
    RecipeRepository _recipeRepository;
    @Autowired
    ProcessedEventRepository _processedEventRepository;

    @KafkaListener(topics = "recipe-topic", groupId = "recipe-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, RecipeDTO> record) {

        String eventId = record.key();
        RecipeDTO recipeDTO = record.value();

        log.info("Received recipe event: {} with eventId: {}", recipeDTO.getTitle(), eventId);

        if (eventId == null) {
            log.error("EventId is null, skipping message");
            throw new RuntimeException("EventId is missing");
        }

        if (_processedEventRepository.existsById(eventId)) {
            log.warn("Duplicate event ignored: {}", eventId);
            return;
        }

        try {

            User chef = _userRepository.findById(recipeDTO.getChefId())
                    .orElseThrow(() -> new RuntimeException("Chef not found"));

            log.info("Saving recipe for chefId: {}", recipeDTO.getChefId());

            Recipe recipe = new Recipe();
            recipe.setTitle(recipeDTO.getTitle());
            recipe.setSummary(recipeDTO.getSummary());
            recipe.setIngredients(recipeDTO.getIngredients());
            recipe.setSteps(recipeDTO.getSteps());

            recipe.setChef(chef);
            recipe.setStatus(Status.DRAFT);
            recipe.setPublishedAt(null);

            _recipeRepository.save(recipe);
            _processedEventRepository.save(new ProcessedEvent(eventId));

            log.info("Recipe saved successfully with eventId: {}", eventId);

        } catch (Exception e){
            log.error("Error processing eventId: {}", eventId, e);
            throw e;
        }
    }

    @KafkaListener(topics = "recipe-topic.DLT", groupId = "recipe-group-dlq", containerFactory = "kafkaListenerContainerFactory")
    public void handleDql(RecipeDTO failedRecipe, @Header(KafkaHeaders.RECEIVED_KEY) String eventId) {
        log.error("ALARM: Recipe event {} is in the DLQ. Manual intervention required for Chef ID: {}",
                eventId, failedRecipe.getChefId());
    }
}
