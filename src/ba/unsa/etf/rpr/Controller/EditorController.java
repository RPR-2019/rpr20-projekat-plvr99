package ba.unsa.etf.rpr.Controller;


import ba.unsa.etf.rpr.Biljeska;
import ba.unsa.etf.rpr.Korisnik;
import ba.unsa.etf.rpr.Models.BiljeskeModel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import java.io.IOException;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class EditorController {
    public HTMLEditor htmlEditor;
    public GridPane gridPane;
    private Biljeska biljeska;
    private static BiljeskeModel biljeskeModel;
    private Korisnik korisnik;
    private String pocetniText;

    public EditorController(Korisnik korisnik,Biljeska biljeska) {
        this.biljeska = biljeska;
        this.korisnik = korisnik;
        biljeskeModel = BiljeskeModel.getModelInstance(korisnik);
        pocetniText = biljeska.getText();
    }

    public EditorController(Korisnik korisnik) {
        this.korisnik = korisnik;
        this.biljeska = null;
        pocetniText = "";
    }

    @FXML
    public void initialize(){
        ((ToolBar) htmlEditor.lookup(".bottom-toolbar")).getItems().addAll(
                FXCollections.observableArrayList(((ToolBar)htmlEditor.lookup(".top-toolbar")).getItems()));
        htmlEditor.lookup(".top-toolbar").setDisable(true);
        htmlEditor.lookup(".top-toolbar").setManaged(false);

        if(biljeska != null){
            htmlEditor.setHtmlText(biljeska.getText());
        }

        htmlEditor.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
                System.out.println(htmlEditor.getHtmlText());
            }
        });
    }

    public void saveBiljeska(ActionEvent actionEvent) throws IOException {
        if(biljeska == null) saveAsBiljeska(actionEvent);
        else biljeskeModel.updateBiljeska(biljeska.getNaziv(), htmlEditor.getHtmlText());
        pocetniText = htmlEditor.getHtmlText();
    }

    public void saveAsBiljeska(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/saveBiljeska.fxml"));
        SaveBiljeskaController ctrl = new SaveBiljeskaController(korisnik, htmlEditor.getHtmlText());
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Notes");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
        // TODO: 12.7.2021 on hiding check jel saveano ako jeste update pocetni text
    }
}
