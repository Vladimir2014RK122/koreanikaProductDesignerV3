package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.EventHandler;

public interface ProjectOpenedEventHandler extends EventHandler {
    void onEvent(final ProjectOpenedEvent e);
}

