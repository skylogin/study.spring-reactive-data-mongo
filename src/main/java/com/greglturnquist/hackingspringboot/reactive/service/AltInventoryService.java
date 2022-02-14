package com.greglturnquist.hackingspringboot.reactive.service;

import com.greglturnquist.hackingspringboot.reactive.domain.cart.Cart;
import com.greglturnquist.hackingspringboot.reactive.domain.cart.CartRepository;
import com.greglturnquist.hackingspringboot.reactive.domain.cartitem.CartItem;
import com.greglturnquist.hackingspringboot.reactive.domain.item.Item;
import com.greglturnquist.hackingspringboot.reactive.domain.item.ItemRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AltInventoryService {

  private final ItemRepository itemRepository;
  private final CartRepository cartRepository;


  public AltInventoryService(
      ItemRepository itemRepository,
      CartRepository cartRepository) {
    this.itemRepository = itemRepository;
    this.cartRepository = cartRepository;
  }

  public Flux<Item> searchByExample(String name, String description, boolean useAnd) {
    Item item = new Item(name, description, 0.0);

    ExampleMatcher matcher = (useAnd? ExampleMatcher.matchingAll(): ExampleMatcher.matchingAny())
        .withStringMatcher(StringMatcher.CONTAINING)
        .withIgnoreCase()
        .withIgnorePaths("price");

    Example<Item> probe = Example.of(item, matcher);

    return itemRepository.findAll(probe);
  }

  public Mono<Cart> getCart(String cartId) {
    return this.cartRepository.findById(cartId);
  }

  public Flux<Item> getInventory() {
    return this.itemRepository.findAll();
  }

  Mono<Item> saveItem(Item newItem) {
    return this.itemRepository.save(newItem);
  }

  Mono<Void> deleteItem(String id) {
    return this.itemRepository.deleteById(id);
  }


  public Mono<Cart> addItemToCart(String cartId, String itemId){
    Cart myCart = this.cartRepository.findById(cartId) //
        .defaultIfEmpty(new Cart(cartId)) //
        .block();


    return myCart.getCartItems().stream() //
        .filter(cartItem -> cartItem.getItem().getId().equals(itemId)) //
        .findAny() //
        .map(cartItem -> {
          cartItem.increment();
          return Mono.just(myCart);
        }) //
        .orElseGet(() -> this.itemRepository.findById(itemId) //
            .map(item -> new CartItem(item)) //
            .map(cartItem -> {
              myCart.getCartItems().add(cartItem);
              return myCart;
            })) //
        .flatMap(cart -> this.cartRepository.save(cart));
  }
}
