package ba.unsa.etf.rpr.Controller;


import ba.unsa.etf.rpr.Biljeska;
import ba.unsa.etf.rpr.Korisnik;
import ba.unsa.etf.rpr.Main;
import ba.unsa.etf.rpr.Models.BiljeskeModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.FlatAlert;
import jfxtras.styles.jmetro.JMetro;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

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
        Platform.runLater(()->{
            gridPane.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
        });
    }

    public void saveBiljeska(ActionEvent actionEvent) throws IOException {
        if(biljeska == null) saveAsBiljeska(actionEvent);
        else biljeskeModel.updateBiljeska(biljeska.getNaziv(), htmlEditor.getHtmlText());
        pocetniText = htmlEditor.getHtmlText();
    }

    public void saveAsBiljeska(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/saveBiljeska.fxml"), ResourceBundle.getBundle("Translation"));
        SaveBiljeskaController ctrl = new SaveBiljeskaController(korisnik, htmlEditor.getHtmlText());
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.setTitle("Notes");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }
    private void closeWindowEvent(WindowEvent event){
        if(!pocetniText.equals(htmlEditor.getHtmlText())) {
            JMetro jMetro = new JMetro(Main.getTheme());
            FlatAlert alert = new FlatAlert(FlatAlert.AlertType.CONFIRMATION);
            ResourceBundle rb = ResourceBundle.getBundle("Translation", Locale.getDefault());
            alert.setTitle(rb.getString("closeWSaving"));
            alert.setHeaderText(rb.getString("closeWSaving1"));
            alert.setContentText(null);
            ButtonType buttonTypeOne = new ButtonType(rb.getString("save"));
            ButtonType buttonTypeTwo = new ButtonType(rb.getString("dontSaven"));
            ButtonType buttonTypeCancel = new ButtonType(rb.getString("cancel"));

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
            jMetro.setScene(alert.getDialogPane().getScene());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne){
                try {
                    saveBiljeska(new ActionEvent());
                    close(new ActionEvent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (result.get() == buttonTypeTwo) {
                event.consume();
                close(new ActionEvent());
            } else {
                event.consume();
            }
        }
    }

    public void exportBiljeska(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse");
        Stage stage = new Stage();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF File ", "*.pdf"));
        File selectedFile = fileChooser.showSaveDialog(stage);
        String text = htmlEditor == null ? pocetniText : htmlEditor.getHtmlText();
        if(selectedFile.exists()  && selectedFile.canWrite()) {
            biljeskeModel.exportFile(selectedFile, text);
        }
        else {
            try {
                selectedFile.createNewFile();
                biljeskeModel.exportFile(selectedFile, text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(ActionEvent actionEvent){
        ((Stage)htmlEditor.getScene().getWindow()).close();
    }
}
