package ru.koreanika.service;

import ru.koreanika.Common.Material.CachingImageLoader;
import ru.koreanika.project.ProjectHandler;
import ru.koreanika.service.eventbus.EventBus;

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
        return null;
    }
}