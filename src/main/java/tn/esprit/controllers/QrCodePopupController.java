package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class QrCodePopupController {

    @FXML private ImageView qrCodeImageView;

    public void setQrCodeImage(Image qrCodeImage) {
        qrCodeImageView.setImage(qrCodeImage);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) qrCodeImageView.getScene().getWindow();
        stage.close();
    }
}