package com.hotel.pattern.chain;

import com.hotel.model.entity.User;

public interface ValidationHandler {

    void setNext(ValidationHandler next);

    ValidationResult handle(User user, Object request);
}