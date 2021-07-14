package ba.unsa.etf.rpr.Controller;

import ba.unsa.etf.rpr.Models.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

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
        if(nameFld.getText().isBlank() || lastNameFld.getText().isBlank() || usernameFld.getText().isBlank()
            || passwordFld.getText().isBlank() || passwordFld2.getText().isBlank()){
            messageLabel.setText("One or more fields is left blank. Please fill them up");
            messageLabel.setTextFill(Paint.valueOf("red"));
            messageLabel.setVisible(true);
        }
        else if(model.usernameCheck(usernameFld.getText())){
            messageLabel.setText("Username taken, please choose another username");
            messageLabel.setTextFill(Paint.valueOf("red"));
            messageLabel.setVisible(true);
        }
        else if (!passwordFld.getText().equals(passwordFld2.getText())){
            messageLabel.setText("Passwords don't match");
            messageLabel.setTextFill(Paint.valueOf("red"));
            messageLabel.setVisible(true);
        }
        else if(!passwordStrengthCheck(passwordFld.getText())){
            messageLabel.setText("Passwords should contain between 8 and 16 characters and have at least one digit, one special sign and one upercase character");
            messageLabel.setTextFill(Paint.valueOf("red"));
            messageLabel.setVisible(true);
        }
        else{
            model.korisnikInsert(usernameFld.getText(), passwordFld.getText(), nameFld.getText(), lastNameFld.getText());
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
