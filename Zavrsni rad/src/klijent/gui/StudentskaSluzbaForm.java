package klijent.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import model.*;

import java.io.*;
import java.net.*;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/** Klasa namenjena za prikaz Forme za korisnike Studentske Sluzbe u JavaFx-u
 *  @author Biljana Stanojevic  */

public class StudentskaSluzbaForm extends Stage {

    private static Stage stage;
    private static final Font font15 = new Font("Arial", 15);
    private static final Font font20 = new Font("Arial", 20);
    private ObservableList<IspitniRok> sviIspitniRokovi = FXCollections.observableArrayList();
    private ObservableList<Student> sviStudenti = FXCollections.observableArrayList();
    private ObservableList<Zaposleni> sviZaposleni = FXCollections.observableArrayList();
    private HashMap<Predmet, Zaposleni> sviPredmeti = new HashMap<>();
    private ObservableList<Sala> sveSale = FXCollections.observableArrayList();
    private HashMap<ZakazivanjeSale, ArrayList<String>> sveZakazaneSale = new HashMap<>();
    private HashMap<ZakazivanjeSale, ArrayList<ArrayList>> rasporedIspita = new HashMap<>();
    Pattern patternEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    Pattern patternTelefon = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
    private static Alert alert = new Alert(Alert.AlertType.NONE);
    private TableView tabela;

    public static Stage getStage() {
        return stage;
    }

    public void setSviIspitniRokovi(ObservableList<IspitniRok> sviIspitniRokovi) {
        this.sviIspitniRokovi = sviIspitniRokovi;
    }

    public void setSviStudenti(ObservableList<Student> sviStudenti) {
        this.sviStudenti = sviStudenti;
    }

    public void setSviZaposleni(ObservableList<Zaposleni> sviZaposleni) {
        this.sviZaposleni = sviZaposleni;
    }

    public void setSviPredmeti(HashMap<Predmet, Zaposleni> sviPredmeti) {
        this.sviPredmeti = sviPredmeti;
    }

    public void setSveSale(ObservableList<Sala> sveSale) {
        this.sveSale = sveSale;
    }

    public void setSveZakazaneSale(HashMap<ZakazivanjeSale, ArrayList<String>> sveZakazaneSale) {
        this.sveZakazaneSale = sveZakazaneSale;
    }

    public void setRasporedIspita(HashMap<ZakazivanjeSale, ArrayList<ArrayList>> rasporedIspita) {
        this.rasporedIspita = rasporedIspita;
    }

    public void setTabela(TableView tabela) {
        this.tabela = tabela;
    }

    public TableView getTabela() {
        return tabela;
    }

    /**
     * Setuje tip i naslov alerta u zavisnosti od prosleđenog tipa
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

    /**
     * Proverava da li ima aktivnih ispitnih rokova ili ne, pa postavlja prikaz za stavku Pocetna iz Menija
     */
    private static void pocetniPrikaz(BorderPane root, List<IspitniRok> sviIspitniRokovi) {

        boolean aktivanRok = false;
        boolean aktivnaPrijava = false;
        for (IspitniRok ispitniRok : sviIspitniRokovi) {

            if (ispitniRok.isAktivnost()) {
                aktivanRok = true;
            } else if (ispitniRok.isAktivnaPrijava()) {
                aktivnaPrijava = true;
            }
        }

        String poruka;
        if (aktivanRok) {
            poruka = "Ispitni rok je u toku.";
        } else if (aktivnaPrijava) {
            poruka = "Prijava ispita je u toku.";
        } else {
            poruka = "Nijedan ispitni rok, kao ni prijava ispita, nije trenutno u toku.";
        }
        Label lblPrikaz = new Label(poruka);
        lblPrikaz.setFont(font20);
        lblPrikaz.setPadding(new Insets(5, 10, 5, 10));
        root.setLeft(lblPrikaz);

    }

    /**
     * Cisti sve strane Border Pane-a, pre prebacivanja na sledeci prikaz iz Menija
     */
    private static void ocistiPane(BorderPane root) {

        root.setLeft(null);
        root.setRight(null);
        root.setBottom(null);
        root.setCenter(null);
    }

    public StudentskaSluzbaForm(Stage stage, ObservableList<IspitniRok> ispitniRokovi, ObservableList<Student> sviStudenti, ObservableList<Zaposleni> sviZaposleni, HashMap<Predmet, Zaposleni> predmeti, ObservableList<Sala> sveSale, HashMap<ZakazivanjeSale, ArrayList<String>> zakazaneSale, HashMap<ZakazivanjeSale, ArrayList<ArrayList>> ispitiRaspored) {

        super();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        initOwner(stage);

        setSviIspitniRokovi(ispitniRokovi);
        setSviStudenti(sviStudenti);
        setSviZaposleni(sviZaposleni);
        setSviPredmeti(predmeti);
        setSveSale(sveSale);
        setSveZakazaneSale(zakazaneSale);
        setRasporedIspita(ispitiRaspored);

        BorderPane root = new BorderPane();
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        root.setTop(menuBar);
        pocetniPrikaz(root, sviIspitniRokovi);

        Scene scene = new Scene(root, 1200, 600);
        setScene(scene);
        setResizable(false);
        setTitle("Studentska služba");

        //Klikom na stavku POČETNA iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a i poziva prikaz za pocetnu stranu
        Label lblPocetna = new Label("POČETNA");
        lblPocetna.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziIspitneRokove");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);
            pocetniPrikaz(root, sviIspitniRokovi);
        });
        Menu pocetnaMenu = new Menu("", lblPocetna);

        //Klikom na stavku STUDENTI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za studente, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi student u Bazu podataka
        Label lblStudenti = new Label("STUDENTI");
        lblStudenti.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziStudente");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5, 10, 10, 10));

            HBox hBoxPretraga = new HBox();
            hBoxPretraga.setAlignment(Pos.CENTER_LEFT);
            hBoxPretraga.setPadding(new Insets(0, 10, 5, 0));
            hBoxPretraga.setSpacing(5);

            Label lblPretraga = new Label("Studenti: ");
            lblPretraga.setFont(font15);
            TextField txtPretraga = new TextField();

            TableView<Student> tableStudenti = new TableView<>();

            Button btnPretrazi = new Button("pretraži");
            btnPretrazi.setFont(font15);
            btnPretrazi.setOnMouseClicked(event -> {
                String trazeno = txtPretraga.getText().toLowerCase();
                ObservableList<Student> nadjeni = (sviStudenti.stream().filter(s -> s.getIme().toLowerCase().contains(trazeno) || s.getPrezime().toLowerCase().contains(trazeno) || String.valueOf(s.getBrojIndeksa()).contains(trazeno) || (s.getAdresa() != null && s.getAdresa().toLowerCase().contains(trazeno)) || s.getFinansiranje().toLowerCase().contains(trazeno) || (s.getBrojTelefona() != null && s.getBrojTelefona().toLowerCase().contains(trazeno)) || (s.getEmail() != null && s.getEmail().toLowerCase().contains(trazeno)))).collect(Collectors.toCollection(FXCollections::observableArrayList));
                tableStudenti.setItems(nadjeni);
            });
            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            tableStudenti.setEditable(true);
            tableStudenti.setPlaceholder(new Label("Ne postoji ni jedan student u Bazi Podataka"));
            tableStudenti.getColumns().clear();

            TableColumn colIme = new TableColumn("Ime");
            colIme.setCellValueFactory(new PropertyValueFactory<Student, String>("ime"));
            colIme.setCellFactory(TextFieldTableCell.forTableColumn());
            colIme.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Student, String> tabela) {
                            String staroIme = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getIme();
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setIme(tabela.getNewValue());
                            //UKOLIKO JE NOVO IME RAZLICITO OD PRVOBITNOG
                            if (!staroIme.equals(tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getIme())) {
                                //AKO JE UNETO IME I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                                if (!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getIme().equals("")) {

                                    Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniStudent);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite ime za studenta.");
                                            alert.showAndWait();
                                        }
                                    });
                                }
                            }
                        }
                    }
            );
            colIme.setMinWidth(100);
            TableColumn colPrezime = new TableColumn("Prezime");
            colPrezime.setCellValueFactory(new PropertyValueFactory<Student, String>("prezime"));
            colPrezime.setCellFactory(TextFieldTableCell.forTableColumn());
            colPrezime.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Student, String> tabela) {
                            String staroPrezime = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getPrezime();
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setPrezime(tabela.getNewValue());
                            //UKOLIKO JE NOVO PREZIME RAZLICITO OD PRVOBITNOG
                            if (!staroPrezime.equals(tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getPrezime())) {
                                //AKO JE UNETO PREZIME I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                                if (!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getPrezime().equals("")) {

                                    Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1  = new ZahtevServeru("izmeni", izabraniStudent);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite prezime za studenta.");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziStudente");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            }
                        }
                    }
            );
            colPrezime.setMinWidth(150);
            TableColumn colIndex = new TableColumn("Broj indeksa");
            colIndex.setCellValueFactory(new PropertyValueFactory<Student, String>("brojIndeksa"));
            colIndex.setStyle("-fx-alignment: center;");
            colIndex.setMinWidth(50);
            TableColumn<Student, ComboBox> colFinansiranje = new TableColumn("Finansiranje");
            colFinansiranje.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, ComboBox>, ObservableValue<ComboBox>>() {

                @Override
                public ObservableValue<ComboBox> call(TableColumn.CellDataFeatures<Student, ComboBox> arg0) {
                    Student mp = arg0.getValue();

                    ComboBox comboBox = new ComboBox();
                    comboBox.getItems().addAll(Student.tipFinansiranja.values());
                    comboBox.setMinWidth(Region.USE_PREF_SIZE);
                    comboBox.setValue((mp.getFinansiranje()));

                    comboBox.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                        //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                        if (!nova_vrednost.toString().equals(stara_vrednost.toString())) {
                            mp.setFinansiranje(Student.tipFinansiranja.valueOf(nova_vrednost.toString()));
                            Student izabraniStudent = arg0.getValue();
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniStudent);
                            zahtevServeru1.KomunikacijaSaServerom();
                        }
                    });

                    return new SimpleObjectProperty<ComboBox>(comboBox);

                }
            });
            colFinansiranje.setMinWidth(50);
            TableColumn colAdresa = new TableColumn("Adresa");
            colAdresa.setCellValueFactory(new PropertyValueFactory<Student, String>("adresa"));
            colAdresa.setCellFactory(TextFieldTableCell.forTableColumn());
            colAdresa.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Student, String> tabela) {
                            String staraAdresa = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getAdresa();
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setAdresa(tabela.getNewValue());
                            //UKOLIKO JE NOVA ADRESA RAZLICITA OD PRVOBITNE
                            if ((staraAdresa != null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getAdresa().equals(staraAdresa)) || (staraAdresa == null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getAdresa().equals(""))) {
                                Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniStudent);
                                zahtevServeru1.KomunikacijaSaServerom();
                            }
                        }
                    }
            );
            colAdresa.setMinWidth(150);
            TableColumn colEmail = new TableColumn("E-mail");
            colEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("email"));
            colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
            colEmail.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Student, String> tabela) {
                            String stariEmail = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail();
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setEmail(tabela.getNewValue());

                            String mail = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail();
                            Matcher emailMatcher = patternEmail.matcher(mail);
                            boolean validniEmail = emailMatcher.find();

                            //UKOLIKO JE NOVI EMAIL RAZLICIT OD PRVOBITNOG
                            if ((stariEmail != null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail().equals(stariEmail)) || (stariEmail == null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail().equals(""))) {
                                //AKO JE EMAIL OBRISAN ILI JE UNET ISPRAVNO
                                if (mail.length() == 0 || validniEmail) {
                                    Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniStudent);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite email ispravno.");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziStudente");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            }
                        }
                    }
            );
            colEmail.setMinWidth(150);
            TableColumn colTelefon = new TableColumn("Broj telefona");
            colTelefon.setCellValueFactory(new PropertyValueFactory<Student, String>("brojTelefona"));
            colTelefon.setCellFactory(TextFieldTableCell.forTableColumn());
            colTelefon.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Student, String> tabela) {
                            String stariTelefon = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona();
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setBrojTelefona(tabela.getNewValue());

                            String telefon = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona();
                            Matcher telefonMatcher = patternTelefon.matcher(telefon);
                            boolean validniTelefon = telefonMatcher.find();

                            //UKOLIKO JE NOVI TELEFON RAZLICIT OD PRVOBITNOG
                            if ((stariTelefon != null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona().equals(stariTelefon)) || (stariTelefon == null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona().equals(""))) {
                                //AKO JE TELEFON OBRISAN ILI JE UNET ISPRAVNO
                                if (telefon.length() == 0 || validniTelefon) {
                                    Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniStudent);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite broj telefona ispravno.");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziStudente");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            }
                        }
                    }
            );
            colTelefon.setMinWidth(100);
            TableColumn colAktivan = new TableColumn("Vidljiv");
            colAktivan.setCellValueFactory(new PropertyValueFactory<Student, Boolean>("vidljiv"));
            colAktivan.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, CheckBox>, ObservableValue<CheckBox>>() {

                @Override
                public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Student, CheckBox> arg0) {
                    Student mp = arg0.getValue();

                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(mp.isVidljiv());

                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        public void changed(ObservableValue<? extends Boolean> ov,
                                            Boolean stara_vrednost, Boolean nova_vrednost) {

                            mp.setVidljiv(nova_vrednost);
                            Student izabraniStudent = arg0.getValue();
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniStudent);
                            zahtevServeru1.KomunikacijaSaServerom();

                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }
            });
            colAktivan.setMinWidth(50);
            colAktivan.setStyle("-fx-alignment: center;");

            tableStudenti.setItems(sviStudenti);
            //sredjuje problem za dodatu kolonu
            tableStudenti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableStudenti.setPrefHeight(500);
            //TODO: sortiranje treba svaki put kad je osvezeno
            colIndex.setSortType(TableColumn.SortType.DESCENDING);
            tableStudenti.getColumns().addAll(colIme, colPrezime, colIndex, colFinansiranje, colAdresa, colEmail, colTelefon, colAktivan);
            tableStudenti.getSortOrder().add(colIndex);
            tableStudenti.sort();

            //ukoliko je pritisnut desni klik miša, prikaz login podataka za odgovarajućeg studenta
            tableStudenti.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent me) {
                    if(me.getButton() == MouseButton.SECONDARY) {

                        Student izabraniStudent = tableStudenti.getFocusModel().getFocusedItem();
                        ZahtevServeru zahtevServeru1 = new ZahtevServeru("loginInfo", izabraniStudent);
                        zahtevServeru1.KomunikacijaSaServerom();
                    }
                }
            });

            TextField txtIme = new TextField("");
            txtIme.setPromptText("Ime");
            txtIme.setMinWidth(100);

            TextField txtPrezime = new TextField();
            txtPrezime.setPromptText("Prezime");
            txtPrezime.setMinWidth(175);

            ComboBox cmbSmer = new ComboBox();
            cmbSmer.getItems().addAll(Student.tipSmera.values());
            cmbSmer.setValue(Student.tipSmera.avt);
            cmbSmer.setMinWidth(Region.USE_PREF_SIZE);
            cmbSmer.setStyle("-fx-font: 12px \"Arial\";");

            Label lblFinansiranje = new Label("Finansiranje: ");
            lblFinansiranje.setMinWidth(Region.USE_PREF_SIZE);
            ComboBox cmbFinansiranje = new ComboBox();
            cmbFinansiranje.getItems().addAll(Student.tipFinansiranja.values());
            cmbFinansiranje.setValue(Student.tipFinansiranja.budzet);
            cmbFinansiranje.setMinWidth(Region.USE_PREF_SIZE);
            cmbFinansiranje.setStyle("-fx-font: 12px \"Arial\";");

            TextField txtAdresa = new TextField();
            txtAdresa.setPromptText("Adresa");
            txtAdresa.setMinWidth(175);

            TextField txtMail = new TextField();
            txtMail.setPromptText("E-mail");
            txtMail.setMinWidth(175);

            TextField txtTelefon = new TextField();
            txtTelefon.setPromptText("Broj telefona");
            txtTelefon.setMinWidth(100);

            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setMinWidth(60);
            btnDodaj.setOnMouseClicked(e -> {

                String ime = txtIme.getText();
                String prezime = txtPrezime.getText();
                String smer = cmbSmer.getValue().toString();
                String finansiranje = cmbFinansiranje.getValue().toString();
                String adresa = txtAdresa.getText();
                String email = txtMail.getText();
                String brojTelefona = txtTelefon.getText();
                int godinaUpisa = Calendar.getInstance().get(Calendar.YEAR);

                Matcher emailMatcher = patternEmail.matcher(email);
                boolean validniEmail = emailMatcher.find();

                Matcher telefonMatcher = patternTelefon.matcher(brojTelefona);
                boolean validniTelefon = telefonMatcher.find();

                //AKO SU UNETI SAMO IME I PREZIME ILI AKO SU UNETI SVI ILI EMAIL ILI TELEFON ALI ISPRAVNO
                if ((ime.length() != 0 && prezime.length() != 0 && email.length() == 0 && brojTelefona.length() == 0) || (ime.length() != 0 && prezime.length() != 0 && validniEmail && validniTelefon) || (ime.length() != 0 && prezime.length() != 0 && validniEmail && brojTelefona.length() == 0) || (ime.length() != 0 && prezime.length() != 0 && email.length() == 0 && validniTelefon)) {
                //da doda u bazu, vrati normalnu boju polja i obrise vrednosti
                    setTabela(tableStudenti);
                    Student student = new Student(Student.idNovogStudenta(sviStudenti, smer, godinaUpisa), godinaUpisa, Student.tipSmera.valueOf(smer), ime, prezime, Student.tipFinansiranja.valueOf(finansiranje), adresa, email, brojTelefona, true);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("dodaj", student, txtIme, txtPrezime, txtAdresa, txtMail, txtTelefon, cmbSmer, cmbFinansiranje);
                    zahtevServeru1.KomunikacijaSaServerom();

                } else {
                    //PROVERA KOJI NIJE UNET ISPRAVNO
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            setAlert(Alert.AlertType.ERROR);

                            if (ime.length() == 0 && prezime.length() == 0) {

                                alert.setContentText("Obavezno je popuniti ime i prezime za studenta.");
                            } else if (ime.length() == 0 && prezime.length() != 0){

                                alert.setContentText("Obavezno je popuniti ime za studenta.");
                            } else if (prezime.length() == 0 && ime.length() != 0) {

                                alert.setContentText("Obavezno je popuniti prezime za studenta.");
                            } else if (email.length() != 0) {

                                if (!validniEmail) {
                                    alert.setContentText("Neispravan unos za e-mail!");
                                }
                            } else if (brojTelefona.length() != 0) {

                                if (!validniTelefon) {
                                    alert.setContentText("Neispravan unos za broj telefona!");
                                }
                            }
                            alert.showAndWait();
                        }
                    });
                }
            });

            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);
            btnObrisi.setOnAction(e -> {

                if (tableStudenti.getSelectionModel().getSelectedItem() != null) {
                    Student izabraniStudent = tableStudenti.getSelectionModel().getSelectedItem();
                    setTabela(tableStudenti);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("obrisi", izabraniStudent);
                    zahtevServeru1.KomunikacijaSaServerom();

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.ERROR);
                            alert.setContentText("Molim vas izaberite studenta u tabeli.");
                            alert.showAndWait();
                        }
                    });
                }
            });

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5, 0, 0, 0));
            hboxAkcija.setAlignment(Pos.CENTER);
            hboxAkcija.getChildren().addAll(txtIme, txtPrezime, cmbSmer, lblFinansiranje, cmbFinansiranje, txtAdresa, txtMail, txtTelefon, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(hBoxPretraga, tableStudenti, hboxAkcija);
            root.setCenter(vbox);
        });
        Menu studentiMenu = new Menu("", lblStudenti);

        //Klikom na stavku ZAPOSLENI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za zaposlene, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi zaposleni u Bazu podataka, ili pogledati detaljnije informacije klikom na odredjenog zaposlenog u tabeli
        Label lblZaposleni = new Label("ZAPOSLENI");
        lblZaposleni.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziZaposlene");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5, 10, 10, 10));

            HBox hBoxPretraga = new HBox();
            hBoxPretraga.setAlignment(Pos.CENTER_LEFT);
            hBoxPretraga.setPadding(new Insets(0, 10, 5, 0));
            hBoxPretraga.setSpacing(5);

            Label lblPretraga = new Label("Zaposleni: ");
            lblPretraga.setFont(font15);
            TextField txtPretraga = new TextField();

            TableView<Zaposleni> tableZaposleni = new TableView<>();

            Button btnPretrazi = new Button("pretraži");
            btnPretrazi.setFont(font15);
            btnPretrazi.setOnMouseClicked(event -> {
                String trazeno = txtPretraga.getText().toLowerCase();
                ObservableList<Zaposleni> nadjeni = (sviZaposleni.stream().filter(z -> z.getIme().toLowerCase().contains(trazeno) || z.getPrezime().toLowerCase().contains(trazeno) || z.getPozicija().contains(trazeno) || (z.getAdresa() != null && z.getAdresa().toLowerCase().contains(trazeno)) || (z.getBrojTelefona() != null && z.getBrojTelefona().toLowerCase().contains(trazeno)) || (z.getEmail() != null && z.getEmail().toLowerCase().contains(trazeno)))).collect(Collectors.toCollection(FXCollections::observableArrayList));
                tableZaposleni.setItems(nadjeni);
            });
            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            tableZaposleni.setEditable(true);
            tableZaposleni.setPlaceholder(new Label("Ne postoji ni jedan zaposleni u Bazi Podataka"));
            tableZaposleni.getColumns().clear();

            TableColumn colPozicija = new TableColumn("Pozicija");
            colPozicija.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("pozicija"));
            colPozicija.setMinWidth(100);
            TableColumn colIme = new TableColumn("Ime");
            colIme.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("ime"));
            colIme.setCellFactory(TextFieldTableCell.forTableColumn());
            colIme.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Zaposleni, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Zaposleni, String> tabela) {
                            String staroIme = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getIme();
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setIme(tabela.getNewValue());
                            //UKOLIKO JE NOVO IME RAZLICITO OD PRVOBITNOG
                            if (!staroIme.equals(tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getIme())) {
                                //AKO JE UNETO IME I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                                if (!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getIme().equals("")) {

                                    Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniZaposleni);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite ime za zaposlenog.");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziZaposlene");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            }
                        }
                    }
            );
            colIme.setMinWidth(125);
            TableColumn colPrezime = new TableColumn("Prezime");
            colPrezime.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("prezime"));
            colPrezime.setCellFactory(TextFieldTableCell.forTableColumn());
            colPrezime.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Zaposleni, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Zaposleni, String> tabela) {
                            String staroPrezime = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getPrezime();
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setPrezime(tabela.getNewValue());
                            //UKOLIKO JE NOVO PREZIME RAZLICITO OD PRVOBITNOG
                            if (!staroPrezime.equals(tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getPrezime())) {
                                //AKO JE UNETO PREZIME I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                                if (!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getPrezime().equals("")) {

                                    Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniZaposleni);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite prezime za zaposlenog.");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziZaposlene");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            }
                        }
                    }
            );
            colPrezime.setMinWidth(225);
            TableColumn colAdresa = new TableColumn("Adresa");
            colAdresa.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("adresa"));
            colAdresa.setCellFactory(TextFieldTableCell.forTableColumn());
            colAdresa.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Zaposleni, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Zaposleni, String> tabela) {
                            String staraAdresa =tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getAdresa();
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setAdresa(tabela.getNewValue());
                            //UKOLIKO JE NOVA ADRESA RAZLICITA OD PRVOBITNE
                            if ((staraAdresa != null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getAdresa().equals(staraAdresa)) || (staraAdresa == null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getAdresa().equals(""))) {
                                if (!staraAdresa.equals(tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getAdresa())) {
                                    Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniZaposleni);
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            }
                        }
                    }
            );
            colAdresa.setMinWidth(150);
            TableColumn colEmail = new TableColumn("E-mail");
            colEmail.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("email"));
            colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
            colEmail.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Zaposleni, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Zaposleni, String> tabela) {
                            String stariEmail = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail();
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setEmail(tabela.getNewValue());

                            String mail = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail();
                            Matcher emailMatcher = patternEmail.matcher(mail);
                            boolean validniEmail = emailMatcher.find();

                            //UKOLIKO JE NOVI EMAIL RAZLICIT OD PRVOBITNOG
                            if ((stariEmail != null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail().equals(stariEmail)) || (stariEmail == null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail().equals(""))) {
                                //AKO JE EMAIL OBRISAN ILI JE UNET ISPRAVNO
                                if (mail.length() == 0 || validniEmail) {
                                    Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniZaposleni);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite email ispravno.");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziZaposlene");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            }
                        }
                    }
            );
            colEmail.setMinWidth(250);
            TableColumn colTelefon = new TableColumn("Broj telefona");
            colTelefon.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("brojTelefona"));
            colTelefon.setCellFactory(TextFieldTableCell.forTableColumn());
            colTelefon.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Zaposleni, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Zaposleni, String> tabela) {
                            String stariTelefon = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona();
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setBrojTelefona(tabela.getNewValue());

                            String telefon = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona();
                            Matcher telefonMatcher = patternTelefon.matcher(telefon);
                            boolean validniTelefon = telefonMatcher.find();

                            //UKOLIKO JE NOVI TELEFON RAZLICIT OD PRVOBITNOG
                            if ((stariTelefon != null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona().equals(stariTelefon)) || (stariTelefon == null && !tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona().equals(""))) {
                                //AKO JE TELEFON OBRISAN ILI JE UNET ISPRAVNO
                                if (telefon.length() == 0 || validniTelefon) {
                                    Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniZaposleni);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite broj telefona ispravno.");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziZaposlene");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            }
                        }
                    }
            );
            colTelefon.setMinWidth(150);
            TableColumn colAktivan = new TableColumn("Vidljiv");
            colAktivan.setCellValueFactory(new PropertyValueFactory<Zaposleni, Boolean>("vidljiv"));
            colAktivan.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Zaposleni, CheckBox>, ObservableValue<CheckBox>>() {

                @Override
                public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Zaposleni, CheckBox> arg0) {
                    Zaposleni mp = arg0.getValue();

                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(mp.isVidljiv());

                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        public void changed(ObservableValue<? extends Boolean> ov,
                                            Boolean stara_vrednost, Boolean nova_vrednost) {

                            mp.setVidljiv(nova_vrednost);
                            Zaposleni izabraniZaposleni = arg0.getValue();
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniZaposleni);
                            zahtevServeru1.KomunikacijaSaServerom();

                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }
            });
            colAktivan.setMinWidth(50);
            colAktivan.setStyle("-fx-alignment: center;");

            tableZaposleni.setItems(sviZaposleni);
            //sredjuje problem za dodatu kolonu
            tableZaposleni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableZaposleni.setPrefHeight(500);
            tableZaposleni.getColumns().addAll(colPozicija, colIme, colPrezime, colAdresa, colEmail, colTelefon, colAktivan);
            //ukoliko je pritisnut desni klik miša, prikaz login podataka za odgovarajućeg zaposlenog
            tableZaposleni.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent me) {
                    if(me.getButton() == MouseButton.SECONDARY) {

                        Zaposleni izabraniZaposleni = tableZaposleni.getFocusModel().getFocusedItem();
                        ZahtevServeru zahtevServeru1 = new ZahtevServeru("loginInfo", izabraniZaposleni);
                        zahtevServeru1.KomunikacijaSaServerom();
                    }
                }
            });

            Label lblPozicija = new Label("Pozicija: ");
            lblPozicija.setMinWidth(Region.USE_PREF_SIZE);
            ComboBox cmbPozicija = new ComboBox();
            cmbPozicija.getItems().addAll(Zaposleni.tipZaposlenog.values());
            cmbPozicija.setValue(Zaposleni.tipZaposlenog.profesor);
            cmbPozicija.setMinWidth(Region.USE_PREF_SIZE);
            cmbPozicija.setStyle("-fx-font: 12px \"Arial\";");

            TextField txtIme = new TextField();
            txtIme.setPromptText("Ime");
            txtIme.setMinWidth(150);

            TextField txtPrezime = new TextField();
            txtPrezime.setPromptText("Prezime");
            txtPrezime.setMinWidth(200);

            TextField txtAdresa = new TextField();
            txtAdresa.setPromptText("Adresa");
            txtAdresa.setMinWidth(180);

            TextField txtMail = new TextField();
            txtMail.setPromptText("E-mail");
            txtMail.setMinWidth(200);

            TextField txtTelefon = new TextField();
            txtTelefon.setPromptText("Broj telefona");
            txtTelefon.setMinWidth(150);

            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setMinWidth(60);
            btnDodaj.setOnMouseClicked(e -> {

                String pozicija = cmbPozicija.getValue().toString();
                String ime = txtIme.getText();
                String prezime = txtPrezime.getText();
                String adresa = txtAdresa.getText();
                String email = txtMail.getText();
                String brojTelefona = txtTelefon.getText();

                Matcher emailMatcher = patternEmail.matcher(email);
                boolean validniEmail = emailMatcher.find();

                Pattern patternTelefon = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
                Matcher telefonMatcher = patternTelefon.matcher(brojTelefona);
                boolean validniTelefon = telefonMatcher.find();

                //AKO SU UNETI SAMO IME I PREZIME ILI AKO SU UNETI SVI ILI EMAIL ILI TELEFON ALI ISPRAVNO
                if ((ime.length() != 0 && prezime.length() != 0 && email.length() == 0 && brojTelefona.length() == 0) || (ime.length() != 0 && prezime.length() != 0 && validniEmail && validniTelefon) || (ime.length() != 0 && prezime.length() != 0 && validniEmail && brojTelefona.length() == 0) || (ime.length() != 0 && prezime.length() != 0 && email.length() == 0 && validniTelefon)) {
                    //da doda u bazu, vrati normalnu boju polja i obrise vrednosti
                    Zaposleni zaposleni = new Zaposleni(Zaposleni.idNovogZaposlenog(sviZaposleni, pozicija), Zaposleni.tipZaposlenog.valueOf(pozicija), ime, prezime, adresa, email, brojTelefona, true);
                    setTabela(tableZaposleni);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("dodaj", zaposleni, cmbPozicija, txtIme, txtPrezime, txtMail, txtTelefon, txtAdresa);
                    zahtevServeru1.KomunikacijaSaServerom();
                } else {
                    //PROVERA KOJI NIJE UNET ISPRAVNO
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            setAlert(Alert.AlertType.ERROR);

                            if (ime.length() == 0 && prezime.length() == 0) {

                                alert.setContentText("Obavezno je popuniti ime i prezime za zaposlenog.");
                            } else if (ime.length() == 0 && prezime.length() != 0){

                                alert.setContentText("Obavezno je popuniti ime za zaposlenog.");
                            } else if (prezime.length() == 0 && ime.length() != 0) {

                                alert.setContentText("Obavezno je popuniti prezime za zaposlenog.");
                            } else if (email.length() != 0) {

                                if (!validniEmail) {
                                    alert.setContentText("Neispravan unos za e-mail!");
                                }
                            } else if (brojTelefona.length() != 0) {

                                if (!validniTelefon) {
                                    alert.setContentText("Neispravan unos za broj telefona!");
                                }
                            }
                            alert.showAndWait();
                        }
                    });
                }
            });
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);
            btnObrisi.setOnAction(e -> {

                if (tableZaposleni.getSelectionModel().getSelectedItem() != null) {
                    setTabela(tableZaposleni);
                    Zaposleni izabraniZaposleni = tableZaposleni.getSelectionModel().getSelectedItem();
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("obrisi", izabraniZaposleni);
                    zahtevServeru1.KomunikacijaSaServerom();

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.ERROR);
                            alert.setContentText("Molim vas izaberite zaposlenog u tabeli.");
                            alert.showAndWait();
                        }
                    });
                }
            });

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5, 0, 0, 0));
            hboxAkcija.setAlignment(Pos.CENTER);
            hboxAkcija.getChildren().addAll(lblPozicija, cmbPozicija, txtIme, txtPrezime, txtAdresa, txtMail, txtTelefon, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(hBoxPretraga, tableZaposleni, hboxAkcija);
            root.setCenter(vbox);
        });
        Menu zaposleniMenu = new Menu("", lblZaposleni);

        //Klikom na stavku PREDMETI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za predmete, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi predmet u Bazu podataka
        Label lblPredmeti = new Label("PREDMETI");
        lblPredmeti.setOnMouseClicked(mouseEvent -> {

            TableView<HashMap.Entry<Predmet, Zaposleni>> tablePredmeti = new TableView<>();
            setTabela(tablePredmeti);

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziPredmete");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5, 10, 10, 10));

            HBox hBoxPretraga = new HBox();
            hBoxPretraga.setAlignment(Pos.CENTER_LEFT);
            hBoxPretraga.setPadding(new Insets(0, 10, 5, 0));
            hBoxPretraga.setSpacing(5);

            Label lblPretraga = new Label("Predmeti: ");
            lblPretraga.setFont(font15);

            TextField txtPretraga = new TextField();

            Button btnPretrazi = new Button("pretraži");
            btnPretrazi.setFont(font15);
            btnPretrazi.setOnMouseClicked(event -> {
                String trazeno = txtPretraga.getText().toLowerCase();
                ObservableList<HashMap.Entry<Predmet, Zaposleni>> nadjeni = FXCollections.observableArrayList(sviPredmeti.entrySet().stream().filter(map -> String.valueOf(map.getKey().getIdPredmeta()).contains(trazeno) || map.getKey().getNaziv().toLowerCase().contains(trazeno) || (map.getKey().getStudijskiSmer() != null && map.getKey().getStudijskiSmer().toLowerCase().contains(trazeno)) || (String.valueOf(map.getKey().getSemestar()) != null && String.valueOf(map.getKey().getSemestar()).contains(trazeno)) || (String.valueOf(map.getKey().getEspb()) != null && String.valueOf(map.getKey().getEspb()).contains(trazeno)) || (map.getValue().getIme() + " " + map.getValue().getPrezime()).toLowerCase().contains(trazeno)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue())).entrySet());
                tablePredmeti.setItems(nadjeni);
            });

            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            tablePredmeti.setEditable(true);
            tablePredmeti.setPlaceholder(new Label("Ne postoji ni jedan predmet u Bazi Podataka"));
            tablePredmeti.getColumns().clear();

            TableColumn<Map.Entry<Predmet, Zaposleni>, Integer> colSifra = new TableColumn<>("Šifra");
            colSifra.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Integer> p) {
                    return new SimpleObjectProperty<Integer>(p.getValue().getKey().getIdPredmeta());
                }

            });
            colSifra.setMinWidth(75);
            TableColumn<Map.Entry<Predmet, Zaposleni>, String> colNaziv = new TableColumn<>("Naziv");
            colNaziv.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, String> p) {
                    return new SimpleObjectProperty<String>(p.getValue().getKey().getNaziv());
                }

            });
            colNaziv.setCellFactory(TextFieldTableCell.forTableColumn());
            colNaziv.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Predmet, Zaposleni>, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Map.Entry<Predmet, Zaposleni>, String> entryStringCellEditEvent) {

                    String stariNaziv = entryStringCellEditEvent.getTableView().getItems().get(entryStringCellEditEvent.getTablePosition().getRow()).getKey().getNaziv();

                    TablePosition<Map.Entry<Predmet, Zaposleni>, String> pozicija = entryStringCellEditEvent.getTablePosition();
                    String novaVrednost = entryStringCellEditEvent.getNewValue();

                    ((Map.Entry<Predmet, Zaposleni>) entryStringCellEditEvent.getTableView().getItems().get(
                            entryStringCellEditEvent.getTablePosition().getRow())
                    ).getKey().setNaziv(novaVrednost);

                    //UKOLIKO JE NOVI NAZIV RAZLICIT OD PRVOBITNOG
                    if (!stariNaziv.equals(novaVrednost)) {
                        //AKO JE UNET NAZIV I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                        if (!novaVrednost.equals("")) {
                            int red = pozicija.getRow();
                            Predmet izabraniPredmet = entryStringCellEditEvent.getTableView().getItems().get(red).getKey();
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniPredmet);
                            zahtevServeru1.KomunikacijaSaServerom();

                        } else {
                            //poruka za neispravan unos
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setAlert(Alert.AlertType.ERROR);
                                    alert.setContentText("Molim vas unesite naziv predmeta.");
                                    alert.showAndWait();
                                }
                            });
                            //da osvezi podatke na formi
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziPredmete");
                            zahtevServeru1.KomunikacijaSaServerom();
                        }
                    }
                }

            });
            colNaziv.setMinWidth(250);
            TableColumn<Map.Entry<Predmet, Zaposleni>, String> colProfesor = new TableColumn<>("Profesor");
            //TODO: izmena u tabeli raspodela predmeta*
            colProfesor.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, String> p) {
                    return new SimpleObjectProperty<String>(p.getValue().getValue().getIme() + " " + p.getValue().getValue().getPrezime());
                }

            });
            colProfesor.setCellFactory(TextFieldTableCell.forTableColumn());
            colProfesor.setMinWidth(250);

            TableColumn<Map.Entry<Predmet, Zaposleni>, String> colSmer = new TableColumn<>("Smer");
            colSmer.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, String> p) {
                    return new SimpleObjectProperty<String>(p.getValue().getKey().getStudijskiSmer());
                }

            });
            colSmer.setMinWidth(50);
            colSmer.setStyle("-fx-alignment: center;");
            TableColumn<Map.Entry<Predmet, Zaposleni>, Spinner> colSemestar = new TableColumn<>("Semestar");
            colSemestar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Spinner>, ObservableValue<Spinner>>() {

                @Override
                public ObservableValue<Spinner> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Spinner> arg0) {
                    Map.Entry<Predmet, Zaposleni> mp = arg0.getValue();

                    Spinner<Integer> spSemestar = new Spinner();
                    SpinnerValueFactory<Integer> vfSemestar = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 6);
                    vfSemestar.setValue(mp.getKey().getSemestar());
                    spSemestar.setValueFactory(vfSemestar);
                    spSemestar.setMinWidth(100);
                    spSemestar.setMaxWidth(100);
                    spSemestar.setInitialDelay(new Duration(1000));
                    spSemestar.setRepeatDelay(new Duration(1000));

                    spSemestar.valueProperty().addListener((obs, stara_vrednost, nova_vrednost) -> {
                        if(nova_vrednost != stara_vrednost) {
                            vfSemestar.setValue(nova_vrednost);
                            spSemestar.setOnMouseReleased(e -> {
                                Predmet izabraniPredmet = arg0.getValue().getKey();
                                izabraniPredmet.setSemestar(nova_vrednost);
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniPredmet);
                                zahtevServeru1.KomunikacijaSaServerom();
                            });
                        }
                    });

                    return new SimpleObjectProperty<Spinner>(spSemestar);

                }
            });
            colSemestar.setMinWidth(50);
            TableColumn<Map.Entry<Predmet, Zaposleni>, Spinner> colEspb = new TableColumn<>("ESPB");
            colEspb.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Spinner>, ObservableValue<Spinner>>() {

                @Override
                public ObservableValue<Spinner> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Spinner> arg0) {
                    Map.Entry<Predmet, Zaposleni> mp = arg0.getValue();

                    Spinner<Integer> spEspb = new Spinner();
                    SpinnerValueFactory<Integer> vfEspb = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10);
                    vfEspb.setValue(mp.getKey().getEspb());
                    spEspb.setValueFactory(vfEspb);
                    spEspb.setMinWidth(100);
                    spEspb.setMaxWidth(100);
                    spEspb.setInitialDelay(new Duration(1000));
                    spEspb.setRepeatDelay(new Duration(1000));

                    spEspb.valueProperty().addListener((obs, stara_vrednost, nova_vrednost) -> {
                        if(nova_vrednost != stara_vrednost) {
                            vfEspb.setValue(nova_vrednost);
                            spEspb.setOnMouseReleased(e -> {
                                Predmet izabraniPredmet = arg0.getValue().getKey();
                                izabraniPredmet.setEspb(nova_vrednost);
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniPredmet);
                                zahtevServeru1.KomunikacijaSaServerom();
                            });
                        }
                    });

                    return new SimpleObjectProperty<Spinner>(spEspb);

                }
            });
            colEspb.setMinWidth(50);
            TableColumn<Map.Entry<Predmet, Zaposleni>, CheckBox> colAktivan = new TableColumn<>("Vidljiv");
            colAktivan.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, CheckBox>, ObservableValue<CheckBox>>() {

                @Override
                public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, CheckBox> arg0) {
                    Map.Entry<Predmet, Zaposleni> mp = arg0.getValue();

                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(mp.getKey().isVidljiv());

                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        public void changed(ObservableValue<? extends Boolean> ov,
                                            Boolean stara_vrednost, Boolean nova_vrednost) {

                            mp.getKey().setVidljiv(nova_vrednost);
                            Predmet izabraniPredmet = arg0.getValue().getKey();
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniPredmet);
                            zahtevServeru1.KomunikacijaSaServerom();

                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }
            });
            colAktivan.setMinWidth(50);
            colAktivan.setStyle("-fx-alignment: center;");

            ObservableList<HashMap.Entry<Predmet, Zaposleni>> stavke = FXCollections.observableArrayList(sviPredmeti.entrySet());
            tablePredmeti.setItems(stavke);
            //sredjuje problem za dodatu kolonu
            tablePredmeti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablePredmeti.setPrefHeight(500);
            tablePredmeti.getColumns().addAll(colSifra, colNaziv, colProfesor, colSmer, colSemestar, colEspb, colAktivan);

            TextField txtSifra = new TextField();
            txtSifra.setPromptText("Šifra");
            txtSifra.setMinWidth(75);

            TextField txtNaziv = new TextField();
            txtNaziv.setPromptText("Naziv");
            txtNaziv.setMinWidth(250);

            TextField txtProfesor = new TextField();
            txtProfesor.setPromptText("Profesor");
            txtProfesor.setMinWidth(300);

            ComboBox cmbSmer = new ComboBox();
            Label lblSmer = new Label("Smer: ");
            lblSmer.setMinWidth(Region.USE_PREF_SIZE);
            cmbSmer.getItems().addAll(Predmet.tipSmera.values());
            cmbSmer.setMinWidth(Region.USE_PREF_SIZE);
            cmbSmer.setStyle("-fx-font: 12px \"Arial\";");

            Label lblSemestar = new Label("Semestar: ");
            lblSemestar.setMinWidth(Region.USE_PREF_SIZE);
            Spinner<Integer> spSemestar = new Spinner();
            spSemestar.setInitialDelay(new Duration(1000));
            spSemestar.setRepeatDelay(new Duration(1000));
            SpinnerValueFactory<Integer> vfSemestar = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 6);
            vfSemestar.setValue(0);
            spSemestar.setValueFactory(vfSemestar);
            vfSemestar.setValue(0);
            spSemestar.setMinWidth(50);
            spSemestar.setMaxWidth(50);

            Label lblEspb = new Label("Broj espb: ");
            lblEspb.setMinWidth(Region.USE_PREF_SIZE);
            Spinner<Integer> spEspb = new Spinner();
            spEspb.setInitialDelay(new Duration(1000));
            spEspb.setRepeatDelay(new Duration(1000));
            ObservableList<Integer> bodovi = FXCollections.observableArrayList(0, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            SpinnerValueFactory<Integer> vfEspb = new SpinnerValueFactory.ListSpinnerValueFactory<Integer>(bodovi);
            vfEspb.setValue(0);
            spEspb.setValueFactory(vfEspb);
            vfEspb.setValue(0);
            spEspb.setValueFactory(vfEspb);
            spEspb.setMinWidth(50);
            spEspb.setMaxWidth(50);

            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setMinWidth(60);
            btnDodaj.setOnMouseClicked(e -> {

                String naziv = txtNaziv.getText();
                String smer = null;
                if (cmbSmer.getValue() != null) {
                    smer = cmbSmer.getValue().toString();
                }
                int semestar = vfSemestar.getValue();
                int espb = vfEspb.getValue();

                //AKO JE UNET NAZIV, OSTALI SU OPCIONI JER SMER MOZE BITI ZAJEDNICKI
                if (naziv.length() != 0) {
                    Predmet predmet;
                    if (smer != null) {
                        predmet = new Predmet(Predmet.idNovogPredmeta(FXCollections.observableArrayList(sviPredmeti.keySet()), smer), naziv, Predmet.tipSmera.valueOf(smer), semestar, espb, true);
                    } else {
                        predmet = new Predmet(Predmet.idNovogPredmeta(FXCollections.observableArrayList(sviPredmeti.keySet()), smer), naziv, null, semestar, espb, true);
                    }
                    setTabela(tablePredmeti);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("dodaj", predmet, txtNaziv, cmbSmer, vfSemestar, vfEspb, txtProfesor);
                    zahtevServeru1.KomunikacijaSaServerom();

                } else {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.ERROR);
                            alert.setContentText("Molim vas unesite podatke.");
                            alert.showAndWait();
                        }
                    });
                }
            });
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);
            btnObrisi.setOnAction(e -> {

                        if (tablePredmeti.getSelectionModel().getSelectedItem() != null) {
                            //TODO: srediti za brisanje predmeta da se azurira i profesor u tabeli raspodela predmeta
                            Map.Entry<Predmet, Zaposleni> predmet = tablePredmeti.getSelectionModel().getSelectedItem();
                            setTabela(tablePredmeti);
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("obrisi", predmet.getKey(), predmet.getValue().getIdZaposlenog());
                            zahtevServeru1.KomunikacijaSaServerom();

                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setAlert(Alert.AlertType.ERROR);
                                    alert.setContentText("Molim vas izaberite predmet u tabeli.");
                                    alert.showAndWait();
                                }
                            });
                        }
            });

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5, 0, 0, 0));
            hboxAkcija.setAlignment(Pos.CENTER);
            hboxAkcija.getChildren().addAll(txtSifra, txtNaziv, txtProfesor, lblSmer, cmbSmer, lblSemestar, spSemestar, lblEspb, spEspb, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(hBoxPretraga, tablePredmeti, hboxAkcija);
            root.setCenter(vbox);
        });

        Menu predmetiMenu = new Menu("", lblPredmeti);

        Label lblSale = new Label("SALE");
        Menu saleMenu = new Menu("", lblSale);
        MenuItem prikazSalaMenuItem = new MenuItem("Prikaz sala");
        MenuItem rasporedPoSalamaMenuItem = new MenuItem("Raspored po salama");

        saleMenu.getItems().addAll(prikazSalaMenuItem, rasporedPoSalamaMenuItem);

        //Klikom na podmeni SALE iz Menija i na njegovu stavku Prikaz sala poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za sale, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i nova sala u Bazu podataka
        prikazSalaMenuItem.setOnAction(event -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziSale");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5, 10, 10, 10));

            TableView<Sala> tableSale = new TableView<>();
            tableSale.setEditable(true);
            tableSale.setPlaceholder(new Label("Ne postoji ni jedna sala u Bazi Podataka"));
            tableSale.getColumns().clear();

            TableColumn colNaziv = new TableColumn("Naziv");
            colNaziv.setCellValueFactory(new PropertyValueFactory<Sala, String>("naziv"));
            colNaziv.setCellFactory(TextFieldTableCell.forTableColumn());
            colNaziv.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Sala, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Sala, String> tabela) {
                            String stariNaziv = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getNaziv();
                            ((Sala) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setNaziv(tabela.getNewValue());
                            //UKOLIKO JE NOVI NAZIV RAZLICIT OD PRVOBITNOG
                            if (!stariNaziv.equals(tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getNaziv())) {
                                //AKO JE UNET NAZIV I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                                if (!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getNaziv().isEmpty()) {

                                    Sala izabranaSala = (Sala) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabranaSala);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite naziv za salu.");
                                            alert.showAndWait();
                                            //.setStyle("-fx-border-color: red;");
                                        }
                                    });
                                }
                            }
                        }
                    }
            );
            colNaziv.setMinWidth(200);
            TableColumn colKapacitet = new TableColumn("Broj mesta");
            colKapacitet.setCellValueFactory(new PropertyValueFactory<Sala, Integer>("brojMesta"));
            colKapacitet.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter(){
                @Override
                public Integer fromString(String value) {
                    try {
                        return super.fromString(value);
                    } catch(NumberFormatException e) {
                        //poruka za neispravan unos
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setAlert(Alert.AlertType.ERROR);
                                alert.setContentText("Molim vas unesite broj.");
                                alert.showAndWait();
                            }
                        });

                        Sala izabranaSala = tableSale.getSelectionModel().getSelectedItem();
                        return izabranaSala.getBrojMesta();
                    }
                }
            }));
            colKapacitet.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Sala, Integer>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Sala, Integer> tabela) {
                            int stariBrojMesta = tabela.getRowValue().getBrojMesta();
                            ((Sala) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setBrojMesta(tabela.getNewValue());

                            //UKOLIKO JE NOVI BROJ MESTA RAZLICIT OD PRVOBITNOG
                            if (stariBrojMesta != tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojMesta()) {

                                Sala izabranaSala = (Sala) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabranaSala);
                                zahtevServeru1.KomunikacijaSaServerom();
                            }
                        }
                    }
            );
            colKapacitet.setMinWidth(100);
            TableColumn<Sala, ComboBox> colOprema = new TableColumn("Oprema");
            colOprema.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sala, ComboBox>, ObservableValue<ComboBox>>() {

                @Override
                public ObservableValue<ComboBox> call(TableColumn.CellDataFeatures<Sala, ComboBox> arg0) {
                    Sala mp = arg0.getValue();

                    ComboBox comboBox = new ComboBox();
                    if (mp != null) {
                        comboBox.getItems().addAll(Sala.tipOpreme.values());
                        comboBox.setMinWidth(Region.USE_PREF_SIZE);
                        comboBox.setValue(mp.getOprema());

                        comboBox.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                            //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                            if (!nova_vrednost.toString().equals(stara_vrednost.toString())) {
                                mp.setOprema(Sala.tipOpreme.valueOf(nova_vrednost.toString().equals("/") ? "ništa" : nova_vrednost.toString()));
                                Sala izabranaSala = arg0.getValue();
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabranaSala);
                                zahtevServeru1.KomunikacijaSaServerom();
                            }
                        });

                        return new SimpleObjectProperty<ComboBox>(comboBox);
                    } else {

                        return null;
                    }

                }
            });
            colOprema.setMinWidth(200);
            colOprema.setStyle("-fx-alignment: center;");
            TableColumn<Sala, CheckBox> colAktivan = new TableColumn<>("Vidljiv");
            colAktivan.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sala, CheckBox>, ObservableValue<CheckBox>>() {

                @Override
                public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Sala, CheckBox> arg0) {
                    Sala mp = arg0.getValue();

                    CheckBox checkBox = new CheckBox();
                    if (mp != null) {
                        checkBox.selectedProperty().setValue(mp.isVidljiv());

                        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                            public void changed(ObservableValue<? extends Boolean> ov,
                                                Boolean stara_vrednost, Boolean nova_vrednost) {

                                mp.setVidljiv(nova_vrednost);
                                Sala izabranaSala = arg0.getValue();
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabranaSala);
                                zahtevServeru1.KomunikacijaSaServerom();

                            }
                        });

                        return new SimpleObjectProperty<CheckBox>(checkBox);
                    } else {

                        return null;
                    }

                }
            });
            colAktivan.setMinWidth(50);
            colAktivan.setStyle("-fx-alignment: center;");

            tableSale.setItems(sveSale);
            //sredjuje problem za dodatu kolonu
            tableSale.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableSale.setPrefHeight(550);
            tableSale.getColumns().addAll(colNaziv, colKapacitet, colOprema, colAktivan);

            TextField txtNaziv = new TextField();
            txtNaziv.setPromptText("Naziv");
            txtNaziv.setMinWidth(150);

            TextField txtKapacitet = new TextField();
            txtKapacitet.setPromptText("Kapacitet");
            txtKapacitet.setMinWidth(100);

            ComboBox cmbOprema = new ComboBox();
            cmbOprema.getItems().addAll(Sala.tipOpreme.values());
            cmbOprema.setValue(Sala.tipOpreme.ništa);
            cmbOprema.setMinWidth(Region.USE_PREF_SIZE);
            cmbOprema.setStyle("-fx-font: 12px \"Arial\";");

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5, 0, 0, 0));
            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setOnMouseClicked(e -> {
                String naziv = txtNaziv.getText();
                try {
                    int brojMesta = Integer.parseInt(txtKapacitet.getText());
                    String oprema = cmbOprema.getValue().toString();

                    //AKO JE UNETI NAZIV I KAPACITET, OPREMA NIJE OBAVEZNA
                    if (naziv.length() != 0 && brojMesta >= 0) {
                        Sala sala = new Sala(naziv, brojMesta, Sala.tipOpreme.valueOf(oprema.equals("/") ? "ništa" : oprema), true);
                        setTabela(tableSale);
                        ZahtevServeru zahtevServeru1 = new ZahtevServeru("dodaj", sala, txtNaziv, txtKapacitet, cmbOprema);
                        zahtevServeru1.KomunikacijaSaServerom();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (naziv.length() == 0) {
                                setAlert(Alert.AlertType.ERROR);
                                alert.setContentText("Molim vas unesite naziv sale.");
                                alert.showAndWait();
                            }
                        }
                    });
                } catch (NumberFormatException ex) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            setAlert(Alert.AlertType.ERROR);
                            if (naziv.length() == 0) {
                                alert.setContentText("Molim vas unesite podatke.");
                            } else {
                                alert.setContentText("Molim vas unesite broj.");
                            }
                            alert.showAndWait();
                        }
                    });
                }
            });
            btnDodaj.setMinWidth(60);
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setOnAction(e -> {
                if (tableSale.getSelectionModel().getSelectedItem() != null) {
                    Sala izabranaSala = tableSale.getSelectionModel().getSelectedItem();
                    setTabela(tableSale);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("obrisi", izabranaSala);
                    zahtevServeru1.KomunikacijaSaServerom();
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.ERROR);
                            alert.setContentText("Molim vas izaberite predmet u tabeli.");
                            alert.showAndWait();
                        }
                    });
                }
            });
            btnObrisi.setMinWidth(60);
            hboxAkcija.getChildren().addAll(txtNaziv, txtKapacitet, cmbOprema, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(tableSale, hboxAkcija);
            root.setCenter(vbox);
        });

        //Klikom na podmeni SALE iz Menija i na njegovu stavku Raspored po salama poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za zakazane sale, koji se mogu pretraziti, izmeniti ili otkazati
        rasporedPoSalamaMenuItem.setOnAction(event -> {

            TableView<HashMap.Entry<ZakazivanjeSale, ArrayList<String>>> tableRasporedPoSalama = new TableView<>();
            setTabela(tableRasporedPoSalama);

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziZakazivanjeSala");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);
            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5, 10, 10, 10));

            tableRasporedPoSalama.setPlaceholder(new Label("Ne postoji ni jedna zakazana sala u Bazi Podataka"));
            tableRasporedPoSalama.getColumns().clear();

            TableColumn<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String> colSala = new TableColumn("Sala");
            colSala.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String> zs) {
                    return new SimpleObjectProperty<String>(zs.getValue().getValue().get(0));
                }

            });
            colSala.setMinWidth(150);
            TableColumn<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String> colPredmet = new TableColumn("Predmet");
            colPredmet.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String> zs) {
                    return new SimpleObjectProperty<String>(zs.getValue().getValue().get(1));
                }

            });
            colPredmet.setMinWidth(200);
            TableColumn<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String> colZaposleni = new TableColumn("Zaposleni");
            colZaposleni.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, String> zs) {
                    return new SimpleObjectProperty<String>(zs.getValue().getValue().get(2));
                }

            });
            colZaposleni.setMinWidth(200);
            TableColumn<Map.Entry<ZakazivanjeSale, ArrayList<String>>, DatePicker> colDatum = new TableColumn("Datum");
            colDatum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, DatePicker>, ObservableValue<DatePicker>>() {

                public SimpleObjectProperty<DatePicker> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, DatePicker> arg0) {
                    //return new SimpleObjectProperty<DatePicker>(zs.getValue().getKey().getDatum());
                    ZakazivanjeSale mp = arg0.getValue().getKey();

                    DatePicker datePicker = new DatePicker();
                    datePicker.setDayCellFactory(picker -> new DateCell() {
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            LocalDate danas = LocalDate.now();

                            setDisable(empty || date.compareTo(danas) < 0 );
                        }
                    });

                    datePicker.valueProperty().setValue(LocalDate.parse(mp.getDatum().toString()));
                    datePicker.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                        //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                        if (!nova_vrednost.equals(stara_vrednost)) {
                            mp.setDatum(Date.valueOf(nova_vrednost));
                            ZakazivanjeSale izabranaZakazanaSala = arg0.getValue().getKey();
                            izabranaZakazanaSala.setDatum(Date.valueOf(nova_vrednost));
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabranaZakazanaSala);
                            zahtevServeru1.KomunikacijaSaServerom();
                            tableRasporedPoSalama.refresh();
                        }
                    });
                    return new SimpleObjectProperty<DatePicker>(datePicker);
                }
            });
            colDatum.setMinWidth(150);
            TableColumn<Map.Entry<ZakazivanjeSale, ArrayList<String>>, Spinner> colVremeOd = new TableColumn("Vreme početka");
            TableColumn<Map.Entry<ZakazivanjeSale, ArrayList<String>>, Spinner> colVremeDo = new TableColumn("Vreme kraja");
            colVremeOd.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, Spinner>, ObservableValue<Spinner>>() {

                public SimpleObjectProperty<Spinner> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, Spinner> arg0) {

                    ObservableList<String> vreme = FXCollections.observableArrayList(
                            "08:00:00", "08:15:00", "08:30:00", "08:45:00", "09:00:00", "09:15:00", "09:30:00", "09:45:00", "10:00:00", "10:15:00", "10:30:00", "10:45:00",
                            "11:00:00", "11:15:00", "11:30:00", "11:45:00", "12:00:00", "12:15:00", "12:30:00", "12:45:00", "13:00:00", "13:15:00", "13:30:00", "13:45:00",
                            "14:00:00", "14:15:00", "14:30:00", "14:45:00", "15:00:00", "15:15:00", "15:30:00", "15:45:00", "16:00:00", "16:15:00", "16:30:00", "16:45:00",
                            "17:00:00", "17:15:00", "17:30:00", "17:45:00", "18:00:00", "18:15:00", "18:30:00", "18:45:00", "19:00:00", "19:15:00", "19:30:00", "19:45:00",
                            "20:00:00", "20:15:00", "20:30:00", "20:45:00");
                    Spinner<String> spPocetak = new Spinner();
                    SpinnerValueFactory<String> vfPocetak = new SpinnerValueFactory.ListSpinnerValueFactory<String>(vreme);
                    vfPocetak.setValue(arg0.getValue().getKey().getVremePocetka().toString());
                    spPocetak.setValueFactory(vfPocetak);
                    spPocetak.setInitialDelay(new Duration(1000));
                    spPocetak.setRepeatDelay(new Duration(1000));

                    //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                    spPocetak.valueProperty().addListener((obs, stara_vrednost, nova_vrednost) -> {
                        if(nova_vrednost != stara_vrednost) {
                            if (LocalTime.parse(nova_vrednost).compareTo(LocalTime.parse(arg0.getValue().getKey().getVremeKraja().toString())) < 0) {
                                vfPocetak.setValue(nova_vrednost);
                                spPocetak.setOnMouseReleased(e -> {
                                    ZakazivanjeSale izabranaZakazanaSala = arg0.getValue().getKey();
                                    izabranaZakazanaSala.setVremePocetka(Time.valueOf(nova_vrednost));
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabranaZakazanaSala);
                                    zahtevServeru1.KomunikacijaSaServerom();
                                });

                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Vreme početka mora biti pre vremena kraja!");
                                        alert.showAndWait();
                                    }
                                });
                            }
                            //tableRasporedPoSalama.refresh();
                        }
                    });

                    return new SimpleObjectProperty<Spinner>(spPocetak);

                }
            });
            colVremeOd.setMinWidth(100);
            colVremeDo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, Spinner>, ObservableValue<Spinner>>() {

                public SimpleObjectProperty<Spinner> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<String>>, Spinner> arg0) {
                    ObservableList<String> vreme = FXCollections.observableArrayList(
                            "09:00:00", "09:15:00", "09:30:00", "09:45:00", "10:00:00", "10:15:00", "10:30:00", "10:45:00", "11:00:00", "11:15:00", "11:30:00", "11:45:00",
                            "12:00:00", "12:15:00", "12:30:00", "12:45:00", "13:00:00", "13:15:00", "13:30:00", "13:45:00", "14:00:00", "14:15:00", "14:30:00", "14:45:00",
                            "15:00:00", "15:15:00", "15:30:00", "15:45:00", "16:00:00", "16:15:00", "16:30:00", "16:45:00", "17:00:00", "17:15:00", "17:30:00", "17:45:00",
                            "18:00:00", "18:15:00", "18:30:00", "18:45:00", "19:00:00", "19:15:00", "19:30:00", "19:45:00", "20:00:00", "20:15:00", "20:30:00", "20:45:00");
                    Spinner<String> spKraj = new Spinner();
                    SpinnerValueFactory<String> vfKraj = new SpinnerValueFactory.ListSpinnerValueFactory<String>(vreme);
                    vfKraj.setValue(arg0.getValue().getKey().getVremeKraja().toString());
                    spKraj.setValueFactory(vfKraj);
                    spKraj.setInitialDelay(new Duration(1000));
                    spKraj.setRepeatDelay(new Duration(1000));

                    //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                    spKraj.valueProperty().addListener((obs, stara_vrednost, nova_vrednost) -> {
                        if(nova_vrednost != stara_vrednost) {
                            if (LocalTime.parse(nova_vrednost).compareTo(LocalTime.parse(arg0.getValue().getKey().getVremePocetka().toString())) > 0) {
                                vfKraj.setValue(nova_vrednost);
                                spKraj.setOnMouseReleased(e -> {
                                    ZakazivanjeSale izabranaZakazanaSala = arg0.getValue().getKey();
                                    izabranaZakazanaSala.setVremeKraja(Time.valueOf(nova_vrednost));
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabranaZakazanaSala);
                                    zahtevServeru1.KomunikacijaSaServerom();
                                });

                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Vreme kraja mora biti posle vremena početka!");
                                        alert.showAndWait();
                                    }
                                });
                            }
                            //tableRasporedPoSalama.refresh();
                        }
                    });

                    return new SimpleObjectProperty<Spinner>(spKraj);
                }
            });
            colVremeDo.setMinWidth(100);

            ObservableList<HashMap.Entry<ZakazivanjeSale, ArrayList<String>>> stavke = FXCollections.observableArrayList(sveZakazaneSale.entrySet());
            tableRasporedPoSalama.setItems(stavke);
            //sredjuje problem za dodatu kolonu
            tableRasporedPoSalama.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableRasporedPoSalama.setPrefHeight(500);
            tableRasporedPoSalama.getColumns().addAll(colSala, colPredmet, colZaposleni, colDatum, colVremeOd, colVremeDo);

            Label lblSala = new Label("Sala: ");
            lblSala.setMinWidth(Region.USE_PREF_SIZE);
            ComboBox cmbSala = new ComboBox();
            cmbSala.getItems().addAll(sveSale.stream().filter(sala -> sala.isVidljiv()).map(Sala::getNaziv).collect(Collectors.toList()));
            Label lblPredmet = new Label("Predmet: ");
            lblPredmet.setMinWidth(Region.USE_PREF_SIZE);
            ComboBox cmbPredmet = new ComboBox();
            //filtriranje da se ne prikazuju dupli predmeti
            cmbPredmet.getItems().addAll(sviPredmeti.keySet().stream().collect(Collectors.toMap(Predmet::getNaziv, p -> p, (p, p1) -> p)).values().stream().map(Predmet::getNaziv).collect(Collectors.toList()));
            ComboBox cmbZaposleni = new ComboBox();
            cmbPredmet.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {

                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                if (!nova_vrednost.equals(stara_vrednost)) {
                    cmbZaposleni.getItems().clear();
                    cmbZaposleni.getItems().addAll(sviPredmeti.entrySet().stream().filter(sp -> sp.getKey().getNaziv().equals(nova_vrednost.toString())).map(i -> i.getValue().getImePrezime()).collect(Collectors.toList()));
                }
            });
            cmbPredmet.setMinWidth(Region.USE_PREF_SIZE);
            cmbPredmet.setStyle("-fx-font: 12px \"Arial\";");

            Label lblPredavac = new Label("Zaposleni: ");
            lblPredavac.setMinWidth(Region.USE_PREF_SIZE);
            cmbZaposleni.setMinWidth(Region.USE_PREF_SIZE);
            cmbZaposleni.setStyle("-fx-font: 12px \"Arial\";");

            Label lblDatum = new Label("Datum: ");
            lblDatum.setMinWidth(Region.USE_PREF_SIZE);
            DatePicker dpDatum = new DatePicker();
            dpDatum.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate danas = LocalDate.now();

                    setDisable(empty || date.compareTo(danas) < 0 );
                }
            });
            dpDatum.setMaxWidth(100);

            Label lblVremeOd = new Label("od: ");
            Spinner<Integer> spSatiOd = new Spinner();
            SpinnerValueFactory<Integer> vfSatiOd = new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 20);
            vfSatiOd.setValue(8);
            spSatiOd.setValueFactory(vfSatiOd);
            spSatiOd.setMinWidth(50);
            spSatiOd.setMaxWidth(50);

            Label lblOdvojiOd = new Label(":");
            Spinner<Integer> spMinutiOd = new Spinner();
            SpinnerValueFactory<Integer> vfMinutiOd = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15);
            vfMinutiOd.setValue(00);
            spMinutiOd.setValueFactory(vfMinutiOd);
            spMinutiOd.setMinWidth(50);
            spMinutiOd.setMaxWidth(50);

            Label lblVremeDo = new Label("do: ");
            Spinner<Integer> spSatiDo = new Spinner();
            SpinnerValueFactory<Integer> vfSatiDo = new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 21);
            vfSatiDo.setValue(9);
            spSatiDo.setValueFactory(vfSatiDo);
            spSatiDo.setMinWidth(50);
            spSatiDo.setMaxWidth(50);

            Label lblOdvojiDo = new Label(":");
            Spinner<Integer> spMinutiDo = new Spinner();
            SpinnerValueFactory<Integer> vfMinutiDo = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15);
            vfMinutiDo.setValue(00);
            spMinutiDo.setValueFactory(vfMinutiDo);
            spMinutiDo.setMinWidth(50);
            spMinutiDo.setMaxWidth(50);

            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setMinWidth(30);
            btnDodaj.setOnMouseClicked(e -> {

                String nazivSale = cmbSala.getValue() == null ? null : cmbSala.getValue().toString();
                String nazivPredmeta = cmbPredmet.getValue() == null ? null : cmbPredmet.getValue().toString();
                String zaposleni = cmbZaposleni.getValue() == null ? null : cmbZaposleni.getValue().toString();
                Date datum = null;
                if (dpDatum.getValue() != null) {
                    datum = Date.valueOf(dpDatum.getValue());
                }
                Time vremePocetka = Time.valueOf(spSatiOd.getValue() + ":" + spMinutiOd.getValue() + ":00");
                Time vremeKraja = Time.valueOf(spSatiDo.getValue() + ":" + spMinutiDo.getValue() + ":00");

                //AKO SU IZABRANI SALA, PREDMET, ZAPOSLENI I DATUM POČETKA I UKOLIKO JE IZABRANO VREME POČETKA PRE VREMENA KRAJA
                if (nazivSale != null && nazivPredmeta != null && zaposleni != null && datum != null && vremePocetka.compareTo(vremeKraja) < 0) {

                    //da doda u bazu, vrati normalnu boju polja i obrise vrednosti
                    OptionalInt idSale = sveSale.stream().filter(s -> s.getNaziv().equals(nazivSale)).mapToInt(Sala::getIdSale).findFirst();
                    OptionalInt idPredmeta = sviPredmeti.keySet().stream().filter(p -> p.getNaziv().equals(nazivPredmeta)).mapToInt(Predmet::getIdPredmeta).findFirst();
                    OptionalInt idZaposlenog = sviZaposleni.stream().filter(z -> z.getImePrezime().equals(zaposleni)).mapToInt(Zaposleni::getIdZaposlenog).findFirst();
                    ZakazivanjeSale zakazivanjeSale = new ZakazivanjeSale(idSale.getAsInt(), idPredmeta.getAsInt(), idZaposlenog.getAsInt(), datum, vremePocetka, vremeKraja);
                    ArrayList<String> vrednost = new ArrayList<>();
                    vrednost.add(nazivSale);
                    vrednost.add(nazivPredmeta);
                    vrednost.add(zaposleni);
                    setTabela(tableRasporedPoSalama);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("dodaj", zakazivanjeSale, vrednost, cmbSala, cmbZaposleni, cmbPredmet, dpDatum, spSatiOd, spMinutiOd, spSatiDo, spMinutiDo);
                    zahtevServeru1.KomunikacijaSaServerom();

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            setAlert(Alert.AlertType.ERROR);
                            alert.setContentText("Molim vas izaberite sve podatke.");
                            alert.showAndWait();
                        }
                    });
                }
            });
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(30);
            btnObrisi.setOnAction(e -> {
                if (tableRasporedPoSalama.getSelectionModel().getSelectedItem() != null) {
                    ZakazivanjeSale izabranaZakazanaSala = tableRasporedPoSalama.getSelectionModel().getSelectedItem().getKey();
                    setTabela(tableRasporedPoSalama);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("obrisi", izabranaZakazanaSala);
                    zahtevServeru1.KomunikacijaSaServerom();

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setAlert(Alert.AlertType.ERROR);
                            alert.setContentText("Molim vas izaberite zakazanu salu u tabeli.");
                            alert.showAndWait();
                        }
                    });
                }
            });

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(3);
            hboxAkcija.setPadding(new Insets(5, 0, 0, 0));
            hboxAkcija.setAlignment(Pos.BOTTOM_LEFT);
            hboxAkcija.getChildren().addAll(lblSala, cmbSala, lblPredmet, cmbPredmet, lblPredavac, cmbZaposleni, lblDatum, dpDatum, lblVremeOd, spSatiOd, lblOdvojiOd, spMinutiOd, lblVremeDo, spSatiDo, lblOdvojiDo, spMinutiDo, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(tableRasporedPoSalama, hboxAkcija);
            root.setCenter(vbox);

        });

        Menu ispitniRokMenu = new Menu("ISPITNI ROK");
        MenuItem aktivirajMenuItem = new MenuItem("Aktiviraj");
        MenuItem rasporedIspitaMenuItem = new MenuItem("Raspored ispita");

        ispitniRokMenu.getItems().addAll(aktivirajMenuItem, rasporedIspitaMenuItem);

        //Klikom na podmeni ISPITNI ROK iz Menija i na njegovu stavku Aktiviraj poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za ispitne rokove, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi ispitni rok u Bazu podataka
        aktivirajMenuItem.setOnAction(event -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziIspitneRokove");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);
            TableView<IspitniRok> tableIspitniRokovi = new TableView<>();
            tableIspitniRokovi.setEditable(true);
            tableIspitniRokovi.setPlaceholder(new Label("Ne postoji ni jedan ispitni rok u Bazi Podataka"));

            tableIspitniRokovi.getColumns().clear();
            TableColumn colNaziv = new TableColumn("Naziv");
            colNaziv.setCellValueFactory(new PropertyValueFactory<IspitniRok, String>("naziv"));
            colNaziv.setCellFactory(TextFieldTableCell.forTableColumn());
            colNaziv.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<IspitniRok, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<IspitniRok, String> tabela) {
                            String stariNaziv = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getNaziv();
                            ((IspitniRok) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setNaziv(tabela.getNewValue());
                            //UKOLIKO JE NOVI NAZIV RAZLICIT OD PRVOBITNOG
                            if (!stariNaziv.equals(tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getNaziv())) {
                                //AKO JE UNET NAZIV I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                                if (!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getNaziv().isEmpty()) {

                                    IspitniRok izabraniIspitniRok = (IspitniRok) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za neispravan unos
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Molim vas unesite naziv za ispitni rok.");
                                            alert.showAndWait();
                                        }
                                    });
                                }
                            }
                        }
                    }
            );
            colNaziv.setMinWidth(200);
            TableColumn<IspitniRok, DatePicker> colPocetak = new TableColumn("Početak");
            colPocetak.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<IspitniRok, DatePicker>, ObservableValue<DatePicker>>() {

                @Override
                public ObservableValue<DatePicker> call(TableColumn.CellDataFeatures<IspitniRok, DatePicker> arg0) {
                    IspitniRok mp = arg0.getValue();

                    DatePicker datePicker = new DatePicker();
                    if(mp.getDatumPocetka() != null) {

                        datePicker.valueProperty().setValue(LocalDate.parse(mp.getDatumPocetka().toString()));
                    } else if (mp.getDatumPocetka() == null) {
                        datePicker.valueProperty().setValue(null);
                    }

                    datePicker.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {
                        //UKOLIKO JE JEDNA OD VREDNOSTI NULL
                        if ((nova_vrednost == null && stara_vrednost != null) || (nova_vrednost != null && stara_vrednost == null))
                        {
                            //provera da li je mozda uneta null vrednost
                            if (nova_vrednost != null) {
                                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                                if (!nova_vrednost.equals(stara_vrednost)) {
                                    mp.setDatumPocetka(Date.valueOf(nova_vrednost));
                                }
                            } else if (nova_vrednost == null) {
                                mp.setDatumPocetka(null);
                            }

                            IspitniRok izabraniIspitniRok = arg0.getValue();
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                            zahtevServeru1.KomunikacijaSaServerom();

                        //UKOLIKO NI JEDNA OD VREDNOSTI NIJE NULL
                        } else if (nova_vrednost != null && stara_vrednost != null) {
                            //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                            if (!nova_vrednost.equals(stara_vrednost)) {
                                mp.setDatumPocetka(Date.valueOf(nova_vrednost));
                                IspitniRok izabraniIspitniRok = arg0.getValue();
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                                zahtevServeru1.KomunikacijaSaServerom();
                            }
                        }
                    });
                    return new SimpleObjectProperty<DatePicker>(datePicker);
                }
            });
            colPocetak.setMinWidth(100);
            colPocetak.setStyle("-fx-alignment: center;");
            TableColumn<IspitniRok, DatePicker> colKraj = new TableColumn("Kraj");
            colKraj.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<IspitniRok, DatePicker>, ObservableValue<DatePicker>>() {

                @Override
                public ObservableValue<DatePicker> call(TableColumn.CellDataFeatures<IspitniRok, DatePicker> arg0) {
                    IspitniRok mp = arg0.getValue();

                    DatePicker dateDo = new DatePicker();
                    //zabranjen odabih dana manjih od izabranog pocetnog datuma + 1
                    dateDo.setDayCellFactory(picker -> new DateCell() {
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);

                            if (mp.getDatumPocetka() != null) {
                                LocalDate danVise = mp.getDatumPocetka().toLocalDate().plusDays(1);

                                setDisable(empty || date.compareTo(danVise) < 0);
                            }
                        }
                    });
                    if(mp.getDatumKraja() != null) {

                        dateDo.valueProperty().setValue(LocalDate.parse(mp.getDatumKraja().toString()));
                    } else if (mp.getDatumPocetka() == null) {
                        dateDo.valueProperty().setValue(null);
                    }

                    //TODO: moglo bi i provera ako tamo nije prazno na pocetnom da stari kad ide na null vrati u tabeli svoju vrednost
                    dateDo.valueProperty().addListener((ov, stara_vrednost, nova_vrednost) -> {
                        //UKOLIKO JE JEDNA OD VREDNOSTI NULL
                        if ((nova_vrednost == null && stara_vrednost != null) || (nova_vrednost != null && stara_vrednost == null))
                        {
                            //provera da li je mozda uneta null vrednost
                            if (nova_vrednost != null) {
                                //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                                if (!nova_vrednost.equals(stara_vrednost)) {
                                    mp.setDatumKraja(Date.valueOf(nova_vrednost));
                                }
                            } else if (nova_vrednost == null) {
                                mp.setDatumKraja(null);
                            }

                            IspitniRok izabraniIspitniRok = arg0.getValue();
                            ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                            zahtevServeru1.KomunikacijaSaServerom();

                        //UKOLIKO NI JEDNA OD VREDNOSTI NIJE NULL
                        } else if (nova_vrednost != null && stara_vrednost != null) {
                            //UKOLIKO JE NOVA VREDNOST RAZLICITA OD PRVOBITNE
                            if (!nova_vrednost.equals(stara_vrednost)) {
                                mp.setDatumKraja(Date.valueOf(nova_vrednost));
                                IspitniRok izabraniIspitniRok = arg0.getValue();
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                                zahtevServeru1.KomunikacijaSaServerom();
                            }
                        }
                    });
                    return new SimpleObjectProperty<DatePicker>(dateDo);
                }
            });
            colKraj.setMinWidth(100);
            colKraj.setStyle("-fx-alignment: center;");
            TableColumn<IspitniRok, CheckBox> colAktivan = new TableColumn<>("Aktivan rok");
            colAktivan.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<IspitniRok, CheckBox>, ObservableValue<CheckBox>>() {

                @Override
                public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<IspitniRok, CheckBox> arg0) {
                    IspitniRok mp = arg0.getValue();

                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(mp.isAktivnost());

                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        public void changed(ObservableValue<? extends Boolean> ov,
                                            Boolean stara_vrednost, Boolean nova_vrednost) {

                            //ukoliko zeli da aktivira neki ispitni rok
                            if (nova_vrednost == true) {
                                //provera da li su svi ostali rokovi neaktivni
                                boolean aktivan = false;
                                for (IspitniRok ispitniRok : sviIspitniRokovi) {

                                    if (ispitniRok.isAktivnost()) {
                                        aktivan = true;
                                        break;
                                    }
                                }
                                if (!aktivan) {
                                    mp.setAktivnost(nova_vrednost);
                                    IspitniRok izabraniIspitniRok = arg0.getValue();
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za nemogucu akciju
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Ne mogu biti dva ispitna roka u toku!");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziIspitneRokove");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            } else {
                                mp.setAktivnost(nova_vrednost);
                                IspitniRok izabraniIspitniRok = arg0.getValue();
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                                zahtevServeru1.KomunikacijaSaServerom();
                            }
                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }
            });
            colAktivan.setMinWidth(50);
            colAktivan.setStyle("-fx-alignment: center;");

            TableColumn<IspitniRok, CheckBox> colAktivnaPrijava = new TableColumn<>("Aktivna prijava");
            colAktivnaPrijava.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<IspitniRok, CheckBox>, ObservableValue<CheckBox>>() {

                @Override
                public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<IspitniRok, CheckBox> arg0) {
                    IspitniRok mp = arg0.getValue();

                    CheckBox checkBox = new CheckBox();
                    checkBox.selectedProperty().setValue(mp.isAktivnaPrijava());

                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        public void changed(ObservableValue<? extends Boolean> ov,
                                            Boolean stara_vrednost, Boolean nova_vrednost) {

                            //ukoliko zeli da aktivira neka prijava
                            if (nova_vrednost == true) {
                                //provera da li su sve ostale prijave neaktivne
                                boolean aktivan = false;
                                for (IspitniRok ispitniRok : sviIspitniRokovi) {

                                    if (ispitniRok.isAktivnaPrijava()) {
                                        aktivan = true;
                                        break;
                                    }
                                }
                                if (!aktivan) {
                                    mp.setAktivnaPrijava(nova_vrednost);
                                    IspitniRok izabraniIspitniRok = arg0.getValue();
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                                    zahtevServeru1.KomunikacijaSaServerom();

                                } else {
                                    //poruka za nemogucu akciju
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAlert(Alert.AlertType.ERROR);
                                            alert.setContentText("Ne mogu biti aktivne dve prijave roka u isto vreme!");
                                            alert.showAndWait();
                                        }
                                    });
                                    //da osvezi podatke na formi
                                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("osveziIspitneRokove");
                                    zahtevServeru1.KomunikacijaSaServerom();
                                }
                            } else {
                                mp.setAktivnaPrijava(nova_vrednost);
                                IspitniRok izabraniIspitniRok = arg0.getValue();
                                ZahtevServeru zahtevServeru1 = new ZahtevServeru("izmeni", izabraniIspitniRok);
                                zahtevServeru1.KomunikacijaSaServerom();
                            }
                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }
            });
            colAktivnaPrijava.setMinWidth(50);
            colAktivnaPrijava.setStyle("-fx-alignment: center;");

            tableIspitniRokovi.setItems(sviIspitniRokovi);
            //sredjuje problem za dodatu kolonu
            tableIspitniRokovi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableIspitniRokovi.setPrefHeight(550);
            colPocetak.setSortType(TableColumn.SortType.DESCENDING);
            tableIspitniRokovi.getColumns().addAll(colNaziv, colPocetak, colKraj, colAktivan, colAktivnaPrijava);
            tableIspitniRokovi.getSortOrder().add(colPocetak);
            tableIspitniRokovi.sort();

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5, 10, 10, 10));

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5, 0, 0, 0));
            hboxAkcija.setAlignment(Pos.CENTER_LEFT);

            TextField txtNaziv = new TextField();
            txtNaziv.setPromptText("Naziv");

            Label lblOd = new Label("Od: ");
            DatePicker dateOd = new DatePicker();
            //zabranjen odabih dana pre današnjeg
            dateOd.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate danas = LocalDate.now();

                    setDisable(empty || date.compareTo(danas) < 0 );
                }
            });
            Label lblDo = new Label("Do: ");
            DatePicker dateDo = new DatePicker();
            //zahtevan odabir makar za 1 dan više od početka roka
            dateDo.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate sutra = LocalDate.now().plusDays(1);

                    setDisable(empty || date.compareTo(sutra) < 0 );
                }
            });

            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setMinWidth(60);
            btnDodaj.setOnMouseClicked(e -> {
                String naziv = txtNaziv.getText();
                Date pocetakRoka = null;
                Date krajRoka = null;
                if (dateOd.getValue() != null) {
                    pocetakRoka = Date.valueOf(dateOd.getValue());
                }
                if (dateDo.getValue() != null) {
                    krajRoka = Date.valueOf(dateDo.getValue());
                }

                //AKO JE UNET NAZIV I AKO SU IZABRANI I DATUM POČETKA I DATUM KRAJA ROKA, ILI AKO JE UNET NAZIV I AKO NI JEDAN OD NJIH NIJE IZABRAN
                if ((naziv.length() != 0 && dateOd.getValue() != null && dateDo.getValue() != null && dateDo.getValue().isAfter(dateOd.getValue())) || (naziv.length() != 0 && dateOd.getValue() == null && dateDo.getValue() == null)) {

                    //da doda u bazu, vrati normalnu boju polja i obrise vrednosti
                    setTabela(tableIspitniRokovi);
                    IspitniRok ispitniRok = new IspitniRok(naziv, pocetakRoka, krajRoka, false);
                    ZahtevServeru zahtevServeru1 = new ZahtevServeru("dodaj", ispitniRok, txtNaziv, dateOd, dateDo);
                    zahtevServeru1.KomunikacijaSaServerom();

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            setAlert(Alert.AlertType.ERROR);
                            alert.setContentText("Molim vas unesite sve podatke.");
                            alert.showAndWait();
                        }
                    });
                }
            });

            hboxAkcija.getChildren().addAll(txtNaziv, lblOd, dateOd, lblDo, dateDo, btnDodaj);

            vbox.getChildren().addAll(tableIspitniRokovi, hboxAkcija);
            root.setCenter(vbox);

        });
        //Klikom na podmeni ISPITNI ROK iz Menija i na njegovu stavku Raspored ispita poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuje raspored ispita u aktivnom ispitnom roku i informacije iz Baze podataka
        rasporedIspitaMenuItem.setOnAction(event -> {

            TableView<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>> tableRasporedIspita = new TableView<>();
            //tableRasporedIspita.setSelectionModel(null);
            setTabela(tableRasporedIspita);

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            ZahtevServeru zahtevServeru = new ZahtevServeru("osveziRasporedIspita");
            zahtevServeru.KomunikacijaSaServerom();

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5, 10, 10, 10));

            HBox hBoxPretraga = new HBox();
            hBoxPretraga.setAlignment(Pos.CENTER_LEFT);
            hBoxPretraga.setPadding(new Insets(0, 10, 5, 0));
            hBoxPretraga.setSpacing(5);

            Label lblPretraga = new Label("Ispiti: ");
            lblPretraga.setFont(font15);
            TextField txtPretraga = new TextField();

            Button btnPretrazi = new Button("pretraži");
            btnPretrazi.setFont(font15);

            btnPretrazi.setOnMouseClicked(e -> {
                String trazeno = txtPretraga.getText().toLowerCase();
                ObservableList<HashMap.Entry<ZakazivanjeSale, ArrayList<ArrayList>>> nadjeni = FXCollections.observableArrayList(rasporedIspita.entrySet().stream().filter(map -> map.getValue().get(0).get(0).toString().toLowerCase().contains(trazeno) || map.getValue().get(0).get(1).toString().toLowerCase().contains(trazeno) || sveSale.stream().anyMatch(s -> s.getNaziv().toLowerCase().contains(trazeno)) || map.getKey().getDatum().toString().contains(trazeno) || map.getKey().getVremePocetka().toString().contains(trazeno)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue())).entrySet());
                tableRasporedIspita.setItems(nadjeni);
            });
            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            tableRasporedIspita.setPlaceholder(new Label("Ne postoji ni jedan zakazani ispit za ispitni rok u Bazi Podataka"));
            tableRasporedIspita.getColumns().clear();

            TableColumn colPredmet = new TableColumn("Predmet");
            colPredmet.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, String> zs) {
                    return new SimpleObjectProperty<String>(zs.getValue().getValue().get(0).get(0).toString());
                }

            });
            colPredmet.setMinWidth(300);
            TableColumn colProfesor = new TableColumn("Profesor");
            colProfesor.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, String> zs) {
                    return new SimpleObjectProperty<String>(zs.getValue().getValue().get(0).get(1).toString());
                }

            });
            colProfesor.setMinWidth(300);
            TableColumn colDatum = new TableColumn("Datum");
            colDatum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, Date>, ObservableValue<Date>>() {

                @Override
                public ObservableValue<Date> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, Date> zs) {
                    return new SimpleObjectProperty<Date>(zs.getValue().getKey().getDatum());
                }

            });
            colDatum.setMinWidth(100);
            TableColumn colVreme = new TableColumn("Vreme");
            colVreme.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, Time>, ObservableValue<Time>>() {

                @Override
                public ObservableValue<Time> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, Time> zs) {
                    return new SimpleObjectProperty<Time>(zs.getValue().getKey().getVremePocetka());
                }

            });
            colVreme.setMinWidth(50);
            TableColumn colSala = new TableColumn("Sala");
            colSala.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, String> zs) {
                    return new SimpleObjectProperty<String>(sveSale.stream().filter(s -> s.getIdSale() == zs.getValue().getKey().getIdSale()).map(i -> i.getNaziv()).findFirst().map(Object::toString).orElse(null));
                }

            });
            colSala.setMinWidth(150);
            TableColumn colBrPrijava = new TableColumn("Prijave");
            colBrPrijava.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, String>, ObservableValue<String>>() {

                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<ZakazivanjeSale, ArrayList<ArrayList>>, String> zs) {

                    return new SimpleObjectProperty<String>(String.valueOf(zs.getValue().getValue().get(1).size()));
                }
            });
            colBrPrijava.setCellFactory(tc -> {

                TableCell<Object, String> cell = new TableCell<Object, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                //na klik broja prijava vide se informacije o studentima koji su prijavili ispit
                cell.setOnMouseClicked(e -> {
                    if (! cell.isEmpty()) {

                        Platform.runLater(new Runnable() {

                            @Override
                            public void run() {

                                setAlert(Alert.AlertType.INFORMATION);
                                ArrayList studenti = (ArrayList) tableRasporedIspita.getItems().stream().filter(sp -> sp.getKey().equals(tableRasporedIspita.getSelectionModel().getSelectedItem().getKey())).map(i -> i.getValue().get(1)).collect(Collectors.toList());
                                String poruka = "";
                                System.out.println(studenti);
                                if (!studenti.toString().replaceAll("\\[", "").replaceAll("\\]", "").isEmpty()) {

                                    for (Object student : studenti) {
                                        poruka += sviStudenti.stream().filter(s -> (s.getSmer() + "/" + s.getIdStudenta() + "-" + String.valueOf(s.getGodinaUpisa()).substring(2)).equals(student.toString().replace("[", "").replace("]", ""))).findFirst().orElseThrow().getImePrezime() + " " + student.toString().replace("[", "").replace("]", "") + "\n";
                                    }
                                    alert.setContentText("Studenti koji su prijavili ispit " + tableRasporedIspita.getSelectionModel().getSelectedItem().getValue().get(0).get(0).toString() + ":\n" + poruka);
                                } else {

                                    alert.setContentText("Nema prijava za ispit:  " + tableRasporedIspita.getSelectionModel().getSelectedItem().getValue().get(0).get(0).toString() + ".");
                                }
                                alert.showAndWait();
                            }
                        });
                    }
                });
                return cell;
            });
            colBrPrijava.setMinWidth(50);

            ObservableList<HashMap.Entry<ZakazivanjeSale, ArrayList<ArrayList>>> stavke = FXCollections.observableArrayList(rasporedIspita.entrySet());
            tableRasporedIspita.setItems(stavke);
            //prijavljeni ispit+br prijava
            //sredjuje problem za dodatu kolonu
            tableRasporedIspita.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableRasporedIspita.getColumns().addAll(colPredmet, colProfesor, colDatum, colVreme, colSala, colBrPrijava);
            tableRasporedIspita.setPrefHeight(550);

            vbox.getChildren().addAll(hBoxPretraga, tableRasporedIspita);
            root.setCenter(vbox);

        });

        menuBar.setStyle("-fx-padding: 3 6 3 6;");
        menuBar.getMenus().addAll(pocetnaMenu, studentiMenu, zaposleniMenu, predmetiMenu, saleMenu, ispitniRokMenu);

    }

    /** Klasa ZahtevServeru namenjena za razmenjivanje objekata sa serverom.
     * Za osvezavanje podataka na formi i dodavanje/izmenu/brisanje studenta/zaposlenog/predmeta/sala/ispitnih rokova/zakazanih sala.
     * @author Biljana Stanojevic   */
    private class ZahtevServeru {

        private static final int TCP_PORT = 9000;
        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object zahtev;
        private Object odgovor;

        private Student student;
        private Zaposleni zaposleni;
        private Predmet predmet;
        private Sala sala;
        private IspitniRok ispitniRok;
        private ZakazivanjeSale zakazivanjeSale;

        private ArrayList vrednosti;
        private int idZaposlenog;
        private TextField txtIme;
        private TextField txtPrezime;
        private TextField txtMail;
        private TextField txtTelefon;
        private TextField txtAdresa;
        private ComboBox cmbPozicija;
        private TextField txtNaziv;
        private ComboBox cmbSmer;
        SpinnerValueFactory vfSemestar;
        private SpinnerValueFactory vfEspb;
        private TextField txtProfesor;
        private ComboBox cmbFinansiranje;
        private TextField txtKapacitet;
        private ComboBox cmbOprema;
        private DatePicker dateOd;
        private DatePicker dateDo;
        private ComboBox cmbSala;
        private ComboBox cmbZaposleni;
        private ComboBox cmbPredmet;
        private DatePicker dpDatum;

        //konstuktor za osvezavanje podataka
        public ZahtevServeru(Object zahtev) {
            this.zahtev = zahtev;
        }

        //konstruktori za brisanje/izmenu/login podatke
        public ZahtevServeru(Object zahtev, Student student) {
            this.zahtev = zahtev;
            this.student = student;
        }

        public ZahtevServeru(Object zahtev, Zaposleni zaposleni) {
            this.zahtev = zahtev;
            this.zaposleni = zaposleni;
        }

        public ZahtevServeru(Object zahtev, Predmet predmet) {
            this.zahtev = zahtev;
            this.predmet = predmet;
        }

        public ZahtevServeru(Object zahtev, Predmet predmet, int idZaposlenog) {
            this.zahtev = zahtev;
            this.predmet = predmet;
            this.idZaposlenog = idZaposlenog;
        }

        public ZahtevServeru(Object zahtev, Sala sala) {
            this.zahtev = zahtev;
            this.sala = sala;
        }

        public ZahtevServeru(Object zahtev, IspitniRok ispitniRok) {
            this.zahtev = zahtev;
            this.ispitniRok = ispitniRok;
        }

        public ZahtevServeru(Object zahtev, ZakazivanjeSale zakazivanjeSale) {
            this.zahtev = zahtev;
            this.zakazivanjeSale = zakazivanjeSale;
        }

        //konstruktori za dodavanje
        public ZahtevServeru(Object zahtev, Student student, TextField txtIme, TextField txtPrezime, TextField txtAdresa, TextField txtMail, TextField txtTelefon, ComboBox cmbSmer, ComboBox cmbFinansiranje) {
            this.zahtev = zahtev;
            this.student = student;
            this.txtIme = txtIme;
            this.txtPrezime = txtPrezime;
            this.txtAdresa = txtAdresa;
            this.txtMail = txtMail;
            this.txtTelefon = txtTelefon;
            this.cmbSmer = cmbSmer;
            this.cmbFinansiranje = cmbFinansiranje;
        }

        public ZahtevServeru(Object zahtev, Zaposleni zaposleni, ComboBox cmbPozicija, TextField txtIme, TextField txtPrezime, TextField txtMail, TextField txtTelefon, TextField txtAdresa) {
            this.zahtev = zahtev;
            this.zaposleni = zaposleni;
            this.cmbPozicija = cmbPozicija;
            this.txtIme = txtIme;
            this.txtPrezime = txtPrezime;
            this.txtMail = txtMail;
            this.txtTelefon = txtTelefon;
            this.txtAdresa = txtAdresa;
        }

        public ZahtevServeru(Object zahtev, Predmet predmet, TextField txtNaziv, ComboBox cmbSmer, SpinnerValueFactory<Integer> vfSemestar, SpinnerValueFactory<Integer> vfEspb, TextField txtProfesor) {
            this.zahtev = zahtev;
            this.predmet = predmet;
            this.txtNaziv = txtNaziv;
            this.cmbSmer = cmbSmer;
            this.vfSemestar = vfSemestar;
            this.vfEspb = vfEspb;
            this.txtProfesor = txtProfesor;
        }

        public ZahtevServeru(Object zahtev, Sala sala, TextField txtNaziv, TextField txtKapacitet, ComboBox cmbOprema) {
            this.zahtev = zahtev;
            this.sala = sala;
            this.txtNaziv = txtNaziv;
            this.txtKapacitet = txtKapacitet;
            this.cmbOprema = cmbOprema;
        }

        public ZahtevServeru(Object zahtev, IspitniRok ispitniRok, TextField txtNaziv, DatePicker dateOd, DatePicker dateDo) {
            this.zahtev = zahtev;
            this.ispitniRok = ispitniRok;
            this.txtNaziv = txtNaziv;
            this.dateOd = dateOd;
            this.dateDo = dateDo;
        }

        public ZahtevServeru(Object zahtev, ZakazivanjeSale zakazivanjeSale, ArrayList vrednosti, ComboBox cmbSala, ComboBox cmbZaposleni, ComboBox cmbPredmet, DatePicker dpDatum, Spinner spSatiOd, Spinner spMinutiOd, Spinner spSatiDo, Spinner spMinutiDo) {
            this.zahtev = zahtev;
            this.zakazivanjeSale = zakazivanjeSale;
            this.vrednosti = vrednosti;
            this.cmbSala = cmbSala;
            this.cmbZaposleni = cmbZaposleni;
            this.cmbPredmet = cmbPredmet;
            this.dpDatum = dpDatum;
            //*
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
                    outObj.writeObject("osvezi" + "Sluzba");
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
            } else if (zahtev.equals("osveziStudente")) {
                try {
                    outObj.writeObject("osvezi" + "Sluzba");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    sviStudenti.clear();
                    while (true) { //nisam sigurna za ovu proveru
                        odgovor = inObj.readObject();
                        if (odgovor.toString().equals("kraj")) {
                            break;
                        } else {
                            Student student = (Student) odgovor;
                            sviStudenti.add(student);
                        }
                    }
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            sviStudenti.sort(Comparator.comparing(s -> s.getBrojIndeksa()));
                            setSviStudenti(sviStudenti);
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
            } else if (zahtev.equals("osveziZaposlene")) {
                try {
                    outObj.writeObject("osvezi" + "Sluzba");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    sviZaposleni.clear();
                    while (true) { //nisam sigurna za ovu proveru
                        odgovor = inObj.readObject();
                        if (odgovor.toString().equals("kraj")) {
                            break;
                        } else {
                            Zaposleni zaposleni = (Zaposleni) odgovor;
                            sviZaposleni.add(zaposleni);
                        }
                    }
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setSviZaposleni(sviZaposleni);
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
            } else if (zahtev.equals("osveziPredmete")) {
                try {
                    outObj.writeObject("osvezi" + "Sluzba");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    sviPredmeti.clear();
                    odgovor = inObj.readObject();
                    sviPredmeti = (HashMap) odgovor;
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setSviPredmeti(sviPredmeti);
                            ObservableList<HashMap.Entry<Predmet, Zaposleni>> stavke = FXCollections.observableArrayList(sviPredmeti.entrySet());
                            getTabela().setItems(stavke);
                            getTabela().refresh();
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
            } else if (zahtev.equals("osveziSale")) {
                try {
                    outObj.writeObject("osvezi" + "Sluzba");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
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
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje svih liste
                            setSveSale(sveSale);
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
            } else if (zahtev.equals("osveziZakazivanjeSala")) {
                try {
                    outObj.writeObject("osvezi" + "Sluzba");
                    outObj.flush();
                    outObj.writeObject(zahtev);
                    outObj.flush();
                    sveZakazaneSale.clear();
                    odgovor = inObj.readObject();
                    sveZakazaneSale = (HashMap) odgovor;
                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {

                            //azuriranje/ponovno popunjavanje liste
                            setSveZakazaneSale(sveZakazaneSale);
                            ObservableList<HashMap.Entry<ZakazivanjeSale, ArrayList<String>>> stavke = FXCollections.observableArrayList(sveZakazaneSale.entrySet());
                            getTabela().setItems(stavke);
                            getTabela().refresh();
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
                    outObj.writeObject("osvezi" + "Sluzba");
                    outObj.flush();
                    outObj.writeObject(zahtev);
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
                            ObservableList<HashMap.Entry<ZakazivanjeSale, ArrayList<ArrayList>>> stavke = FXCollections.observableArrayList(rasporedIspita.entrySet());
                            getTabela().setItems(stavke);
                            getTabela().refresh();
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
            } else if (zahtev.equals("loginInfo")) {
                if (student != null) {
                    try {
                        outObj.writeObject(zahtev + "Student");
                        outObj.flush();
                        outObj.writeObject(student);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("postoji")) {
                                Login login = (Login) inObj.readObject();
                                Platform.runLater(new Runnable() {
                                    @Override
                                        public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Korisničko ime: " + login.getKorisnickoIme() + "\nLozinka: " + login.getLozinka());
                                        alert.showAndWait();
                                    }
                                });
                            } else if (odgovor.equals("nepostoji")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Ne postoje Login podaci za izabranog studenta.");
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
                        e.printStackTrace();
                    }
                } else if (zaposleni != null) {
                    try {
                        outObj.writeObject(zahtev + "Zaposleni");
                        outObj.flush();
                        outObj.writeObject(zaposleni);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("postoji")) {
                                Login login = (Login) inObj.readObject();
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Korisničko ime: " + login.getKorisnickoIme() + "\nLozinka: " + login.getLozinka());
                                        alert.showAndWait();
                                    }
                                });
                            } else if (odgovor.equals("nepostoji")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Ne postoje Login podaci za izabranog zaposlenog.");
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
                        e.printStackTrace();
                    }
                }
            } else if (zahtev.equals("izmeni")) {
                if (student != null) {
                    try {
                        outObj.writeObject(zahtev + "Studenta");
                        outObj.flush();
                        outObj.writeObject(student);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspesno izmenjeni podaci za studenta.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziStudente");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Nije uspela izmena podataka za studenta.");
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
                } else if (zaposleni != null) {
                    try {
                        outObj.writeObject(zahtev + "Zaposlenog");
                        outObj.flush();
                        outObj.writeObject(zaposleni);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno izmenjeni podaci za zaposlenog.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziZaposlene");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Nije uspela izmena podataka za zaposlenog.");
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
                } else if (predmet != null) {
                    try {
                        outObj.writeObject(zahtev + "Predmet");
                        outObj.flush();
                        outObj.writeObject(predmet);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno izmenjeni podaci za predmet.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziPredmete");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Nije uspela izmena podataka za predmet.");
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
                } else if (sala != null) {
                    try {
                        outObj.writeObject(zahtev + "Salu");
                        outObj.flush();
                        outObj.writeObject(sala);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno izmenjeni podaci za salu.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziSale");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Nije uspela izmena podataka za salu.");
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
                } else if (ispitniRok != null) {
                    try {
                        outObj.writeObject(zahtev + "IspitniRok");
                        outObj.flush();
                        outObj.writeObject(ispitniRok);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspesno izmenjeni podaci za ispitni rok.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziIspitneRokove");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Nije uspela izmena podataka za ispitni rok.");
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
                } else if (zakazivanjeSale != null) {
                    try {
                        outObj.writeObject(zahtev + "ZakazanuSalu");
                        outObj.flush();
                        outObj.writeObject(zakazivanjeSale);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspesno izmenjeni podaci za zakazanu salu.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziSale");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Nije uspela izmena podataka za zakazanu salu.");
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
            } else if (zahtev.equals("dodaj")) {
                if (student != null) {

                    //ukoliko je pozvan konstruktor za studenta
                    try {
                        outObj.writeObject(zahtev + "Studenta");
                        outObj.flush();
                        outObj.writeObject(student);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sviStudenti.add(student);
                                        getTabela().setItems(sviStudenti);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno dodat student u Bazu.");
                                        alert.showAndWait();
                                        txtIme.clear();
                                        txtPrezime.clear();
                                        txtAdresa.clear();
                                        txtMail.clear();
                                        txtTelefon.clear();
                                        cmbSmer.setValue(Student.tipSmera.avt);
                                        cmbFinansiranje.setValue(Student.tipFinansiranja.budzet);
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziStudente");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Taj student već postoji u Bazi.");
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
                } else if (zaposleni != null) {

                    //ukoliko je pozvan konstruktor za zaposlenog
                    try {
                        outObj.writeObject(zahtev + "Zaposlenog");
                        outObj.flush();
                        outObj.writeObject(zaposleni);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sviZaposleni.add(zaposleni);
                                        getTabela().setItems(sviZaposleni);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno dodat zaposleni u Bazu.");
                                        alert.showAndWait();
                                        txtIme.clear();
                                        txtPrezime.clear();
                                        txtAdresa.clear();
                                        txtMail.clear();
                                        txtTelefon.clear();
                                        cmbPozicija.setValue(Zaposleni.tipZaposlenog.profesor);
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziZaposlene");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Taj zaposleni već postoji u Bazi.");
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
                } else if (predmet != null) {

                    //ukoliko je pozvan konstruktor za predmet
                    try {
                        outObj.writeObject(zahtev + "Predmet");
                        outObj.flush();
                        outObj.writeObject(predmet);
                        outObj.flush();
                        String idZaposlenog = String.valueOf(sviZaposleni.stream().filter(z -> z.getImePrezime().equals(txtProfesor.getText())).mapToInt(Zaposleni::getIdZaposlenog).findFirst().orElse(0));
                        outObj.writeObject(idZaposlenog);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sviPredmeti.put(predmet, (Zaposleni) sviZaposleni.stream().filter(z -> z.getImePrezime().equals(txtProfesor.getText())).findFirst().orElse(new Zaposleni()));
                                        ObservableList<HashMap.Entry<Predmet, Zaposleni>> stavke = FXCollections.observableArrayList(sviPredmeti.entrySet());
                                        getTabela().setItems(stavke);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno dodat predmet u Bazu.");
                                        alert.showAndWait();
                                        cmbSmer.setValue(null);
                                        vfSemestar.setValue(0);
                                        vfEspb.setValue(0);
                                        txtNaziv.clear();
                                        txtProfesor.clear();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziPredmete");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Taj predmet već postoji u Bazi.");
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
                        e.printStackTrace();
                    }
                } else if (sala != null) {

                    //ukoliko je pozvan konstruktor za salu
                    try {
                        outObj.writeObject(zahtev + "Salu");
                        outObj.flush();
                        outObj.writeObject(sala);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sveSale.add(sala);
                                        getTabela().setItems(sveSale);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno dodata sala u Bazu.");
                                        alert.showAndWait();
                                        cmbOprema.setValue(Sala.tipOpreme.ništa);
                                        txtNaziv.clear();
                                        txtKapacitet.clear();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziSale");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Nije uspelo dodavanje sale u Bazu.");
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
                } else if (ispitniRok != null) {

                    //ukoliko je pozvan konstruktor za ispitni rok
                    try {
                        outObj.writeObject(zahtev + "IspitniRok");
                        outObj.flush();
                        outObj.writeObject(ispitniRok);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sviIspitniRokovi.add(ispitniRok);
                                        getTabela().setItems(sviIspitniRokovi);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno dodat ispitni rok u Bazu.");
                                        alert.showAndWait();
                                        txtNaziv.clear();
                                        dateOd.setValue(null);
                                        dateDo.setValue(null);
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziIspitneRokove");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.ERROR);
                                        alert.setContentText("Nije uspelo dodavanje ispitnog roka u Bazu.");
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
                } else if (zakazivanjeSale != null) {
                    //ukoliko je pozvan konstruktor za zakazivanje sale
                    try {
                        outObj.writeObject(zahtev + "ZakazivanjeSale");
                        outObj.flush();
                        outObj.writeObject(zakazivanjeSale);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sveZakazaneSale.put(zakazivanjeSale, vrednosti);
                                        ObservableList<HashMap.Entry<ZakazivanjeSale, ArrayList<String>>> stavke = FXCollections.observableArrayList(sveZakazaneSale.entrySet());
                                        getTabela().setItems(stavke);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno zakazana sala u Bazi.");
                                        alert.showAndWait();
                                        cmbSala.setValue(null);
                                        cmbZaposleni.setValue(null);
                                        cmbPredmet.setValue(null);
                                        dpDatum.setValue(null);
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziZakazivanjeSala");
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
                                alert.setContentText("Nije uspelo zakazivanje sale za izabran datum i vreme.");
                                alert.showAndWait();
                            }
                        });
                        //e.printStackTrace();
                    }
                }
            } else if (zahtev.equals("obrisi")) {
                //ukoliko je pozvan konstruktor za brisanje studenta/zaposlenog/predmeta/sale/zakazane sale
                if (student != null) {
                    try {
                        outObj.writeObject(zahtev + "Studenta");
                        outObj.flush();
                        outObj.writeObject(student);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sviStudenti.stream().filter(s -> s.getIdStudenta() == student.getIdStudenta()).map(s -> { s.setVidljiv(false); return s;});
                                        getTabela().setItems(sviStudenti);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno obrisan student.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziStudente");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Nije uspelo brisanje studenta.");
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
                } else if (zaposleni != null) {
                    try {
                        outObj.writeObject(zahtev + "Zaposlenog");
                        outObj.flush();
                        outObj.writeObject(zaposleni);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sviZaposleni.stream().filter(z -> z.getIdZaposlenog() == zaposleni.getIdZaposlenog()).map(z -> { z.setVidljiv(false); return z;});
                                        getTabela().setItems(sviZaposleni);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno obrisan zaposleni.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziZaposlene");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Nije uspelo brisanje zaposlenog.");
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
                } else if (predmet != null) {
                    try {
                        outObj.writeObject(zahtev + "Predmet");
                        outObj.flush();
                        outObj.writeObject(predmet);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                sviPredmeti.entrySet().stream().filter(p -> p.getKey().getIdPredmeta() == predmet.getIdPredmeta() && idZaposlenog == p.getValue().getIdZaposlenog()).map(p -> { p.getKey().setVidljiv(false); return p;});
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        ObservableList<HashMap.Entry<Predmet, Zaposleni>> stavke = FXCollections.observableArrayList(sviPredmeti.entrySet());
                                        getTabela().setItems(stavke);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno obrisan predmet.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziPredmete");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Nije uspelo brisanje predmeta.");
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
                } else if (sala != null) {
                    try {
                        outObj.writeObject(zahtev + "Salu");
                        outObj.flush();
                        outObj.writeObject(sala);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        sveSale.stream().filter(s -> s.getIdSale() == sala.getIdSale()).map(s -> { s.setVidljiv(false); return s;});
                                        getTabela().setItems(sveSale);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno obrisana sala.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziSale");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Nije uspelo brisanje sale.");
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
                }  else if (zakazivanjeSale != null) {
                    try {
                        outObj.writeObject(zahtev + "ZakazanuSalu");
                        outObj.flush();
                        outObj.writeObject(zakazivanjeSale);
                        outObj.flush();
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                sveZakazaneSale.entrySet().removeIf(s -> s.getKey().getIdSale() == zakazivanjeSale.getIdSale() && s.getKey().getVremePocetka() == zakazivanjeSale.getVremePocetka() && s.getKey().getDatum() == zakazivanjeSale.getDatum());
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        ObservableList<HashMap.Entry<ZakazivanjeSale, ArrayList<String>>> stavke = FXCollections.observableArrayList(sveZakazaneSale.entrySet());
                                        getTabela().setItems(stavke);
                                        getTabela().refresh();
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Uspešno oslobođena sala.");
                                        alert.showAndWait();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                ZahtevServeru zahtevServeru = new ZahtevServeru("osveziZakazivanjeSala");
                                zahtevServeru.KomunikacijaSaServerom();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAlert(Alert.AlertType.INFORMATION);
                                        alert.setContentText("Nije uspelo oslobađanje sale.");
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
            student = null;
            zaposleni = null;
            predmet = null;
            sala = null;
            ispitniRok = null;
            zakazivanjeSale = null;
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