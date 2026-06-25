package com.hotel.pattern.proxy;

import com.hotel.model.entity.User;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PermissionProxy implements InvocationHandler {

    private final Object target;
    private final User currentUser;

    private PermissionProxy(Object target, User currentUser) {
        this.target = target;
        this.currentUser = currentUser;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, Class<T> interfaceType, User currentUser) {
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                new PermissionProxy(target, currentUser)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (currentUser == null) {
            throw new SecurityException("未登录，无权操作");
        }
        return method.invoke(target, args);
    }
}