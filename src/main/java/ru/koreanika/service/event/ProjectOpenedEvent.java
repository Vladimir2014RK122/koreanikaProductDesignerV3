package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.Event;

import java.io.File;

public class ProjectOpenedEvent extends Event<ProjectOpenedEventHandler> {

    public final static Type<ProjectOpenedEventHandler> TYPE = new Type<>();

    private final File projectFile;

    public ProjectOpenedEvent(File projectFile) {
        this.projectFile = projectFile;
    }

    public File getProjectFile() {
        return projectFile;
    }

    @Override
    public Type<ProjectOpenedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ProjectOpenedEventHandler handler) {
        handler.onEvent(this);
    }
}
