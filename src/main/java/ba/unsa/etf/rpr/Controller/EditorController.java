package ba.unsa.etf.rpr.Controller;


import ba.unsa.etf.rpr.Main;
import ba.unsa.etf.rpr.Models.NotesModel;
import ba.unsa.etf.rpr.Note;
import ba.unsa.etf.rpr.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.FlatAlert;
import jfxtras.styles.jmetro.JMetro;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class EditorController {
    public HTMLEditor htmlEditor;
    public GridPane gridPane;
    private Note note;
    private static NotesModel notesModel;
    private User user;
    private String beginText;
    private ResourceBundle rb;

    public EditorController(User user, Note note) {
        this.note = note;
        this.user = user;
        notesModel = NotesModel.getModelInstance(user);
        beginText = note.getText();
    }

    public EditorController(User user) {
        this.user = user;
        this.note = null;
        beginText = "";
    }

    @FXML
    public void initialize(){
        rb = ResourceBundle.getBundle("Translation", Locale.getDefault());
        htmlEditor.setVisible(false);
        Platform.runLater(() -> {
            createCustomButtons();
            htmlEditor.setVisible(true);
        });

        if(note != null){
            htmlEditor.setHtmlText(note.getText());
        }

        Platform.runLater(()->{
            gridPane.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
        });
    }

    public void saveBiljeska(ActionEvent actionEvent) throws IOException {
        if(note == null) saveAsBiljeska(actionEvent);
        else notesModel.updateNote(note.getNaziv(), htmlEditor.getHtmlText());
        beginText = htmlEditor.getHtmlText();
    }

    public void saveAsBiljeska(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/saveNote.fxml"), ResourceBundle.getBundle("Translation"));
        SaveNoteController ctrl = new SaveNoteController(user, htmlEditor.getHtmlText());
        loader.setController(ctrl);
        Parent root = loader.load();
        stage.getIcons().add(new Image("/images/app_icon.png"));
        stage.setTitle("E-Notes");
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);
        stage.setScene(scene);
        stage.show();
    }
    private void closeWindowEvent(WindowEvent event){
        if(!beginText.equals(htmlEditor.getHtmlText())) {
            JMetro jMetro = new JMetro(Main.getTheme());
            FlatAlert alert = new FlatAlert(FlatAlert.AlertType.CONFIRMATION);
            alert.setTitle(rb.getString("closeWSaving"));
            alert.setHeaderText(rb.getString("closeWSaving1"));
            alert.setContentText(null);
            ButtonType buttonTypeOne = new ButtonType(rb.getString("save"));
            ButtonType buttonTypeTwo = new ButtonType(rb.getString("dontSave"));
            ButtonType buttonTypeCancel = new ButtonType(rb.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

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

    public void exportBiljeska(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("browse"));
        Stage stage = new Stage();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF File ", "*.pdf"));
        File selectedFile = fileChooser.showSaveDialog(stage);
        String text = htmlEditor == null ? beginText : htmlEditor.getHtmlText();
        if(selectedFile == null) return;
        if(selectedFile.exists()  && selectedFile.canWrite()) {
            notesModel.exportFile(selectedFile, text);
        }
        else {
            try {
                selectedFile.createNewFile();
                notesModel.exportFile(selectedFile, text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(ActionEvent actionEvent){
        ((Stage)htmlEditor.getScene().getWindow()).close();
    }

    private void createCustomButtons() {
        //add embed file button
        ImageView graphic = new ImageView(new Image(
                getClass().getResourceAsStream("/images/add_grey_24dp.png")));
        Button mImportFileButton = new Button("", graphic);
        mImportFileButton.setTooltip(new Tooltip(rb.getString("insertImage")));
        mImportFileButton.setOnAction((event) -> onImportFileButtonAction());

        //add to top toolbar
        ((ToolBar) htmlEditor.lookup(".bottom-toolbar")).getItems().add(mImportFileButton);
        ((ToolBar) htmlEditor.lookup(".bottom-toolbar")).getItems().add(new Separator(Orientation.VERTICAL));
    }

    private void onImportFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("browse"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG file", "*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG file", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(htmlEditor.getScene().getWindow());
        if (selectedFile != null) {
            importDataFile(selectedFile);
        }
    }

    private void importDataFile(File file) {
        try {
            //check if file is too big
            if (file.length() > 1024 * 1024) {
                throw new VerifyError("File is too big.");
            }
            //get mime type of the file
            String type = Files.probeContentType(file.toPath());
            //get html content
            byte[] data = Files.readAllBytes(file.toPath());
            String base64data = java.util.Base64.getEncoder().encodeToString(data);
            String htmlData = String.format(
                    "<img src='data:%s;base64,%s' type='%s' />",
                    type, base64data, type);
            //insert html
            insertHtmlAfterCursor(htmlData);
        } catch (IOException ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void insertHtmlAfterCursor(String html) {
        //replace invalid chars
        html = html.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
        //get script
        String script = String.format(
                "(function(html) {"
                        + "  var sel, range;"
                        + "  if (window.getSelection) {"
                        + "    sel = window.getSelection();"
                        + "    if (sel.getRangeAt && sel.rangeCount) {"
                        + "      range = sel.getRangeAt(0);"
                        + "      range.deleteContents();"
                        + "      var el = document.createElement(\"div\");"
                        + "      el.innerHTML = html;"
                        + "      var frag = document.createDocumentFragment(),"
                        + "        node, lastNode;"
                        + "      while ((node = el.firstChild)) {"
                        + "        lastNode = frag.appendChild(node);"
                        + "      }"
                        + "      range.insertNode(frag);"
                        + "      if (lastNode) {"
                        + "        range = range.cloneRange();"
                        + "        range.setStartAfter(lastNode);"
                        + "        range.collapse(true);"
                        + "        sel.removeAllRanges();"
                        + "        sel.addRange(range);"
                        + "      }"
                        + "    }"
                        + "  }"
                        + "  else if (document.selection && "
                        + "           document.selection.type != \"Control\") {"
                        + "    document.selection.createRange().pasteHTML(html);"
                        + "  }"
                        + "})(\"%s\");", html);
        //execute script
        WebView mWebView = (WebView) htmlEditor.lookup( ".web-view");
        mWebView.getEngine().executeScript(script);
    }
}
