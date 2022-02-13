package com.greglturnquist.hackingspringboot.reactive;

import com.mongodb.client.model.geojson.Point;
import java.util.Date;
import org.springframework.data.annotation.Id;

public class Item {
  private @Id String id;
  private String name;
  private String description;
  private double price;
  private String distributorRegion;
  private Date releaseDate;
  private int availableUnits;
  private Point location;
  private boolean active;

  private Item(){

  }

  public Item(String name, double price) {
    this.name = name;
    this.price = price;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(double price) {
    this.price = price;
  }
}
