package com.greglturnquist.hackingspringboot.reactive.domain.item;

import org.springframework.data.repository.CrudRepository;

public interface BlockingItemRepository extends CrudRepository<Item, String> {

}
