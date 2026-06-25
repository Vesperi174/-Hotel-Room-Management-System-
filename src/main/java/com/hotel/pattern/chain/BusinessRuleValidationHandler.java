package com.hotel.pattern.chain;

import com.hotel.model.entity.User;

public class BusinessRuleValidationHandler extends AbstractValidationHandler {

    @Override
    public ValidationResult handle(User user, Object request) {
        if (request == null) {
            return ValidationResult.fail("请求参数不能为空");
        }
        return checkNext(user, request);
    }
}