package com.example.RecipeWorkerService.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;

@Configuration
public class KafkaConfig {
    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<String, Object> template) {

        FixedBackOff fixedBackOff = new FixedBackOff(2000L, 3);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (record, ex) -> {
                    return new TopicPartition(record.topic() + ".DLT", -1);
                });

        return new DefaultErrorHandler(recoverer, fixedBackOff);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            CommonErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
