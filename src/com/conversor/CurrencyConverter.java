package com.conversor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrencyConverter {
    private static final String API_KEY = "6eba290225c5b5bb1bfee16c";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    public static JsonObject getExchangeRates() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JsonObject response = new Gson().fromJson(reader, JsonObject.class);
        reader.close();

        if (!response.has("conversion_rates")) {
            throw new Exception("Error al obtener tasas de conversión.");
        }
        return response.getAsJsonObject("conversion_rates");
    }

    public static void main(String[] args) {
        try {
            JsonObject rates = getExchangeRates();
            Set<String> availableCurrencies = rates.keySet();

            Map<String, String> currencyNames = new LinkedHashMap<>();
            currencyNames.put("USD", "Dólar estadounidense");
            currencyNames.put("EUR", "Euro");
            currencyNames.put("GBP", "Libra esterlina");
            currencyNames.put("JPY", "Yen japonés");
            currencyNames.put("COP", "Peso colombiano");
            currencyNames.put("MXN", "Peso mexicano");
            currencyNames.put("BRL", "Real brasileño");

            Map<String, String> filteredCurrencies = availableCurrencies.stream()
                    .filter(currencyNames::containsKey)
                    .collect(Collectors.toMap(currency -> currency + " - " + currencyNames.get(currency), currency -> currency));

            String fromCurrency = (String) JOptionPane.showInputDialog(null, "Seleccione la moneda de origen:",
                    "Monedas Disponibles", JOptionPane.QUESTION_MESSAGE, null,
                    filteredCurrencies.keySet().toArray(), "USD - Dólar estadounidense");

            if (fromCurrency == null) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar una moneda de origen.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String toCurrency = (String) JOptionPane.showInputDialog(null, "Seleccione la moneda de destino:",
                    "Monedas Disponibles", JOptionPane.QUESTION_MESSAGE, null,
                    filteredCurrencies.keySet().toArray(), "EUR - Euro");

            if (toCurrency == null) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar una moneda de destino.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            fromCurrency = filteredCurrencies.get(fromCurrency);
            toCurrency = filteredCurrencies.get(toCurrency);

            double amount;
            while (true) {
                String amountStr = JOptionPane.showInputDialog("Ingrese la cantidad a convertir:");
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount < 0) {
                        JOptionPane.showMessageDialog(null, "No se permiten valores negativos. Inténtelo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            double fromRate = rates.get(fromCurrency).getAsDouble();
            double toRate = rates.get(toCurrency).getAsDouble();
            double convertedAmount = (amount / fromRate) * toRate;

            JOptionPane.showMessageDialog(null, String.format("Monto convertido: %.2f %s", convertedAmount, toCurrency));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
