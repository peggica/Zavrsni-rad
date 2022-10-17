package klijent.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
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
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

/** Klasa namenjena za prikaz Forme za Zaposlene u JavaFx-u
 *  Ukoliko je tip zaposlenog profesor u Meniju je dostupna i opcija za snimanje ocena u zapisnik - DODAJ U ZAPISNIK
 *  @author Biljana Stanojevic  */

public class ZaposleniForm extends Stage {

    private static final Font font15 = new Font("Arial", 15);
    private static final Font font20 = new Font("Arial", 20);
    private Zaposleni zaposleni;
    private ObservableList<IspitniRok> sviIspitniRokovi = FXCollections.observableArrayList();
    private ObservableList<Predmet> sviPredmeti = FXCollections.observableArrayList();
    private ObservableList<Zapisnik> zapisnik = FXCollections.observableArrayList();
    private ObservableList<Sala> sveSlobodneSale = FXCollections.observableArrayList();
    private HashMap<ZakazivanjeSale, ArrayList> rasporedIspita = new HashMap<>();
    private static Alert alert = new Alert(Alert.AlertType.NONE);
    private TableView tabela;
    private int aktivniRok;

    public void setZaposleni(Zaposleni zaposleni) {
        this.zaposleni = zaposleni;
    }

    public void setSviIspitniRokovi(ObservableList<IspitniRok> sviIspitniRokovi) {
        this.sviIspitniRokovi = sviIspitniRokovi;
    }

    public void setSviPredmeti(ObservableList<Predmet> sviPredmeti) {
        this.sviPredmeti = sviPredmeti;
    }

    public void setZapisnik(ObservableList<Zapisnik> zapisnik) {
        this.zapisnik = zapisnik;
    }

    public void setSveSlobodneSale(ObservableList<Sala> sveSlobodneSale) {
        this.sveSlobodneSale = sveSlobodneSale;
    }

    public void setTabela(TableView tabela) {
        this.tabela = tabela;
    }

    public TableView getTabela() {
        return tabela;
    }

    public int getAktivniRok() {
        return aktivniRok;
    }

    public void setAktivniRok(int aktivniRok) {
        this.aktivniRok = aktivniRok;
    }

    public void setRasporedIspita(HashMap<ZakazivanjeSale, ArrayList> rasporedIspita) {
        this.rasporedIspita = rasporedIspita;
    }

    public HashMap<ZakazivanjeSale, ArrayList> getRasporedIspita() {
        return rasporedIspita;
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

        setAktivniRok(0);
        boolean aktivan = false;
        for (IspitniRok ispitniRok:this.sviIspitniRokovi) {
            if (ispitniRok.isAktivnost()) {
                aktivan = true;
                setAktivniRok(ispitniRok.getIdRoka());
                break;
            }
        }

        String poruka;
        if (aktivan) {
            poruka = "Ispitni rok je u toku.\n\n";
        } else {
            poruka = "Nijedan ispitni rok trenutno nije u toku.\n\n";
        }
        if (!getRasporedIspita().isEmpty()) {

            //TODO: srediti prikaz i sortirati po datumu i vremenu*
            poruka += "Raspored zakazanih predmeta\n";
            poruka += "Sala" + " ".repeat(24) + "Predmet" + " ".repeat(33) + "Datum" + " ".repeat(11) + "Vreme" + " ".repeat(6);
            for(Map.Entry<ZakazivanjeSale, ArrayList> entry : getRasporedIspita().entrySet()) {

                ZakazivanjeSale zakazanaSala = entry.getKey();
                ArrayList podaci = entry.getValue();
                poruka += "\n" + podaci.get(0).toString() + " ".repeat((int) Math.floor(30 - podaci.get(0).toString().split("").length * 1.58)) + podaci.get(1).toString() + " ".repeat((int) Math.floor(45 - (podaci.get(1).toString().split("").length) * 1.62)) + zakazanaSala.getDatum() + " ".repeat((int) Math.ceil((17 - zakazanaSala.getDatum().toString().split("").length) / 2)) + zakazanaSala.getVremePocetka() + " ".repeat((int) Math.ceil((17 - zakazanaSala.getVremePocetka().toString().split("").length) / 2));
            }
        } else {

            poruka += "\nJoš uvek nije napravljen raspored ispita.";
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

    public ZaposleniForm(Stage stage, Zaposleni zaposleni, ObservableList<IspitniRok> ispitniRokovi, ObservableList<Predmet> sviPredmeti, ObservableList<Sala> sveSale, ObservableList<Zapisnik> zapisnik, HashMap<ZakazivanjeSale, ArrayList> rasporedIspita) {

        super();
        initOwner(stage);
        setZaposleni(zaposleni);
        setSviIspitniRokovi(ispitniRokovi);
        setSviPredmeti(sviPredmeti);
        setSveSlobodneSale(sveSale);
        setZapisnik(zapisnik);
        setRasporedIspita(rasporedIspita);

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
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziIspitneRokove");
            ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziRasporedIspita");
            zahtevServeru.KomunikacijaSaServerom();
            zahtevServeru1.KomunikacijaSaServerom();

            ocistiPane(root);
            pocetniPrikaz(root);
        });
        Menu pocetnaMenu = new Menu("", lblPocetna);

        //Klikom na stavku ZAKAŽI SALU iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega za izabran datum i vrem prikazuje u tabeli dostupne sale za zakazivanje
        Label lblZakaziSalu = new Label("ZAKAŽI SALU");
        lblZakaziSalu.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziSlobodneSale", new ZakazivanjeSale(Date.valueOf(now()), Time.valueOf(LocalTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1)), Time.valueOf(LocalTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1).plusMinutes(15))));
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(5, 10, 10, 10));

            TableView<Sala> tableSale = new TableView<>();
            tableSale.setPlaceholder(new Label("Nije slobodna ni jedna sala za izabran datum i vreme"));
            tableSale.getColumns().clear();

            HBox hboxPredmet = new HBox();
            hboxPredmet.setAlignment(Pos.CENTER_LEFT);
            hboxPredmet.setPadding(new Insets(0, 10, 5, 0));
            hboxPredmet.setSpacing(5);

            Label lblPredmet = new Label("Predmet: ");
            lblPredmet.setFont(font15);
            ComboBox cmbPredmet = new ComboBox();
            cmbPredmet.getItems().addAll(sviPredmeti.stream().map(Predmet::getNaziv).collect(Collectors.toList()));
            cmbPredmet.setValue(sviPredmeti.stream().map(Predmet::getNaziv).collect(Collectors.toList()).get(0));
            cmbPredmet.setMinWidth(Region.USE_PREF_SIZE);
            hboxPredmet.getChildren().addAll(lblPredmet, cmbPredmet);

            HBox hboxDatumVreme = new HBox();
            hboxDatumVreme.setAlignment(Pos.CENTER_LEFT);
            hboxDatumVreme.setPadding(new Insets(0, 10, 5, 0));
            hboxDatumVreme.setSpacing(5);

            Label lblDatum = new Label("Datum: ");
            lblDatum.setFont(font15);
            DatePicker datumDP = new DatePicker();
            //zabranjen odabih dana pre današnjeg
            datumDP.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate danas = now();

                    setDisable(empty || date.compareTo(danas) < 0 );
                }
            });
            datumDP.setValue(now());
            Spinner<Integer> spSatiOd = new Spinner();
            Spinner<Integer> spMinutiOd = new Spinner();
            Spinner<Integer> spSatiDo = new Spinner();
            Spinner<Integer> spMinutiDo = new Spinner();
            datumDP.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                if (!nova_vrednost.equals(stara_vrednost)) {
                    //poslati zahtev za proveru slobodnih sala serveru
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziSlobodneSale", new ZakazivanjeSale(Date.valueOf(nova_vrednost), Time.valueOf(String.valueOf(spSatiOd.getValue()) + ":" + String.valueOf(spMinutiOd.getValue()) + ":00"), Time.valueOf(String.valueOf(spSatiDo.getValue()) + ":" + String.valueOf(spMinutiDo.getValue()) + ":00")));
                    zahtevServeru1.KomunikacijaSaServerom();

                    tableSale.refresh();
                }
            });

            Label lblVremeOd = new Label("od: ");
            lblVremeOd.setFont(font15);
            SpinnerValueFactory<Integer> vfSatiOd = new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 20);
            vfSatiOd.setValue(LocalTime.now().getHour() + 1);
            spSatiOd.setValueFactory(vfSatiOd);
            spSatiOd.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                if (!nova_vrednost.equals(stara_vrednost)) {
                    //poslati zahtev za proveru slobodnih sala serveru
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziSlobodneSale", new ZakazivanjeSale(Date.valueOf(datumDP.getValue()), Time.valueOf(String.valueOf(nova_vrednost) + ":" + String.valueOf(spMinutiOd.getValue()) + ":00"), Time.valueOf(String.valueOf(spSatiDo.getValue()) + ":" + String.valueOf(spMinutiDo.getValue()) + ":00")));
                    zahtevServeru1.KomunikacijaSaServerom();

                    tableSale.refresh();
                }
            });
            spSatiOd.setMinWidth(50);
            spSatiOd.setMaxWidth(50);

            Label lblOdvojiOd = new Label(":");
            lblOdvojiOd.setFont(font15);
            SpinnerValueFactory<Integer> vfMinutiOd = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15);
            vfMinutiOd.setValue(00);
            spMinutiOd.setValueFactory(vfMinutiOd);
            spMinutiOd.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                if (!nova_vrednost.equals(stara_vrednost)) {
                    //poslati zahtev za proveru slobodnih sala serveru
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziSlobodneSale", new ZakazivanjeSale(Date.valueOf(datumDP.getValue()), Time.valueOf(String.valueOf(spSatiOd.getValue()) + ":" + String.valueOf(nova_vrednost) + ":00"), Time.valueOf(String.valueOf(spSatiDo.getValue()) + ":" + String.valueOf(spMinutiDo.getValue()) + ":00")));
                    zahtevServeru1.KomunikacijaSaServerom();

                    tableSale.refresh();
                }
            });
            spMinutiOd.setMinWidth(50);
            spMinutiOd.setMaxWidth(50);

            Label lblVremeDo = new Label("do: ");
            lblVremeDo.setFont(font15);
            SpinnerValueFactory<Integer> vfSatiDo = new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 21);
            vfSatiDo.setValue(LocalTime.now().getHour() + 1);
            spSatiDo.setValueFactory(vfSatiDo);
            spSatiDo.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                if (!nova_vrednost.equals(stara_vrednost)) {
                    //poslati zahtev za proveru slobodnih sala serveru
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziSlobodneSale", new ZakazivanjeSale(Date.valueOf(datumDP.getValue()), Time.valueOf(String.valueOf(spSatiOd.getValue()) + ":" + String.valueOf(spMinutiOd.getValue()) + ":00"), Time.valueOf(String.valueOf(nova_vrednost) + ":" + String.valueOf(spMinutiDo.getValue()) + ":00")));
                    zahtevServeru1.KomunikacijaSaServerom();

                    tableSale.refresh();
                }
            });
            spSatiDo.setMinWidth(50);
            spSatiDo.setMaxWidth(50);

            Label lblOdvojiDo = new Label(":");
            lblOdvojiDo.setFont(font15);
            SpinnerValueFactory<Integer> vfMinutiDo = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15);
            vfMinutiDo.setValue(15);
            spMinutiDo.setValueFactory(vfMinutiDo);
            spMinutiDo.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                if (!nova_vrednost.equals(stara_vrednost)) {
                    //poslati zahtev za proveru slobodnih sala serveru
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziSlobodneSale", new ZakazivanjeSale(Date.valueOf(datumDP.getValue()), Time.valueOf(String.valueOf(spSatiOd.getValue()) + ":" + String.valueOf(spMinutiOd.getValue()) + ":00"), Time.valueOf(String.valueOf(spSatiDo.getValue()) + ":" + String.valueOf(nova_vrednost) + ":00")));
                    zahtevServeru1.KomunikacijaSaServerom();

                    tableSale.refresh();
                }
            });
            spMinutiDo.setMinWidth(50);
            spMinutiDo.setMaxWidth(50);

            hboxDatumVreme.getChildren().addAll(lblDatum, datumDP, lblVremeOd, spSatiOd, lblOdvojiOd, spMinutiOd, lblVremeDo, spSatiDo, lblOdvojiDo, spMinutiDo);

            HBox hboxSale = new HBox();
            hboxSale.setAlignment(Pos.TOP_LEFT);
            hboxSale.setPadding(new Insets(0, 10, 5, 0));
            hboxSale.setSpacing(12);
            Label lblSale = new Label("Dostupne sale: ");
            lblSale.setFont(font15);

            TableColumn<Sala, String> colNaziv = new TableColumn("Naziv");
            colNaziv.setCellValueFactory(new PropertyValueFactory<>("naziv"));
            colNaziv.setMinWidth(200);
            TableColumn<Sala, Integer> colKapacitet = new TableColumn("Broj mesta");
            colKapacitet.setCellValueFactory(new PropertyValueFactory<>("brojMesta"));
            colKapacitet.setMinWidth(100);
            TableColumn<Sala, String> colOprema = new TableColumn("Oprema");
            colOprema.setCellValueFactory(new PropertyValueFactory<>("oprema"));
            colOprema.setMinWidth(200);

            tableSale.setItems(sveSlobodneSale);
            tableSale.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableSale.getColumns().addAll(colNaziv, colKapacitet, colOprema);

            hboxSale.getChildren().addAll(lblSale, tableSale);

            HBox hboxRezervisi = new HBox();
            hboxRezervisi.setAlignment(Pos.BOTTOM_RIGHT);
            hboxRezervisi.setPadding(new Insets(0, 10, 5, 0));
            hboxRezervisi.setSpacing(5);

            Button btnRezervisi = new Button("Rezerviši salu");
            btnRezervisi.setOnMouseClicked(e -> {
                if (tableSale.getSelectionModel().getSelectedItem() != null) {
                    //ukoliko je neka sala u tabeli selektovana - poslati serveru podatke za rezervaciju, ukoliko je ta sala idalje slobodna

                    Sala izabranaSala = (Sala) tableSale.getSelectionModel().getSelectedItem();
                    setTabela(tableSale);
                    int idPredmeta = sviPredmeti.stream().filter(p -> p.getNaziv().equals(cmbPredmet.getValue())).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("zakaziSalu", idPredmeta, izabranaSala, Date.valueOf(datumDP.getValue()), Time.valueOf(String.valueOf(spSatiOd.getValue()) + ":" + String.valueOf(spMinutiOd.getValue()) + ":00"), Time.valueOf(String.valueOf(spSatiDo.getValue()) + ":" + String.valueOf(spMinutiDo.getValue()) + ":00"));
                    zahtevServeru1.KomunikacijaSaServerom();
                    tableSale.refresh();

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            setAlert(Alert.AlertType.ERROR);

                            if (tableSale.getItems().isEmpty()) {

                                alert.setContentText("Tabela sa salama za zakazivanje je prazna.");
                            } else {

                                alert.setContentText("Molim vas izaberite neku salu u tabeli.");
                            }

                            alert.showAndWait();
                        }
                    });
                }
            });
            btnRezervisi.setFont(font15);
            btnRezervisi.setMinWidth(60);
            hboxRezervisi.getChildren().add(btnRezervisi);

            vBox.getChildren().addAll(hboxPredmet, hboxDatumVreme, hboxSale, hboxRezervisi);
            root.setCenter(vBox);
        });
        Menu zakaziSaluMenu = new Menu("", lblZakaziSalu);

        //Klikom na stavku DODAJ U ZAPISNIK iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega za izabran predmet koji profesor predaje u tabeli prikazuju svi studenti koji su prijavili ispit i za koje profesor moze da unese ocenu i snimi u zapisnik
        Label lblDodajUZapisnik = new Label("DODAJ U ZAPISNIK");
        lblDodajUZapisnik.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziPrijave");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(5, 10, 10, 10));

            HBox hboxPredmet = new HBox();
            hboxPredmet.setAlignment(Pos.CENTER_LEFT);
            hboxPredmet.setPadding(new Insets(0, 10, 5, 0));
            hboxPredmet.setSpacing(5);

            Label lblIspit = new Label("Ispit: ");
            lblIspit.setFont(font15);
            ComboBox cmbIspit = new ComboBox();
            cmbIspit.getItems().addAll(sviPredmeti.stream().map(Predmet::getNaziv).collect(Collectors.toList()));
            cmbIspit.setValue(sviPredmeti.stream().map(Predmet::getNaziv).collect(Collectors.toList()).get(0));

            TableView<Zapisnik> tablePrijave = new TableView();
            tablePrijave.setItems(zapisnik.stream().filter(pi -> pi.getIdPredmeta() == sviPredmeti.stream().filter(p -> p.getNaziv().equals(cmbIspit.getValue())).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0)).collect(Collectors.toCollection(FXCollections::observableArrayList)));

            cmbIspit.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                if (!nova_vrednost.equals(stara_vrednost)) {
                    tablePrijave.setItems(zapisnik.stream().filter(pi -> pi.getIdPredmeta() == sviPredmeti.stream().filter(p -> p.getNaziv().equals(cmbIspit.getValue())).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0)).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                }
            });
            cmbIspit.setMinWidth(Region.USE_PREF_SIZE);
            hboxPredmet.getChildren().addAll(lblIspit, cmbIspit);

            HBox hboxPrijave = new HBox();
            hboxPrijave.setAlignment(Pos.TOP_LEFT);
            hboxPrijave.setPadding(new Insets(0, 10, 5, 0));
            hboxPrijave.setSpacing(100);
            Label lblPrijave = new Label("Lista studenata (sa prijavljenim ispitom): ");
            lblPrijave.setFont(font15);

            tablePrijave.setEditable(true);
            if (getAktivniRok() == 0) {
                tablePrijave.setPlaceholder(new Label("Ispitni rok nije u toku."));
            } else {
                tablePrijave.setPlaceholder(new Label("Nema prijava za ispit"));
            }
            tablePrijave.getColumns().clear();
            TableColumn colIndex = new TableColumn("Broj indeksa");
            colIndex.setStyle("-fx-alignment: center;");
            colIndex.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Zapisnik, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Zapisnik, String> z) {
                    return new SimpleObjectProperty<String>(z.getValue().getSmer() + "/" + String.valueOf(z.getValue().getIdStudenta()) + "-" + String.valueOf(z.getValue().getGodinaUpisa()).substring(2));
                }

            });
            colIndex.setMinWidth(100);
            TableColumn colOcena = new TableColumn("Ocena");
            colOcena.setStyle("-fx-alignment: center;");
            colOcena.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Zapisnik, ComboBox>, ObservableValue<ComboBox>>() {

                @Override
                public ObservableValue<ComboBox> call(TableColumn.CellDataFeatures<Zapisnik, ComboBox> z) {

                    ComboBox comboBox = new ComboBox();
                    comboBox.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
                    comboBox.setMinWidth(Region.USE_PREF_SIZE);
                    comboBox.setValue(z.getValue().getOcena());
                    comboBox.getSelectionModel().selectedItemProperty().addListener((obs, stara_vrednost, nova_vrednost) -> {

                        if(!stara_vrednost.equals(nova_vrednost)){
                            z.getValue().setOcena(Integer.valueOf(nova_vrednost.toString()));
                            Platform.runLater(() -> comboBox.setValue(z.getValue().getOcena()));
                        }
                    });

                    return new SimpleObjectProperty<ComboBox>(comboBox);

                }
            });

            colOcena.setMinWidth(50);

            tablePrijave.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablePrijave.getColumns().addAll(colIndex, colOcena);

            hboxPrijave.getChildren().addAll(lblPrijave, tablePrijave);

            HBox hboxSnimi = new HBox();
            hboxSnimi.setAlignment(Pos.BOTTOM_RIGHT);
            hboxSnimi.setPadding(new Insets(0, 10, 5, 0));
            hboxSnimi.setSpacing(5);

            Button btnSnimi = new Button("Snimi izmene");
            btnSnimi.setOnAction(e -> {
                //ukoliko ima nešto u tabeli
                if (!tablePrijave.getItems().isEmpty()) {

                    int idPredmeta = sviPredmeti.stream().filter(p -> p.getNaziv().equals(cmbIspit.getValue())).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0);
                    //ukoliko snimi i zatvori se ispitni rok posle - izmene su moguće samo nakon ponovnog aktiviranja ispitnog roka od strane studentske službe, tj jednom kad se završi i zaključa zapisnik nije moguće snimiti/izmeniti unetu ocenu
                    for (int i = 0; i < tablePrijave.getItems().stream().count(); i++) {

                        int ocena = Integer.valueOf(((ComboBox)tablePrijave.getColumns().get(1).getCellObservableValue(i).getValue()).getValue().toString());
                        zapisnik.stream().filter(pi -> pi.getIdPredmeta() == sviPredmeti.stream().filter(p -> p.getIdPredmeta() == idPredmeta).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0)).collect(Collectors.toCollection(FXCollections::observableArrayList)).get(i).setOcena(ocena);
                        zapisnik.stream().filter(pi -> pi.getIdPredmeta() == sviPredmeti.stream().filter(p -> p.getIdPredmeta() == idPredmeta).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0)).collect(Collectors.toCollection(FXCollections::observableArrayList)).get(i).setIdRoka(getAktivniRok());

                    }
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("unesiOcenuUZapisnik", idPredmeta);
                    zahtevServeru1.KomunikacijaSaServerom();

                } else {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.ERROR);

                            if (getAktivniRok() == 0) {
                                alert.setContentText("Nije moguće snimiti izmene jer ispitni rok nije u toku.");
                            } else {
                                alert.setContentText("Nije moguće snimiti izmene jer nema ni jedne prijave.");
                            }
                            alert.showAndWait();
                        }
                    });

                }
                tablePrijave.refresh();
            });
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

    /** Klasa ZahtevServeru namenjena za razmenjivanje objekata sa serverom.
     * Za osvezavanje podataka na formi, zakazivanje sale i unos ocena nakon ispita.
     * @author Biljana Stanojevic   */
    private class ZahtevServeru {

        private static final int TCP_PORT = 9000;
        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object zahtev;
        private Object odgovor;

        private Date datum;
        private Time vremePocetka;
        private Time vremeKraja;
        private int idPredmeta;
        private Sala sala;
        private ZakazivanjeSale zakazivanjeSale;

        //konstuktor za osvezavanje podataka
        public ZahtevServeru(Object zahtev) {
            this.zahtev = zahtev;
        }

        //konstruktor za osvezavanje slobodnih sala
        public ZahtevServeru(Object zahtev, ZakazivanjeSale zakazivanjeSale) {
            this.zahtev = zahtev;
            this.zakazivanjeSale = zakazivanjeSale;
        }

        //konstruktor za zakazivanje sale
        public ZahtevServeru(Object zahtev, int idPredmeta, Sala sala, Date datum, Time vremePocetka, Time vremeKraja) {
            this.zahtev = zahtev;
            this.idPredmeta = idPredmeta;
            this.sala = sala;
            this.datum = datum;
            this.vremePocetka = vremePocetka;
            this.vremeKraja = vremeKraja;
        }

        //konstruktor za snimanje ocena u zapisnik
        public ZahtevServeru(Object zahtev, int idPredmeta) {
            this.zahtev = zahtev;
            this.idPredmeta = idPredmeta;
        }

        public void KomunikacijaSaServerom() {

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
                        alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije.");
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
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije.");
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
                    zapisnik.clear();
                    while (true) { //nisam sigurna za ovu proveru
                        odgovor = inObj.readObject();
                        if (odgovor.toString().equals("kraj")) {
                            break;
                        } else {
                            Zapisnik pojedinacni = (Zapisnik) odgovor;
                            zapisnik.add(pojedinacni);
                        }
                    }
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setZapisnik(zapisnik);
                            System.out.println("Osvezeni podaci sa strane servera.");

                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije.");
                            alert.showAndWait();
                        }
                    });
                    //e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (zahtev.equals("osveziSlobodneSale")) {
                try {
                    outObj.writeObject("osvezi" + "Zaposlenog");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    outObj.writeObject(zakazivanjeSale);
                    outObj.flush();
                    sveSlobodneSale.clear();
                    while (true) {
                        odgovor = inObj.readObject();
                        if (odgovor.equals("kraj")) {
                            break;
                        }
                        Sala sala = (Sala) odgovor;
                        sveSlobodneSale.add(sala);
                    }
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setSveSlobodneSale(sveSlobodneSale);
                            System.out.println("Osvezeni podaci sa strane servera.");

                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije.");
                            alert.showAndWait();
                        }
                    });
                    //e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (zahtev.equals("osveziRasporedIspita")) {
                try {
                    outObj.writeObject("osvezi" + "Zaposlenog");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    outObj.writeObject(zaposleni);
                    outObj.flush();
                    rasporedIspita.clear();
                    odgovor = inObj.readObject();
                    rasporedIspita = (HashMap) odgovor;

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setRasporedIspita(rasporedIspita);
                            System.out.println("Osvezeni podaci sa strane servera.");

                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije.");
                            alert.showAndWait();
                        }
                    });
                    //e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (zahtev.equals("zakaziSalu")) {
                //ukoliko je pozvan konstruktor za zakazivanje sale
                try {
                    outObj.writeObject(zahtev + "Zaposleni");
                    outObj.flush();
                    ZakazivanjeSale zakazivanjeSale = new ZakazivanjeSale(sala.getIdSale(), idPredmeta, zaposleni.getIdZaposlenog(), datum, vremePocetka, vremeKraja);
                    outObj.writeObject(zakazivanjeSale);
                    outObj.flush();
                    try {
                        odgovor = inObj.readObject();
                        if (odgovor.equals("uspelo")) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    sveSlobodneSale.remove(sala);
                                    ObservableList<Sala> stavke = FXCollections.observableArrayList(sveSlobodneSale);
                                    getTabela().setItems(stavke);
                                    getTabela().refresh();
                                    setAlert(Alert.AlertType.INFORMATION);
                                    alert.setContentText("Uspešno zakazana sala u Bazi.");
                                    alert.showAndWait();
                                }
                            });
                            //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziSlobodneSale", new ZakazivanjeSale(datum, vremePocetka, vremeKraja));
                            zahtevServeru.KomunikacijaSaServerom();

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setAlert(Alert.AlertType.ERROR);
                                    alert.setContentText("Ta sala je već zauzeta za izabran datum i vreme!");
                                    alert.showAndWait();
                                }
                            });
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije.");
                            alert.showAndWait();
                        }
                    });
                    //e.printStackTrace();
                }
            } else if (zahtev.equals("unesiOcenuUZapisnik")) {
                //ukoliko je pozvan konstruktor za snimanje u zapisnik
                try {
                    outObj.writeObject(zahtev + "Zaposleni");
                    outObj.flush();
                    for (Zapisnik pojedinacni : zapisnik.stream().filter(pi -> pi.getIdPredmeta() == sviPredmeti.stream().filter(p -> p.getIdPredmeta() == idPredmeta).mapToInt(Predmet::getIdPredmeta).findFirst().orElse(0)).collect(Collectors.toCollection(FXCollections::observableArrayList))) {
                        outObj.writeObject(pojedinacni);
                    }
                    outObj.writeObject("kraj");
                    outObj.flush();
                    try {
                        odgovor = inObj.readObject();
                        if (odgovor.equals("uspelo")) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setAlert(Alert.AlertType.INFORMATION);
                                    alert.setContentText("Uspešno snimljene ocene u Zapisnik.");
                                    alert.showAndWait();
                                }
                            });
                            //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziPrijave");
                            zahtevServeru.KomunikacijaSaServerom();

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setAlert(Alert.AlertType.ERROR);
                                    alert.setContentText("Nije uspelo snimanje ocena u zapisnik!");
                                    alert.showAndWait();
                                }
                            });
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Server je trenutno nedostupan!\nMolimo vas pokušajte kasnije.");
                            alert.showAndWait();
                        }
                    });
                    //e.printStackTrace();
                }
            }
        }
    }
}