package ru.koreanika.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {

    private static final Map<String, Object> registry = new HashMap<>();

    public static <T> T getService(String serviceName, Class<T> clazz) {
        if (registry.containsKey(serviceName)) {
            return clazz.cast(registry.get(serviceName));
        }

        InitialContext context = new InitialContext();
        T service = clazz.cast(context.lookup(serviceName));
        registry.put(serviceName, service);
        return service;
    }

}