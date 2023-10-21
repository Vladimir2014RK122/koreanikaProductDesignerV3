package ru.koreanika.utils.Currency;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import ru.koreanika.service.ServiceLocator;
import ru.koreanika.service.event.NotificationEvent;
import ru.koreanika.service.eventbus.EventBus;
import ru.koreanika.utils.InfoMessage;

public class BankCurrency {

    private final EventBus eventBus;
    private SimpleBooleanProperty bankServerAvailable = new SimpleBooleanProperty(false);

    private SimpleDoubleProperty bankUSDValue = new SimpleDoubleProperty(-1.0);
    private SimpleDoubleProperty bankEURValue = new SimpleDoubleProperty(-1.0);

    private static BankCurrency instance;
    private static BankCurrencyThread bankCurrencyThread;

    private BankCurrency() {
        eventBus = ServiceLocator.getService("EventBus", EventBus.class);

        bankServerAvailable.addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.SUCCESS, "Удалось подключится к серверу валют."));
            } else {
                eventBus.fireEvent(new NotificationEvent(InfoMessage.MessageType.ERROR, "Не удалось подключится к серверу валют."));
            }
        });

        bankUSDValue.addListener((observableValue, oldVal, newVal) -> {
            System.out.println("BANK CURRENCY UPDATE: USD = " + newVal);
        });

        bankEURValue.addListener((observableValue, oldVal, newVal) -> {
            System.out.println("BANK CURRENCY UPDATE: EUR = " + newVal);
        });
    }

    public static BankCurrency getInstance() {
        if (instance == null) {
            instance = new BankCurrency();
        }
        return instance;
    }

    public void startMonitor() {
        bankCurrencyThread = new BankCurrencyThread(bankServerAvailable, bankUSDValue, bankEURValue);
        bankCurrencyThread.setDaemon(true);
        bankCurrencyThread.start();
    }

    public void stopMonitor() {
        if (bankCurrencyThread != null && bankCurrencyThread.isAlive()) {
            bankCurrencyThread.interrupt();
            bankCurrencyThread = null;
        }
    }

    public void setFirstCurrencyServerAnswer(FirstCurrencyServerAnswer firstCurrencyServerAnswer) {
        bankCurrencyThread.firstCurrencyServerAnswer = firstCurrencyServerAnswer;
    }

    public double getBankEURValue() {
        return bankEURValue.get();
    }

    public double getBankUSDValue() {
        return bankUSDValue.get();
    }

    public boolean isValueActual() {
        return bankServerAvailable.get();
    }
}
