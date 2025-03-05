package tn.esprit.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tn.esprit.models.Camera;
import tn.esprit.services.ServiceCamera;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class CameraIpUpdateServer {
    private static final int PORT = 8081;
    private static final ServiceCamera serviceCamera = new ServiceCamera();

    public static void startServer() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/updateCameraIP", new UpdateCameraIpHandler());
        server.setExecutor(null); // Utilise le pool de threads par défaut
        server.start();
        System.out.println("Serveur HTTP démarré sur le port " + PORT);
    }

    static class UpdateCameraIpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String query = t.getRequestURI().getQuery();
            String response = "Erreur: Paramètres invalides";

            if (query != null) {
                String[] params = query.split("&");
                int cameraId = -1;
                String ip = null;

                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        if (keyValue[0].equals("cameraId")) {
                            try {
                                cameraId = Integer.parseInt(keyValue[1]);
                            } catch (NumberFormatException e) {
                                response = "Erreur: ID de caméra invalide";
                            }
                        } else if (keyValue[0].equals("ip")) {
                            ip = keyValue[1];
                        }
                    }
                }

                if (cameraId > 0 && ip != null) {
                    Camera camera = serviceCamera.getById(cameraId);
                    if (camera != null) {
                        camera.setIpAddress(ip);
                        serviceCamera.update(camera);
                        response = "IP mise à jour avec succès pour la caméra " + cameraId;
                    } else {
                        response = "Caméra non trouvée";
                    }
                }
            }

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }

    public static void main(String[] args) throws Exception {
        startServer();
    }
}