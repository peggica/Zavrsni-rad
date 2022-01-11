package klijent;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

/** Klase Klijent i RunnableKlijent namenjene za povezivanje klijenta sa serverom i razmenu zahteva/odgovora,
 *  kao i prikaza forme za Login u JavaFx-u
 *  @author Biljana Stanojevic  */

public class Klijent extends Application {

    @Override
    public void start(Stage loginStage) throws Exception {

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 0, 10, 0));
        root.setHgap(10);
        root.setVgap(10);

        Font font = new Font("Arial", 15);

        Label lblUserName = new Label("Korisničko ime: ");
        lblUserName.setFont(font);
        root.add(lblUserName, 0, 0);

        TextField txtInput = new TextField();
        root.add(txtInput, 1, 0);

        Label lblPassword = new Label("Lozinka: ");
        lblPassword.setFont(font);
        root.add(lblPassword, 0, 1);

        PasswordField pfPassword = new PasswordField();
        root.add(pfPassword, 1, 1);

        HBox hBox = new HBox();
        Button btnLogin = new Button("Prijavi se");
        btnLogin.setFont(font);

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //TODO: provera na Serveru u bazi za login i pozivanje odgovarajuće forme u zavisnosti od tipa korisnika
                //prosledjeni uneti podaci sa forme na klik dugmeta
                Thread runnableKlijent = new Thread(new RunnableKlijent(txtInput.getText(), pfPassword.getText()));
                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                //runnableKlijent.setDaemon(true);
                runnableKlijent.start();
            }
        });

        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(btnLogin);
        root.add(hBox, 1, 2);

        Scene scene = new Scene(root, 320, 160);
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        loginStage.setTitle("Login");
        loginStage.show();

    }

    public static void main(String[] args) {

        launch(args);

    }

    private class RunnableKlijent implements Runnable {

        public static final int TCP_PORT = 9000;
        private Socket socket = null;
        private BufferedReader in;
        private PrintWriter out;
        private String zahtev;
        private String odgovor;
        private String korisnickoIme;
        private String lozinka;

        public RunnableKlijent(String korisnickoIme, String lozinka) {

            this.korisnickoIme = korisnickoIme;
            this.lozinka = lozinka;
        }

        @Override
        public void run() {

            //OTVARANJE KONEKCIJE
            try {
                InetAddress addr = InetAddress.getByName("127.0.0.1");
                socket = new Socket(addr, TCP_PORT);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //procitati podatke sa forme
                out.println(korisnickoIme + " " + lozinka);

                odgovor = in.readLine();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                //ZATVARANJE KONEKCIJE
                try {

                    socket.close();
                    in.close();
                    out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}