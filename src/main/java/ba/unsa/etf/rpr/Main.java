package ba.unsa.etf.rpr;

import ba.unsa.etf.rpr.Controller.LogInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Main extends Application {
    public static Style theme = Style.LIGHT;

    public static Style getTheme() {
        return theme;
    }

    public static void setTheme(Style theme) {
        Main.theme = theme;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/logIn.fxml"), ResourceBundle.getBundle("Translation"));
        loader.setController(new LogInController());
        Parent root = loader.load();
        Scene scene = new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        JMetro jMetro = new JMetro(Main.getTheme());
        jMetro.setScene(scene);

        primaryStage.getIcons().add(new Image("/images/app_icon.png"));
        primaryStage.setScene(scene);
       // primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable(false);
        primaryStage.setTitle("E-Notes");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
