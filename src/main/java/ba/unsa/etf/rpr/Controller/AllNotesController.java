package ba.unsa.etf.rpr.Controller;


import ba.unsa.etf.rpr.Main;
import ba.unsa.etf.rpr.Models.NotesModel;
import ba.unsa.etf.rpr.Note;
import ba.unsa.etf.rpr.Subject;
import ba.unsa.etf.rpr.User;
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
import javafx.scene.input.KeyCode;
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

public class AllNotesController {
    private User user;
    public TableView<Note> tableViewNotes;
    public TableColumn<Note, String> colNaziv;
    public TableColumn<Note, String> colDateCreated;
    public TableColumn<Note, String> colLastModified;
    public TableColumn<Note, Boolean> colFavorite;
    public TextField nameFld;
    public ChoiceBox<Subject> subjectChoiceBox;
    public DatePicker datePicker;
    public Label loadingLabel;
    public CheckBox favoriteCheckBox;
    public GridPane grid;
    public VBox vBox;
    public HBox hBox;
    public BorderPane borderPane;
    public MenuItem menuRemove;
    public MenuItem menuExport;
    public Button removeBtn;
    private static NotesModel notesModel;

    public AllNotesController(User user) {
        this.user = user;
        notesModel = NotesModel.getModelInstance(user);
    }

    @FXML
    public void initialize(){
        borderPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        grid.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        vBox.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        hBox.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        notesModel.fillModelWData();
        ObservableList<Note> notes= notesModel.getNotes();
        subjectChoiceBox.setItems(notesModel.getSubjects());
        tableViewNotes.setItems(notes);
        loadingLabel.textProperty().bind(Bindings.size(notes).asString());
        tableViewNotes.setEditable(true);
        colNaziv.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        colDateCreated.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        colLastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));

        colFavorite.setCellFactory(tc -> new CheckBoxTableCell<>());
        colFavorite.setCellValueFactory(
                c -> {
                    Note note = c.getValue();
                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(note.isFavorite());
                    checkBox
                            .selectedProperty()
                            .addListener((ov, old_val, new_val) -> {
                                note.setFavorite(new_val);
                                notesModel.updateNoteFavorite(note, new_val);
                            });
                    return checkBox.selectedProperty();
                });
        tableViewNotes.setOnKeyPressed(event->{
            if(tableViewNotes.isFocused() && event.getCode().equals(KeyCode.ENTER) && (tableViewNotes.getSelectionModel().getSelectedItem() != null)){
                try {
                    openBiljeska(tableViewNotes.getSelectionModel().getSelectedItem());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tableViewNotes.setRowFactory(tw->{
            TableRow<Note> row = new TableRow<>();
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
        menuExport.setDisable(true);
        tableViewNotes.getSelectionModel().selectedItemProperty().addListener(((observableValue, note, selection) -> {
            menuRemove.setDisable(selection == null);
            removeBtn.setDisable(selection == null);
            menuExport.setDisable(selection == null);
        }));
    }
    public void openBiljeska(Note note) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editor.fxml"), ResourceBundle.getBundle("Translation"));
        EditorController ctrl ;
        if(note == null) ctrl = new EditorController(user);
        else ctrl = new EditorController(user, note);
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.getIcons().add(new Image("/images/app_icon.png"));
        stage.setTitle("E-Notes");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        iconsChange(scene);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
        stage.setOnHiding((event) -> {
            clearFilters(new ActionEvent());
            notesModel.clearNotes();
            notesModel.allNotes();
        });
    }

    public void addBiljeska(ActionEvent actionEvent) throws IOException {
        openBiljeska(null);
    }

    public void subjectOpen(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/subjects.fxml"), ResourceBundle.getBundle("Translation"));
        SubjectsController ctrl = new SubjectsController(user);
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.getIcons().add(new Image("/images/app_icon.png"));
        ResourceBundle rb = ResourceBundle.getBundle("Translation", Locale.getDefault());
        stage.setTitle(rb.getString("subjectNew"));
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void searchBiljeske(ActionEvent actionEvent){
        String name= nameFld.getText().isBlank() ? null : nameFld.getText();
        Subject subject = subjectChoiceBox.getValue()==null ? null : subjectChoiceBox.getValue();
        String date = datePicker.getEditor().getText().isBlank() ? null : datePicker.getEditor().getText();
        new Thread(()->{
            if(name==null && subject ==null && date==null && !favoriteCheckBox.isSelected()){
                Platform.runLater(()->{
                    notesModel.clearNotes();
                    notesModel.allNotes();
                    tableViewNotes.setItems(notesModel.getNotes());
                });
            }
            else {
                Platform.runLater(()->{
                    notesModel.clearNotes();
                    notesModel.filterNotes(name, subject,date,favoriteCheckBox.isSelected());
                    tableViewNotes.setItems(notesModel.getNotes());
                });
            }
        }).start();
    }

    public void clearFilters(ActionEvent actionEvent){
        nameFld.clear();
        subjectChoiceBox.setValue(null);
        datePicker.getEditor().clear();
        favoriteCheckBox.setSelected(false);
    }

    public void removeBiljeska(ActionEvent actionEvent){
        notesModel.noteRemove(tableViewNotes.getSelectionModel().getSelectedItem());
        tableViewNotes.refresh();
    }

    public void exportBiljeska(ActionEvent actionEvent) throws IOException {
        new EditorController(user, tableViewNotes.getSelectionModel().getSelectedItem()).exportBiljeska(actionEvent);
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
        notesModel.clearNotes();
        notesModel.clearSubjects();
        Stage stage = (Stage) tableViewNotes.getScene().getWindow();
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/allNotes.fxml"), ResourceBundle.getBundle("Translation"));
        loader.setController(new AllNotesController(user));
        try {
            Scene scene = new Scene(loader.load());
            iconsChange(scene);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logIn.fxml"), ResourceBundle.getBundle("Translation"));
        LogInController ctrl = new LogInController();
        loader.setController(ctrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        ((Stage) tableViewNotes.getScene().getWindow()).close();
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.setTitle("E-Notes");
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
        Locale locale;
        ResourceBundle rb = ResourceBundle.getBundle("Translation", Locale.getDefault());
        stage.setTitle(rb.getString("aboutTitle"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
    private void iconsChange(Scene scene){
        scene.getStylesheets().clear();
        if (Main.getTheme().equals(Style.DARK)) {
            scene.getStylesheets().
                    add(getClass().getResource("/css/iconsWhite.css").toExternalForm());
        } else {
            scene.getStylesheets().
                    add(getClass().getResource("/css/iconsBlack.css").toExternalForm());
        }
    }
}
