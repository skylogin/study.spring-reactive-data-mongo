package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class HackingSpringBootChapter2ReactiveApplication {

  public static void main(String[] args) {
    BlockHound.builder()
        .allowBlockingCallsInside(
            TemplateEngine.class.getCanonicalName(), "process"
        ).install();

    SpringApplication.run(HackingSpringBootChapter2ReactiveApplication.class, args);
  }

}
