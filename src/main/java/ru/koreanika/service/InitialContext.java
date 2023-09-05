package ru.koreanika.service;

import ru.koreanika.service.eventbus.EventBus;

public class InitialContext {
    public Object lookup(String serviceName) {
        if (serviceName.equalsIgnoreCase("EventBus")) {
            return new EventBus();
        }
        return null;
    }
}