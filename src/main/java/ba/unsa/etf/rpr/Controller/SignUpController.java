package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.Models.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class SignUpController {
    public TextField nameFld;
    public TextField lastNameFld;
    public TextField usernameFld;
    public PasswordField passwordFld;
    public PasswordField passwordFld2;
    public Label messageLabel;
    private Model model = new Model();
    @FXML
    public void initialize(){
        messageLabel.setVisible(false);
    }

    public void signUpAccount(ActionEvent actionEvent){
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translation", Locale.getDefault());
        if(nameFld.getText().isBlank() || lastNameFld.getText().isBlank() || usernameFld.getText().isBlank()
            || passwordFld.getText().isBlank() || passwordFld2.getText().isBlank()){
            messageLabel.setText(resourceBundle.getString("signUpMessage1"));
            messageLabel.setTextFill(Paint.valueOf("red"));
            messageLabel.setVisible(true);
        }
        else if(model.usernameCheck(usernameFld.getText())){
            messageLabel.setText(resourceBundle.getString("signUpMessage2"));
            messageLabel.setTextFill(Paint.valueOf("red"));
            messageLabel.setVisible(true);
        }
        else if (!passwordFld.getText().equals(passwordFld2.getText())){
            messageLabel.setText(resourceBundle.getString("signUpMessage3"));
            messageLabel.setTextFill(Paint.valueOf("red"));
            messageLabel.setVisible(true);
        }
        else if(!passwordStrengthCheck(passwordFld.getText())){
            messageLabel.setText(resourceBundle.getString("signUpMessage4"));
            messageLabel.setTextFill(Paint.valueOf("red"));
            messageLabel.setVisible(true);
        }
        else{
            model.korisnikInsert(usernameFld.getText(), passwordFld.getText(), nameFld.getText(), lastNameFld.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resourceBundle.getString("signUpSuccess"));
            alert.setHeaderText(null);
            alert.setContentText(resourceBundle.getString("signUpSuccess1"));
            Optional<ButtonType> res = alert.showAndWait();
            if (res.get() == ButtonType.OK){
                actionEvent.consume();
                cancel(actionEvent);
            }
        }
    }
    public void cancel(ActionEvent actionEvent){
        Node node = (Node)actionEvent.getSource();
        ((Stage)node.getScene().getWindow()).close();
    }

    private boolean passwordStrengthCheck(String password){
        int min =8;
        int max=16;
        int digit=0;
        int special=0;
        int upCount=0;
        int loCount=0;
        if(password.length()>=min && password.length()<=max){
            for(int i =0;i<password.length();i++){
                char c = password.charAt(i);
                if(Character.isUpperCase(c)){
                    upCount++;
                }
                if(Character.isLowerCase(c)){
                    loCount++;
                }
                if(Character.isDigit(c)){
                    digit++;
                }
                if(c>=33&&c<=46||c==64){
                    special++;
                }
            }
            return (special>=1&&loCount>=1&&upCount>=1&&digit>=1);
        }
        return false;
    }
}
