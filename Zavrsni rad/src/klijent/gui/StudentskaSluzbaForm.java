package klijent.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.*;

import java.io.*;
import java.net.*;
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
    private static ObservableList<Student> sviStudenti = FXCollections.observableArrayList();
    private static ObservableList<Zaposleni> sviZaposleni = FXCollections.observableArrayList();
    private HashMap<Predmet, Zaposleni> sviPredmeti = new HashMap<>();
    private ObservableList<Sala> sveSale = FXCollections.observableArrayList();
    Pattern patternEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    Pattern patternTelefon = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");

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

    /**
     * Proverava da li ima aktivnih ispitnih rokova ili ne, pa postavlja prikaz za stavku Pocetna iz Menija
     */
    private static void pocetniPrikaz(BorderPane root, List<IspitniRok> sviIspitniRokovi) {

        boolean aktivan = false;
        for (IspitniRok ispitniRok : sviIspitniRokovi) {

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

    public StudentskaSluzbaForm(Stage stage, ObservableList<IspitniRok> sviIspitniRokovi, ObservableList<Student> sviStudenti, ObservableList<Zaposleni> sviZaposleni, HashMap<Predmet, Zaposleni> polozeniPredmeti, ObservableList<Sala> sveSale) {

        super();
        initOwner(stage);

        setSviIspitniRokovi(sviIspitniRokovi);
        setSviStudenti(sviStudenti);
        setSviZaposleni(sviZaposleni);
        setSviPredmeti(sviPredmeti);
        setSveSale(sveSale);

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
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

            ocistiPane(root);
            pocetniPrikaz(root, sviIspitniRokovi);
        });
        Menu pocetnaMenu = new Menu("", lblPocetna);

        //Klikom na stavku STUDENTI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za studente, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi student u Bazu podataka
        Label lblStudenti = new Label("STUDENTI");
        lblStudenti.setOnMouseClicked(mouseEvent -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

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
                ObservableList<Student> nadjeni = (sviStudenti.stream().filter(s -> s.getIme().toLowerCase().contains(trazeno) || s.getPrezime().toLowerCase().contains(trazeno) || String.valueOf(s.getBrojIndeksa()).contains(trazeno) || String.valueOf(s.getSemestar()).contains(trazeno) || (s.getAdresa() != null && s.getAdresa().toLowerCase().contains(trazeno)) || s.getFinansiranje().toLowerCase().contains(trazeno) || (s.getBrojTelefona() != null && s.getBrojTelefona().toLowerCase().contains(trazeno)) || (s.getEmail() != null && s.getEmail().toLowerCase().contains(trazeno)))).collect(Collectors.toCollection(FXCollections::observableArrayList));
                tableStudenti.setItems(nadjeni);
            });
            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            tableStudenti.setEditable(true);
            tableStudenti.getColumns().clear();

            TableColumn colIme = new TableColumn("Ime");
            colIme.setCellValueFactory(new PropertyValueFactory<Student, String>("ime"));
            colIme.setCellFactory(TextFieldTableCell.forTableColumn());
            colIme.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Student, String> tabela) {
                            //AKO JE UNETO IME I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setIme(tabela.getNewValue());
                            if(!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getIme().equals("")) {

                                Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniStudent));
                                t.setDaemon(true);
                                t.start();
                            } else {
                                //poruka za neispravan unos
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Molim vas unesite ime za studenta");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //da osvezi podatke na formi
                                Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                                t.setDaemon(true);
                                t.start();
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
                            //AKO JE UNETO PREZIME I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setPrezime(tabela.getNewValue());
                            if (!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getPrezime().equals("")) {

                                Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniStudent));
                                t.setDaemon(true);
                                t.start();
                            } else {
                                //poruka za neispravan unos
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Molim vas unesite prezime za studenta");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //da osvezi podatke na formi
                                Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                                t.setDaemon(true);
                                t.start();
                            }
                        }
                    }
            );
            colPrezime.setMinWidth(150);
            TableColumn colIndex = new TableColumn("Broj indeksa");
            colIndex.setCellValueFactory(new PropertyValueFactory<Student, String>("brojIndeksa"));
            colIndex.setMinWidth(50);
            TableColumn colFinansiranje = new TableColumn("Finansiranje");
            colFinansiranje.setCellValueFactory(new PropertyValueFactory<Student, String>("finansiranje"));
            colFinansiranje.setCellFactory(ComboBoxTableCell.forTableColumn(Student.tipFinansiranja.values()));
            colFinansiranje.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Predmet, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Predmet, String> tabela) {

                        }
                    }
            );
            colFinansiranje.setMinWidth(50);
            TableColumn colAdresa = new TableColumn("Adresa");
            colAdresa.setCellValueFactory(new PropertyValueFactory<Student, String>("adresa"));
            colAdresa.setCellFactory(TextFieldTableCell.forTableColumn());
            colAdresa.setOnEditCommit(
                    new EventHandler<TableColumn.CellEditEvent<Student, String>>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent<Student, String> tabela) {
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setAdresa(tabela.getNewValue());
                            Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                            Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniStudent));
                            t.setDaemon(true);
                            t.start();
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
                            //AKO JE EMAIL OBRISAN ILI JE UNET ISPRAVNO
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setEmail(tabela.getNewValue());

                            String mail = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail();
                            Matcher emailMatcher = patternEmail.matcher(mail);
                            boolean validniEmail = emailMatcher.find();

                            if(mail.length() == 0 || validniEmail) {
                                Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniStudent));
                                t.setDaemon(true);
                                t.start();
                            } else {
                                //poruka za neispravan unos
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Molim vas unesite email ispravno");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //da osvezi podatke na formi
                                Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                                t.setDaemon(true);
                                t.start();
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
                            //AKO JE TELEFON OBRISAN ILI JE UNET ISPRAVNO
                            ((Student) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setBrojTelefona(tabela.getNewValue());

                            String telefon = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona();
                            Matcher telefonMatcher = patternTelefon.matcher(telefon);
                            boolean validniTelefon = telefonMatcher.find();

                            if(telefon.length() == 0 || validniTelefon) {
                                Student izabraniStudent = (Student) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniStudent));
                                t.setDaemon(true);
                                t.start();
                            } else {
                                //poruka za neispravan unos
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Molim vas unesite broj telefona ispravno");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //da osvezi podatke na formi
                                Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                                t.setDaemon(true);
                                t.start();
                            }
                        }
                    }
            );
            colTelefon.setMinWidth(100);
            TableColumn colAktivan = new TableColumn("Aktivan");
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
                            Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniStudent));
                            t.setDaemon(true);
                            t.start();

                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }
            });
            colAktivan.setMinWidth(50);

            tableStudenti.setItems(sviStudenti);
            //sredjuje problem za dodatu kolonu
            tableStudenti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableStudenti.setPrefHeight(500);
            tableStudenti.getColumns().addAll(colIme, colPrezime, colIndex, colFinansiranje, colAdresa, colEmail, colTelefon, colAktivan);

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
                if (cmbFinansiranje.getValue() != null) {
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
                        Student student = new Student(Student.idNovogStudenta(sviStudenti, smer, godinaUpisa), godinaUpisa, Student.tipSmera.valueOf(smer), ime, prezime, Student.tipFinansiranja.valueOf(finansiranje), adresa, email, brojTelefona, true);
                        Thread t = new Thread(new RunnableZahtevServeru("dodaj", student, txtIme, txtPrezime, txtAdresa, txtMail, txtTelefon, cmbSmer, cmbFinansiranje));
                        t.setDaemon(true);
                        t.start();

                    } else {
                        //PROVERA KOJI NIJE UNET ISPRAVNO I BOJENJE OKVIRA TOG POLJA
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (ime.length() == 0) {
                                    txtIme.setStyle("-fx-border-color: red;");
                                } else {
                                    txtIme.setStyle("-fx-border-width: 0;");
                                }
                                if (prezime.length() == 0) {
                                    txtPrezime.setStyle("-fx-border-color: red;");
                                } else {
                                    txtPrezime.setStyle("-fx-border-width: 0;");
                                }
                                if (email.length() != 0) {
                                    if (!validniEmail) {
                                        txtMail.setStyle("-fx-border-color: red;");
                                    } else {
                                        txtMail.setStyle("-fx-border-width: 0;");
                                    }
                                }
                                if (brojTelefona.length() != 0) {
                                    if (!validniTelefon) {
                                        txtTelefon.setStyle("-fx-border-color: red;");
                                    } else {
                                        txtTelefon.setStyle("-fx-border-width: 0;");
                                    }
                                }
                                Stage dialog = new Dialog(getStage(), "Molim vas unesite podatke");
                                dialog.sizeToScene();
                                dialog.show();
                            }
                        });

                    }
                }
            });

            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);
            btnObrisi.setOnAction(e -> {
                if (tableStudenti.getSelectionModel().getSelectedItem() != null) {
                    Student izabraniStudent = tableStudenti.getSelectionModel().getSelectedItem();
                    Thread t = new Thread(new RunnableZahtevServeru("obrisi", tableStudenti, izabraniStudent));
                    t.setDaemon(true);
                    t.start();
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Stage dialog = new Dialog(getStage(), "Molim vas izaberite studenta u tabeli");
                            dialog.sizeToScene();
                            dialog.show();
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
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

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
                            //AKO JE UNETO IME I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setIme(tabela.getNewValue());
                            if(!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getIme().equals("")) {

                                Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniZaposleni));
                                t.setDaemon(true);
                                t.start();
                            } else {
                                //poruka za neispravan unos
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Molim vas unesite ime za zaposlenog");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //da osvezi podatke na formi
                                Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                                t.setDaemon(true);
                                t.start();
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
                            //AKO JE UNETO PREZIME I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setPrezime(tabela.getNewValue());
                            if(!tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getPrezime().equals("")) {

                                Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniZaposleni));
                                t.setDaemon(true);
                                t.start();
                            } else {
                                //poruka za neispravan unos
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Molim vas unesite prezime za zaposlenog");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //da osvezi podatke na formi
                                Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                                t.setDaemon(true);
                                t.start();
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
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setAdresa(tabela.getNewValue());
                            Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                            Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniZaposleni));
                            t.setDaemon(true);
                            t.start();
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
                            //AKO JE EMAIL OBRISAN ILI JE UNET ISPRAVNO
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setEmail(tabela.getNewValue());

                            String mail = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getEmail();
                            Matcher emailMatcher = patternEmail.matcher(mail);
                            boolean validniEmail = emailMatcher.find();

                            if(mail.length() == 0 || validniEmail) {
                                Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniZaposleni));
                                t.setDaemon(true);
                                t.start();
                            } else {
                                //poruka za neispravan unos
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Molim vas unesite email ispravno");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //da osvezi podatke na formi
                                Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                                t.setDaemon(true);
                                t.start();
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
                            //AKO JE TELEFON OBRISAN ILI JE UNET ISPRAVNO
                            ((Zaposleni) tabela.getTableView().getItems().get(
                                    tabela.getTablePosition().getRow())
                            ).setBrojTelefona(tabela.getNewValue());

                            String telefon = tabela.getTableView().getItems().get(tabela.getTablePosition().getRow()).getBrojTelefona();
                            Matcher telefonMatcher = patternTelefon.matcher(telefon);
                            boolean validniTelefon = telefonMatcher.find();

                            if(telefon.length() == 0 || validniTelefon) {
                                Zaposleni izabraniZaposleni = (Zaposleni) tabela.getTableView().getItems().get(tabela.getTablePosition().getRow());
                                Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniZaposleni));
                                t.setDaemon(true);
                                t.start();
                            } else {
                                //poruka za neispravan unos
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Molim vas unesite broj telefona ispravno");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //da osvezi podatke na formi
                                Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                                t.setDaemon(true);
                                t.start();
                            }
                        }
                    }
            );
            colTelefon.setMinWidth(150);
            TableColumn colAktivan = new TableColumn("Aktivan");
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
                            Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniZaposleni));
                            t.setDaemon(true);
                            t.start();

                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }
            });
            colAktivan.setMinWidth(50);

            tableZaposleni.setItems(sviZaposleni);
            //sredjuje problem za dodatu kolonu
            tableZaposleni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableZaposleni.setPrefHeight(500);
            tableZaposleni.getColumns().addAll(colPozicija, colIme, colPrezime, colAdresa, colEmail, colTelefon, colAktivan);

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
                    Thread t = new Thread(new RunnableZahtevServeru("dodaj", zaposleni, cmbPozicija, txtIme, txtPrezime, txtMail, txtTelefon, txtAdresa));
                    t.setDaemon(true);
                    t.start();
                } else {
                    //PROVERA KOJI NIJE UNET ISPRAVNO I BOJENJE OKVIRA TOG POLJA
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (ime.length() == 0) {
                                txtIme.setStyle("-fx-border-color: red;");
                            } else {
                                txtIme.setStyle("-fx-border-width: 0;");
                            }
                            if (prezime.length() == 0) {
                                txtPrezime.setStyle("-fx-border-color: red;");
                            } else {
                                txtPrezime.setStyle("-fx-border-width: 0;");
                            }
                            if (email.length() != 0) {
                                if (!validniEmail) {
                                    txtMail.setStyle("-fx-border-color: red;");
                                } else {
                                    txtMail.setStyle("-fx-border-width: 0;");
                                }
                            }
                            if (brojTelefona.length() != 0) {
                                if (!validniTelefon) {
                                    txtTelefon.setStyle("-fx-border-color: red;");
                                } else {
                                    txtTelefon.setStyle("-fx-border-width: 0;");
                                }
                            }
                            Stage dialog = new Dialog(getStage(), "Molim vas unesite podatke");
                            dialog.sizeToScene();
                            dialog.show();
                        }
                    });
                }
            });
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);
            btnObrisi.setOnAction(e -> {
                if (tableZaposleni.getSelectionModel().getSelectedItem() != null) {
                    Zaposleni izabraniZaposleni = tableZaposleni.getSelectionModel().getSelectedItem();
                    Thread t = new Thread(new RunnableZahtevServeru("obrisi", tableZaposleni, izabraniZaposleni));
                    t.setDaemon(true);
                    t.start();

                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Stage dialog = new Dialog(getStage(), "Molim vas izaberite zaposlenog u tabeli");
                            dialog.sizeToScene();
                            dialog.show();
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

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

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

            TableView<HashMap.Entry<Predmet, Zaposleni>> tablePredmeti = new TableView<>();

            Button btnPretrazi = new Button("pretraži");
            btnPretrazi.setFont(font15);
            btnPretrazi.setOnMouseClicked(event -> {
                String trazeno = txtPretraga.getText().toLowerCase();
                ObservableList<HashMap.Entry<Predmet, Zaposleni>> nadjeni = FXCollections.observableArrayList(sviPredmeti.entrySet().stream().filter(map -> String.valueOf(map.getKey().getIdPredmeta()).contains(trazeno) || map.getKey().getNaziv().toLowerCase().contains(trazeno) || (map.getKey().getStudijskiSmer() != null && map.getKey().getStudijskiSmer().toLowerCase().contains(trazeno)) || (String.valueOf(map.getKey().getSemestar()) != null && String.valueOf(map.getKey().getSemestar()).contains(trazeno)) || (String.valueOf(map.getKey().getEspb()) != null && String.valueOf(map.getKey().getEspb()).contains(trazeno)) || (map.getValue().getIme() + " " + map.getValue().getPrezime()).toLowerCase().contains(trazeno)).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue())).entrySet());
                tablePredmeti.setItems(nadjeni);
            });

            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            tablePredmeti.setEditable(true);
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
                    TablePosition<Map.Entry<Predmet, Zaposleni>, String> pozicija = entryStringCellEditEvent.getTablePosition();
                    String novaVrednost = entryStringCellEditEvent.getNewValue();

                    ((Map.Entry<Predmet, Zaposleni>) entryStringCellEditEvent.getTableView().getItems().get(
                            entryStringCellEditEvent.getTablePosition().getRow())
                    ).getKey().setNaziv(novaVrednost);

                    //AKO JE UNET NAZIV I NIJE PRAZAN STRING, U SUPROTNOM PORUKA O GRESCI
                    if(!novaVrednost.equals("")) {
                        int red = pozicija.getRow();
                        Predmet izabraniPredmet = entryStringCellEditEvent.getTableView().getItems().get(red).getKey();
                        Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniPredmet));
                        t.setDaemon(true);
                        t.start();
                    } else {
                        //poruka za neispravan unos
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Stage dialog = new Dialog(getStage(), "Molim vas unesite naziv predmeta");
                                dialog.sizeToScene();
                                dialog.show();
                            }
                        });
                        //da osvezi podatke na formi
                        Thread t = new Thread(new RunnableZahtevServeru("osvezi"));
                        t.setDaemon(true);
                        t.start();
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
            //TODO: spinner unutar polja, da mora ili prazno - null ili broj, bez greske za unet prazan string ili tekst
            TableColumn<Map.Entry<Predmet, Zaposleni>, Integer> colSemestar = new TableColumn<>("Semestar");
            colSemestar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Integer> p) {
                    return new SimpleObjectProperty<Integer>(p.getValue().getKey().getSemestar());
                }

            });
            colSemestar.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            colSemestar.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Predmet, Zaposleni>, Integer>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Map.Entry<Predmet, Zaposleni>, Integer> entryStringCellEditEvent) {
                    TablePosition<Map.Entry<Predmet, Zaposleni>, Integer> pozicija = entryStringCellEditEvent.getTablePosition();
                    Integer novaVrednost = entryStringCellEditEvent.getNewValue();

                    ((Map.Entry<Predmet, Zaposleni>) entryStringCellEditEvent.getTableView().getItems().get(
                            entryStringCellEditEvent.getTablePosition().getRow())
                    ).getKey().setSemestar(novaVrednost);

                    int red = pozicija.getRow();
                    Predmet izabraniPredmet = entryStringCellEditEvent.getTableView().getItems().get(red).getKey();
                    Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniPredmet));
                    t.setDaemon(true);
                    t.start();
                }
            });
            colSemestar.setMinWidth(50);
            TableColumn<Map.Entry<Predmet, Zaposleni>, Integer> colEspb = new TableColumn<>("ESPB");
            colEspb.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Integer>, ObservableValue<Integer>>() {

                @Override
                public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map.Entry<Predmet, Zaposleni>, Integer> p) {
                    return new SimpleObjectProperty<Integer>(p.getValue().getKey().getEspb());
                }

            });
            colEspb.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            //TODO: spinner unutar polja, da mora ili prazno - null ili broj, bez greske za unet prazan string ili tekst
            colEspb.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map.Entry<Predmet, Zaposleni>, Integer>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Map.Entry<Predmet, Zaposleni>, Integer> entryStringCellEditEvent) {
                    TablePosition<Map.Entry<Predmet, Zaposleni>, Integer> pozicija = entryStringCellEditEvent.getTablePosition();
                    Integer novaVrednost = entryStringCellEditEvent.getNewValue();

                    ((Map.Entry<Predmet, Zaposleni>) entryStringCellEditEvent.getTableView().getItems().get(
                            entryStringCellEditEvent.getTablePosition().getRow())
                    ).getKey().setEspb(novaVrednost);

                    int red = pozicija.getRow();
                    Predmet izabraniPredmet = entryStringCellEditEvent.getTableView().getItems().get(red).getKey();
                    Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniPredmet));
                    t.setDaemon(true);
                    t.start();
                }

            });
            colEspb.setMinWidth(50);
            TableColumn<Map.Entry<Predmet, Zaposleni>, CheckBox> colAktivan = new TableColumn<>("Aktivan");
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
                            Thread t = new Thread(new RunnableZahtevServeru("izmeni", izabraniPredmet));
                            t.setDaemon(true);
                            t.start();

                        }
                    });

                    return new SimpleObjectProperty<CheckBox>(checkBox);

                }
            });
            colAktivan.setMinWidth(50);

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
            SpinnerValueFactory<Integer> vfSemestar = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 6);
            vfSemestar.setValue(0);
            spSemestar.setValueFactory(vfSemestar);
            vfSemestar.setValue(0);
            spSemestar.setMinWidth(50);
            spSemestar.setMaxWidth(50);

            Label lblEspb = new Label("Broj espb: ");
            lblEspb.setMinWidth(Region.USE_PREF_SIZE);
            Spinner<Integer> spEspb = new Spinner();
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
                //TODO: metoda u predmetu koja generise sifru
                //TODO: moze id za izmenu da se unosi, a moze i direktno izmena u tabeli

                String naziv = txtNaziv.getText();
                String smer = null;
                if (cmbSmer.getValue() != null) {
                    smer = cmbSmer.getValue().toString();
                }
                int semestar = vfSemestar.getValue();
                int espb = vfEspb.getValue();
                String profesor = txtProfesor.getText();

                //AKO JE UNET NAZIV, OSTALI SU OPCIONI JER SMER MOZE BITI ZAJEDNICKI
                if (naziv.length() != 0) {
                    Predmet predmet;
                    if (smer != null) {
                        predmet = new Predmet(Predmet.idNovogPredmeta(FXCollections.observableArrayList(sviPredmeti.keySet()), smer), naziv, Predmet.tipSmera.valueOf(smer), semestar, espb, true);
                    } else {
                        predmet = new Predmet(Predmet.idNovogPredmeta(FXCollections.observableArrayList(sviPredmeti.keySet()), smer), naziv, null, semestar, espb, true);
                    }
                    Thread t = new Thread(new RunnableZahtevServeru("dodaj", predmet, txtNaziv, cmbSmer, vfSemestar, vfEspb, txtProfesor));
                    t.setDaemon(true);
                    t.start();

                } else {
                    txtNaziv.setStyle("-fx-border-color: red;");

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Stage dialog = new Dialog(getStage(), "Molim vas unesite podatke");
                            dialog.sizeToScene();
                            dialog.show();
                        }
                    });
                }
            });
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);
            btnObrisi.setOnAction(e -> {
                        if (tablePredmeti.getSelectionModel().getSelectedItem() != null) {
                            //TODO: srediti za brisanje predmeta da se azurira i profesor u tabeli raspodela predmeta
                            Map.Entry<Predmet, Zaposleni> izabraniPredmet = tablePredmeti.getSelectionModel().getSelectedItem();
                            Thread t = new Thread(new RunnableZahtevServeru("obrisi", tablePredmeti, izabraniPredmet.getKey()));
                            t.setDaemon(true);
                            t.start();
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Stage dialog = new Dialog(getStage(), "Molim vas izaberite predmet u tabeli");
                                    dialog.sizeToScene();
                                    dialog.show();
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

        Menu ispitniRokMenu = new Menu("ISPITNI ROK");
        MenuItem prikazSalaMenuItem = new MenuItem("Prikaz sala");
        MenuItem aktivirajMenuItem = new MenuItem("Aktiviraj");
        MenuItem rasporedIspitaMenuItem = new MenuItem("Raspored ispita");

        ispitniRokMenu.getItems().addAll(prikazSalaMenuItem, aktivirajMenuItem, rasporedIspitaMenuItem);

        //Klikom na podmeni ISPITNI ROK iz Menija i na njegovu stavku Prikaz sala poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za sale, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i nova sala u Bazu podataka
        prikazSalaMenuItem.setOnAction(event -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5, 10, 10, 10));

            TableView<Sala> tableSale = new TableView<>();
            tableSale.setEditable(true);
            tableSale.getColumns().clear();

            TableColumn colNaziv = new TableColumn("Naziv");
            colNaziv.setCellValueFactory(new PropertyValueFactory<Sala, String>("naziv"));
            colNaziv.setMinWidth(200);
            TableColumn colKapacitet = new TableColumn("Broj mesta");
            colKapacitet.setCellValueFactory(new PropertyValueFactory<Sala, String>("brojMesta"));
            colKapacitet.setMinWidth(100);
            TableColumn colOprema = new TableColumn("Oprema");
            colOprema.setCellValueFactory(new PropertyValueFactory<Sala, String>("oprema"));
            colOprema.setMinWidth(200);

            tableSale.setItems(sveSale);
            //sredjuje problem za dodatu kolonu
            tableSale.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableSale.setPrefHeight(550);
            tableSale.getColumns().addAll(colNaziv, colKapacitet, colOprema);

            TextField txtNaziv = new TextField();
            txtNaziv.setPromptText("Naziv");
            txtNaziv.setMinWidth(150);

            TextField txtKapacitet = new TextField();
            txtKapacitet.setPromptText("Kapacitet");
            txtKapacitet.setMinWidth(100);

            ComboBox cmbOprema = new ComboBox();
            cmbOprema.getItems().addAll(Sala.tipOpreme.values());
            cmbOprema.setValue(Sala.tipOpreme.računari);
            cmbOprema.setMinWidth(Region.USE_PREF_SIZE);
            cmbOprema.setStyle("-fx-font: 12px \"Arial\";");

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5, 0, 0, 0));
            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setMinWidth(60);
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);
            hboxAkcija.getChildren().addAll(txtNaziv, txtKapacitet, cmbOprema, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(tableSale, hboxAkcija);
            root.setCenter(vbox);
        });
        //Klikom na podmeni ISPITNI ROK iz Menija i na njegovu stavku Aktiviraj poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za ispitne rokove, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi ispitni rok u Bazu podataka
        aktivirajMenuItem.setOnAction(event -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

            ocistiPane(root);
            TableView<IspitniRok> tableIspitniRokovi = new TableView<>();
            tableIspitniRokovi.setEditable(true);

            tableIspitniRokovi.getColumns().clear();
            TableColumn colNaziv = new TableColumn("Naziv");
            colNaziv.setCellValueFactory(new PropertyValueFactory<Sala, String>("naziv"));
            colNaziv.setMinWidth(200);
            TableColumn colPocetak = new TableColumn("Početak");
            colPocetak.setCellValueFactory(new PropertyValueFactory<Sala, String>("datumPocetka"));
            colPocetak.setMinWidth(100);
            TableColumn colKraj = new TableColumn("Kraj");
            colKraj.setCellValueFactory(new PropertyValueFactory<Sala, String>("datumKraja"));
            colKraj.setMinWidth(100);
            TableColumn colAktivan = new TableColumn("Aktivan");
            //TODO: Staviti checkbox umesto true/false
            colAktivan.setCellValueFactory(new PropertyValueFactory<Sala, String>("aktivnost"));
            colAktivan.setMinWidth(200);

            tableIspitniRokovi.setItems(sviIspitniRokovi);
            //sredjuje problem za dodatu kolonu
            tableIspitniRokovi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableIspitniRokovi.setPrefHeight(550);
            tableIspitniRokovi.getColumns().addAll(colNaziv, colPocetak, colKraj, colAktivan);

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
            Label lblDo = new Label("Do: ");
            DatePicker dateDo = new DatePicker();

            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setMinWidth(60);
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);
            hboxAkcija.getChildren().addAll(txtNaziv, lblOd, dateOd, lblDo, dateDo, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(tableIspitniRokovi, hboxAkcija);
            root.setCenter(vbox);

        });
        //Klikom na podmeni ISPITNI ROK iz Menija i na njegovu stavku Raspored ispita poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju svi ispitni rokovi i njihove informacije iz Baze podataka
        rasporedIspitaMenuItem.setOnAction(event -> {

            //kada se prebaci na drugu stavku iz menija da osvezi podatke
            Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
            //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
            runnableZahtevServeru.setDaemon(true);
            runnableZahtevServeru.start();

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
            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            TableView<String> tableRasporedIspita = new TableView<String>();
            tableRasporedIspita.getColumns().clear();

            TableColumn<String, String> colSifra = new TableColumn("Šifra");    //TODO: nazive kolona dobiti iz baze
            colSifra.setMinWidth(100);
            TableColumn<String, String> colPredmet = new TableColumn("Predmet");
            colPredmet.setMinWidth(240);
            TableColumn<String, String> colProfesor = new TableColumn("Profesor");
            colProfesor.setMinWidth(200);
            TableColumn<String, String> colDatum = new TableColumn("Datum");
            colDatum.setMinWidth(100);
            TableColumn<String, String> colVreme = new TableColumn("Vreme");
            colVreme.setMinWidth(50);
            TableColumn<String, String> colSala = new TableColumn("Sala");
            colSala.setMinWidth(50);
            TableColumn<String, String> colBrPrijava = new TableColumn("Prijave");
            colBrPrijava.setMinWidth(50);

            tableRasporedIspita.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableRasporedIspita.getColumns().addAll(colSifra, colPredmet, colProfesor, colDatum, colVreme, colSala, colBrPrijava);
            tableRasporedIspita.setPrefHeight(550);
            //TODO: na klik broja prijava da vidi detaljno sve brojeve indeksa

            vbox.getChildren().addAll(hBoxPretraga, tableRasporedIspita);
            root.setCenter(vbox);

        });

        menuBar.setStyle("-fx-padding: 3 6 3 6;");
        menuBar.getMenus().addAll(pocetnaMenu, studentiMenu, zaposleniMenu, predmetiMenu, ispitniRokMenu);

    }

    /** Klasa RunnableZahtevServeru namenjena za slanje zahteva serveru i
     * dodavanje/izmenu/brisanje studenta/zaposlenog/predmeta.
     * @author Biljana Stanojevic   */
    private class RunnableZahtevServeru implements Runnable {

        private static final int TCP_PORT = 9000;
        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object zahtev;
        private Object odgovor;
        private Student student;
        private Zaposleni zaposleni;
        private Predmet predmet;
        private TableView tabela;
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

        //konstuktor za osvezavanje podataka
        public RunnableZahtevServeru(Object zahtev) {
            this.zahtev = zahtev;
        }

        //konstruktori za izmenu
        public RunnableZahtevServeru(Object zahtev, Student student) {
            this.zahtev = zahtev;
            this.student = student;
        }

        public RunnableZahtevServeru(Object zahtev, Zaposleni zaposleni) {
            this.zahtev = zahtev;
            this.zaposleni = zaposleni;
        }

        public RunnableZahtevServeru(Object zahtev, Predmet predmet) {
            this.zahtev = zahtev;
            this.predmet = predmet;
        }

        //konstruktori za dodavanje
        public RunnableZahtevServeru(Object zahtev, Student student, TextField txtIme, TextField txtPrezime, TextField txtAdresa, TextField txtMail, TextField txtTelefon, ComboBox cmbSmer, ComboBox cmbFinansiranje) {
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

        public RunnableZahtevServeru(Object zahtev, Zaposleni zaposleni, ComboBox cmbPozicija, TextField txtIme, TextField txtPrezime, TextField txtMail, TextField txtTelefon, TextField txtAdresa) {
            this.zahtev = zahtev;
            this.zaposleni = zaposleni;
            this.cmbPozicija = cmbPozicija;
            this.txtIme = txtIme;
            this.txtPrezime = txtPrezime;
            this.txtMail = txtMail;
            this.txtTelefon = txtTelefon;
            this.txtAdresa = txtAdresa;
        }

        public RunnableZahtevServeru(Object zahtev, Predmet predmet, TextField txtNaziv, ComboBox cmbSmer, SpinnerValueFactory<Integer> vfSemestar, SpinnerValueFactory<Integer> vfEspb, TextField txtProfesor) {
            this.zahtev = zahtev;
            this.predmet = predmet;
            this.txtNaziv = txtNaziv;
            this.cmbSmer = cmbSmer;
            this.vfSemestar = vfSemestar;
            this.vfEspb = vfEspb;
            this.txtProfesor = txtProfesor;
        }

        public RunnableZahtevServeru(Object zahtev, TableView<Student> tabela, Student student) {
            this.zahtev = zahtev;
            this.tabela = tabela;
            this.student = student;
        }

        public RunnableZahtevServeru(Object zahtev, TableView<Zaposleni> tabela, Zaposleni zaposleni) {
            this.zahtev = zahtev;
            this.tabela = tabela;
            this.zaposleni = zaposleni;
        }

        public RunnableZahtevServeru(Object zahtev, TableView<HashMap.Entry<Predmet, Zaposleni>> tabela, Predmet predmet) {
            this.zahtev = zahtev;
            this.tabela = tabela;
            this.predmet = predmet;
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
            if(zahtev.equals("osvezi")) {
                if (student == null && zaposleni == null) {
                    //ukoliko je pozvan konstruktor za studentsku sluzbu
                    try {
                        outObj.writeObject(zahtev + "Sluzba");
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

                                //azuriranje/ponovno popunjavanje svih listi
                                setSviIspitniRokovi(sviIspitniRokovi);
                                setSviStudenti(sviStudenti);
                                setSviZaposleni(sviZaposleni);
                                setSviPredmeti(sviPredmeti);
                                setSveSale(sveSale);
                                System.out.println("Osvezeni podaci sa strane servera");

                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if(zahtev.equals("izmeni")) {
                if(student != null) {
                    try {
                        outObj.writeObject(zahtev + "Studenta");
                        outObj.writeObject(student);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno izmenjeni podaci za studenta");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Nije uspela izmena podataka za studenta");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if(zaposleni != null) {
                    try {
                        outObj.writeObject(zahtev + "Zaposlenog");
                        outObj.writeObject(zaposleni);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno izmenjeni podaci za zaposlenog");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Nije uspela izmena podataka za zaposlenog");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if(predmet != null) {
                    try {
                        outObj.writeObject(zahtev + "Predmet");
                        outObj.writeObject(predmet);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno izmenjeni podaci za predmet");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Nije uspela izmena podataka za predmet");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if(zahtev.equals("dodaj")) {
                if (student != null) {

                    //ukoliko je pozvan konstruktor za studenta
                    try {
                        outObj.writeObject(zahtev + "Studenta");
                        outObj.writeObject(student);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno dodat student u Bazu");
                                        dialog.sizeToScene();
                                        dialog.show();
                                        txtIme.setStyle("-fx-border-width: 0;");
                                        txtPrezime.setStyle("-fx-border-width: 0;");
                                        txtAdresa.setStyle("-fx-border-width: 0;");
                                        txtMail.setStyle("-fx-border-width: 0;");
                                        txtTelefon.setStyle("-fx-border-width: 0;");
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
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Taj student vec postoji u Bazi");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (zaposleni != null) {

                    //ukoliko je pozvan konstruktor za zaposlenog
                    try {
                        outObj.writeObject(zahtev + "Zaposlenog");
                        outObj.writeObject(zaposleni);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno dodat zaposleni u Bazu");
                                        dialog.sizeToScene();
                                        dialog.show();
                                        txtIme.setStyle("-fx-border-width: 0;");
                                        txtPrezime.setStyle("-fx-border-width: 0;");
                                        txtMail.setStyle("-fx-border-width: 0;");
                                        txtTelefon.setStyle("-fx-border-width: 0;");
                                        txtIme.clear();
                                        txtPrezime.clear();
                                        txtAdresa.clear();
                                        txtMail.clear();
                                        txtTelefon.clear();
                                        cmbPozicija.setValue(Zaposleni.tipZaposlenog.profesor);
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Taj zaposleni vec postoji u Bazi");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (predmet != null) {

                    //ukoliko je pozvan konstruktor za predmet
                    try {
                        outObj.writeObject(zahtev + "Predmet");
                        outObj.writeObject(predmet);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno dodat predmet u Bazu");
                                        dialog.sizeToScene();
                                        dialog.show();
                                        txtNaziv.setStyle("-fx-border-width: 0;");
                                        cmbSmer.setValue(null);
                                        vfSemestar.setValue(0);
                                        vfEspb.setValue(0);
                                        txtNaziv.clear();
                                        txtProfesor.clear();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Taj predmet vec postoji u Bazi");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (zahtev.equals("obrisi")) {
                //ukoliko je pozvan konstruktor za brisanje studenta/zaposlenog/predmeta
                if (student != null) {
                    try {
                        outObj.writeObject(zahtev + "Studenta");
                        outObj.writeObject(student);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno obrisan student");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "...");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (zaposleni != null) {
                    try {
                        outObj.writeObject(zahtev + "Zaposlenog");
                        outObj.writeObject(zaposleni);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno obrisan zaposleni");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "...");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (predmet != null) {
                    try {
                        outObj.writeObject(zahtev + "Predmet");
                        outObj.writeObject(predmet);
                        try {
                            odgovor = inObj.readObject();
                            if (odgovor.equals("uspelo")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "Uspesno obrisan predmet");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                                //kada snimi da osvezi podatke kako bi se odmah prikazali na formi
                                Thread runnableZahtevServeru = new Thread(new RunnableZahtevServeru("osvezi"));
                                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                                runnableZahtevServeru.setDaemon(true);
                                runnableZahtevServeru.start();
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage dialog = new Dialog(getStage(), "...");
                                        dialog.sizeToScene();
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            student = null;
            zaposleni = null;
            predmet = null;
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