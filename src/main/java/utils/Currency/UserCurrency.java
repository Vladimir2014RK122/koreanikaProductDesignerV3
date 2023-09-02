package utils.Currency;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import utils.MainWindow;
import utils.ProjectHandler;
import utils.Receipt.ReceiptManager;

import java.util.Locale;
import java.util.Optional;

public class UserCurrency {

    public static final double MIN_USD_VALUE = 70.00;
    public static final double MIN_EUR_VALUE = 70.00;

    public static final double BANK_MULT_COEFF_USD = 1.05;
    public static final double BANK_MULT_COEFF_EUR = 1.05;

    private static UserCurrency userCurrency = null;

    public static UserCurrency getInstance(){
        if(userCurrency == null){
            userCurrency = new UserCurrency();
        }
        return userCurrency;
    }

    public void updateCurrencyValue(){

        System.out.println("UPDATE CURRENCY VALUE");

        if(BankCurrency.getInstance().isValueActual()){

            double usd = BankCurrency.getInstance().getBankUSDValue() * BANK_MULT_COEFF_USD;
            double eur = BankCurrency.getInstance().getBankEURValue() * BANK_MULT_COEFF_EUR;

            if(usd >= MIN_USD_VALUE){
                MainWindow.getUSDValue().set(usd);
            }else{
                MainWindow.getUSDValue().set(MIN_USD_VALUE);
            }

            if(eur >= MIN_EUR_VALUE){
                MainWindow.getEURValue().set(eur);
            }else{
                MainWindow.getEURValue().set(MIN_EUR_VALUE);
            }
        }
    }

    public void updateCurrencyValueWithRequest(){

        if(!BankCurrency.getInstance().isValueActual()) return;

        double usd = BankCurrency.getInstance().getBankUSDValue() * BANK_MULT_COEFF_USD;
        double eur = BankCurrency.getInstance().getBankEURValue() * BANK_MULT_COEFF_EUR;

        if(!(usd > MIN_USD_VALUE ||  eur > MIN_EUR_VALUE)){
            return;
        }

        if(usd <= MIN_USD_VALUE){
            usd = MIN_USD_VALUE;
        }

        if(eur <= MIN_EUR_VALUE){
            eur = MIN_EUR_VALUE;
        }

        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ usd = " + usd);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ eur = " + eur);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ PROJ USD = " + MainWindow.getUSDValue());
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ PROJ EUR = " + MainWindow.getEURValue());

        if((int)(usd * 100) == (int)(MainWindow.getUSDValue().doubleValue() * 100) &&
                (int)(eur * 100) == (int)(MainWindow.getEURValue().doubleValue() * 100)){
            return;
        }

//        MainWindow.getUSDValue();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Курс валют изменился!");

        alert.setHeaderText("Обновить курс валют на следующий:");
        alert.setContentText(String.format(Locale.ENGLISH, "\r\n1%s = %.2f%s \r\n1%s = %.2f%s",
                ReceiptManager.USD_SYMBOL,
                usd,
                ReceiptManager.RUR_SYMBOL,
                ReceiptManager.EUR_SYMBOL,
                eur,
                ReceiptManager.RUR_SYMBOL));

        ButtonType  buttonTypeOk = new ButtonType("ДА", ButtonBar.ButtonData.OK_DONE);
        ButtonType  buttonTypeNo = new ButtonType("НЕТ", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeNo);

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/colorsKoreanika.css").toExternalForm());
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/rootTheme.css").toExternalForm());

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/dialogs.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {

            if(result.get() == buttonTypeOk){

                updateCurrencyValue();

            }else if(result.get() == buttonTypeNo){

            }

        }
    }

    public void checkCurrencyLvl(TextField textField, String currency){

        boolean showInfo = false;
        if(currency.equals("USD")){
            double value = 0;
            try {
                value = Double.parseDouble(textField.getText());

                if (value < MIN_USD_VALUE) {
                    value = MIN_USD_VALUE;
                    showInfo = true;
                }
                MainWindow.getUSDValue().set(value);
                textField.setText(String.format(Locale.ENGLISH, "%.1f", value));
            } catch (NumberFormatException ex) {
                MainWindow.getUSDValue().set(MIN_USD_VALUE);
            }
        }else if(currency.equals("EUR")){
            double value = 0;
            try {
                value = Double.parseDouble(textField.getText());

                if (value < MIN_EUR_VALUE) {
                    value = MIN_EUR_VALUE;
                    showInfo = true;
                }
                MainWindow.getEURValue().set(value);
                textField.setText(String.format(Locale.ENGLISH, "%.1f", value));
            } catch (NumberFormatException ex) {
                MainWindow.getUSDValue().set(MIN_EUR_VALUE);
            }
        }

    }
}
