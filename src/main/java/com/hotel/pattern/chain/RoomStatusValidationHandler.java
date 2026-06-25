package com.hotel.pattern.chain;

import com.hotel.model.entity.User;

public class RoomStatusValidationHandler extends AbstractValidationHandler {

    @Override
    public ValidationResult handle(User user, Object request) {
        return checkNext(user, request);
    }
}