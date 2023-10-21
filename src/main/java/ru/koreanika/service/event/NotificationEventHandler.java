package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.EventHandler;

public interface NotificationEventHandler extends EventHandler {
    void onEvent(final NotificationEvent e);
}
