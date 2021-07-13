package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.Main;
import ba.unsa.etf.rpr.Models.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainController {
    public GridPane grid;
    public TextField usernameFld;
    public PasswordField passwordFld;
    public Label errorLabel;
    public Model model = new Model();
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        grid.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }

    public void closeApp(ActionEvent actionEvent){
        Node node = (Node)actionEvent.getSource();
        ((Stage)node.getScene().getWindow()).close();
    }

    public void login(ActionEvent actionEvent) throws IOException {
        if(model.korisnikProvjera(usernameFld.getText(), passwordFld.getText())) {
            errorLabel.setVisible(false);
            usernameFld.setStyle("");
            passwordFld.setStyle("");
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sveBiljeske.fxml"));
            SveBiljeskeController ctrl = new SveBiljeskeController(model.getkorisnik(usernameFld.getText()));
            loader.setController(ctrl);
            Parent root = loader.load();
            stage.setTitle("Notes");
           // stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            stage.setScene(scene);
            stage.show();
            Node node = (Node)actionEvent.getSource();
            ((Stage)node.getScene().getWindow()).close();
        }
        else {
            errorLabel.setVisible(true);
            errorLabel.setTextFill(Paint.valueOf("red"));
//            usernameFld.getStyleClass().add("border_color_wrong");
//            passwordFld.getStyleClass().add("border_color_wrong");
            usernameFld.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ; -fx-border-color: red ; -fx-border-width: 2px ;");
            passwordFld.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ; -fx-border-color: red ; -fx-border-width: 2px ;");
        }
    }

    public void signUpAccount(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signUp.fxml"));
        SignUpController ctrl = new SignUpController();
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Notes");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }
}
