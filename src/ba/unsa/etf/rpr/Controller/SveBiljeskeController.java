package ba.unsa.etf.rpr.Controller;


import ba.unsa.etf.rpr.Biljeska;
import ba.unsa.etf.rpr.Korisnik;
import ba.unsa.etf.rpr.Main;
import ba.unsa.etf.rpr.Models.BiljeskeModel;
import ba.unsa.etf.rpr.Predmet;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

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
    public CheckBox favoriteCheckBox;
    public GridPane grid;
    public VBox vBox;
    public HBox hBox;
    public BorderPane borderPane;
    public MenuItem menuRemove;
    public Button removeBtn;
    private static BiljeskeModel biljeskeModel;

    public SveBiljeskeController(Korisnik korisnik) {
        this.korisnik = korisnik;
        biljeskeModel = BiljeskeModel.getModelInstance(korisnik);
    }

    @FXML
    public void initialize(){
        borderPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        grid.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        vBox.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        hBox.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        biljeskeModel.napuniModelPodacima();
        ObservableList<Biljeska> biljeske= biljeskeModel.getBiljeske();
        predmetChoiceBox.setItems(biljeskeModel.getPredmeti());
        tableViewBiljeske.setItems(biljeske);
        loadingLabel.textProperty().bind(Bindings.size(biljeske).asString());
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

        menuRemove.setDisable(true);
        removeBtn.setDisable(true);
        tableViewBiljeske.getSelectionModel().selectedItemProperty().addListener(((observableValue, biljeska, selection) -> {
            menuRemove.setDisable(selection == null);
            removeBtn.setDisable(selection == null);
        }));
    }
    public void openBiljeska(Biljeska biljeska) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editor.fxml"), ResourceBundle.getBundle("Translation"));
        EditorController ctrl ;
        if(biljeska == null) ctrl = new EditorController(korisnik);
        else ctrl = new EditorController(korisnik, biljeska);
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.getIcons().add(new Image("/images/app_icon.png"));
        stage.setTitle("Notes");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
        stage.setOnHiding((event) -> {
            clearFilters(new ActionEvent());
            biljeskeModel.clearBiljeske();
            biljeskeModel.sveBiljeske();
        });
    }

    public void addBiljeska(ActionEvent actionEvent) throws IOException {
        openBiljeska(null);
    }

    public void subjectOpen(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/subjects.fxml"), ResourceBundle.getBundle("Translation"));
        PredmetController ctrl = new PredmetController(korisnik);
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.getIcons().add(new Image("/images/app_icon.png"));
        stage.setTitle("Notes");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void searchBiljeske(ActionEvent actionEvent){
        String naziv= nameFld.getText().isBlank() ? null : nameFld.getText();
        Predmet predmet = predmetChoiceBox.getValue()==null ? null : predmetChoiceBox.getValue();
        String datum = datePicker.getEditor().getText().isBlank() ? null : datePicker.getEditor().getText();
        new Thread(()->{
            if(naziv==null && predmet==null && datum==null){
                Platform.runLater(()->{
                    biljeskeModel.clearBiljeske();
                    biljeskeModel.sveBiljeske();
                    tableViewBiljeske.setItems(biljeskeModel.getBiljeske());
                });
            }
            else {
                Platform.runLater(()->{
                    biljeskeModel.clearBiljeske();
                    biljeskeModel.filterBiljeske(naziv,predmet,datum,favoriteCheckBox.isSelected());
                    tableViewBiljeske.setItems(biljeskeModel.getBiljeske());
                });
            }
        }).start();
    }

    public void clearFilters(ActionEvent actionEvent){
        nameFld.clear();
        predmetChoiceBox.setValue(null);
        datePicker.getEditor().clear();
        favoriteCheckBox.setSelected(false);
    }

    public void removeBiljeska(ActionEvent actionEvent){
        biljeskeModel.biljeskaRemove(tableViewBiljeske.getSelectionModel().getSelectedItem());
        tableViewBiljeske.refresh();
    }

    public void exportBiljeska(ActionEvent actionEvent) throws IOException {
        new EditorController(korisnik,tableViewBiljeske.getSelectionModel().getSelectedItem()).exportBiljeska(actionEvent);
    }

    public void languageChange(ActionEvent actionEvent){
        MenuItem item = (MenuItem)actionEvent.getSource();
        String language = item.getText();
        promjenaJezikaIliTeme(language);
    }

    public void themeChange(ActionEvent actionEvent) {
        MenuItem item = (MenuItem) actionEvent.getSource();
        String theme = item.getText();
        if (theme.equals("Light") && Main.getTheme().equals(Style.LIGHT)) return;
        if (theme.equals("Dark") && Main.getTheme().equals(Style.DARK)) return;
        promjenaJezikaIliTeme(theme);
    }

    private void promjenaJezikaIliTeme(String promjena){
        biljeskeModel.clearBiljeske();
        biljeskeModel.clearPredmeti();
        Stage stage = (Stage) tableViewBiljeske.getScene().getWindow();
        stage.getIcons().add(new Image("/images/app_icon.png"));
        switch (promjena) {
            case "Bosanski" -> Locale.setDefault(new Locale("bs", "BA"));
            case "English" -> Locale.setDefault(new Locale("en", "US"));
            case "Light" -> {
                Main.setTheme(Style.LIGHT);
            }
            case "Dark" -> {
                Main.setTheme(Style.DARK);
            }
            default -> Locale.setDefault(new Locale("bs", "BA"));
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sveBiljeske.fxml"), ResourceBundle.getBundle("Translation"));
        loader.setController(new SveBiljeskeController(korisnik));
        try {
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().clear();
            if (Main.getTheme().equals(Style.DARK)) {
                scene.getStylesheets().
                        add(getClass().getResource("/css/iconsWhite.css").toExternalForm());
            } else {
                scene.getStylesheets().
                        add(getClass().getResource("/css/iconsBlack.css").toExternalForm());
            }
            JMetro jMetro = new JMetro(Main.getTheme());
            jMetro.setScene(scene);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent){
        Platform.exit();
    }
    public void signOut(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sample.fxml"), ResourceBundle.getBundle("Translation"));
        MainController ctrl = new MainController();
        loader.setController(ctrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        ((Stage) tableViewBiljeske.getScene().getWindow()).close();
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/images/app_icon.png"));
        //stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.show();
    }
    public void openAbout(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/about.fxml"), ResourceBundle.getBundle("Translation"));
        AboutController ctrl = new AboutController();
        loader.setController(ctrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/images/app_icon.png"));
        stage.setTitle("Notes");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
}
