package com.hotel.pattern.chain;

import com.hotel.model.entity.User;

public class PermissionValidationHandler extends AbstractValidationHandler {

    private final String requiredPermission;

    public PermissionValidationHandler(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    @Override
    public ValidationResult handle(User user, Object request) {
        if (user == null) {
            return ValidationResult.fail("用户未登录，请先登录");
        }
        if (user.getRoleId() == null) {
            return ValidationResult.fail("用户角色未分配");
        }
        return checkNext(user, request);
    }
}