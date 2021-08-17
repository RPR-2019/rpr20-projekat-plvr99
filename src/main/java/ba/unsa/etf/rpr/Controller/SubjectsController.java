package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.Main;
import ba.unsa.etf.rpr.Models.NotesModel;
import ba.unsa.etf.rpr.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.FlatAlert;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.util.Locale;
import java.util.ResourceBundle;

public class SubjectsController {
    public TextField nameFld;
    public Button addBtn;
    public Text messageText;
    public GridPane grid;
    private static NotesModel notesModel;

    public SubjectsController(User user) {
        notesModel = NotesModel.getModelInstance(user);
    }

    @FXML
    public void initialize(){
        grid.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        messageText.setVisible(false);
        nameFld.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            messageText.setVisible(false);
            if(!newValue.isEmpty()){
                addBtn.setDisable(false);
            }else{
                addBtn.setDisable(true);
            }
        }));
    }

    public void addPredmet(ActionEvent actionEvent){
        ResourceBundle rb = ResourceBundle.getBundle("Translation", Locale.getDefault());
       if(notesModel.subjectInDBCheck(nameFld.getText())){
            messageText.setText(rb.getString("subjectError"));
            messageText.setFill(Paint.valueOf("red"));
            messageText.setVisible(true);
       }else{
           notesModel.subjectInsert(nameFld.getText());
           JMetro jMetro = new JMetro(Main.getTheme());
           FlatAlert alert = new FlatAlert(FlatAlert.AlertType.INFORMATION);
           alert.setTitle(rb.getString("signUpSuccess"));
           alert.setHeaderText(null);
           alert.setContentText(rb.getString("subjectSucces"));
           jMetro.setScene(alert.getDialogPane().getScene());
           alert.showAndWait();
       }
    }
    public void close(ActionEvent actionEvent){
        Node node = (Node)actionEvent.getSource();
        ((Stage)node.getScene().getWindow()).close();
    }
}
