package klijent.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.stream.Collectors;

/** Klasa namenjena za prikaz Forme za Zaposlene u JavaFx-u
 *  Ukoliko je tip zaposlenog profesor u Meniju je dostupna i opcija za snimanje ocena u zapisnik - DODAJ U ZAPISNIK
 *  @author Biljana Stanojevic  */

public class ZaposleniForm extends Stage {

    private static final Font font15 = new Font("Arial", 15);
    private static final Font font20 = new Font("Arial", 20);
    private Zaposleni zaposleni;
    private ObservableList<IspitniRok> sviIspitniRokovi = FXCollections.observableArrayList();
    private ObservableList<Predmet> sviPredmeti = FXCollections.observableArrayList();
    private ObservableList<PrijaveIspita> svePrijaveIspita = FXCollections.observableArrayList();
    private static Alert alert = new Alert(Alert.AlertType.NONE);

    public void setZaposleni(Zaposleni zaposleni) {
        this.zaposleni = zaposleni;
    }

    public void setSviIspitniRokovi(ObservableList<IspitniRok> sviIspitniRokovi) {
        this.sviIspitniRokovi = sviIspitniRokovi;
    }

    public void setSviPredmeti(ObservableList<Predmet> sviPredmeti) {
        this.sviPredmeti = sviPredmeti;
    }

    public void setPrijaveIspita(ObservableList<PrijaveIspita> svePrijaveIspita) {
        this.svePrijaveIspita = svePrijaveIspita;
    }

    /**
     * Setuje tip i naslov statičkog alerta u zavisnosti od prosleđenog tipa
     */
    public static void setAlert(Alert.AlertType at) {
        if (at == Alert.AlertType.ERROR) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
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

    public ZaposleniForm(Stage stage, Zaposleni zaposleni, ObservableList<IspitniRok> ispitniRokovi, ObservableList<Predmet> sviPredmeti, ObservableList<PrijaveIspita> prijaveIspita) {

        super();
        initOwner(stage);
        setZaposleni(zaposleni);
        setSviIspitniRokovi(ispitniRokovi);
        setSviPredmeti(sviPredmeti);
        setPrijaveIspita(prijaveIspita);

        BorderPane root = new BorderPane();
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        root.setTop(menuBar);
        pocetniPrikaz(root);

        Scene scene = new Scene(root, 650, 400);
        setScene(scene);
        setResizable(false);
        if (zaposleni.getPozicija().equals("profesor")) {
            setTitle("Profesor: " + zaposleni.getImePrezime());
        } else if (zaposleni.getPozicija().equals("asistent")) {
            setTitle("Asistent: " + zaposleni.getImePrezime());
        } else {
            setTitle("Saradnik: " + zaposleni.getImePrezime());
        }

        //Klikom na stavku POČETNA iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a i poziva prikaz za pocetnu stranu
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

        //Klikom na stavku ZAKAŽI SALU iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega za izabran datum i vrem prikazuje u tabeli dostupne sale za zakazivanje
        Label lblZakaziSalu = new Label("ZAKAŽI SALU");
        lblZakaziSalu.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(5, 10, 10, 10));

            HBox hboxIspit = new HBox();
            hboxIspit.setAlignment(Pos.CENTER_LEFT);
            hboxIspit.setPadding(new Insets(0, 10, 5, 0));
            hboxIspit.setSpacing(5);

            Label lblIspit = new Label("Ispit: ");
            lblIspit.setFont(font15);
            ComboBox cmbIspit = new ComboBox();
            cmbIspit.getItems().addAll("ovde idu ispiti");  //TODO: da ucita iz baze
            cmbIspit.setMinWidth(Region.USE_PREF_SIZE);
            hboxIspit.getChildren().addAll(lblIspit, cmbIspit);

            HBox hboxDatumVreme = new HBox();
            hboxDatumVreme.setAlignment(Pos.CENTER_LEFT);
            hboxDatumVreme.setPadding(new Insets(0, 10, 5, 0));
            hboxDatumVreme.setSpacing(5);

            Label lblDatum = new Label("Datum: ");
            lblDatum.setFont(font15);
            DatePicker datumDP = new DatePicker();

            Label lblVremeOd = new Label("od: ");
            lblVremeOd.setFont(font15);
            Spinner<Integer> spSatiOd = new Spinner();
            SpinnerValueFactory<Integer> vfSatiOd = new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 20);
            vfSatiOd.setValue(8);
            spSatiOd.setValueFactory(vfSatiOd);
            spSatiOd.setMinWidth(50);
            spSatiOd.setMaxWidth(50);

            Label lblOdvojiOd = new Label(":");
            lblOdvojiOd.setFont(font15);
            Spinner<Integer> spMinutiOd = new Spinner();
            SpinnerValueFactory<Integer> vfMinutiOd = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15);
            vfMinutiOd.setValue(00);
            spMinutiOd.setValueFactory(vfMinutiOd);
            spMinutiOd.setMinWidth(50);
            spMinutiOd.setMaxWidth(50);

            Label lblVremeDo = new Label("do: ");
            lblVremeDo.setFont(font15);
            Spinner<Integer> spSatiDo = new Spinner();
            SpinnerValueFactory<Integer> vfSatiDo = new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 21);
            vfSatiDo.setValue(9);
            spSatiDo.setValueFactory(vfSatiDo);
            spSatiDo.setMinWidth(50);
            spSatiDo.setMaxWidth(50);

            Label lblOdvojiDo = new Label(":");
            lblOdvojiDo.setFont(font15);
            Spinner<Integer> spMinutiDo = new Spinner();
            SpinnerValueFactory<Integer> vfMinutiDo = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15);
            vfMinutiDo.setValue(00);
            spMinutiDo.setValueFactory(vfMinutiDo);
            spMinutiDo.setMinWidth(50);
            spMinutiDo.setMaxWidth(50);

            hboxDatumVreme.getChildren().addAll(lblDatum, datumDP, lblVremeOd, spSatiOd, lblOdvojiOd, spMinutiOd, lblVremeDo, spSatiDo, lblOdvojiDo, spMinutiDo);

            HBox hboxSale = new HBox();
            hboxSale.setAlignment(Pos.TOP_LEFT);
            hboxSale.setPadding(new Insets(0, 10, 5, 0));
            hboxSale.setSpacing(12);
            Label lblSale = new Label("Dostupne sale: ");
            lblSale.setFont(font15);

            TableView<String> tableSale = new TableView<String>();
            tableSale.getColumns().clear();
            TableColumn<String, String> colNaziv = new TableColumn("Naziv");    //TODO: nazive kolona dobiti iz baze
            colNaziv.setMinWidth(200);
            TableColumn<String, String> colKapacitet = new TableColumn("Broj mesta");
            colKapacitet.setMinWidth(100);
            TableColumn<String, String> colOprema = new TableColumn("Oprema");
            colOprema.setMinWidth(200);

            tableSale.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableSale.getColumns().addAll(colNaziv, colKapacitet, colOprema);

            hboxSale.getChildren().addAll(lblSale, tableSale);

            HBox hboxRezervisi = new HBox();
            hboxRezervisi.setAlignment(Pos.BOTTOM_RIGHT);
            hboxRezervisi.setPadding(new Insets(0, 10, 5, 0));
            hboxRezervisi.setSpacing(5);

            Button btnRezervisi = new Button("Rezerviši salu");
            btnRezervisi.setFont(font15);
            btnRezervisi.setMinWidth(60);
            hboxRezervisi.getChildren().add(btnRezervisi);

            vBox.getChildren().addAll(hboxIspit, hboxDatumVreme, hboxSale, hboxRezervisi);
            root.setCenter(vBox);
        });
        Menu zakaziSaluMenu = new Menu("", lblZakaziSalu);

        //Klikom na stavku DODAJ U ZAPISNIK iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega za izabran predmet koji profesor predaje u tabeli prikazuju svi studenti koji su prijavili ispit i za koje profesor moze da unese ocenu i snimi u zapisnik
        Label lblDodajUZapisnik = new Label("DODAJ U ZAPISNIK");
        lblDodajUZapisnik.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osveziPrijave"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

            ocistiPane(root);
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(5, 10, 10, 10));

            HBox hboxPredmet = new HBox();
            hboxPredmet.setAlignment(Pos.CENTER_LEFT);
            hboxPredmet.setPadding(new Insets(0, 10, 5, 0));
            hboxPredmet.setSpacing(5);

            Label lblPredmet = new Label("Predmet: ");
            lblPredmet.setFont(font15);
            ComboBox cmbPredmet = new ComboBox();
            cmbPredmet.getItems().addAll(sviPredmeti.stream().map(Predmet::getNaziv).collect(Collectors.toList()));
            cmbPredmet.setValue(sviPredmeti.stream().map(Predmet::getNaziv).collect(Collectors.toList()).get(0));

            TableView<PrijaveIspita> tablePrijave = new TableView();
            tablePrijave.setItems(prijaveIspita.stream().filter(pi -> pi.getIdPredmeta() == sviPredmeti.stream().filter(p -> p.getNaziv().equals(cmbPredmet.getValue())).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0)).collect(Collectors.toCollection(FXCollections::observableArrayList)));

            cmbPredmet.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                if (!nova_vrednost.equals(stara_vrednost)) {
                    tablePrijave.setItems(prijaveIspita.stream().filter(pi -> pi.getIdPredmeta() == sviPredmeti.stream().filter(p -> p.getNaziv().equals(cmbPredmet.getValue())).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0)).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                }
            });
            cmbPredmet.setMinWidth(Region.USE_PREF_SIZE);
            hboxPredmet.getChildren().addAll(lblPredmet, cmbPredmet);

            HBox hboxPrijave = new HBox();
            hboxPrijave.setAlignment(Pos.TOP_LEFT);
            hboxPrijave.setPadding(new Insets(0, 10, 5, 0));
            hboxPrijave.setSpacing(100);
            Label lblPrijave = new Label("Lista studenata (sa prijavljenim ispitom): ");
            lblPrijave.setFont(font15);

            tablePrijave.setEditable(true);
            tablePrijave.setPlaceholder(new Label("Nema prijava za ispit"));
            tablePrijave.getColumns().clear();
            TableColumn colIndex = new TableColumn("Broj indeksa");
            colIndex.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PrijaveIspita, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<PrijaveIspita, String> pi) {
                    return new SimpleObjectProperty<String>(pi.getValue().getSmer() + "/" + String.valueOf(pi.getValue().getIdStudenta()) + "-" + String.valueOf(pi.getValue().getGodinaUpisa()).substring(2));
                }

            });
            colIndex.setMinWidth(100);
            TableColumn colOcena = new TableColumn("Ocena");
            colOcena.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PrijaveIspita, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<PrijaveIspita, Integer> pi) {
                    return new SimpleObjectProperty<Integer>(0);
                }

            });
            colOcena.setMinWidth(50);

            //TODO: osvezavanje na serveru i git push
            tablePrijave.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablePrijave.getColumns().addAll(colIndex, colOcena);

            hboxPrijave.getChildren().addAll(lblPrijave, tablePrijave);

            HBox hboxSnimi = new HBox();
            hboxSnimi.setAlignment(Pos.BOTTOM_RIGHT);
            hboxSnimi.setPadding(new Insets(0, 10, 5, 0));
            hboxSnimi.setSpacing(5);

            Button btnSnimi = new Button("Snimi izmene");
            btnSnimi.setFont(font15);
            btnSnimi.setMinWidth(60);
            hboxSnimi.getChildren().add(btnSnimi);

            vBox.getChildren().addAll(hboxPredmet, hboxPrijave, hboxSnimi);
            root.setCenter(vBox);

        });

        Menu dodajUZapisnikMenu = new Menu("", lblDodajUZapisnik);

        menuBar.setStyle("-fx-padding: 3 6 3 6;");

        //samo profesor ima opciju da unese ocene nakon ispita
        if (zaposleni.getPozicija().equals("profesor")) {
            menuBar.getMenus().addAll(pocetnaMenu, zakaziSaluMenu, dodajUZapisnikMenu);
        } else {
            menuBar.getMenus().addAll(pocetnaMenu, zakaziSaluMenu);
        }

    }

    /** Klasa RunnableZahtevServeru namenjena za razmenjivanje objekata sa serverom.
     * Za osvezavanje podataka na formi, zakazivanje sale i unos ocena nakon ispita.
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
                    outObj.writeObject("osvezi" + "Zaposlenog");
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
            } else if (zahtev.equals("osveziPrijave")) {
                try {
                    outObj.writeObject("osvezi" + "Zaposlenog");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    outObj.writeObject(zaposleni);
                    outObj.flush();
                    svePrijaveIspita.clear();
                    while (true) { //nisam sigurna za ovu proveru
                        odgovor = inObj.readObject();
                        if (odgovor.toString().equals("kraj")) {
                            break;
                        } else {
                            PrijaveIspita prijavaIspita = (PrijaveIspita) odgovor;
                            svePrijaveIspita.add(prijavaIspita);
                        }
                    }
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setPrijaveIspita(svePrijaveIspita);
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
            }
        }
    }
}