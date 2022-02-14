package com.greglturnquist.hackingspringboot.reactive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.greglturnquist.hackingspringboot.reactive.domain.cart.Cart;
import com.greglturnquist.hackingspringboot.reactive.domain.cart.CartRepository;
import com.greglturnquist.hackingspringboot.reactive.domain.cartitem.CartItem;
import com.greglturnquist.hackingspringboot.reactive.domain.item.Item;
import com.greglturnquist.hackingspringboot.reactive.domain.item.ItemRepository;
import com.greglturnquist.hackingspringboot.reactive.service.AltInventoryService;
import java.time.Duration;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class BlockHoundIntegrationTest {

  AltInventoryService inventoryService;

  @MockBean
  ItemRepository itemRepository;

  @MockBean
  CartRepository cartRepository;

  @BeforeEach
  void setUp(){
    // 테스트 데이터 정의
    Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
    CartItem sampleCartItem = new CartItem(sampleItem);
    Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

    // 협력자와의 상호작용 정의
    when(cartRepository.findById(anyString()))
        .thenReturn(Mono.<Cart> empty().hide());

    when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
    when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

    inventoryService = new AltInventoryService(itemRepository, cartRepository);
  }

  @Test
  void blockHoundShouldTrapBlockingCall() {
    Mono.delay(Duration.ofSeconds(1))
        .flatMap(tick -> inventoryService.addItemToCart("My Cart", "item1"))
        .as(StepVerifier::create)
        .verifyErrorSatisfies(throwable -> {
          assertThat(throwable).hasMessageContaining(
              "block()/blockFirst()/blockLast() are blocking");
        });
  }
}
