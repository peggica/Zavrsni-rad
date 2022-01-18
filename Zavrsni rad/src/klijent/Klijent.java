package klijent;

import javafx.application.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import klijent.gui.*;
import model.*;

import java.io.*;
import java.net.*;

/** Klase Klijent i RunnableKlijent namenjene za povezivanje klijenta sa serverom i razmenu zahteva/odgovora,
 *  kao i prikaza forme za Login u JavaFx-u
 *  @author Biljana Stanojevic  */

public class Klijent extends Application {

    private static Stage stage;

    public static Stage getStage() {

        return stage;
    }

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
        txtInput.setPromptText("Unesite korisničko ime");
        root.add(txtInput, 1, 0);

        Label lblPassword = new Label("Lozinka: ");
        lblPassword.setFont(font);
        root.add(lblPassword, 0, 1);

        PasswordField pfPassword = new PasswordField();
        pfPassword.setPromptText("Unesite lozinku");
        root.add(pfPassword, 1, 1);

        HBox hBox = new HBox();
        Button btnLogin = new Button("Prijavi se");
        btnLogin.setFont(font);

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //TODO: provera na Serveru u bazi za login i pozivanje odgovarajuće forme u zavisnosti od tipa korisnika
                //prosledjeni uneti podaci sa forme na klik dugmeta
                if(txtInput.getText() != "" && pfPassword.getText() != "") {

                    Thread runnableKlijent = new Thread(new RunnableKlijent(txtInput.getText(), pfPassword.getText()));
                    //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                    runnableKlijent.setDaemon(true);
                    runnableKlijent.start();

                } else {

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            Stage dialog = new Dialog(getStage(), "Molimo vas unesite podatke!");
                            dialog.sizeToScene();
                            dialog.show();
                        }
                    });
                }
            }
        });

        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(btnLogin);
        root.add(hBox, 1, 2);

        Scene scene = new Scene(root, 320, 160);
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        loginStage.setTitle("Login");
        stage = loginStage;
        loginStage.show();

    }

    public static void main(String[] args) {

        launch(args);

    }

    private class RunnableKlijent implements Runnable {

        public static final int TCP_PORT = 9000;
        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object odgovor;
        private String korisnickoIme;
        private String lozinka;
        private ObservableList<Student> sviStudenti = FXCollections.observableArrayList();
        private ObservableList<Zaposleni> sviZaposleni = FXCollections.observableArrayList();
        private ObservableList<Predmet> sviPredmeti = FXCollections.observableArrayList();

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

                inObj = new ObjectInputStream(socket.getInputStream());
                outObj = new ObjectOutputStream(socket.getOutputStream());

                Login login = new Login(korisnickoIme, lozinka);
                outObj.writeObject("login");
                outObj.flush();
                outObj.writeObject(login);
                outObj.flush();

                odgovor = inObj.readObject();

                if(odgovor.toString().equals("sluzba")) {

                    //ovde treba da ucita sve podatke od sluzbe
                    try {
                        odgovor = inObj.readObject();
                        if(odgovor.toString().equals("svistudenti")) {

                            while (true) { //nisam sigurna za ovu proveru
                                odgovor = inObj.readObject();
                                if(odgovor.toString().equals("svizaposleni")) {
                                    break;
                                } else {
                                    Student student = (Student) odgovor;
                                    sviStudenti.add(student);
                                }
                            }
                            if(odgovor.toString().equals("svizaposleni")) {

                                while (true) { //nisam sigurna za ovu proveru
                                    odgovor = inObj.readObject();
                                    if (odgovor.toString().equals("svipredmeti")) {
                                        break;
                                    } else {
                                        Zaposleni zaposleni = (Zaposleni) odgovor;
                                        sviZaposleni.add(zaposleni);
                                    }
                                }
                            }
                            if(odgovor.toString().equals("svipredmeti")) {

                                while (true) { //nisam sigurna za ovu proveru
                                    odgovor = inObj.readObject();
                                    if (odgovor.toString().equals("kraj")) {
                                        break;
                                    } else {
                                        Predmet predmet = (Predmet) odgovor;
                                        sviPredmeti.add(predmet);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            StudentskaSluzbaForm studentskaSluzbaForm = new StudentskaSluzbaForm(getStage(), sviStudenti, sviZaposleni, sviPredmeti);
                            getStage().close();
                            studentskaSluzbaForm.show();

                        }
                    });
                } else if(odgovor.toString().equals("nepostoji")) {
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            Stage dialog = new Dialog(getStage(), "Pogrešno uneto korisničko ime ili lozinka");
                            dialog.sizeToScene();
                            dialog.show();
                        }
                    });
                } else {
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Student student = (Student) odgovor;
                                StudentForm studentForm = new StudentForm(getStage());
                                getStage().close();
                                studentForm.show();
                            } catch (Exception e) {
                                // e.printStackTrace();
                            }
                            try {
                                Zaposleni zaposleni = (Zaposleni) odgovor;
                                ZaposleniForm zaposleniForm = new ZaposleniForm(getStage());
                                getStage().close();
                                zaposleniForm.show();
                            } catch (Exception e) {
                                // e.printStackTrace();
                            }
                        }
                    });
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();

            } catch (IOException e) {

                //e.printStackTrace();
                //update na JavaFx application niti
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        Stage dialog = new Dialog(getStage(), "Server je trenutno nedostupan!", "Molimo vas pokusajte kasnije");
                        dialog.sizeToScene();
                        dialog.show();
                    }
                });

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {

                //ZATVARANJE KONEKCIJE - ovo treba tek kad se ide na X
                if(socket != null && (inObj != null || outObj != null)) {

                    try {
                        socket.close();
                        inObj.close();
                        outObj.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /** Klasa Dialog namenjena za prikaz poruke korisniku,
     *  ukoliko server nije podignut ili ukoliko nisu uneti podaci
     *  @author Biljana Stanojevic  */

    private class Dialog extends Stage {

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
}