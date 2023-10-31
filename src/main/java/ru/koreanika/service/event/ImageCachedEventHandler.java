package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.EventHandler;

public interface ImageCachedEventHandler extends EventHandler {
    void onEvent(ImageCachedEvent e);
}
