package com.greglturnquist.hackingspringboot.reactive.domain.cart;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CartRepository extends ReactiveCrudRepository<Cart, String> {

}
