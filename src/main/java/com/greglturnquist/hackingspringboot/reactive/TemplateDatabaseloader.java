package com.greglturnquist.hackingspringboot.reactive;

import com.greglturnquist.hackingspringboot.reactive.domain.item.Item;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class TemplateDatabaseloader {

  @Bean
  CommandLineRunner initialize(MongoOperations mongo){
    return args -> {
      mongo.save(new Item("alf alarm clock", 19.99));
      mongo.save(new Item("Smurf TV tray", 24.99));
      mongo.save(new Item("test stuff", 49.99));
    };
  }
}
