package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.Event;
import ru.koreanika.utils.InfoMessage;

public class NotificationEvent extends Event<NotificationEventHandler>  {

    public final static Type<NotificationEventHandler> TYPE = new Type<>();

    private final InfoMessage.MessageType messageType;
    private final String message;

    public NotificationEvent(InfoMessage.MessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public InfoMessage.MessageType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Type<NotificationEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NotificationEventHandler handler) {
        handler.onEvent(this);
    }
}
