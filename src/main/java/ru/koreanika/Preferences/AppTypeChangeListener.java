package ru.koreanika.Preferences;

import ru.koreanika.PortalClient.Authorization.AppType;

public interface AppTypeChangeListener {

    void changed(AppType newValue);
}
