package com.hotel.pattern.chain;

import com.hotel.model.entity.User;

public abstract class AbstractValidationHandler implements ValidationHandler {

    protected ValidationHandler next;

    @Override
    public void setNext(ValidationHandler next) {
        this.next = next;
    }

    protected ValidationResult checkNext(User user, Object request) {
        if (next == null) {
            return ValidationResult.success();
        }
        return next.handle(user, request);
    }
}