package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.Korisnik;
import ba.unsa.etf.rpr.Main;
import ba.unsa.etf.rpr.Models.BiljeskeModel;
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

public class PredmetController {
    public TextField nameFld;
    public Button addBtn;
    public Text messageText;
    public GridPane grid;
    private static BiljeskeModel biljeskeModel;

    public PredmetController(Korisnik korisnik) {
        biljeskeModel = BiljeskeModel.getModelInstance(korisnik);
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
        // TODO: 16.7.2021 dodati translations text
       if(biljeskeModel.predmetUBaziCheck(nameFld.getText())){
            messageText.setText("Subject with that name already exists");
            messageText.setFill(Paint.valueOf("red"));
            messageText.setVisible(true);
       }else{
           biljeskeModel.predmetInsert(nameFld.getText());
           JMetro jMetro = new JMetro(Main.getTheme());
           FlatAlert alert = new FlatAlert(FlatAlert.AlertType.INFORMATION);
           alert.setTitle("Succes");
           alert.setHeaderText(null);
           alert.setContentText("Succesfully added subject!");
           jMetro.setScene(alert.getDialogPane().getScene());
           alert.showAndWait();
       }
    }
    public void close(ActionEvent actionEvent){
        Node node = (Node)actionEvent.getSource();
        ((Stage)node.getScene().getWindow()).close();
    }
}
