package klijent.gui;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.IspitniRok;
import model.Predmet;
import model.Student;
import model.Zaposleni;

import java.util.Optional;

/** Klasa namenjena za prikaz Studentske Forme u JavaFx-u
 *   @author Biljana Stanojevic  */

public class StudentForm extends Stage {

    private static Font font15 = new Font("Arial", 15);
    private static Font font20 = new Font("Arial", 20);
    private Student student;
    private ObservableList<IspitniRok> sviIspitniRokovi = FXCollections.observableArrayList();
    private ObservableList<Predmet> polozeniPredmeti = FXCollections.observableArrayList();
    private ObservableList<Predmet> nepolozeniPredmeti = FXCollections.observableArrayList();

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setSviIspitniRokovi(ObservableList<IspitniRok> sviIspitniRokovi, ObservableList<Predmet> polozeniPredmeti, ObservableList<Predmet> nepolozeniPredmeti) {
        this.sviIspitniRokovi = sviIspitniRokovi;
        this.polozeniPredmeti = polozeniPredmeti;
        this.nepolozeniPredmeti = nepolozeniPredmeti;
    }

    /** Proverava da li ima aktivnih ispitnih rokova ili ne, pa postavlja prikaz za stavku Pocetna iz Menija    */
    private void pocetniPrikaz(BorderPane root){

        boolean aktivan = false;
        for (IspitniRok ispitniRok:this.sviIspitniRokovi) {
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

    /** Cisti sve strane Border Pane-a, pre prebacivanja na sledeci prikaz iz Menija  */
    private static void ocistiPane(BorderPane root) {

        root.setLeft(null);
        root.setRight(null);
        root.setBottom(null);
        root.setCenter(null);
    }

    public StudentForm(Stage stage) {

        super();
        initOwner(stage);

        BorderPane root = new BorderPane();
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        root.setTop(menuBar);
        pocetniPrikaz(root);

        Scene scene = new Scene(root, 900, 700);
        setScene(scene);
        setResizable(false);
        setTitle("Student");

        //Klikom na stavku Pocetna iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a i poziva prikaz za pocetnu stranu
        Label lblPocetna = new Label("POČETNA");
        lblPocetna.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);
            pocetniPrikaz(root);
        });
        Menu pocetnaMenu = new Menu("", lblPocetna);

        //Klikom na stavku PREDMETI iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabelama prikazuju svi položeni i nepoloženi predmeti za tog studenta
        Label lblPredmeti = new Label("PREDMETI");
        lblPredmeti.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);
            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            Label lblPolozeni = new Label("Položeni predmeti");
            lblPolozeni.setFont(font20);
            lblPolozeni.setAlignment(Pos.CENTER_LEFT);
            lblPolozeni.setPadding(new Insets(0,10,5,0));

            TableView<Predmet> tablePolozeni = new TableView<>();
            tablePolozeni.getColumns().clear();

            TableColumn colSifra = new TableColumn("Šifra");
            colSifra.setCellValueFactory(new PropertyValueFactory<Predmet, String>("idPredmeta"));
            colSifra.setMinWidth(75);
            TableColumn colNaziv = new TableColumn("Naziv predmeta");
            colNaziv.setCellValueFactory(new PropertyValueFactory<Predmet, String>("naziv"));
            colNaziv.setMinWidth(350);
            TableColumn colEspb = new TableColumn("ESPB");
            colEspb.setCellValueFactory(new PropertyValueFactory<Predmet, String>("espb"));
            colEspb.setMinWidth(75);
            TableColumn colSemestar = new TableColumn("Semestar");
            colSemestar.setCellValueFactory(new PropertyValueFactory<Predmet, String>("semestar"));
            colSemestar.setMinWidth(75);
            TableColumn colOcena = new TableColumn("Ocena");
            colOcena.setMinWidth(75);

            tablePolozeni.setItems(polozeniPredmeti);
            //sredjuje problem za dodatu kolonu
            tablePolozeni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablePolozeni.getColumns().addAll(colSifra, colNaziv, colEspb, colSemestar, colOcena);

            Label lblNepolozeni = new Label("Nepoloženi predmeti");
            lblNepolozeni.setFont(font20);
            lblNepolozeni.setAlignment(Pos.CENTER_LEFT);
            lblNepolozeni.setPadding(new Insets(5,10,5,0));

            TableView<Predmet> tableNeplozeni = new TableView<>();
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

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            Label lblPrijavaIspita = new Label("Prijava ispita");
            lblPrijavaIspita.setFont(font20);
            lblPrijavaIspita.setAlignment(Pos.CENTER_LEFT);
            lblPrijavaIspita.setPadding(new Insets(0,10,5,0));

            TableView<String> tablePrijava = new TableView<String>();
            tablePrijava.getColumns().clear();

            TableColumn<String, String> colSifra = new TableColumn("Šifra");    //TODO: nazive kolona dobiti iz baze
            colSifra.setMinWidth(75);
            TableColumn<String, String> colNaziv = new TableColumn("Naziv predmeta");
            colNaziv.setMinWidth(250);
            TableColumn<String, String> colEspb = new TableColumn("Espb");
            colEspb.setMinWidth(75);
            TableColumn<String, String> colSemestar = new TableColumn("Semestar");
            colSemestar.setMinWidth(75);
            TableColumn<String, String> colProfesor = new TableColumn("Profesor");
            colProfesor.setMinWidth(250);
            TableColumn<String, String> colCena = new TableColumn("Cena");
            colCena.setMinWidth(75);

            tablePrijava.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tablePrijava.setPrefHeight(550);
            tablePrijava.getColumns().addAll(colSifra, colNaziv, colEspb, colSemestar, colProfesor, colCena);

            HBox hboxPrijavi = new HBox();
            hboxPrijavi.setAlignment(Pos.BOTTOM_RIGHT);
            hboxPrijavi.setPadding(new Insets(15,0,0,0));
            hboxPrijavi.setSpacing(5);
            Button btnPrijavi = new Button("Prijavi ispit");
            btnPrijavi.setMinWidth(100);
            btnPrijavi.setFont(font15);
            hboxPrijavi.getChildren().add(btnPrijavi);
            vbox.getChildren().addAll(lblPrijavaIspita, tablePrijava, hboxPrijavi);

            root.setCenter(vbox);
        });
        Menu prijavaMenu = new Menu("", lblPrijava);

        //Klikom na stavku ŠKOLARINE iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a, nakon cega se u tabeli prikazuju sve dosadašnje uplate, a ispod njih preostala dugovanja skolarine za studenta ako postoje
        Label lblSkolarine = new Label("ŠKOLARINE");
        lblSkolarine.setOnMouseClicked(mouseEvent -> {

            ocistiPane(root);

            VBox vbox = new VBox();
            vbox.setPadding(new Insets(5,10,10,10));

            Label lblSkolarineIspis = new Label("Školarine");
            lblSkolarineIspis.setFont(font20);
            lblSkolarineIspis.setAlignment(Pos.CENTER_LEFT);
            lblSkolarineIspis.setPadding(new Insets(0,10,5,0));

            TableView<String> tableSkolarine = new TableView<String>();
            tableSkolarine.getColumns().clear();

            TableColumn<String, String> colDatum = new TableColumn("Datum");    //TODO: nazive kolona dobiti iz baze
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

            Label lblBrIndeksa = new Label("Broj indeksa: " + student.brojIndeksa());
            lblBrIndeksa.setPadding(new Insets(20,0,0,0));
            lblBrIndeksa.setFont(font15);

            Label lblImePrezime = new Label("Ime i prezime: " + student.imePrezime());
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

}