package klijent.gui;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/** Klasa namenjena za prikaz Studentske Forme u JavaFx-u
 *   @author Biljana Stanojevic  */

public class StudentForm extends Stage {

    private static final Font font15 = new Font("Arial", 15);
    private static final Font font20 = new Font("Arial", 20);
    private Student student;
    private ObservableList<IspitniRok> sviIspitniRokovi = FXCollections.observableArrayList();
    private HashMap<Predmet, Integer> polozeniPredmeti = new HashMap<>();
    private ObservableList<Predmet> nepolozeniPredmeti = FXCollections.observableArrayList();
    private HashMap<Predmet, ArrayList> prijavaIspita = new HashMap<>();
    private static Alert alert = new Alert(Alert.AlertType.NONE);

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setSviIspitniRokovi(ObservableList<IspitniRok> sviIspitniRokovi) {
        this.sviIspitniRokovi = sviIspitniRokovi;
    }

    public void setPolozeniPredmeti(HashMap<Predmet, Integer> polozeniPredmeti) {
        this.polozeniPredmeti = polozeniPredmeti;
    }

    public void setNepolozeniPredmeti(ObservableList<Predmet> nepolozeniPredmeti) {
        this.nepolozeniPredmeti = nepolozeniPredmeti;
    }

    public void setPrijavaIspita(HashMap<Predmet, ArrayList> prijavaIspita) {
        this.prijavaIspita = prijavaIspita;
    }

    public Student getStudent() {
        return student;
    }

    /**
     * Setuje tip i naslov statičkog alerta u zavisnosti od prosleđenog tipa
     */
    public static void setAlert(Alert.AlertType at) {
        if (at == Alert.AlertType.ERROR) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Greška");
            alert.setHeaderText("");
        } else if (at == Alert.AlertType.INFORMATION) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText("");
        }
    }

    /** Proverava da li ima aktivnih ispitnih rokova ili ne, pa postavlja prikaz za stavku Pocetna iz Menija    */
    private void pocetniPrikaz(BorderPane root){

        boolean aktivan = false;
        for (IspitniRok ispitniRok:this.sviIspitniRokovi) {
            if (ispitniRok.isAktivnost()) {
                aktivan = true;
                break;
            }
        }

        String poruka;
        if (aktivan) {
            poruka = "Ispitni rok je u toku.";
        } else {
            poruka = "Nijedan ispitni rok trenutno nije u toku.";
        }
        Label lblPrikaz = new Label(poruka);
        lblPrikaz.setFont(font20);
        lblPrikaz.setPadding(new Insets(5,10,5,10));
        root.setLeft(lblPrikaz);

    }

    /** Cisti sve strane Border Pane-a, pre prebacivanja na sledeci prikaz iz Menija  */
    private static void ocistiPane(BorderPane root) {

        root.setLeft(null);
        root.setRight(null);
        root.setBottom(null);
        root.setCenter(null);
    }

    public StudentForm(Stage stage, Student student, ObservableList<IspitniRok> sviIspitniRokovi, HashMap<Predmet, Integer> polozeni, ObservableList<Predmet> nepolozeni, HashMap<Predmet, ArrayList> prijave) {

        super();
        initOwner(stage);
        setStudent(student);
        setSviIspitniRokovi(sviIspitniRokovi);
        setPolozeniPredmeti(polozeni);
        setNepolozeniPredmeti(nepolozeni);
        setPrijavaIspita(prijave);

        BorderPane root = new BorderPane();
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        root.setTop(menuBar);
        pocetniPrikaz(root);

        Scene scene = new Scene(root, 900, 700);
        setScene(scene);
        setResizable(false);
        setTitle("Student: " + student.getImePrezime());

        //Klikom na stavku Pocetna iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a i poziva prikaz za pocetnu stranu
        Label lblPocetna = new Label("POČETNA");
        lblPocetna.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osveziIspitneRokove"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

            ocistiPane(root);
            pocetniPrikaz(root);
        });
        Menu pocetnaMenu = new Menu("", lblPocetna);

        //Klikom na stavku PREDMETI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabelama prikazuju svi položeni i nepoloženi predmeti za tog studenta
        Label lblPredmeti = new Label("PREDMETI");
        lblPredmeti.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osveziPredmete"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

            ocistiPane(root);
            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            Label lblPolozeni = new Label("Položeni predmeti");
            lblPolozeni.setFont(font20);
            lblPolozeni.setAlignment(Pos.CENTER_LEFT);
            lblPolozeni.setPadding(new Insets(0,10,5,0));

            TableView<HashMap.Entry<Predmet, Integer>> tablePolozeni = new TableView<>();
            tablePolozeni.setPlaceholder(new Label("Nije položen nijedan ispit"));
            tablePolozeni.getColumns().clear();

            TableColumn<Map.Entry<Predmet, Integer>, Integer> colSifra = new TableColumn<>("Šifra");
            colSifra.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, Integer> p) {
                    return new SimpleObjectProperty<Integer>(p.getValue().getKey().getIdPredmeta());
                }

            });
            colSifra.setMinWidth(75);
            TableColumn<Map.Entry<Predmet, Integer>, String> colNaziv = new TableColumn<>("Naziv predmeta");
            colNaziv.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, String> p) {
                    return new SimpleObjectProperty<String>(p.getValue().getKey().getNaziv());
                }

            });
            colNaziv.setMinWidth(350);
            TableColumn<Map.Entry<Predmet, Integer>, Integer> colEspb = new TableColumn<>("ESPB");
            colEspb.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, Integer> p) {
                    return new SimpleObjectProperty<Integer>(p.getValue().getKey().getEspb());
                }

            });
            colEspb.setMinWidth(75);
            TableColumn<Map.Entry<Predmet, Integer>, Integer> colSemestar = new TableColumn<>("Semestar");
            colSemestar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, Integer> p) {
                    return new SimpleObjectProperty<Integer>(p.getValue().getKey().getSemestar());
                }

            });
            colSemestar.setMinWidth(75);

            TableColumn<Map.Entry<Predmet, Integer>, Integer> colOcena = new TableColumn<>("Ocena");
            colOcena.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Integer>, Integer> p) {
                    return new SimpleObjectProperty<Integer>(p.getValue().getValue());
                }

            });
            colOcena.setMinWidth(75);

            ObservableList<HashMap.Entry<Predmet, Integer>> stavke = FXCollections.observableArrayList(polozeniPredmeti.entrySet());
            tablePolozeni.setItems(stavke);
            //sredjuje problem za dodatu kolonu
            tablePolozeni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablePolozeni.getColumns().addAll(colSifra, colNaziv, colEspb, colSemestar, colOcena);

            Label lblNepolozeni = new Label("Nepoloženi predmeti");
            lblNepolozeni.setFont(font20);
            lblNepolozeni.setAlignment(Pos.CENTER_LEFT);
            lblNepolozeni.setPadding(new Insets(5,10,5,0));

            TableView<Predmet> tableNeplozeni = new TableView<>();
            tableNeplozeni.setPlaceholder(new Label("Nema nepoloženih ispita"));
            tableNeplozeni.getColumns().clear();

            TableColumn colSifraNe = new TableColumn("Šifra");
            colSifraNe.setCellValueFactory(new PropertyValueFactory<Predmet, String>("idPredmeta"));
            colSifraNe.setMinWidth(75);
            TableColumn colNazivNe = new TableColumn("Naziv predmeta");
            colNazivNe.setCellValueFactory(new PropertyValueFactory<Predmet, String>("naziv"));
            colNazivNe.setMinWidth(350);
            TableColumn colEspbNe = new TableColumn("ESPB");
            colEspbNe.setCellValueFactory(new PropertyValueFactory<Predmet, String>("espb"));
            colEspbNe.setMinWidth(75);
            TableColumn colSemestarNe = new TableColumn("Semestar");
            colSemestarNe.setCellValueFactory(new PropertyValueFactory<Predmet, String>("semestar"));
            colSemestarNe.setMinWidth(75);

            tableNeplozeni.setItems(nepolozeniPredmeti);
            //sredjuje problem za dodatu kolonu
            tableNeplozeni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableNeplozeni.getColumns().addAll(colSifraNe, colNazivNe, colEspbNe, colSemestarNe);

            vbox.getChildren().addAll(lblPolozeni, tablePolozeni, lblNepolozeni, tableNeplozeni);

            root.setCenter(vbox);

        });
        Menu predmetiMenu = new Menu("", lblPredmeti);

        //Klikom na stavku PRIJAVA ISPITA iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju nepolozeni ispiti koji se mogu prijaviti za vreme aktivnog ispitnog roka ukoliko student ima dovoljno novcanih sredstava
        Label lblPrijava = new Label("PRIJAVA ISPITA");
        lblPrijava.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osveziPrijave"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            Label lblPrijavaIspita = new Label("Prijava ispita");
            lblPrijavaIspita.setFont(font20);
            lblPrijavaIspita.setAlignment(Pos.CENTER_LEFT);
            lblPrijavaIspita.setPadding(new Insets(0,10,5,0));

            TableView<HashMap.Entry<Predmet, ArrayList>> tablePrijava = new TableView();
            tablePrijava.setPlaceholder(new Label("Ne postoji nijedan ispit za prijavu"));
            tablePrijava.getColumns().clear();

            TableColumn<Map.Entry<Predmet, ArrayList>, Integer> colSifra = new TableColumn<>("Šifra");
            colSifra.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, ArrayList>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, ArrayList>, Integer> p) {
                    return new SimpleObjectProperty<Integer>(p.getValue().getKey().getIdPredmeta());
                }

            });
            colSifra.setMinWidth(100);
            TableColumn<Map.Entry<Predmet, ArrayList>, String> colNaziv = new TableColumn<>("Naziv predmeta");
            colNaziv.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, ArrayList>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, ArrayList>, String> p) {
                    return new SimpleObjectProperty<String>(p.getValue().getKey().getNaziv());
                }

            });
            colNaziv.setMinWidth(300);
            TableColumn<Map.Entry<Predmet, ArrayList>, String> colProfesor = new TableColumn<>("Profesor");
            colProfesor.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, ArrayList>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, ArrayList>, String> p) {
                    return new SimpleObjectProperty<String>((String) p.getValue().getValue().get(0));
                }

            });
            colProfesor.setMinWidth(300);
            TableColumn<Map.Entry<Predmet, ArrayList>, Double> colCena = new TableColumn<>("Cena");
            colCena.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, ArrayList>, Double>, ObservableValue<Double>>() {


                @Override
                public ObservableValue<Double> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, ArrayList>, Double> p) {
                    return new SimpleObjectProperty<Double>((Double) p.getValue().getValue().get(1));
                }
            });
            colCena.setMinWidth(100);

            ObservableList<HashMap.Entry<Predmet, ArrayList>> stavke = FXCollections.observableArrayList(prijavaIspita.entrySet());
            tablePrijava.setItems(stavke);
            //sredjuje problem za dodatu kolonu
            tablePrijava.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablePrijava.setPrefHeight(550);
            tablePrijava.getColumns().addAll(colSifra, colNaziv, colProfesor, colCena);

            HBox hboxPrijavi = new HBox();
            hboxPrijavi.setAlignment(Pos.BOTTOM_RIGHT);
            hboxPrijavi.setPadding(new Insets(15,0,0,0));
            hboxPrijavi.setSpacing(5);
            Button btnPrijavi = new Button("Prijavi ispit");
            btnPrijavi.setMinWidth(100);
            btnPrijavi.setFont(font15);
            btnPrijavi.setOnMouseClicked(e -> {
                if (tablePrijava.getSelectionModel().getSelectedItem() != null) {
                    HashMap.Entry<Predmet, ArrayList> izabraniIspit = tablePrijava.getSelectionModel().getSelectedItem();
                    //setTabela(tablePrijava);
                    /*Thread t = new Thread(new RunnableZahtevServeru("obrisi", izabraniIspit));
                    t.setDaemon(true);
                    t.start();*/
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.ERROR);
                            alert.setContentText("Molim vas izaberite neki ispit u tabeli");
                            alert.showAndWait();
                        }
                    });
                }
            });
            hboxPrijavi.getChildren().add(btnPrijavi);
            vbox.getChildren().addAll(lblPrijavaIspita, tablePrijava, hboxPrijavi);

            root.setCenter(vbox);
        });
        Menu prijavaMenu = new Menu("", lblPrijava);

        //Klikom na stavku ŠKOLARINE iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve dosadašnje uplate, a ispod njih preostala dugovanja skolarine za studenta ako postoje
        Label lblSkolarine = new Label("ŠKOLARINE");
        lblSkolarine.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            /*Thread runnableZahtevServeru = new Thread(new StudentskaSluzbaForm("osveziSkolarine"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();*/

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            Label lblSkolarineIspis = new Label("Školarine");
            lblSkolarineIspis.setFont(font20);
            lblSkolarineIspis.setAlignment(Pos.CENTER_LEFT);
            lblSkolarineIspis.setPadding(new Insets(0,10,5,0));

            TableView<String> tableSkolarine = new TableView<String>();
            tableSkolarine.setPlaceholder(new Label("Nema podataka za školarinu"));
            tableSkolarine.getColumns().clear();

            TableColumn<String, String> colDatum = new TableColumn("Datum");
            colDatum.setMinWidth(250);
            TableColumn<String, String> colZaduzenja = new TableColumn("Zaduženja");
            colZaduzenja.setMinWidth(150);
            TableColumn<String, String> colUplata = new TableColumn("Uplata");
            colUplata.setMinWidth(150);
            TableColumn<String, String> colSvrha = new TableColumn("Svrha uplate");
            colSvrha.setMinWidth(250);

            tableSkolarine.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableSkolarine.setPrefHeight(550);
            tableSkolarine.getColumns().addAll(colDatum, colZaduzenja, colUplata, colSvrha);

            Label lblUkupniDug = new Label("Ukupan dug: ");
            lblUkupniDug.setFont(font15);
            lblUkupniDug.setAlignment(Pos.CENTER_LEFT);
            lblUkupniDug.setPadding(new Insets(10,10,0,0));

            vbox.getChildren().addAll(lblSkolarineIspis, tableSkolarine, lblUkupniDug);

            root.setCenter(vbox);
        });
        Menu skolarineMenu = new Menu("", lblSkolarine);

        //Klikom na stavku PODACI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se ispisuju jedinstveni podaci za tog studenta
        Label lblPodaci = new Label("PODACI");
        lblPodaci.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            Label lblPrikaz = new Label("Podaci");
            lblPrikaz.setFont(font20);

            Label lblBrIndeksa = new Label("Broj indeksa: " + student.getBrojIndeksa());
            lblBrIndeksa.setPadding(new Insets(20,0,0,0));
            lblBrIndeksa.setFont(font15);

            Label lblImePrezime = new Label("Ime i prezime: " + student.getImePrezime());
            lblImePrezime.setPadding(new Insets(10,0,0,0));
            lblImePrezime.setFont(font15);

            Label lblAdresa = new Label("Adresa: " + Optional.ofNullable(student.getAdresa()).orElse("/"));
            lblAdresa.setPadding(new Insets(10,0,0,0));
            lblAdresa.setFont(font15);

            Label lblTelefon = new Label("Broj telefona: " + Optional.ofNullable(student.getBrojTelefona()).orElse("/"));
            lblTelefon.setPadding(new Insets(10,0,0,0));
            lblTelefon.setFont(font15);

            Label lblEmail = new Label("E-mail: " + Optional.ofNullable(student.getEmail()).orElse("/"));
            lblEmail.setPadding(new Insets(10,0,0,0));
            lblEmail.setFont(font15);

            Label lblStatus = new Label("Status: " + student.getFinansiranje());
            lblStatus.setPadding(new Insets(10,0,0,0));
            lblStatus.setFont(font15);

            Label lblSemestar = new Label("Semestar: " + student.getSemestar());
            lblSemestar.setPadding(new Insets(10,0,0,0));
            lblSemestar.setFont(font15);

            vbox.getChildren().addAll(lblPrikaz, lblBrIndeksa, lblImePrezime, lblAdresa, lblTelefon, lblEmail, lblStatus, lblSemestar);
            root.setCenter(vbox);
        });
        Menu podaciMenu = new Menu("", lblPodaci);

        menuBar.setStyle("-fx-padding: 3 6 3 6;");
        menuBar.getMenus().addAll(pocetnaMenu, predmetiMenu, prijavaMenu, skolarineMenu, podaciMenu);

    }

    /** Klasa RunnableZahtevServeru namenjena za razmenjivanje objekata sa serverom.
     * Za osvezavanje podataka na formi i prijavu ispita.
     * @author Biljana Stanojevic   */
    private class RunnableZahtevServeru implements Runnable {

        private static final int TCP_PORT = 9000;
        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object zahtev;
        private Object odgovor;

        //konstuktor za osvezavanje podataka
        public RunnableZahtevServeru(Object zahtev) {
            this.zahtev = zahtev;
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
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        setAlert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije");
                        alert.showAndWait();
                    }
                });
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (zahtev.equals("osveziIspitneRokove")) {
                try {
                    outObj.writeObject("osvezi" + "Studenta");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    sviIspitniRokovi.clear();
                    while (true) { //nisam sigurna za ovu proveru
                        odgovor = inObj.readObject();
                        if (odgovor.toString().equals("kraj")) {
                            break;
                        } else {
                            IspitniRok ispitniRok = (IspitniRok) odgovor;
                            sviIspitniRokovi.add(ispitniRok);
                        }
                    }
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setSviIspitniRokovi(sviIspitniRokovi);
                            System.out.println("Osvezeni podaci sa strane servera");

                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije");
                            alert.showAndWait();
                        }
                    });
                    //e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (zahtev.equals("osveziPredmete")) {
                try {
                    outObj.writeObject("osvezi" + "Studenta");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    outObj.writeObject(getStudent());
                    outObj.flush();

                    polozeniPredmeti.clear();
                    nepolozeniPredmeti.clear();
                    odgovor = inObj.readObject();
                    polozeniPredmeti = (HashMap) odgovor;

                    while (true) {
                        odgovor = inObj.readObject();
                        if (odgovor.equals("kraj")) {
                            break;
                        }
                        Predmet predmet = (Predmet) odgovor;
                        nepolozeniPredmeti.add(predmet);
                    }
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setPolozeniPredmeti(polozeniPredmeti);
                            setNepolozeniPredmeti(nepolozeniPredmeti);
                            System.out.println("Osvezeni podaci sa strane servera");

                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije");
                            alert.showAndWait();
                        }
                    });
                    //e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (zahtev.equals("osveziPrijave")) {
                try {
                    outObj.writeObject("osvezi" + "Studenta");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    outObj.writeObject(getStudent());
                    outObj.flush();

                    prijavaIspita.clear();
                    odgovor = inObj.readObject();
                    prijavaIspita = (HashMap) odgovor;

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setPrijavaIspita(prijavaIspita);
                            System.out.println("Osvezeni podaci sa strane servera");

                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije");
                            alert.showAndWait();
                        }
                    });
                    //e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (zahtev.equals("osveziSkolarine")) {

            }
        }
    }
}