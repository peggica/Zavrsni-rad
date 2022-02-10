package klijent.gui;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Klasa Dialog namenjena za prikaz poruke korisniku,
 * ukoliko server nije podignut ili ukoliko nisu uneti podaci
 * @author Biljana Stanojevic   */

public class Dialog extends Stage {

    String poruka1;
    String poruka2;

    public Dialog(Stage loginStage, String poruka1) {

        super();
        this.poruka1 = poruka1;
        initOwner(loginStage);
        setTitle("Info");
        setResizable(false);
        GridPane root = new GridPane();
        Scene scene = new Scene(root, 280, 40);
        setScene(scene);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 0, 10, 0));
        root.setHgap(10);
        root.setVgap(10);

        Font font = new Font("Arial", 15);

        Label lblInfo1 = new Label(poruka1);
        lblInfo1.setTextFill(Color.RED);
        lblInfo1.setFont(font);

        root.add(lblInfo1, 0, 0);
    }

    public Dialog(Stage stage, String poruka1, String poruka2) {

        super();
        this.poruka1 = poruka1;
        this.poruka2 = poruka2;
        initOwner(stage);
        setTitle("Info");
        setResizable(false);
        GridPane root = new GridPane();
        Scene scene = new Scene(root, 280, 80);
        setScene(scene);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 0, 10, 0));
        root.setHgap(10);
        root.setVgap(10);

        Font font = new Font("Arial", 15);

        Label lblInfo1 = new Label(poruka1);
        lblInfo1.setFont(font);
        Label lblInfo2 = new Label(poruka2);
        lblInfo2.setFont(font);

        root.add(lblInfo1, 0, 0);
        root.add(lblInfo2, 0, 1);

    }

}