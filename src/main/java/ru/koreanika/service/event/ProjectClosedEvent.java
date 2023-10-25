package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.Event;

public class ProjectClosedEvent extends Event<ProjectClosedEventHandler> {

    public final static Type<ProjectClosedEventHandler> TYPE = new Type<>();

    public ProjectClosedEvent() {
    }

    @Override
    public Type<ProjectClosedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ProjectClosedEventHandler handler) {
        handler.onEvent(this);
    }
}
