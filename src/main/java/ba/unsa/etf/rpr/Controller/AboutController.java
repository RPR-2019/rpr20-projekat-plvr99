package ba.unsa.etf.rpr.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {


    public Button closeBtn;
    public ImageView imgView;
    public GridPane grid;

    @FXML
    public void initialize(){
        grid.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        imgView.setImage(new Image("/images/logo_main.png"));
    }
    public void openGit(ActionEvent actionEvent) {
        new Thread(() -> {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI("https://github.com/plvr99"));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void closeAction(ActionEvent actionEvent) {
        ((Stage)closeBtn.getScene().getWindow()).close();
    }
}
