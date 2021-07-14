package ba.unsa.etf.rpr.Controller;


import ba.unsa.etf.rpr.Biljeska;
import ba.unsa.etf.rpr.Korisnik;
import ba.unsa.etf.rpr.Main;
import ba.unsa.etf.rpr.Models.BiljeskeModel;
import ba.unsa.etf.rpr.Predmet;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class SveBiljeskeController {
    private Korisnik korisnik;
    public TableView<Biljeska> tableViewBiljeske;
    public TableColumn<Biljeska, String> colNaziv;
    public TableColumn<Biljeska, String> colDateCreated;
    public TableColumn<Biljeska, String> colLastModified;
    public TableColumn<Biljeska, Boolean> colFavorite;
    public TextField nameFld;
    public ChoiceBox<Predmet> predmetChoiceBox;
    public DatePicker datePicker;
    public Label loadingLabel;
    public ProgressIndicator loadingCircle;
    public CheckBox favoriteCheckBox;

    private static BiljeskeModel biljeskeModel;

    public SveBiljeskeController(Korisnik korisnik) {
        this.korisnik = korisnik;
        biljeskeModel = BiljeskeModel.getModelInstance(korisnik);
    }

    @FXML
    public void initialize(){
        loadingLabel.setVisible(false);
        loadingCircle.setVisible(false);

        biljeskeModel.napuniModelPodacima();
        predmetChoiceBox.setItems(biljeskeModel.getPredmeti());
        tableViewBiljeske.setItems(biljeskeModel.getBiljeske());
        tableViewBiljeske.setEditable(true);
        colNaziv.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        colDateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        colLastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));

        colFavorite.setCellFactory(tc -> new CheckBoxTableCell<>());
        colFavorite.setCellValueFactory(
                c -> {
                    Biljeska biljeska = c.getValue();
                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(biljeska.isFavorite());
                    checkBox
                            .selectedProperty()
                            .addListener((ov, old_val, new_val) -> {
                                biljeska.setFavorite(new_val);
                                biljeskeModel.updateBiljeskaFavorite(biljeska, new_val);
                            });
                    return checkBox.selectedProperty();
                });
        tableViewBiljeske.setRowFactory(tw->{
            TableRow<Biljeska> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    try {
                        openBiljeska(row.getItem());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }
    public void openBiljeska(Biljeska biljeska) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editor.fxml"));
        EditorController ctrl ;
        if(biljeska == null) ctrl = new EditorController(korisnik);
        else ctrl = new EditorController(korisnik, biljeska);
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
