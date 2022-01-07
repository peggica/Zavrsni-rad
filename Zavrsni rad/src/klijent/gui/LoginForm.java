package klijent.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Klasa namenjena za prikaz Login Forme u JavaFx-u
*   @author Biljana Stanojevic  */

public class LoginForm extends Application {
    @Override
    public void start(Stage loginStage) throws Exception {

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10,0,10,0));
        root.setHgap(10);
        root.setVgap(10);

        Font font = new Font("Arial", 15);

        Label lblUserName = new Label("Korisničko ime: ");
        lblUserName.setFont(font);
        root.add(lblUserName,0,0);

        TextField txtInput = new TextField();
        root.add(txtInput,1,0);

        Label lblPassword = new Label("Lozinka: ");
        lblPassword.setFont(font);
        root.add(lblPassword,0,1);

        PasswordField pfPassword = new PasswordField();
        root.add(pfPassword, 1,1);

        HBox hBox = new HBox();
        Button btnLogin = new Button("Prijavi se");
        btnLogin.setFont(font);

        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(btnLogin);
        root.add(hBox,1,2);

        Scene scene = new Scene(root, 320, 160);
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        loginStage.setTitle("Login");
        loginStage.show();

    }

    public void createGUI(String[] args) {

        launch(args);
    }

}
