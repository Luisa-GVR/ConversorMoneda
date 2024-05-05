import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException {

        int choice;
        do {
            choice = showMainMenu();
            switch (choice) {
                case 0:
                    // Convert
                    showConversionMenu();
                    break;
                case 1:
                    // Exit
                    JOptionPane.showMessageDialog(null, "Saliendo...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "No se ha elegido una opcion");
                    break;
            }
        } while (choice != 1);

    }

    public static int showMainMenu(){
        Object[] options = {"Convertir", "Salir"};
        return JOptionPane.showOptionDialog(
                null,
                "Elige una opcion:",
                "Menu principal",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    public static void showConversionMenu() throws IOException {

        String[] rates = {"AUD", "ATS", "BEF", "BRL", "CAD", "CHF", "CNY", "DEM",
                "DKK", "ESP", "EUR", "FIM", "FRF", "GBP", "GRD", "HKD",
                "IEP", "INR", "IRR", "ITL", "JPY", "KRW", "LKR", "MXN",
                "MYR", "NOK", "NLG", "NZD", "PTE", "SEK", "SGD", "THB",
                "TWD", "USD", "ZAR"};
        JComboBox<Object> comboBox1 = new JComboBox<>(rates);
        JComboBox<Object> comboBox2 = new JComboBox<>(rates);
        JTextField valueField = new JTextField(5);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Selecciona el currency a convertir:"));
        panel.add(comboBox1);
        panel.add(new JLabel("Selecciona a que convertir:"));
        panel.add(comboBox2);
        panel.add(new JLabel("Agrega un valor numerico:"));
        panel.add(valueField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Conversion Menu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String convRate = (String) comboBox1.getSelectedItem();
            String targetRate = (String) comboBox2.getSelectedItem();
            double value = Double.parseDouble(valueField.getText());

            JsonObject finalResult = conversor(convRate, targetRate, value);

            String conversionResult = finalResult.get("conversion_result").getAsString();
            JOptionPane.showMessageDialog(null, "Resultado: " + conversionResult);


        }

    }


    public static JsonObject conversor(String convRate, String targetRate, Double amount) throws IOException {

        // Setting URL
        String url_base = "https://v6.exchangerate-api.com/v6/";
        String apiKey = "";
        String url_str = String.format("%s%s/pair/%s/%s/%.2f/", url_base, apiKey, convRate, targetRate, amount);

        // Making Request
        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convert to JSON
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();

        return jsonobj;

    }

}
