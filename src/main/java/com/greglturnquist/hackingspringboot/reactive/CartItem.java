package com.greglturnquist.hackingspringboot.reactive;

public class CartItem {

  private Item item;
  private int quantity;

  private CartItem(){

  }

  public CartItem(Item item) {
    this.item = item;
    this.quantity = 1;
  }

  public Item getItem() {
    return item;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void increment() {
    this.quantity++;
  }
}
