package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.EventHandler;

public interface ApplicationTypeChangeEventHandler extends EventHandler {
    void onEvent(final ApplicationTypeChangeEvent e);
}
