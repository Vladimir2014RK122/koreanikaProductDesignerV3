package ru.koreanika.service.event;

import ru.koreanika.PortalClient.Authorization.AppType;
import ru.koreanika.service.eventbus.Event;

public class ApplicationTypeChangeEvent extends Event<ApplicationTypeChangeEventHandler> {

    public final static Type<ApplicationTypeChangeEventHandler> TYPE = new Type<>();

    private final AppType applicationType;

    public ApplicationTypeChangeEvent(AppType applicationType) {
        this.applicationType = applicationType;
    }

    public AppType getApplicationType() {
        return applicationType;
    }

    @Override
    public Type<ApplicationTypeChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ApplicationTypeChangeEventHandler handler) {
        handler.onEvent(this);
    }

}
