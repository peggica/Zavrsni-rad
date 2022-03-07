package klijent;

import javafx.application.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import klijent.gui.*;
import klijent.gui.Dialog;
import model.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;

/** Klase Klijent i RunnableKlijent namenjene za povezivanje klijenta sa serverom i razmenu zahteva/odgovora,
 *  kao i prikaza forme za Login u JavaFx-u
 *  @author Biljana Stanojevic  */

public class Klijent extends Application {

    private static Stage stage;
    private ObservableList<IspitniRok> sviIspitniRokovi = FXCollections.observableArrayList();
    private ObservableList<Student> sviStudenti = FXCollections.observableArrayList();
    private ObservableList<Zaposleni> sviZaposleni = FXCollections.observableArrayList();
    private HashMap<Predmet, Zaposleni> sviPredmeti = new HashMap<>();
    private ObservableList<Sala> sveSale = FXCollections.observableArrayList();
    private HashMap<Predmet, Integer> polozeniPredmeti = new HashMap<>();
    private ObservableList<Predmet> nepolozeniPredmeti = FXCollections.observableArrayList();
    private StudentskaSluzbaForm studentskaSluzbaForm;
    private ZaposleniForm zaposleniForm;
    private StudentForm studentForm;
    private static final int TCP_PORT = 9000;

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

        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object odgovor;
        private String korisnickoIme;
        private String lozinka;
        private Zaposleni ovajZaposleni;
        private Student ovajStudent;

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

                    odgovor = inObj.readObject();
                    if (odgovor.toString().equals("sviispitnirokovi")) {

                        sviIspitniRokovi.clear();
                        while (true) { //nisam sigurna za ovu proveru
                            odgovor = inObj.readObject();
                            if (odgovor.toString().equals("svistudenti")) {
                                break;
                            } else {
                                IspitniRok ispitniRok = (IspitniRok) odgovor;
                                sviIspitniRokovi.add(ispitniRok);
                            }
                        }

                    }
                    if (odgovor.toString().equals("svistudenti")) {

                        sviStudenti.clear();
                        while (true) { //nisam sigurna za ovu proveru
                            odgovor = inObj.readObject();
                            if (odgovor.toString().equals("svizaposleni")) {
                                break;
                            } else {
                                Student student = (Student) odgovor;
                                sviStudenti.add(student);
                            }
                        }
                    }
                    if (odgovor.toString().equals("svizaposleni")) {

                        sviZaposleni.clear();
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
                    if (odgovor.toString().equals("svipredmeti")) {

                        sviPredmeti.clear();
                        odgovor = inObj.readObject();
                        sviPredmeti = (HashMap) odgovor;
                    }
                    odgovor = inObj.readObject();
                    if (odgovor.toString().equals("svesale")) {

                        sveSale.clear();
                        while (true) { //nisam sigurna za ovu proveru
                            odgovor = inObj.readObject();
                            if (odgovor.toString().equals("kraj")) {
                                break;
                            } else {
                                Sala sala = (Sala) odgovor;
                                sveSale.add(sala);
                            }
                        }
                    }

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //prikaz forme za studentsku sluzbu
                            studentskaSluzbaForm = new StudentskaSluzbaForm(getStage(), sviIspitniRokovi, sviStudenti, sviZaposleni, sviPredmeti, sveSale);
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
                } else if(odgovor.equals("zaposleni")) {

                    odgovor = inObj.readObject();
                    ovajZaposleni = (Zaposleni) odgovor;

                    while (true) {
                        odgovor = inObj.readObject();
                        if (odgovor.equals("kraj")) {
                            break;
                        }
                        IspitniRok ispitniRok = (IspitniRok) odgovor;
                        sviIspitniRokovi.add(ispitniRok);
                    }

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //prikaz forme za zaposlenog
                            zaposleniForm = new ZaposleniForm(getStage(), sviIspitniRokovi);
                            getStage().close();
                            zaposleniForm.show();

                        }
                    });
                    while(true) {

                        //na svakih 10 sekundi da ponovo pokrene nit i na taj nacin osvezi podatke
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Thread runnableKlijentOsvezi = new Thread(new RunnableKlijentOsvezi(ovajZaposleni));
                        //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                        runnableKlijentOsvezi.setDaemon(true);
                        runnableKlijentOsvezi.start();
                    }

                }  else if(odgovor.equals("student")) {

                    odgovor = inObj.readObject();
                    ovajStudent = (Student) odgovor;

                    odgovor = inObj.readObject();
                    polozeniPredmeti = (HashMap) odgovor;

                    while (true) {
                        odgovor = inObj.readObject();
                        if (odgovor.equals("sviispitnirokovi")) {
                            break;
                        }
                        Predmet predmet = (Predmet) odgovor;
                        nepolozeniPredmeti.add(predmet);
                    }

                    while (true) {
                        odgovor = inObj.readObject();
                        if (odgovor.equals("kraj")) {
                            break;
                        }
                        IspitniRok ispitniRok = (IspitniRok) odgovor;
                        sviIspitniRokovi.add(ispitniRok);
                    }

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //prikaz forme za studenta
                            studentForm = new StudentForm(getStage(), ovajStudent, sviIspitniRokovi, polozeniPredmeti, nepolozeniPredmeti);
                            getStage().close();
                            studentForm.show();

                        }
                    });
                    while(true) {

                        //na svakih 10 sekundi da ponovo pokrene nit i na taj nacin osvezi podatke
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Thread runnableKlijentOsvezi = new Thread(new RunnableKlijentOsvezi(ovajStudent));
                        //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                        runnableKlijentOsvezi.setDaemon(true);
                        runnableKlijentOsvezi.start();
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {

                //update na JavaFx application niti
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        Stage dialog = new Dialog(getStage(), "Server je trenutno nedostupan!", "Molimo vas pokusajte kasnije");
                        System.out.println("Server je trenutno nedostupan.");
                        dialog.sizeToScene();
                        dialog.show();
                    }
                });
                //e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {

                //ZATVARANJE KONEKCIJE
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

    /** Klasa RunnableKlijentOsveziZaposleni namenjena za osvezavanje tj ponovno
     *  preuzimanje vrednosti sa servera za prikaz na formi korisnika.
     *  @author Biljana Stanojevic  */
    private class RunnableKlijentOsvezi implements Runnable {

        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object odgovor;
        private Student student;
        private Zaposleni zaposleni;

        public RunnableKlijentOsvezi(Zaposleni zaposleni) {
            this.zaposleni = zaposleni;
        }

        public RunnableKlijentOsvezi(Student student) {
            this.student = student;
        }

        @Override
        public void run() {

            //OTVARANJE KONEKCIJE
            try {
                InetAddress addr = InetAddress.getByName("127.0.0.1");
                Socket socket = new Socket(addr, TCP_PORT);
                inObj = new ObjectInputStream(socket.getInputStream());
                outObj = new ObjectOutputStream(socket.getOutputStream());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (zaposleni != null) {

                //ukoliko je pozvan konstruktor za zaposlenog
                try {
                    outObj.writeObject("zaposleni");
                    try {
                        odgovor = inObj.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (odgovor.toString().equals("sviispitnirokovi")) {

                    sviIspitniRokovi.clear();
                    while (true) { //nisam sigurna za ovu proveru
                        try {
                            odgovor = inObj.readObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (odgovor.toString().equals("kraj")) {
                            break;
                        } else {
                            IspitniRok ispitniRok = (IspitniRok) odgovor;
                            sviIspitniRokovi.add(ispitniRok);
                        }
                    }
                }
                //update na JavaFx application niti
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        //azuriranje/ponovno popunjavanje svih listi
                        zaposleniForm.setSviIspitniRokovi(sviIspitniRokovi);
                        System.out.println("Osvezeni podaci sa strane servera");

                    }
                });
            } else if (student != null) {

                //ukoliko je pozvan konstruktor za studenta
                try {
                    outObj.writeObject("student");
                    try {
                        odgovor = inObj.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (odgovor.toString().equals("sviispitnirokovi")) {

                    sviIspitniRokovi.clear();
                    while (true) { //nisam sigurna za ovu proveru
                        try {
                            odgovor = inObj.readObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (odgovor.toString().equals("kraj")) {
                            break;
                        } else {
                            IspitniRok ispitniRok = (IspitniRok) odgovor;
                            sviIspitniRokovi.add(ispitniRok);
                        }
                    }
                }
                //update na JavaFx application niti
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        //azuriranje/ponovno popunjavanje svih listi
                        //TODO: da li mi treba da se osvezavaju stalno polozeni-nepolozeni predmeti?
                        studentForm.setSviIspitniRokovi(sviIspitniRokovi);
                        studentForm.setPolozeniPredmeti(polozeniPredmeti);
                        studentForm.setNepolozeniPredmeti(nepolozeniPredmeti);
                        System.out.println("Osvezeni podaci sa strane servera");

                    }
                });

            }
            //ZATVARANJE KONEKCIJE
            if (socket != null && (inObj != null || outObj != null)) {
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