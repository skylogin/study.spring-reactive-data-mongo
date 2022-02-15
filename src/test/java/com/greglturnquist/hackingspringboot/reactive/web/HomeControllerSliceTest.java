package com.greglturnquist.hackingspringboot.reactive.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.greglturnquist.hackingspringboot.reactive.domain.cart.Cart;
import com.greglturnquist.hackingspringboot.reactive.domain.item.Item;
import com.greglturnquist.hackingspringboot.reactive.service.InventoryService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Disabled("pom.xml에서 blockhound-junit-platform 의존 관계를 제거한 후에 실행해야 성공한다.")
@WebFluxTest(HomeController.class)
public class HomeControllerSliceTest {

  @Autowired
  private WebTestClient client;

  @MockBean
  InventoryService inventoryService;

  @Test
  void homePage(){
    when(inventoryService.getInventory()).thenReturn(Flux.just(
        new Item("id1", "name1", "desc1", 1.99),
        new Item("id2", "name2", "desc2", 9.99)
    ));
    when(inventoryService.getCart("My Cart"))
        .thenReturn(Mono.just(new Cart("My Cart")));

    client.get().uri("/").exchange()
        .expectStatus().isOk()
        .expectBody(String.class)
        .consumeWith(exchangeResult -> {
          assertThat(exchangeResult.getResponseBody()).contains("action=\"/add/id1\"");
          assertThat(exchangeResult.getResponseBody()).contains("action=\"/add/id2\"");
        });
  }
}
