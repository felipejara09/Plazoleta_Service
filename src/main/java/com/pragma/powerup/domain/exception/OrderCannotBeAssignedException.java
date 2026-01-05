package com.pragma.powerup.domain.exception;

public class OrderCannotBeAssignedException extends DomainException {
  public OrderCannotBeAssignedException() {
    super("Order cannot be assigned in its current status");
  }
}
