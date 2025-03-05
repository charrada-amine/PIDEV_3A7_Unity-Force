package tn.esprit.models;

import javafx.beans.property.*;

public class Camera {
    private final IntegerProperty idCamera = new SimpleIntegerProperty();
    private final StringProperty urlFlux = new SimpleStringProperty();
    private final StringProperty ipAddress = new SimpleStringProperty(); // Ajout pour stocker l'IP de l'ESP32

    public Camera() {}

    public Camera(int idCamera, String urlFlux, String ipAddress) {
        this.idCamera.set(idCamera);
        this.urlFlux.set(urlFlux);
        this.ipAddress.set(ipAddress);
    }

    // Getters et setters pour idCamera
    public int getIdCamera() { return idCamera.get(); }
    public void setIdCamera(int idCamera) { this.idCamera.set(idCamera); }
    public IntegerProperty idCameraProperty() { return idCamera; }

    // Getters et setters pour urlFlux
    public String getUrlFlux() { return urlFlux.get(); }
    public void setUrlFlux(String urlFlux) { this.urlFlux.set(urlFlux); }
    public StringProperty urlFluxProperty() { return urlFlux; }

    // Getters et setters pour ipAddress
    public String getIpAddress() { return ipAddress.get(); }
    public void setIpAddress(String ipAddress) { this.ipAddress.set(ipAddress); }
    public StringProperty ipAddressProperty() { return ipAddress; }

    @Override
    public String toString() {
        return "Camera{" +
                "idCamera=" + idCamera.get() +
                ", urlFlux='" + urlFlux.get() + '\'' +
                ", ipAddress='" + ipAddress.get() + '\'' +
                '}';
    }
}