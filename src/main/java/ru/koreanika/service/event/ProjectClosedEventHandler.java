package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.EventHandler;

public interface ProjectClosedEventHandler extends EventHandler {
    void onEvent(final ProjectClosedEvent e);
}

