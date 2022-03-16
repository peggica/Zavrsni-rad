package klijent.gui;

import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.IspitniRok;

/** Klasa namenjena za prikaz Forme za Zaposlene u JavaFx-u
 *  Ukoliko je tip zaposlenog profesor u Meniju je dostupna i opcija za snimanje ocena u zapisnik - DODAJ U ZAPISNIK
 *  @author Biljana Stanojevic  */

public class ZaposleniForm extends Stage {

    private static final Font font15 = new Font("Arial", 15);
    private static final Font font20 = new Font("Arial", 20);
    private ObservableList<IspitniRok> sviIspitniRokovi = FXCollections.observableArrayList();

    public void setSviIspitniRokovi(ObservableList<IspitniRok> sviIspitniRokovi) {
        this.sviIspitniRokovi = sviIspitniRokovi;
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

    public ZaposleniForm(Stage stage, ObservableList<IspitniRok> sviIspitniRokovi) {

        super();
        initOwner(stage);
        setSviIspitniRokovi(sviIspitniRokovi);

        BorderPane root = new BorderPane();
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        root.setTop(menuBar);
        pocetniPrikaz(root);

        Scene scene = new Scene(root, 650, 400);
        setScene(scene);
        setResizable(false);
        setTitle("Profesor");

        //Klikom na stavku POČETNA iz Menija poziva se metoda ocistiPane() za ciscenje svih strana BorderPane-a i poziva prikaz za pocetnu stranu
        Label lblPocetna = new Label("POČETNA");
        lblPocetna.setOnMouseClicked(mouseEvent -> {

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
            cmbPredmet.getItems().addAll("ovde idu predmeti"); //da ucita iz baze
            cmbPredmet.setMinWidth(Region.USE_PREF_SIZE);
            hboxPredmet.getChildren().addAll(lblPredmet, cmbPredmet);

            HBox hboxPrijave = new HBox();
            hboxPrijave.setAlignment(Pos.TOP_LEFT);
            hboxPrijave.setPadding(new Insets(0, 10, 5, 0));
            hboxPrijave.setSpacing(100);
            Label lblPrijave = new Label("Lista studenata (sa prijavljenim ispitom): ");
            lblPrijave.setFont(font15);

            TableView<String> tablePrijave = new TableView<String>();
            tablePrijave.setEditable(true);
            tablePrijave.getColumns().clear();
            TableColumn<String, String> colIndex = new TableColumn("Broj indeksa"); //TODO: nazive kolona dobiti iz baze
            colIndex.setMinWidth(100);
            TableColumn<String, String> colOcena = new TableColumn("Ocena");
            colOcena.setMinWidth(50);

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
        menuBar.getMenus().addAll(pocetnaMenu, zakaziSaluMenu, dodajUZapisnikMenu);

    }

}