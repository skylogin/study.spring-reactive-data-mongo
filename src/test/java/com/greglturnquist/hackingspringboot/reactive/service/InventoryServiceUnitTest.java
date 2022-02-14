package com.greglturnquist.hackingspringboot.reactive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.greglturnquist.hackingspringboot.reactive.domain.cart.Cart;
import com.greglturnquist.hackingspringboot.reactive.domain.cart.CartRepository;
import com.greglturnquist.hackingspringboot.reactive.domain.cartitem.CartItem;
import com.greglturnquist.hackingspringboot.reactive.domain.item.Item;
import com.greglturnquist.hackingspringboot.reactive.domain.item.ItemRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class InventoryServiceUnitTest {

  InventoryService inventoryService;

  @MockBean
  private ItemRepository itemRepository;

  @MockBean
  private CartRepository cartRepository;


//  아래 코드는 @MockBean을 작성하는것과 동일
//  @BeforeEach
//  void setUp(){
//    itemRepository = mock(ItemRepository.class);
//    cartRepository = mock(CartRepository.class);
//  }

  @BeforeEach
  void setUp(){
    // 테스트 데이터 정의
    Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
    CartItem sampleCartItem = new CartItem(sampleItem);
    Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

    // 협력자와의 상호작용 정의
    when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
    when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
    when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

    inventoryService = new InventoryService(itemRepository, cartRepository);
  }


  @Test
  void addItemToEmptyCartShouldProduceOneCartItem(){
    inventoryService.addItemToCart("My Cart", "item1")
        .as(StepVerifier::create)
        .expectNextMatches(cart -> {
          assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
              .containsExactlyInAnyOrder(1);

          assertThat(cart.getCartItems()).extracting(CartItem::getItem)
              .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99));

          return true;
        })
        .verifyComplete();
  }

}
