package org.example.phoneduck;

import org.example.phoneduck.model.ChatRoomModel;
import org.example.phoneduck.model.MessageModel;
import org.example.phoneduck.repository.ChatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class PhoneDuckApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneDuckApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ChatRepository chatRepository){
        return args -> chatRepository.save(new ChatRoomModel(1L, "General Chat", null));
    }
}
