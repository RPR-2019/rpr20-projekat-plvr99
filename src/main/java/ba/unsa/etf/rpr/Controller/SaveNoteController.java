package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.Models.NotesModel;
import ba.unsa.etf.rpr.Subject;
import ba.unsa.etf.rpr.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class SaveNoteController {
    private static NotesModel notesModel;
    public ChoiceBox<Subject> predmetChoiceBox;
    public TextField nameFld;
    public Button saveBtn;
    private String text;
    private User user;

    public SaveNoteController(User user, String htmlText) {
        this.user = user;
        this.text = htmlText;
        notesModel = NotesModel.getModelInstance(user);
    }

    @FXML
    public void initialize(){
        saveBtn.setDisable(true);
        predmetChoiceBox.setItems(FXCollections.observableArrayList(notesModel.getSubjects()));
        nameFld.textProperty().addListener((observableValue, s, t1) -> saveBtn.setDisable(t1.isBlank()));
    }

    public void saveBiljeska(ActionEvent actionEvent){
        if(notesModel.noteInDBCheck(nameFld.getText())){
            Locale locale;
            ResourceBundle rb = ResourceBundle.getBundle("Translation", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(rb.getString("overwrite"));
            alert.setHeaderText(rb.getString("noteWSameName"));
            alert.setContentText(rb.getString("noteWSameName1"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                 notesModel.updateNote(nameFld.getText(), text);
                 close(actionEvent);
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }
        else notesModel.noteInsert(predmetChoiceBox.getValue().getId(), nameFld.getText(), text);
        close(actionEvent);
    }

    public void close(ActionEvent actionEvent){
        Node node = (Node)actionEvent.getSource();
        ((Stage)node.getScene().getWindow()).close();
    }

}
