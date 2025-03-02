package tn.esprit.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.json.JSONObject;
import tn.esprit.controllers.GestionLampadaireController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class TrafficUpdateServer {
    private static final int PORT = 8089;
    private static Label trafficStatusLabel;
    private static GestionLampadaireController controller; // Référence au contrôleur

    public static void startServer(Label label, GestionLampadaireController ctrl) throws Exception {
        trafficStatusLabel = label;
        controller = ctrl; // Stocker le contrôleur
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/trafficUpdate", new TrafficUpdateHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Serveur de mise à jour de trafic démarré sur le port " + PORT);
    }

    static class TrafficUpdateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
                reader.close();

                System.out.println("Requête reçue: " + requestBody.toString());

                JSONObject json = new JSONObject(requestBody.toString());
                int carCount = json.getInt("carCount");
                String trafficStatus = json.getString("trafficStatus");
                int cameraId = json.getInt("cameraId"); // Récupérer l'ID de la caméra

                Platform.runLater(() -> {
                    if (trafficStatusLabel != null) {
                        trafficStatusLabel.setText("Statut du trafic: " + trafficStatus);
                        trafficStatusLabel.setStyle(
                                "-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #202124; " +
                                        "-fx-background-color: " + (carCount > 3 ? "#ea4335" : "#34a853") + "; " +
                                        "-fx-padding: 10; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #dadce0; -fx-border-width: 1;"
                        );
                        System.out.println("Label mis à jour: " + trafficStatus);

                        // Si embouteillage détecté, déclencher l’animation pour le lampadaire associé
                        if (carCount > 3 && controller != null) {
                            controller.highlightLampadaireWithCamera(cameraId);
                        }
                    }
                });

                String response = "Mise à jour reçue";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            } else {
                String response = "Méthode non supportée";
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }
}