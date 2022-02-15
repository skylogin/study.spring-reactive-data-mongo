package com.greglturnquist.hackingspringboot.reactive.web;

import com.greglturnquist.hackingspringboot.reactive.domain.cart.Cart;
import com.greglturnquist.hackingspringboot.reactive.domain.cart.CartRepository;
import com.greglturnquist.hackingspringboot.reactive.service.CartService;
import com.greglturnquist.hackingspringboot.reactive.service.InventoryService;
import com.greglturnquist.hackingspringboot.reactive.domain.item.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {

  private final InventoryService inventoryService;

  public HomeController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }


  @GetMapping
  public Mono<Rendering> home(){
    return Mono.just(Rendering.view("home.html")
      .modelAttribute("items", inventoryService.getInventory().doOnNext(System.out::println))
      .modelAttribute("cart", inventoryService.getCart("My Cart")
        .defaultIfEmpty(new Cart("My Cart")))
      .build()
    );
  }

  @PostMapping("/add/{id}")
  public Mono<String> addToCart(@PathVariable String id){
//    return this.cartService.addToCart("My Cart", id)
//        .thenReturn("redirect:/");
    return this.inventoryService.addItemToCart("My Cart", id)
        .thenReturn("redirect:/");
  }

  @DeleteMapping("/delete/{id}")
  public Mono<String> removeOneFromCart(@PathVariable String id){
    return this.inventoryService.removeOneFromCart("My Cart", id)
        .thenReturn("redirect:/");
  }

  @GetMapping("/search")
  public Mono<Rendering> search(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String description,
      @RequestParam boolean useAnd) {
    return Mono.just(Rendering.view("home.html")
      .modelAttribute("results", inventoryService.searchByExample(name, description, useAnd))
      .build()
    );
  }

}
