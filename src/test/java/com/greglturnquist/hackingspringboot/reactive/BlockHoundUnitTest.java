package com.greglturnquist.hackingspringboot.reactive;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class BlockHoundUnitTest {

  @Test
  void threadSleepIsABlockingCall(){
    Mono.delay(Duration.ofSeconds(1))
        .flatMap(tick -> {
          try{
            Thread.sleep(10);
            return Mono.just(true);
          } catch(InterruptedException e){
            return Mono.error(e);
          }
        })
        .as(StepVerifier::create)
        .verifyComplete();
  }
}
