package tn.esprit.models;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
    private final String serverUrl;

    public HttpClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getData() {
        try {
            // Créer une connexion HTTP
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");  // Utiliser POST
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Préparer les données à envoyer (simuler une température)
            String jsonData = "{\"temperature\": 21.00}";  // Exemple de données

            // Envoyer les données
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Lire la réponse
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Lire la réponse du serveur
                try (java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            } else {
                return "Erreur HTTP : " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur : " + e.getMessage();
        }
    }
}