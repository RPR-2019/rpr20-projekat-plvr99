package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.Korisnik;
import ba.unsa.etf.rpr.Models.BiljeskeModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PredmetController {
    public TextField nameFld;
    public Button addBtn;
    public Text messageText;
    private static BiljeskeModel biljeskeModel;

    public PredmetController(Korisnik korisnik) {
        biljeskeModel = BiljeskeModel.getModelInstance(korisnik);
    }

    @FXML
    public void initialize(){
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
       if(biljeskeModel.predmetUBaziCheck(nameFld.getText())){
            messageText.setText("Subject with that name already exists");
            messageText.setFill(Paint.valueOf("red"));
            messageText.setVisible(true);
       }else{
           biljeskeModel.predmetInsert(nameFld.getText());
           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.setTitle("Succes");
           alert.setHeaderText(null);
           alert.setContentText("Succesfully added subject!");
           alert.showAndWait();
       }
    }
    public void close(ActionEvent actionEvent){
        Node node = (Node)actionEvent.getSource();
        ((Stage)node.getScene().getWindow()).close();
    }
}
