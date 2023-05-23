package dev.practice.PubSubChat;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class PubSubChatApplication implements CommandLineRunner {

	private final ChatService chatService;

	public static void main(String[] args) {
		SpringApplication.run(PubSubChatApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Application Started..");

		chatService.enterChatRoom("chat1");
	}
}
