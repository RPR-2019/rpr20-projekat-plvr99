package ba.unsa.etf.rpr.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {

    public Hyperlink webHyperlink;
    public Hyperlink gitHyperlink;
    public Button closeBtn;
    public ImageView imgView;
    public GridPane grid;

    @FXML
    public void initialize(){
        grid.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        //Image image = new Image(getClass().getResourceAsStream("/images/logo.jpg"));
       // imgView.setImage(image);
    }
    public void openGit(ActionEvent actionEvent) {
        new Thread(()->{
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
    public void openLink(ActionEvent actionEvent) {
        new Thread(()->{
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI("https://www.etf.unsa.ba"));
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
