package com.greglturnquist.hackingspringboot.reactive.domain.item;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

public interface ItemByExampleRepository extends ReactiveQueryByExampleExecutor<Item> {

}
