package klijent.gui;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.*;

import java.util.List;

/** Klasa namenjena za prikaz Forme za korisnike Studentske Sluzbe u JavaFx-u
 *  @author Biljana Stanojevic  */

public class StudentskaSluzbaForm extends Stage {

    //TODO: umesto <nesto> tipa za table column i view da ide klasa

    private static Font font15 = new Font("Arial", 15);
    private static Font font20 = new Font("Arial", 20);
    private ObservableList<IspitniRok> sviIspitniRokovi = FXCollections.observableArrayList();
    private ObservableList<Student> sviStudenti = FXCollections.observableArrayList();
    private ObservableList<Zaposleni> sviZaposleni = FXCollections.observableArrayList();
    private ObservableList<Predmet> sviPredmeti = FXCollections.observableArrayList();
    private ObservableList<Sala> sveSale = FXCollections.observableArrayList();

    public void setSviIspitniRokovi(ObservableList<IspitniRok> sviIspitniRokovi) {
        this.sviIspitniRokovi = sviIspitniRokovi;
    }

    public void setSviStudenti(ObservableList<Student> sviStudenti) {
        this.sviStudenti = sviStudenti;
    }

    public void setSviZaposleni(ObservableList<Zaposleni> sviZaposleni) {
        this.sviZaposleni = sviZaposleni;
    }

    public void setSviPredmeti(ObservableList<Predmet> sviPredmeti) {
        this.sviPredmeti = sviPredmeti;
    }

    public void setSveSale(ObservableList<Sala> sveSale) {
        this.sveSale = sveSale;
    }

    /** Proverava da li ima aktivnih ispitnih rokova ili ne, pa postavlja prikaz za stavku Pocetna iz Menija    */
    private static void pocetniPrikaz(BorderPane root, List<IspitniRok> sviIspitniRokovi){

        boolean aktivan = false;
        for (IspitniRok ispitniRok:sviIspitniRokovi) {
            if(ispitniRok.isAktivnost()==true) {
                aktivan = true;
                break;
            }
        }

        String poruka = "";
        if(aktivan) {
            poruka = "Ispitni rok je u toku.";
        } else {
            poruka = "Nijedan ispitni rok trenutno nije u toku.";
        }
        Label lblPrikaz = new Label(poruka);
        lblPrikaz.setFont(font20);
        lblPrikaz.setPadding(new Insets(5,10,5,10));
        root.setLeft(lblPrikaz);

    }

    /** Cisti sve strane Border Pane-a, pre prebacivanja na sledeci prikaz iz Menija    */
    private static void ocistiPane(BorderPane root){

        root.setLeft(null);
        root.setRight(null);
        root.setBottom(null);
        root.setCenter(null);
    }

    public StudentskaSluzbaForm(Stage stage, ObservableList<IspitniRok> sviIspitniRokovi, ObservableList<Student> sviStudenti, ObservableList<Zaposleni> sviZaposleni, ObservableList<Predmet> sviPredmeti, ObservableList<Sala> sveSale) {

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

                    ocistiPane(root);
                    pocetniPrikaz(root, sviIspitniRokovi);
        });
        Menu pocetnaMenu = new Menu("", lblPocetna);

        //Klikom na stavku STUDENTI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za studente, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi student u Bazu podataka
        Label lblStudenti = new Label("STUDENTI");
        lblStudenti.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            HBox hBoxPretraga = new HBox();
            hBoxPretraga.setAlignment(Pos.CENTER_LEFT);
            hBoxPretraga.setPadding(new Insets(0,10,5,0));
            hBoxPretraga.setSpacing(5);

            Label lblPretraga = new Label("Studenti: ");
            lblPretraga.setFont(font15);

            TextField txtPretraga = new TextField();

            Button btnPretrazi = new Button("pretraži");
            btnPretrazi.setFont(font15);
            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            TableView<Student> tableStudenti = new TableView<>();
            tableStudenti.setEditable(true);
            tableStudenti.getColumns().clear();

            TableColumn colIme = new TableColumn("Ime");
            colIme.setCellValueFactory(new PropertyValueFactory<Student, String>("ime"));
            colIme.setMinWidth(100);
            TableColumn colPrezime = new TableColumn("Prezime");
            colPrezime.setCellValueFactory(new PropertyValueFactory<Student, String>("prezime"));
            colPrezime.setMinWidth(150);
            TableColumn colIndex = new TableColumn("Broj indeksa");
            colIndex.setCellValueFactory(new PropertyValueFactory<Student, String>("smer"));
            colIndex.setMinWidth(50);
            TableColumn colFinansiranje = new TableColumn("Finansiranje");
            colFinansiranje.setCellValueFactory(new PropertyValueFactory<Student, String>("finansiranje"));
            colFinansiranje.setMinWidth(50);
            TableColumn colAdresa = new TableColumn("Adresa");
            colAdresa.setCellValueFactory(new PropertyValueFactory<Student, String>("adresa"));
            colAdresa.setMinWidth(150);
            TableColumn colEmail = new TableColumn("E-mail");
            colEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("email"));
            colEmail.setMinWidth(150);
            TableColumn colTelefon = new TableColumn("Broj telefona");
            colTelefon.setCellValueFactory(new PropertyValueFactory<Student, String>("brojTelefona"));
            colTelefon.setMinWidth(100);

            tableStudenti.setItems(sviStudenti);
            //sredjuje problem za dodatu kolonu
            tableStudenti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableStudenti.setPrefHeight(500);
            tableStudenti.getColumns().addAll(colIme, colPrezime, colIndex, colFinansiranje, colAdresa, colEmail, colTelefon);

            TextField txtIme = new TextField("");
            txtIme.setPromptText("Ime");
            txtIme.setMinWidth(100);

            TextField txtPrezime = new TextField();
            txtPrezime.setPromptText("Prezime");
            txtPrezime.setMinWidth(175);

            TextField txtIndex = new TextField("");
            txtIndex.setPromptText("Broj indeksa");
            txtIndex.setPrefWidth(100);

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
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5,0,0,0));
            hboxAkcija.setAlignment(Pos.CENTER);
            hboxAkcija.getChildren().addAll(txtIme, txtPrezime, txtIndex, lblFinansiranje, cmbFinansiranje, txtAdresa, txtMail, txtTelefon, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(hBoxPretraga, tableStudenti, hboxAkcija);
            root.setCenter(vbox);
        });
        Menu studentiMenu = new Menu("", lblStudenti);

        //Klikom na stavku ZAPOSLENI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za zaposlene, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi zaposleni u Bazu podataka, ili pogledati detaljnije informacije klikom na odredjenog zaposlenog u tabeli
        Label lblZaposleni = new Label("ZAPOSLENI");
        lblZaposleni.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            HBox hBoxPretraga = new HBox();
            hBoxPretraga.setAlignment(Pos.CENTER_LEFT);
            hBoxPretraga.setPadding(new Insets(0,10,5,0));
            hBoxPretraga.setSpacing(5);

            Label lblPretraga = new Label("Zaposleni: ");
            lblPretraga.setFont(font15);
            TextField txtPretraga = new TextField();

            Button btnPretrazi = new Button("pretraži");
            btnPretrazi.setFont(font15);
            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            TableView<Zaposleni> tableZaposleni = new TableView<>();
            tableZaposleni.setEditable(true);
            tableZaposleni.getColumns().clear();

            TableColumn colPozicija = new TableColumn("Pozicija");
            colPozicija.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("pozicija"));
            colPozicija.setMinWidth(100);
            TableColumn colIme = new TableColumn("Ime");
            colIme.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("ime"));
            colIme.setMinWidth(125);
            TableColumn colPrezime = new TableColumn("Prezime");
            colPrezime.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("prezime"));
            colPrezime.setMinWidth(225);
            TableColumn colAdresa = new TableColumn("Adresa");
            colAdresa.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("adresa"));
            colAdresa.setMinWidth(150);
            TableColumn colEmail = new TableColumn("E-mail");
            colEmail.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("email"));
            colEmail.setMinWidth(250);
            TableColumn colTelefon = new TableColumn("Broj telefona");
            colTelefon.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("brojTelefona"));
            colTelefon.setMinWidth(150);

            tableZaposleni.setItems(sviZaposleni);
            //sredjuje problem za dodatu kolonu
            tableZaposleni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableZaposleni.setPrefHeight(500);
            tableZaposleni.getColumns().addAll(colPozicija, colIme, colPrezime, colAdresa, colEmail, colTelefon);

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
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5,0,0,0));
            hboxAkcija.setAlignment(Pos.CENTER);
            hboxAkcija.getChildren().addAll(lblPozicija, cmbPozicija, txtIme, txtPrezime, txtAdresa, txtMail, txtTelefon, btnDodaj, btnObrisi);

            vbox.getChildren().addAll(hBoxPretraga, tableZaposleni, hboxAkcija);
            root.setCenter(vbox);
        });
        Menu zaposleniMenu = new Menu("", lblZaposleni);

        //Klikom na stavku PREDMETI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve informacije vezane za predmete, koji se mogu pretraziti ili izmeniti, i takodje se moze dodati i novi predmet u Bazu podataka
        Label lblPredmeti = new Label("PREDMETI");
        lblPredmeti.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            HBox hBoxPretraga = new HBox();
            hBoxPretraga.setAlignment(Pos.CENTER_LEFT);
            hBoxPretraga.setPadding(new Insets(0,10,5,0));
            hBoxPretraga.setSpacing(5);

            Label lblPretraga = new Label("Predmeti: ");
            lblPretraga.setFont(font15);

            TextField txtPretraga = new TextField();

            Button btnPretrazi = new Button("pretraži");
            btnPretrazi.setFont(font15);
            hBoxPretraga.getChildren().addAll(lblPretraga, txtPretraga, btnPretrazi);

            TableView<Predmet> tablePredmeti = new TableView<>();
            tablePredmeti.setEditable(true);
            tablePredmeti.getColumns().clear();

            TableColumn colSifra = new TableColumn("Šifra");
            colSifra.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("idPredmeta"));
            colSifra.setMinWidth(75);
            TableColumn colNaziv = new TableColumn("Naziv");
            colNaziv.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("naziv"));
            colNaziv.setMinWidth(250);
            TableColumn colProfesor = new TableColumn("Profesor");
            colProfesor.setMinWidth(250);
            TableColumn colSmer = new TableColumn("Smer");
            colSmer.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("studijskiSmer"));
            colSmer.setMinWidth(50);
            TableColumn colSemestar = new TableColumn("Semestar");
            colSemestar.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("semestar"));
            colSemestar.setMinWidth(50);
            TableColumn colEspb = new TableColumn("ESPB");
            colEspb.setCellValueFactory(new PropertyValueFactory<Zaposleni, String>("espb"));
            colEspb.setMinWidth(50);

            tablePredmeti.setItems(sviPredmeti);
            //sredjuje problem za dodatu kolonu
            tablePredmeti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablePredmeti.setPrefHeight(500);
            tablePredmeti.getColumns().addAll(colSifra, colNaziv, colProfesor, colSmer, colSemestar, colEspb);

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
            cmbSmer.setValue(Predmet.tipSmera.avt);
            cmbSmer.setMinWidth(Region.USE_PREF_SIZE);
            cmbSmer.setStyle("-fx-font: 12px \"Arial\";");

            Label lblSemestar = new Label("Semestar: ");
            lblSemestar.setMinWidth(Region.USE_PREF_SIZE);
            Spinner<Integer> spSemestar = new Spinner();
            SpinnerValueFactory<Integer> vfSemestar = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,6);
            vfSemestar.setValue(1);
            spSemestar.setValueFactory(vfSemestar);
            spSemestar.setMinWidth(50);
            spSemestar.setMaxWidth(50);

            Label lblEspb = new Label("Broj espb: ");
            lblEspb.setMinWidth(Region.USE_PREF_SIZE);
            Spinner<Integer> spEspb = new Spinner();
            SpinnerValueFactory<Integer> vfEspb = new SpinnerValueFactory.IntegerSpinnerValueFactory(2,10);
            vfEspb.setValue(2);
            spEspb.setValueFactory(vfEspb);
            spEspb.setMinWidth(50);
            spEspb.setMaxWidth(50);

            Button btnDodaj = new Button("Dodaj");
            btnDodaj.setMinWidth(60);
            Button btnObrisi = new Button("Obriši");
            btnObrisi.setMinWidth(60);

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5,0,0,0));
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

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

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
            hboxAkcija.setPadding(new Insets(5,0,0,0));
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
            vbox.setPadding(new Insets(5,10,10,10));

            HBox hboxAkcija = new HBox();
            hboxAkcija.setSpacing(5);
            hboxAkcija.setPadding(new Insets(5,0,0,0));
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

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            HBox hBoxPretraga = new HBox();
            hBoxPretraga.setAlignment(Pos.CENTER_LEFT);
            hBoxPretraga.setPadding(new Insets(0,10,5,0));
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

}