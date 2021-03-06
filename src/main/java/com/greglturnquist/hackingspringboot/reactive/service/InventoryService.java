package com.greglturnquist.hackingspringboot.reactive.service;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.greglturnquist.hackingspringboot.reactive.domain.cart.Cart;
import com.greglturnquist.hackingspringboot.reactive.domain.cart.CartRepository;
import com.greglturnquist.hackingspringboot.reactive.domain.cartitem.CartItem;
import com.greglturnquist.hackingspringboot.reactive.domain.item.Item;
import com.greglturnquist.hackingspringboot.reactive.domain.item.ItemRepository;
import java.util.stream.Collectors;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InventoryService {

  private final ItemRepository itemRepository;
  private final CartRepository cartRepository;


  public InventoryService(
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
    return this.cartRepository.findById(cartId)
          .log("foundCart")
        .defaultIfEmpty(new Cart(cartId))
          .log("emptyCart")
        .flatMap(cart -> cart.getCartItems().stream()
          .filter(cartItem -> cartItem.getItem()
            .getId().equals(itemId))
            .findAny()
            .map(cartItem -> {
              cartItem.increment();
              return Mono.just(cart).log("newCartItem");
            })
          .orElseGet(() -> {
            return this.itemRepository.findById(itemId)
                  .log("fetchedItem")
                .map(item -> new CartItem(item))
                  .log("cartItem")
                .map(cartItem -> {
                  cart.getCartItems().add(cartItem);
                  return cart;
                })
                  .log("addedCartItem");
          })
        )
          .log("cartWithAnotherItem")
        .flatMap(cart -> this.cartRepository.save(cart))
          .log("saveCart");

  }

  public Mono<Cart> removeOneFromCart(String cartId, String itemId) {
    return this.cartRepository.findById(cartId)
        .defaultIfEmpty(new Cart(cartId))
        .flatMap(cart -> cart.getCartItems().stream()
            .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
            .findAny()
            .map(cartItem -> {
              cartItem.decrement();
              return Mono.just(cart);
            }) //
            .orElse(Mono.empty()))
        .map(cart -> new Cart(cart.getId(), cart.getCartItems().stream()
            .filter(cartItem -> cartItem.getQuantity() > 0)
            .collect(Collectors.toList())))
        .flatMap(cart -> this.cartRepository.save(cart));
  }
}
