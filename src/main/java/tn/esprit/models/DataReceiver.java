package tn.esprit.models;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataReceiver {

    private static final String SERVER_URL = "http://192.168.1.13:8000"; // URL de votre serveur

    public String fetchDataFromServer() {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); // Utiliser GET

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("Réponse du serveur : " + response.toString()); // Afficher la réponse
            } else {
                System.err.println("Erreur lors de la récupération des données. Code de réponse : " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}