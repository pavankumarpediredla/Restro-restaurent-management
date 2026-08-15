package com.restro.restaurantservice.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

	@Bean
	public ProducerFactory<String, String> producerFactory(
			@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configuration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configuration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configuration.put(ProducerConfig.ACKS_CONFIG, "1");
		return new DefaultKafkaProducerFactory<>(configuration);
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}
}
