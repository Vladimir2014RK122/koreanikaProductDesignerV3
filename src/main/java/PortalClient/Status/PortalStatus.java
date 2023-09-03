package PortalClient.Status;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PortalStatus {

    private static PortalStatus portalStatus;
    private PortalStatusThread portalStatusThread;
    private final BooleanProperty portalAvailable = new SimpleBooleanProperty(false);

    public static synchronized PortalStatus getInstance() {
        if (portalStatus == null) {
            portalStatus = new PortalStatus();
        }
        return portalStatus;
    }

    public void startMonitoring(String host) {
        portalStatusThread = new PortalStatusThread(portalAvailable, host);
        portalStatusThread.setDaemon(true);
        portalStatusThread.start();
    }

    public void stopMonitoring() {
        portalStatusThread.interrupt();
        portalStatusThread = null;
    }

    public BooleanProperty portalAvailableProperty() {
        return portalAvailable;
    }

    public boolean isPortalAvailable() {
        return portalAvailable.get();
    }
}
