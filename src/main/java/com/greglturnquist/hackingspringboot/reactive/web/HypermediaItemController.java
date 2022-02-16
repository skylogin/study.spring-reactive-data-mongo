package com.greglturnquist.hackingspringboot.reactive.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;

import com.greglturnquist.hackingspringboot.reactive.domain.item.Item;
import com.greglturnquist.hackingspringboot.reactive.domain.item.ItemRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HypermediaItemController {

  private final ItemRepository repository;

  public HypermediaItemController(
      ItemRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/hypermedia")
  Mono<RepresentationModel<?>> root() {
    HypermediaItemController controller = //
        methodOn(HypermediaItemController.class);

    Mono<Link> selfLink = linkTo(controller.root()).withSelfRel().toMono();

    Mono<Link> itemsAggregateLink = //
        linkTo(controller.findAll()) //
            .withRel(IanaLinkRelations.ITEM) //
            .toMono();

    return selfLink.zipWith(itemsAggregateLink) //
        .map(links -> Links.of(links.getT1(), links.getT2())) //
        .map(links -> new RepresentationModel<>(links.toList()));
  }

  @GetMapping("/hypermedia/items")
  Mono<CollectionModel<EntityModel<Item>>> findAll() {
    return this.repository.findAll() //
        .flatMap(item -> findOne(item.getId())) //
        .collectList() //
        .flatMap(entityModels -> linkTo(methodOn(HypermediaItemController.class) //
            .findAll()).withSelfRel() //
            .toMono() //
            .map(selfLink -> CollectionModel.of(entityModels, selfLink)));
  }


  @GetMapping("/hypermedia/items/{id}")
  Mono<EntityModel<Item>> findOne(@PathVariable String id){
    HypermediaItemController controller = methodOn(HypermediaItemController.class);

    Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel().toMono();

    Mono<Link> aggregateLink = linkTo(controller.findAll()).withRel(IanaLinkRelations.ITEM).toMono();

    return Mono.zip(repository.findById(id), selfLink, aggregateLink)
        .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));
  }
}
