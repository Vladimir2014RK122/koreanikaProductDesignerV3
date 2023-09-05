package ru.koreanika.service.event;

import javafx.scene.Node;
import ru.koreanika.service.eventbus.Event;

public class MouseClickedEvent extends Event<MouseClickedEventHandler> {

    public final static Type<MouseClickedEventHandler> TYPE = new Type<MouseClickedEventHandler>();

    private final Node source;

    public MouseClickedEvent(Node source) {
        this.source = source;
    }

    @Override
    public Type<MouseClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MouseClickedEventHandler handler) {
        handler.onEvent(this);
    }

    @Override
    public Node getSource() {
        return source;
    }
}
