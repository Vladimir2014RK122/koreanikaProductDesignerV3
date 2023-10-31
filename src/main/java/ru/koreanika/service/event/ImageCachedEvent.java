package ru.koreanika.service.event;

import ru.koreanika.service.eventbus.Event;

public class ImageCachedEvent extends Event<ImageCachedEventHandler> {

    public final static Type<ImageCachedEventHandler> TYPE = new Type<>();

    private final String remotePath;
    private final String localPath;

    public ImageCachedEvent(String remotePath, String localPath) {
        this.remotePath = remotePath;
        this.localPath = localPath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public String getLocalPath() {
        return localPath;
    }

    @Override
    public Type<ImageCachedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ImageCachedEventHandler handler) {
        handler.onEvent(this);
    }
}
