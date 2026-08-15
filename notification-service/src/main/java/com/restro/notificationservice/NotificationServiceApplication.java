package com.restro.notificationservice;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.http.MediaType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@SpringBootApplication
public class NotificationServiceApplication {
	private static final Logger log = LoggerFactory.getLogger(NotificationServiceApplication.class);
	private final List<SseEmitter> kitchenSubscribers = new CopyOnWriteArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@KafkaListener(topics = "restro.order-events")
	public void receiveOrderEvent(String event) {
		log.info("Received restaurant notification event: {}", event);
		kitchenSubscribers.forEach(emitter -> {
			try {
				emitter.send(SseEmitter.event().name("order-created").data(event));
			} catch (IOException exception) {
				kitchenSubscribers.remove(emitter);
			}
		});
	}

	@RestController
	@CrossOrigin(origins = {"http://localhost:4200"})
	class KitchenNotificationController {
		@GetMapping(value = "/api/notifications/kitchen/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
		public SseEmitter stream() {
			SseEmitter emitter = new SseEmitter(0L);
			kitchenSubscribers.add(emitter);
			emitter.onCompletion(() -> kitchenSubscribers.remove(emitter));
			emitter.onTimeout(() -> kitchenSubscribers.remove(emitter));
			return emitter;
		}
	}
}
