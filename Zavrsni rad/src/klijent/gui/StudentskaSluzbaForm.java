package klijent.gui;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Klasa namenjena za prikaz Forme za korisnike Studentske Sluzbe u JavaFx-u
 *  @author Biljana Stanojevic  */
public class StudentskaSluzbaForm extends Application {

    //TODO: umesto <nesto> tipa za table column i view da ide klasa

    private static Font font15 = new Font("Arial", 15);
    private static Font font20 = new Font("Arial", 20);

    /** Postavlja prikaz za stavku Pocetna iz Menija  */
    private static void pocetniPrikaz(BorderPane root){

        Label lblPrikaz = new Label("Ispitni rok: ");
        lblPrikaz.setFont(font20);
        lblPrikaz.setPadding(new Insets(5,10,5,10));
        root.setLeft(lblPrikaz);
    }

    /** Cisti sve strane Border Pane-a, pre prebacivanja na sledeci prikaz iz Menija  */
    private static void ocistiPane(BorderPane root){

        root.setLeft(null);
        root.setRight(null);
        root.setBottom(null);
        root.setCenter(null);
    }

    @Override
    public void start(Stage sluzbaStage) {

        BorderPane root = new BorderPane();
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(sluzbaStage.widthProperty());
        root.setTop(menuBar);
        pocetniPrikaz(root);

        //Klikom na stavku POČETNA iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a i poziva prikaz za pocetnu stranu
        Label lblPocetna = new Label("POČETNA");
        lblPocetna.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);
            pocetniPrikaz(root);
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

            TableView<String> tableStudenti = new TableView<String>();
            tableStudenti.setEditable(true);
            tableStudenti.getColumns().clear();

            TableColumn<String, String> colIme = new TableColumn("Ime");    //TODO: nazive kolona dobiti iz baze
            colIme.setMinWidth(100);
            TableColumn<String, String> colPrezime = new TableColumn("Prezime");
            colPrezime.setMinWidth(150);
            TableColumn<String, String> colIndex = new TableColumn("Broj indeksa");
            colIndex.setMinWidth(50);
            TableColumn<String, String> colFinansiranje = new TableColumn("Finansiranje");
            colFinansiranje.setMinWidth(50);
            TableColumn<String, String> colAdresa = new TableColumn("Adresa");
            colAdresa.setMinWidth(150);
            TableColumn<String, String> colEmail = new TableColumn("E-mail");
            colEmail.setMinWidth(150);
            TableColumn<String, String> colTelefon = new TableColumn("Broj telefona");
            colTelefon.setMinWidth(100);    //TODO: ovo dole sreduje problem - za dodatnu kolonu

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
            cmbFinansiranje.getItems().addAll("SAF", "BUDZ");   //TODO: da ucita iz baze
            cmbFinansiranje.setValue("SAF");
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

            TableView<String> tableZaposleni = new TableView<String>();
            tableZaposleni.setEditable(true);
            tableZaposleni.getColumns().clear();

            TableColumn<String, String> colPozicija = new TableColumn("Pozicija");  //TODO: nazive kolona dobiti iz baze
            colPozicija.setMinWidth(100);
            TableColumn<String, String> colIme = new TableColumn("Ime");
            colIme.setMinWidth(125);
            TableColumn<String, String> colPrezime = new TableColumn("Prezime");
            colPrezime.setMinWidth(225);
            TableColumn<String, String> colAdresa = new TableColumn("Adresa");
            colAdresa.setMinWidth(150);
            TableColumn<String, String> colEmail = new TableColumn("E-mail");
            colEmail.setMinWidth(250);
            TableColumn<String, String> colTelefon = new TableColumn("Broj telefona"); //TODO: ovo dole sreduje problem - za dodatnu kolonu
            colTelefon.setMinWidth(150);

            tableZaposleni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableZaposleni.setPrefHeight(500);
            tableZaposleni.getColumns().addAll(colPozicija, colIme, colPrezime, colAdresa, colEmail, colTelefon);

            Label lblPozicija = new Label("Pozicija: ");
            lblPozicija.setMinWidth(Region.USE_PREF_SIZE);
            ComboBox cmbPozicija = new ComboBox();
            cmbPozicija.getItems().addAll("Profesor", "Asistent", "Saradnik"); //da ucita iz baze
            cmbPozicija.setValue("Profesor");
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

            TableView<String> tablePredmeti = new TableView<String>();
            tablePredmeti.setEditable(true);
            tablePredmeti.getColumns().clear();

            TableColumn<String, String> colSifra = new TableColumn("Šifra");    //TODO: nazive kolona dobiti iz baze
            colSifra.setMinWidth(75);
            TableColumn<String, String> colNaziv = new TableColumn("Naziv");
            colNaziv.setMinWidth(250);
            TableColumn<String, String> colProfesor = new TableColumn("Profesor");
            colProfesor.setMinWidth(250);
            TableColumn<String, String> colSmer = new TableColumn("Smer");
            colSmer.setMinWidth(50);
            TableColumn<String, String> colSemestar = new TableColumn("Semestar");
            colSemestar.setMinWidth(50);
            TableColumn<String, String> colEspb = new TableColumn("ESPB"); //TODO: ovo dole sreduje problem - za dodatnu kolonu
            colEspb.setMinWidth(50);

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
            cmbSmer.getItems().addAll("AVT", "ASUV", "EKO", "ELITE", "EPO", "IST", "NET", "NRT", "RT"); //TODO: da ucita iz baze
            cmbSmer.setValue("AVT");
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

            TableView<String> tableSale = new TableView<String>();
            tableSale.setEditable(true);
            tableSale.getColumns().clear();

            TableColumn<String, String> colNaziv = new TableColumn("Naziv");    //TODO: nazive kolona dobiti iz baze
            colNaziv.setMinWidth(200);
            TableColumn<String, String> colKapacitet = new TableColumn("Broj mesta");
            colKapacitet.setMinWidth(100);
            TableColumn<String, String> colOprema = new TableColumn("Oprema");
            colOprema.setMinWidth(200);

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
            cmbOprema.getItems().addAll("Računari", "/"); //da ucita iz baze
            cmbOprema.setValue("Računari");
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
            TableView<String> tableIspitniRokovi = new TableView<String>();
            tableIspitniRokovi.setEditable(true);

            tableIspitniRokovi.getColumns().clear();
            TableColumn<String, String> colNaziv = new TableColumn("Naziv");    //TODO: nazive kolona dobiti iz baze
            colNaziv.setMinWidth(200);
            TableColumn<String, String> colPocetak = new TableColumn("Početak");
            colPocetak.setMinWidth(100);
            TableColumn<String, String> colKraj = new TableColumn("Kraj");
            colKraj.setMinWidth(100);
            TableColumn<String, String> colAktivan = new TableColumn("Aktivan");    //true ili false, moze samo 1!
            colAktivan.setMinWidth(200);

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

        Scene scene = new Scene(root, 1200, 600);
        sluzbaStage.setScene(scene);
        sluzbaStage.setResizable(false);
        sluzbaStage.setTitle("Studentska služba");
        sluzbaStage.show();

    }

    public void createGUI(String[] args) {

        launch(args);
    }
}