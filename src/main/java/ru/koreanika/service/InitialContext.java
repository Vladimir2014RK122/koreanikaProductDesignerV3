package ru.koreanika.service;

import ru.koreanika.common.material.CachingImageLoader;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.service.eventbus.EventBus;

import java.util.concurrent.Executors;

public class InitialContext {
    public Object lookup(String serviceName) {
        if (serviceName.equalsIgnoreCase("EventBus")) {
            return new EventBus();
        }
        if (serviceName.equalsIgnoreCase("ImageIndex")) {
            return new ImageIndexProvider().get();
        }
        if (serviceName.equalsIgnoreCase("ImageLoader")) {
            return new CachingImageLoader();
        }
        if (serviceName.equalsIgnoreCase("ProjectHandler")) {
            return new ProjectHandler();
        }
        if (serviceName.equalsIgnoreCase("ExecutorService")) {
            return Executors.newSingleThreadExecutor();
        }
        return null;
    }
}