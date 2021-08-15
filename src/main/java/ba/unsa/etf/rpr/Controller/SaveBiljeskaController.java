package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.Korisnik;
import ba.unsa.etf.rpr.Models.BiljeskeModel;
import ba.unsa.etf.rpr.Predmet;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class SaveBiljeskaController {
    private static BiljeskeModel biljeskeModel;
    public ChoiceBox<Predmet> predmetChoiceBox;
    public TextField nameFld;
    public Button saveBtn;
    private String text;
    private Korisnik korisnik;

    public SaveBiljeskaController(Korisnik korisnik, String htmlText) {
        this.korisnik = korisnik;
        this.text = htmlText;
        biljeskeModel = BiljeskeModel.getModelInstance(korisnik);
    }

    @FXML
    public void initialize(){
        saveBtn.setDisable(true);
        predmetChoiceBox.setItems(FXCollections.observableArrayList(biljeskeModel.getPredmeti()));
        nameFld.textProperty().addListener((observableValue, s, t1) -> saveBtn.setDisable(t1.isBlank()));
    }

    public void saveBiljeska(ActionEvent actionEvent){
        if(biljeskeModel.biljeskaUBaziCheck(nameFld.getText())){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Overwrite ");
            alert.setHeaderText("A note with the same name already exists");
            alert.setContentText("Are you sure you want to overwrite the existing note?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                 biljeskeModel.updateBiljeska(nameFld.getText(), text);
                 close(actionEvent);
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }
        else biljeskeModel.insertBiljeska(predmetChoiceBox.getValue().getId(), nameFld.getText(), text);
        close(actionEvent);
    }

    public void close(ActionEvent actionEvent){
        Node node = (Node)actionEvent.getSource();
        ((Stage)node.getScene().getWindow()).close();
    }

}
