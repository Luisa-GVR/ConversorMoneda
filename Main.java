import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Main {
    static List<String> conversionHistory = new ArrayList<>();
    static Map<String, Integer> conversionCounts = new HashMap<>();
    static Map<String, Integer> conversionToCounts = new HashMap<>();
    static Set<String> supportedCurrencies = new HashSet<>();
    static String url_base = "https://v6.exchangerate-api.com/v6/";
    static String apiKey = "insert ur api key here :)";

    public static void main(String[] args) throws IOException {

        fetchSupportedCurrencies();

        int choice;
        do {
            choice = showMainMenu();
            switch (choice) {
                case 0:
                    // Convert
                    showConversionMenu();
                    break;
                case 1:
                    // History
                    showConversionHistory();
                    break;
                case 2:
                    // Top 3
                    showTop3();
                    break;
                case 3:
                    // Exit
                    JOptionPane.showMessageDialog(null, "Saliendo...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "No se ha elegido una opcion");
                    break;
            }
        } while (choice != 3);

    }

    public static void fetchSupportedCurrencies() throws IOException {
        // Setting URL

        String url_str = url_base + apiKey + "/codes";
        // Making Request
        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        // Convert to JSON
        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject jsonobj = root.getAsJsonObject();
        // Extract codes
        JsonArray codesArray = jsonobj.getAsJsonArray("supported_codes");
        for (JsonElement element : codesArray) {
            String code = element.getAsJsonArray().get(0).getAsString();
            supportedCurrencies.add(code);
        }
    }

    public static int showMainMenu(){
        Object[] options = {"Convertir", "Historial", "Top 3", "Salir"};
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
        String[] rates = supportedCurrencies.toArray(new String[0]);
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
            conversionHistory.add(String.format("%s %s = %s %s", value, convRate, conversionResult, targetRate));
            updateConversionCounts(convRate);
            updateConversionToCounts(targetRate);
            JOptionPane.showMessageDialog(null, "Resultado: " + conversionResult);
        }

    }

    public static void updateConversionCounts(String convRate) {
        conversionCounts.put(convRate, conversionCounts.getOrDefault(convRate, 0) + 1);
    }

    public static void updateConversionToCounts(String targetRate) {
        conversionToCounts.put(targetRate, conversionToCounts.getOrDefault(targetRate, 0) + 1);
    }

    public static void showConversionHistory() {
        StringBuilder history = new StringBuilder();
        if (conversionHistory.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Historial vacío.");
        } else {
            for (String entry : conversionHistory) {
                history.append(entry).append("\n");
            }
            JOptionPane.showMessageDialog(null, "Historial:\n" + history.toString());
        }
    }

    public static void showTop3() {
        String top3Converted = getTop3(conversionCounts);
        String top3ConvertedTo = getTop3(conversionToCounts);
        JOptionPane.showMessageDialog(null, "Top 3 más convertidos:\n" + top3Converted + "\n\nTop 3 más convertidos a:\n" + top3ConvertedTo);
    }

    public static String getTop3(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        StringBuilder result = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Integer> entry : list) {
            if (count >= 3) break;
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append(" veces\n");
            count++;
        }
        return result.toString();
    }

    public static JsonObject conversor(String convRate, String targetRate, Double amount) throws IOException {

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
