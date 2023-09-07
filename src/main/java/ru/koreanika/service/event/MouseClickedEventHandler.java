package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.EventHandler;

public interface MouseClickedEventHandler extends EventHandler {
    void onEvent(final MouseClickedEvent e);
}

