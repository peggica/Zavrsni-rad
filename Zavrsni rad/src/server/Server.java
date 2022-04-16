package server;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.*;
import model.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.sql.JDBCType.NULL;

/** Klase Server, ServerThread i ServerPrihvatanje namenjene za podizanje servera i prihvatanje konekcija klijenata
 *  i razmenu zahteva/odgovora sa njima prikaz u JavaFx formi
 *  @author Biljana Stanojevic  */

public class Server extends Application {

    private static Connection connection = null;
    private TextArea areaIspis = new TextArea();
    private ServerSocket serverSocket;

    public static void main(String[] args) {

        launch(args);

    }

    /**
     * Dopisuje novi red u TextArea za prikaz uspesno podignutnog servera i ostvarenih novih konekcija na formi
     */
    private void ispis(String poruka) {

        areaIspis.appendText(poruka + "\n");
    }

    @Override
    public void start(Stage serverStage) throws Exception {

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10, 0, 10, 0));
        root.setHgap(10);
        root.setVgap(10);

        Font font = new Font("Arial", 15);

        areaIspis.setPrefSize(380, 480);
        areaIspis.setFont(font);
        areaIspis.setEditable(false);

        root.add(areaIspis, 0, 0);

        Scene scene = new Scene(root, 400, 500);
        serverStage.setScene(scene);
        serverStage.setResizable(false);
        serverStage.setTitle("Server");
        serverStage.show();

        Thread serverThread = new Thread(new ServerThread());
        //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
        serverThread.setDaemon(true);
        serverThread.start();

    }

    private class ServerThread extends Thread {

        private static final int TCP_PORT = 9000;

        @Override
        public void run() {

            try {

                //OTVARANJE KONEKCIJE
                Socket socket = null;
                serverSocket = new ServerSocket(TCP_PORT);

                //update na JavaFx application niti
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ispis("Server je pokrenut");
                    }
                });

                while (true) {

                    socket = serverSocket.accept();

                    //pokretanje nove niti da ne bi doslo do blokiranja od strane praznog inputstreama
                    Thread acceptedThread = new Thread(new ServerThreadPrihvatanje(socket, socket.getInetAddress().getHostName()));
                    //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                    acceptedThread.setDaemon(true);
                    acceptedThread.start();

                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    private class ServerThreadPrihvatanje extends Thread {

        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object zahtev;
        private Object odgovor;
        private String adresa;

        ServerThreadPrihvatanje(Socket socket, String adresa) {

            this.socket = socket;
            this.adresa = adresa;
        }

        @Override
        public void run() {

            try {

                outObj = new ObjectOutputStream(socket.getOutputStream());
                inObj = new ObjectInputStream(socket.getInputStream());

                //update na JavaFx application niti
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        ispis("Zahtev od strane klijenta: " + adresa);
                    }
                });

                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentskasluzba", "root", "");
                //update na JavaFx application niti
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        ispis("Ostvarena konekcija sa Bazom");
                        System.out.println("Ostvarena je konekcija sa Bazom");

                    }
                });

                String query = "";
                Statement statement = connection.createStatement();
                ResultSet resultset;

                //RAZMENA SA KLIJENTOM
                zahtev = inObj.readObject();
                if (zahtev.toString().equals("login")) {

                    Login login = (Login) inObj.readObject();
                    //provera u bazi da li postoji korisnik za primljene podatke
                    String korisnickoIme = login.getKorisnickoIme();
                    String lozinka = login.getLozinka();
                    //da bi bilo case sensitive, posto mi nije collation?
                    query = "SELECT * FROM Login WHERE korisnickoIme = BINARY '" + korisnickoIme + "' AND lozinka = BINARY '" + lozinka + "' AND aktivan = 1";
                    resultset = statement.executeQuery(query);

                    if (!resultset.next()) {

                        odgovor = "nepostoji";
                        outObj.writeObject(odgovor);
                        outObj.flush();

                    } else {

                        int idZaposlenog = resultset.getInt("idZaposlenog");
                        int idStudenta = resultset.getInt("idStudenta");
                        String smer = resultset.getString("smer");
                        int godinaUpisa = resultset.getInt("godinaUpisa");

                        if (idZaposlenog != 0) {

                            query = "SELECT * FROM Zaposleni WHERE idZaposlenog = '" + idZaposlenog + "'";
                            resultset = statement.executeQuery(query);
                            if (resultset.next()) {
                                String pozicija = resultset.getString("pozicija");
                                String ime = resultset.getString("ime");
                                String prezime = resultset.getString("prezime");
                                String adresa = resultset.getString("adresa");
                                String email = resultset.getString("email");
                                String telefon = resultset.getString("telefon");
                                boolean vidljiv = resultset.getBoolean("vidljiv");
                                Zaposleni zaposleni = new Zaposleni(idZaposlenog, Zaposleni.tipZaposlenog.valueOf(pozicija), ime, prezime, adresa, email, telefon, vidljiv);
                                odgovor = "zaposleni";
                                outObj.writeObject(odgovor);
                                outObj.flush();
                                odgovor = zaposleni;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }

                                /*odgovor = "sviispitnirokovi";
                                outObj.writeObject(odgovor);
                                outObj.flush();*/
                            query = "SELECT * FROM IspitniRok";
                            resultset = statement.executeQuery(query);

                            while (resultset.next()) {
                                int idRoka = resultset.getInt("idRoka");
                                String naziv = resultset.getString("naziv");
                                Date datumPocetka = resultset.getDate("datumPocetka");
                                Date datumKraja = resultset.getDate("datumKraja");
                                boolean aktivnost = resultset.getBoolean("aktivnost");
                                IspitniRok ispitniRok = new IspitniRok(idRoka, naziv, datumPocetka, datumKraja, aktivnost);
                                odgovor = ispitniRok;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }

                            outObj.writeObject("kraj");
                            outObj.flush();

                        } else if (idStudenta != 0 && smer != null && godinaUpisa != 0) {

                            query = "SELECT * FROM Student WHERE idStudenta = '" + idStudenta + "' AND smer = '" + smer + "' AND godinaUpisa = '" + godinaUpisa + "'";
                            resultset = statement.executeQuery(query);
                            if (resultset.next()) {
                                String ime = resultset.getString("ime");
                                String prezime = resultset.getString("prezime");
                                String finansiranje = resultset.getString("finansiranje");
                                String adresa = resultset.getString("adresa");
                                String email = resultset.getString("email");
                                String telefon = resultset.getString("telefon");
                                boolean vidljiv = resultset.getBoolean("vidljiv");
                                Student student = new Student(idStudenta, godinaUpisa, Student.tipSmera.valueOf(smer), ime, prezime, Student.tipFinansiranja.valueOf(finansiranje), adresa, email, telefon, vidljiv);
                                odgovor = "student";
                                outObj.writeObject(odgovor);
                                outObj.flush();
                                odgovor = student;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }
                            query = "SELECT p.idPredmeta, p.naziv, p.studijskiSmer, p.semestar, p.espb, z.ocena FROM predmet AS p JOIN zapisnik AS z ON p.idPredmeta = z.idPredmeta WHERE z.idStudenta = '" + idStudenta + "' AND z.smer = '" + smer + "' AND z.godinaUpisa = '" + godinaUpisa + "'";
                            resultset = statement.executeQuery(query);
                            HashMap<Predmet, Integer> polozeniPredmeti = new HashMap<>();
                            while (resultset.next()) {
                                int idPredmeta = resultset.getInt("idPredmeta");
                                String naziv = resultset.getString("naziv");
                                String studijskiSmer = resultset.getString("studijskiSmer");
                                int semestar = resultset.getInt("semestar");
                                int espb = resultset.getInt("espb");
                                int ocena = resultset.getInt("ocena");
                                //boolean vidljiv = resultset.getBoolean("vidljiv");
                                Predmet predmet;
                                if (studijskiSmer != null) {
                                    predmet = new Predmet(idPredmeta, naziv, Predmet.tipSmera.valueOf(studijskiSmer), semestar, espb);
                                } else {
                                    predmet = new Predmet(idPredmeta, naziv, null, semestar, espb);
                                }
                                polozeniPredmeti.put(predmet, ocena);
                            }

                            odgovor = polozeniPredmeti;
                            outObj.writeObject(odgovor);
                            outObj.flush();

                            query = "SELECT * FROM predmet WHERE idPredmeta IN (SELECT idPredmeta FROM izabranipredmeti WHERE idStudenta = '" + idStudenta + "' AND smer = '" + smer + "' AND godinaUpisa = '" + godinaUpisa + "') AND idPredmeta NOT IN (SELECT idPredmeta FROM zapisnik WHERE idStudenta  = '" + idStudenta + "' AND smer = '" + smer + "' AND godinaUpisa = '" + godinaUpisa + "' AND ocena > 5)";
                            resultset = statement.executeQuery(query);
                            while (resultset.next()) {
                                int idPredmeta = resultset.getInt("idPredmeta");
                                String naziv = resultset.getString("naziv");
                                String studijskiSmer = resultset.getString("studijSkismer");
                                int semestar = resultset.getInt("semestar");
                                int espb = resultset.getInt("espb");
                                boolean vidljiv = resultset.getBoolean("vidljiv");
                                Predmet predmet;
                                if (studijskiSmer != null) {
                                    predmet = new Predmet(idPredmeta, naziv, Predmet.tipSmera.valueOf(studijskiSmer), semestar, espb, vidljiv);
                                } else {
                                    predmet = new Predmet(idPredmeta, naziv, null, semestar, espb, vidljiv);
                                }
                                odgovor = predmet;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }

                            odgovor = "sviispitnirokovi";
                            outObj.writeObject(odgovor);
                            outObj.flush();
                            query = "SELECT * FROM IspitniRok";
                            resultset = statement.executeQuery(query);

                            while (resultset.next()) {
                                int idRoka = resultset.getInt("idRoka");
                                String naziv = resultset.getString("naziv");
                                Date datumPocetka = resultset.getDate("datumPocetka");
                                Date datumKraja = resultset.getDate("datumKraja");
                                boolean aktivnost = resultset.getBoolean("aktivnost");
                                IspitniRok ispitniRok = new IspitniRok(idRoka, naziv, datumPocetka, datumKraja, aktivnost);
                                odgovor = ispitniRok;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }

                            outObj.writeObject("kraj");
                            outObj.flush();

                        } else {

                            odgovor = "sluzba";
                            outObj.writeObject(odgovor);
                            outObj.flush();

                            odgovor = "sviispitnirokovi";
                            outObj.writeObject(odgovor);
                            outObj.flush();
                            query = "SELECT * FROM IspitniRok";
                            resultset = statement.executeQuery(query);

                            while (resultset.next()) {
                                int idRoka = resultset.getInt("idRoka");
                                String naziv = resultset.getString("naziv");
                                Date datumPocetka = resultset.getDate("datumPocetka");
                                Date datumKraja = resultset.getDate("datumKraja");
                                boolean aktivnost = resultset.getBoolean("aktivnost");
                                IspitniRok ispitniRok = new IspitniRok(idRoka, naziv, datumPocetka, datumKraja, aktivnost);
                                odgovor = ispitniRok;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }

                            odgovor = "svistudenti";
                            outObj.writeObject(odgovor);
                            outObj.flush();
                            query = "SELECT * FROM Student";
                            resultset = statement.executeQuery(query);

                            while (resultset.next()) {
                                idStudenta = resultset.getInt("idStudenta");
                                smer = resultset.getString("smer");
                                godinaUpisa = resultset.getInt("godinaUpisa");
                                String ime = resultset.getString("ime");
                                String prezime = resultset.getString("prezime");
                                String finansiranje = resultset.getString("finansiranje");
                                String adresa = resultset.getString("adresa");
                                String email = resultset.getString("email");
                                String telefon = resultset.getString("telefon");
                                boolean vidljiv = resultset.getBoolean("vidljiv");
                                Student student = new Student(idStudenta, godinaUpisa, Student.tipSmera.valueOf(smer), ime, prezime, Student.tipFinansiranja.valueOf(finansiranje), adresa, email, telefon, vidljiv);
                                odgovor = student;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }

                            odgovor = "svizaposleni";
                            outObj.writeObject(odgovor);
                            outObj.flush();
                            query = "SELECT * FROM Zaposleni";
                            resultset = statement.executeQuery(query);

                            while (resultset.next()) {
                                idZaposlenog = resultset.getInt("idZaposlenog");
                                String pozicija = resultset.getString("pozicija");
                                String ime = resultset.getString("ime");
                                String prezime = resultset.getString("prezime");
                                String adresa = resultset.getString("adresa");
                                String email = resultset.getString("email");
                                String telefon = resultset.getString("telefon");
                                boolean vidljiv = resultset.getBoolean("vidljiv");
                                Zaposleni zaposleni = new Zaposleni(idZaposlenog, Zaposleni.tipZaposlenog.valueOf(pozicija), ime, prezime, adresa, email, telefon, vidljiv);
                                odgovor = zaposleni;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }

                            odgovor = "svipredmeti";
                            outObj.writeObject(odgovor);
                            outObj.flush();
                            //TODO: sta je sa nerasporedjenim predmetima?
                            query = "SELECT p.idPredmeta, p.naziv, p.studijskiSmer, p.semestar, p.espb, p.vidljiv, z.idZaposlenog, z.pozicija, z.ime, z.prezime FROM predmet p JOIN raspodelapredmeta rp ON p.idPredmeta = rp.idPredmeta JOIN zaposleni z ON rp.idZaposlenog = z.idZaposlenog";
                            resultset = statement.executeQuery(query);
                            HashMap<Predmet, Zaposleni> sviPredmeti = new HashMap<>();

                            while (resultset.next()) {
                                int idPredmeta = resultset.getInt("idPredmeta");
                                String naziv = resultset.getString("naziv");
                                String studijskiSmer = resultset.getString("studijskiSmer");
                                int semestar = resultset.getInt("semestar");
                                int espb = resultset.getInt("Espb");
                                boolean vidljiv = resultset.getBoolean("vidljiv");
                                idZaposlenog = resultset.getInt("idZaposlenog");
                                String ime = resultset.getString("ime");
                                String prezime = resultset.getString("prezime");
                                String pozicija = resultset.getString("pozicija");
                                Predmet predmet;
                                Zaposleni zaposleni;
                                if (studijskiSmer != null) {
                                    predmet = new Predmet(idPredmeta, naziv, Predmet.tipSmera.valueOf(studijskiSmer), semestar, espb, vidljiv);
                                } else {
                                    predmet = new Predmet(idPredmeta, naziv, null, semestar, espb, vidljiv);
                                }
                                zaposleni = new Zaposleni(idZaposlenog, Zaposleni.tipZaposlenog.valueOf(pozicija), ime, prezime);
                                sviPredmeti.put(predmet, zaposleni);
                            }
                            odgovor = sviPredmeti;
                            outObj.writeObject(odgovor);
                            outObj.flush();

                            odgovor = "svesale";
                            outObj.writeObject(odgovor);
                            outObj.flush();
                            query = "SELECT * FROM Sala";
                            resultset = statement.executeQuery(query);

                            while (resultset.next()) {
                                int idSale = resultset.getInt("idSale");
                                String naziv = resultset.getString("naziv");
                                int brojMesta = resultset.getInt("brojMesta");
                                String oprema = resultset.getString("oprema");
                                boolean vidljiv = resultset.getBoolean("vidljiv");
                                if (oprema.equals("/")) {
                                    oprema = "ništa";
                                }
                                Sala sala = new Sala(idSale, naziv, brojMesta, Sala.tipOpreme.valueOf(oprema), vidljiv);
                                odgovor = sala;
                                outObj.writeObject(odgovor);
                                outObj.flush();
                            }
                            odgovor = "svezakazanesale";
                            outObj.writeObject(odgovor);
                            outObj.flush();
                            query = "SELECT zs.idSale, zs.idPredmeta, zs.idZaposlenog, zs.datum, zs.vremePocetka, zs.vremeKraja, z.ime, z.prezime, s.naziv, p.naziv FROM zaposleni z JOIN zakazivanjesale zs ON z.idZaposlenog = zs.idZaposlenog JOIN sala s ON zs.idSale = s.idSale JOIN predmet p ON zs.idPredmeta = p.idPredmeta WHERE zs.datum >= CURDATE()";
                            resultset = statement.executeQuery(query);
                            HashMap<ZakazivanjeSale, ArrayList<String>> sveZakazaneSale = new HashMap<>();

                            while (resultset.next()) {
                                int idSale = resultset.getInt("idSale");
                                int idPredmeta = resultset.getInt("idPredmeta");
                                idZaposlenog = resultset.getInt("idZaposlenog");
                                Date datum = resultset.getDate("datum");
                                Time vremePocetka = resultset.getTime("vremePocetka");
                                Time vremeKraja = resultset.getTime("vremeKraja");
                                String zaposleni = resultset.getString("ime") + " " + resultset.getString("prezime");
                                String nazivSale = resultset.getString("s.naziv");
                                String nazivPredmeta = resultset.getString("p.naziv");
                                ZakazivanjeSale zakazivanjeSale = new ZakazivanjeSale(idSale, idPredmeta, idZaposlenog, datum, vremePocetka, vremeKraja);
                                ArrayList SalaZaposleni = new ArrayList();
                                SalaZaposleni.add(nazivSale);
                                SalaZaposleni.add(nazivPredmeta);
                                SalaZaposleni.add(zaposleni);
                                sveZakazaneSale.put(zakazivanjeSale, SalaZaposleni);
                            }
                            odgovor = sveZakazaneSale;
                            outObj.writeObject(odgovor);
                            outObj.flush();
                            //outObj.writeObject("kraj");
                        }
                    }
                } else if (zahtev.equals("osveziSluzba")) {

                    odgovor = "sviispitnirokovi";
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    query = "SELECT * FROM IspitniRok";
                    resultset = statement.executeQuery(query);

                    while (resultset.next()) {
                        int idRoka = resultset.getInt("idRoka");
                        String naziv = resultset.getString("naziv");
                        Date datumPocetka = resultset.getDate("datumPocetka");
                        Date datumKraja = resultset.getDate("datumKraja");
                        boolean aktivnost = resultset.getBoolean("aktivnost");
                        IspitniRok ispitniRok = new IspitniRok(idRoka, naziv, datumPocetka, datumKraja, aktivnost);
                        odgovor = ispitniRok;
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }

                    odgovor = "svistudenti";
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    query = "SELECT * FROM Student";
                    resultset = statement.executeQuery(query);

                    while (resultset.next()) {
                        int idStudenta = resultset.getInt("idStudenta");
                        String smer = resultset.getString("smer");
                        int godinaUpisa = resultset.getInt("godinaUpisa");
                        String ime = resultset.getString("ime");
                        String prezime = resultset.getString("prezime");
                        String finansiranje = resultset.getString("finansiranje");
                        String adresa = resultset.getString("adresa");
                        String email = resultset.getString("email");
                        String telefon = resultset.getString("telefon");
                        boolean vidljiv = resultset.getBoolean("vidljiv");
                        Student student = new Student(idStudenta, godinaUpisa, Student.tipSmera.valueOf(smer), ime, prezime, Student.tipFinansiranja.valueOf(finansiranje), adresa, email, telefon, vidljiv);
                        odgovor = student;
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }

                    odgovor = "svizaposleni";
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    query = "SELECT * FROM Zaposleni";
                    resultset = statement.executeQuery(query);

                    while (resultset.next()) {
                        int idZaposlenog = resultset.getInt("idZaposlenog");
                        String pozicija = resultset.getString("pozicija");
                        String ime = resultset.getString("ime");
                        String prezime = resultset.getString("prezime");
                        String adresa = resultset.getString("adresa");
                        String email = resultset.getString("email");
                        String telefon = resultset.getString("telefon");
                        boolean vidljiv = resultset.getBoolean("vidljiv");
                        Zaposleni zaposleni = new Zaposleni(idZaposlenog, Zaposleni.tipZaposlenog.valueOf(pozicija), ime, prezime, adresa, email, telefon, vidljiv);
                        odgovor = zaposleni;
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }

                    odgovor = "svipredmeti";
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    query = "SELECT p.idPredmeta, p.naziv, p.studijskiSmer, p.semestar, p.espb, p.vidljiv, z.idZaposlenog, z.pozicija, z.ime, z.prezime FROM predmet p JOIN raspodelapredmeta rp ON p.idPredmeta = rp.idPredmeta JOIN zaposleni z ON rp.idZaposlenog = z.idZaposlenog";
                    resultset = statement.executeQuery(query);
                    HashMap<Predmet, Zaposleni> sviPredmeti = new HashMap<>();

                    while (resultset.next()) {
                        int idPredmeta = resultset.getInt("idPredmeta");
                        String naziv = resultset.getString("naziv");
                        String studijskiSmer = resultset.getString("studijskiSmer");
                        int semestar = resultset.getInt("semestar");
                        int espb = resultset.getInt("Espb");
                        boolean vidljiv = resultset.getBoolean("vidljiv");
                        int idZaposlenog = resultset.getInt("idZaposlenog");
                        String ime = resultset.getString("ime");
                        String prezime = resultset.getString("prezime");
                        String pozicija = resultset.getString("pozicija");
                        Predmet predmet;
                        Zaposleni zaposleni;
                        if (studijskiSmer != null) {
                            predmet = new Predmet(idPredmeta, naziv, Predmet.tipSmera.valueOf(studijskiSmer), semestar, espb, vidljiv);
                        } else {
                            predmet = new Predmet(idPredmeta, naziv, null, semestar, espb, vidljiv);
                        }
                        zaposleni = new Zaposleni(idZaposlenog, Zaposleni.tipZaposlenog.valueOf(pozicija), ime, prezime);
                        sviPredmeti.put(predmet, zaposleni);
                    }
                    odgovor = sviPredmeti;
                    outObj.writeObject(odgovor);
                    outObj.flush();

                    odgovor = "svesale";
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    query = "SELECT * FROM Sala";
                    resultset = statement.executeQuery(query);

                    while (resultset.next()) {
                        int idSale = resultset.getInt("idSale");
                        String naziv = resultset.getString("naziv");
                        int brojMesta = resultset.getInt("brojMesta");
                        String oprema = resultset.getString("oprema");
                        boolean vidljiv = resultset.getBoolean("vidljiv");
                        if (oprema.equals("/")) {
                            oprema = "ništa";
                        }
                        Sala sala = new Sala(idSale, naziv, brojMesta, Sala.tipOpreme.valueOf(oprema), vidljiv);
                        odgovor = sala;
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }
                    odgovor = "svezakazanesale";
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    query = "SELECT zs.idSale, zs.idPredmeta, zs.idZaposlenog, zs.datum, zs.vremePocetka, zs.vremeKraja, z.ime, z.prezime, s.naziv, p.naziv FROM zaposleni z JOIN zakazivanjesale zs ON z.idZaposlenog = zs.idZaposlenog JOIN sala s ON zs.idSale = s.idSale JOIN predmet p ON zs.idPredmeta = p.idPredmeta WHERE zs.datum >= CURDATE()";
                    resultset = statement.executeQuery(query);
                    HashMap<ZakazivanjeSale, ArrayList<String>> sveZakazaneSale = new HashMap<>();

                    while (resultset.next()) {
                        int idSale = resultset.getInt("idSale");
                        int idPredmeta = resultset.getInt("idPredmeta");
                        int idZaposlenog = resultset.getInt("idZaposlenog");
                        Date datum = resultset.getDate("datum");
                        Time vremePocetka = resultset.getTime("vremePocetka");
                        Time vremeKraja = resultset.getTime("vremeKraja");
                        String zaposleni = resultset.getString("ime") + " " + resultset.getString("prezime");
                        String nazivSale = resultset.getString("s.naziv");
                        String nazivPredmeta = resultset.getString("p.naziv");
                        ZakazivanjeSale zakazivanjeSale = new ZakazivanjeSale(idSale, idPredmeta, idZaposlenog, datum, vremePocetka, vremeKraja);
                        ArrayList SalaZaposleni = new ArrayList();
                        SalaZaposleni.add(nazivSale);
                        SalaZaposleni.add(nazivPredmeta);
                        SalaZaposleni.add(zaposleni);
                        sveZakazaneSale.put(zakazivanjeSale, SalaZaposleni);
                    }
                    odgovor = sveZakazaneSale;
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    //outObj.writeObject("kraj");

                } else if (zahtev.equals("zaposleni")) {

                    odgovor = "sviispitnirokovi";
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    query = "SELECT * FROM IspitniRok";
                    resultset = statement.executeQuery(query);

                    while (resultset.next()) {
                        int idRoka = resultset.getInt("idRoka");
                        String naziv = resultset.getString("naziv");
                        Date datumPocetka = resultset.getDate("datumPocetka");
                        Date datumKraja = resultset.getDate("datumKraja");
                        boolean aktivnost = resultset.getBoolean("aktivnost");
                        IspitniRok ispitniRok = new IspitniRok(idRoka, naziv, datumPocetka, datumKraja, aktivnost);
                        odgovor = ispitniRok;
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }

                    outObj.writeObject("kraj");
                    outObj.flush();

                } else if (zahtev.equals("student")) {

                    odgovor = "sviispitnirokovi";
                    outObj.writeObject(odgovor);
                    outObj.flush();
                    query = "SELECT * FROM IspitniRok";
                    resultset = statement.executeQuery(query);

                    while (resultset.next()) {
                        int idRoka = resultset.getInt("idRoka");
                        String naziv = resultset.getString("naziv");
                        Date datumPocetka = resultset.getDate("datumPocetka");
                        Date datumKraja = resultset.getDate("datumKraja");
                        boolean aktivnost = resultset.getBoolean("aktivnost");
                        IspitniRok ispitniRok = new IspitniRok(idRoka, naziv, datumPocetka, datumKraja, aktivnost);
                        odgovor = ispitniRok;
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }

                    outObj.writeObject("kraj");
                    outObj.flush();

                } else if (zahtev.equals("izmeniStudenta")) {
                    Student student = (Student) inObj.readObject();
                    query = "UPDATE Student SET `ime` = '" + student.getIme() + "', `prezime` = '" + student.getPrezime() + "', `adresa` = ";
                    if (student.getAdresa() == null || student.getAdresa().equals("")) {
                        query += NULL + ", `email` = ";
                    } else {
                        query += "'" + student.getAdresa() + "', `email` = ";
                    }
                    if (student.getEmail() == null || student.getEmail().equals("")) {
                        query += NULL + ", `telefon` = ";
                    } else {
                        query += "'" + student.getEmail() + "', `telefon` = ";
                    }
                    if (student.getBrojTelefona() == null || student.getBrojTelefona().equals("")) {
                        query += NULL + ", `vidljiv` = '" + (student.isVidljiv() ? 1 : 0) + "' WHERE (`idStudenta` = '" + student.getIdStudenta() + "' AND `smer` = '" + student.getSmer() + "' AND `godinaUpisa` = '" + student.getGodinaUpisa() + "')";
                    } else {
                        query += "'" + student.getBrojTelefona() + "', `vidljiv` = '" + (student.isVidljiv() ? 1 : 0) + "' WHERE (`idStudenta` = '" + student.getIdStudenta() + "' AND `smer` = '" + student.getSmer() + "' AND `godinaUpisa` = '" + student.getGodinaUpisa() + "')";
                    }
                    int izmene = statement.executeUpdate(query);
                    if (student.isVidljiv()) {
                        //vraćanje login prava u slučaju da je student bio "obrisan" pre toga
                        query = "UPDATE Login SET `aktivan` = '1' WHERE (`idStudenta` = '" + student.getIdStudenta() + "' AND `smer` = '" + student.getSmer() + "' AND `godinaUpisa` = '" + student.getGodinaUpisa() + "')";
                        izmene += statement.executeUpdate(query);
                    }
                    if (izmene != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }

                } else if (zahtev.equals("izmeniZaposlenog")) {

                    Zaposleni zaposleni = (Zaposleni) inObj.readObject();
                    query = "UPDATE Zaposleni SET `ime` = '" + zaposleni.getIme() + "', `prezime` = '" + zaposleni.getPrezime() + "', `adresa` = ";
                    if (zaposleni.getAdresa() == null || zaposleni.getAdresa().equals("")) {
                        query += NULL + ", `email` = ";
                    } else {
                        query += "'" + zaposleni.getAdresa() + "', `email` = ";
                    }
                    if (zaposleni.getEmail() == null || zaposleni.getEmail().equals("")) {
                        query += NULL + ", `telefon` = ";
                    } else {
                        query += "'" + zaposleni.getEmail() + "', `telefon` = ";
                    }
                    if (zaposleni.getBrojTelefona() == null || zaposleni.getBrojTelefona().equals("")) {
                        query += NULL + ", `vidljiv` = '" + (zaposleni.isVidljiv() ? 1 : 0) + "' WHERE `idZaposlenog` = '" + zaposleni.getIdZaposlenog() + "'";
                    } else {
                        query += "'" + zaposleni.getBrojTelefona() + "', `vidljiv` = '" + (zaposleni.isVidljiv() ? 1 : 0) + "' WHERE `idZaposlenog` = '" + zaposleni.getIdZaposlenog() + "'";
                    }
                    int izmene = statement.executeUpdate(query);
                    if (zaposleni.isVidljiv()) {
                        //vraćanje login prava u slučaju da je zaposleni bio "obrisan" pre toga
                        query = "UPDATE Login SET `aktivan` = '1' WHERE (`idZaposlenog` = '" + zaposleni.getIdZaposlenog() + "')";
                        izmene += statement.executeUpdate(query);
                    }
                    if (izmene != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }

                } else if (zahtev.equals("izmeniPredmet")) {

                    Predmet predmet = (Predmet) inObj.readObject();
                    query = "UPDATE Predmet SET `naziv` = '" + predmet.getNaziv() + "', `studijskiSmer` = ";

                    if (predmet.getStudijskiSmer() == null || predmet.getStudijskiSmer().equals("")) {
                        query += NULL + ", `semestar` = ";
                    } else {
                        query += "'" + predmet.getStudijskiSmer() + "', `semestar` = ";
                    }
                    if(predmet.getSemestar() == 0) {
                        query += NULL + ", `Espb` = ";
                    } else {
                        query += "'" + predmet.getSemestar() + "', `Espb` = ";
                    }
                    if(predmet.getEspb() == 0) {
                        query += NULL + ", `vidljiv` = '" + (predmet.isVidljiv() ? 1 : 0) + "' WHERE `idPredmeta` = '" + predmet.getIdPredmeta() + "'";
                    } else {
                        query += "'" + predmet.getEspb() + "', `vidljiv` = '" + (predmet.isVidljiv() ? 1 : 0) + "' WHERE `idPredmeta` = '" + predmet.getIdPredmeta() + "'";
                    }
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }
                } else if (zahtev.equals("izmeniSalu")) {

                    Sala sala = (Sala) inObj.readObject();
                    query = "UPDATE Sala SET `naziv` = '" + sala.getNaziv() + "', `brojMesta` = '" + sala.getBrojMesta() + "', `oprema` = '" + sala.getOprema() + "', `vidljiv` = '" + (sala.isVidljiv() ? 1 : 0)  + "' WHERE `idSale` = '" + sala.getIdSale() + "'";

                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }
                } else if (zahtev.equals("izmeniIspitniRok")) {

                    IspitniRok ispitniRok = (IspitniRok) inObj.readObject();
                    query = "UPDATE IspitniRok SET `naziv` = '" + ispitniRok.getNaziv() + "', `datumPocetka` = ";
                    if (ispitniRok.getDatumPocetka() == null) {
                        query += NULL + ", `datumKraja` = ";
                    } else {
                        query += "'" + ispitniRok.getDatumPocetka() + "', `datumKraja` = ";
                    }
                    if (ispitniRok.getDatumKraja() == null) {
                        query += NULL + ", `aktivnost` = '" + (ispitniRok.isAktivnost() ? 1 : 0) + "' WHERE `idRoka` = '" + ispitniRok.getIdRoka() + "'";
                    } else {
                        query += "'" + ispitniRok.getDatumKraja() + "', `aktivnost` = '" + (ispitniRok.isAktivnost() ? 1 : 0) + "' WHERE `idRoka` = '" + ispitniRok.getIdRoka() + "'";
                    }
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }
                } else if (zahtev.equals("dodajStudenta")) {

                    Student student = (Student) inObj.readObject();
                    query = "INSERT INTO Student(idStudenta, smer, godinaUpisa, ime, prezime, finansiranje, adresa, email, telefon, vidljiv) VALUES ('" + student.getIdStudenta() + "', '" + student.getSmer() + "', '" + student.getGodinaUpisa() + "', '" + student.getIme() + "', '" + student.getPrezime() + "', '" + student.getFinansiranje() + "', '" + student.getAdresa() + "', '" + student.getEmail() + "', '" + student.getBrojTelefona() + "', '" + (student.isVidljiv() ? 1 : 0) + "')";
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("vecPostoji");
                        outObj.flush();
                    }

                } else if (zahtev.equals("dodajZaposlenog")) {

                    Zaposleni zaposleni = (Zaposleni) inObj.readObject();
                    query = "INSERT INTO Zaposleni(idZaposlenog, pozicija, ime, prezime, adresa, email, telefon, vidljiv) VALUES ('" + zaposleni.getIdZaposlenog() + "', '" + zaposleni.getPozicija() + "', '" + zaposleni.getIme() + "', '" + zaposleni.getPrezime() + "', '" + zaposleni.getAdresa() + "', '" + zaposleni.getEmail() + "', '" + zaposleni.getBrojTelefona() + "', '" + (zaposleni.isVidljiv() ? 1 : 0) + "')";
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("vecPostoji");
                        outObj.flush();
                    }

                } else if (zahtev.equals("dodajPredmet")) {

                    Predmet predmet = (Predmet) inObj.readObject();
                    //RaspodelaPredmeta raspodelaPredmeta = (RaspodelaPredmeta) inObj.readObject();

                    query = "INSERT INTO Predmet(idPredmeta, naziv, studijskiSmer, semestar, espb, vidljiv) VALUES ('" + predmet.getIdPredmeta() + "', '" + predmet.getNaziv() + "', ";
                    if (predmet.getStudijskiSmer() == null) {
                        query += predmet.getStudijskiSmer() + ", ";
                    } else {
                        query += "'" + predmet.getStudijskiSmer() + "', ";
                    }
                    if (predmet.getSemestar() == 0) {
                        query += NULL + ", ";

                    } else {
                        query += "'" + predmet.getSemestar() + "', ";
                    }
                    if (predmet.getEspb() == 0) {
                        query += NULL + ", '" + (predmet.isVidljiv() ? 1 : 0) + "')";
                    } else {
                        query += "'" + predmet.getEspb() + "', '" + (predmet.isVidljiv() ? 1 : 0) + "')";
                    }
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("vecPostoji");
                        outObj.flush();
                    }

                }  else if (zahtev.equals("dodajSalu")) {

                    Sala sala = (Sala) inObj.readObject();
                    query = "INSERT INTO Sala(naziv, brojMesta, oprema) VALUES ('" + sala.getNaziv() + "', '" + sala.getBrojMesta() + "', '" + sala.getOprema() + "', `vidljiv` = '" + (sala.isVidljiv() ? 1 : 0) + "')";
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }

                } else if (zahtev.equals("dodajIspitniRok")) {

                    IspitniRok ispitniRok = (IspitniRok) inObj.readObject();
                    query = "INSERT INTO IspitniRok(naziv, datumPocetka, datumKraja, aktivnost) VALUES ('" + ispitniRok.getNaziv() + "', ";
                    if (ispitniRok.getDatumPocetka() == null) {
                        query += NULL + ", ";
                    } else {
                        query += "'" + ispitniRok.getDatumPocetka() + "', ";
                    }
                    if (ispitniRok.getDatumKraja() == null) {
                        query += NULL + ", '" + (ispitniRok.isAktivnost() ? 1 : 0) + "')";
                    } else {
                        query += "'" + ispitniRok.getDatumKraja() + "', '" + (ispitniRok.isAktivnost() ? 1 : 0) + "')";
                    }
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }
                } else if (zahtev.equals("dodajZakazivanjeSale")) {

                    ZakazivanjeSale zakazivanjeSale = (ZakazivanjeSale) inObj.readObject();
                    String query1 = "SELECT * FROM ZakazivanjeSale WHERE datum = '" + zakazivanjeSale.getDatum() + "' AND '" + zakazivanjeSale.getVremePocetka() + "'BETWEEN vremePocetka AND vremeKraja OR '" + zakazivanjeSale.getVremeKraja() + "'BETWEEN vremePocetka AND vremeKraja";
                    resultset = statement.executeQuery(query1);
                    if (!resultset.next()) {
                        int izmena = 0;
                        String query2 = "INSERT INTO ZakazivanjeSale(idSale, idPredmeta, idZaposlenog, datum, vremePocetka, vremeKraja) VALUES ('" + zakazivanjeSale.getIdSale() + "', '" + zakazivanjeSale.getIdPredmeta() + "', '" + zakazivanjeSale.getIdZaposlenog() + "', '" + zakazivanjeSale.getDatum() + "', '" + zakazivanjeSale.getVremePocetka() + "', '" + zakazivanjeSale.getVremeKraja() + "')";
                        izmena = statement.executeUpdate(query2);
                        if (izmena != 0) {
                            outObj.writeObject("uspelo");
                            outObj.flush();
                        } else {
                            outObj.writeObject("nijeUspelo");
                            outObj.flush();
                            System.out.println('b');
                        }
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                        System.out.println("a");
                    }
                } else if (zahtev.equals("obrisiStudenta")) {

                    int izmene = 0;
                    Student student = (Student) inObj.readObject();
                    query = "UPDATE Student SET `vidljiv` = '0' WHERE (`idStudenta` = '" + student.getIdStudenta() + "') AND (`smer` = '" + student.getSmer() + "') AND (`godinaUpisa` = '" + student.getGodinaUpisa() + "')";
                    izmene += statement.executeUpdate(query);
                    query = "UPDATE Login SET `aktivan` = '0' WHERE (`idStudenta` = '" + student.getIdStudenta() + "') AND (`smer` = '" + student.getSmer() + "') AND (`godinaUpisa` = '" + student.getGodinaUpisa() + "')";
                    izmene += statement.executeUpdate(query);
                    //MOZDA GA NEMAM U SIFRAMA, TREBA TEK DA DODAM, PA JE PROVERA != 0
                    if (izmene != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }

                } else if (zahtev.equals("obrisiZaposlenog")) {

                    int izmene = 0;
                    Zaposleni zaposleni = (Zaposleni) inObj.readObject();
                    query = "UPDATE Zaposleni SET `vidljiv` = '0' WHERE (`idZaposlenog` = '" + zaposleni.getIdZaposlenog() + "')";
                    izmene += statement.executeUpdate(query);
                    query = "UPDATE Login SET `aktivan` = '0' WHERE (`idZaposlenog` = '" + zaposleni.getIdZaposlenog() + "')";
                    izmene += statement.executeUpdate(query);
                    //MOZDA GA NEMAM U SIFRAMA, TREBA TEK DA DODAM, PA JE PROVERA != 0
                    if (izmene != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nijeUspelo");
                        outObj.flush();
                    }

                } else if (zahtev.equals("obrisiPredmet")) {

                    Predmet predmet = (Predmet) inObj.readObject();
                    query = "UPDATE Predmet SET `vidljiv` = '0' WHERE (`idPredmeta` = '" + predmet.getIdPredmeta() + "')";
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nepostoji");
                        outObj.flush();
                    }
                } else if (zahtev.equals("obrisiSalu")) {

                    Sala sala = (Sala) inObj.readObject();
                    query = "UPDATE Sala SET `vidljiv` = '0' WHERE (`idSale` = '" + sala.getIdSale() + "')";
                    int izmena = statement.executeUpdate(query);
                    if (izmena != 0) {
                        outObj.writeObject("uspelo");
                        outObj.flush();
                    } else {
                        outObj.writeObject("nepostoji");
                        outObj.flush();
                    }
                } else if (zahtev.equals("loginInfoStudent")) {

                    Student student = (Student) inObj.readObject();
                    query = "SELECT korisnickoIme, lozinka FROM Login WHERE idStudenta = '" + student.getIdStudenta() + "' AND smer = '" + student.getSmer() + "' AND godinaUpisa = '" + student.getGodinaUpisa() + "'";
                    resultset = statement.executeQuery(query);

                    if (!resultset.next()) {
                        odgovor = "nepostoji";
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    } else {
                        String korisnickoIme = resultset.getString("korisnickoIme");
                        String lozinka = resultset.getString("lozinka");
                        Login login = new Login(korisnickoIme, lozinka);
                        odgovor = "postoji";
                        outObj.writeObject(odgovor);
                        outObj.flush();
                        odgovor = login;
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }
                } else if (zahtev.equals("loginInfoZaposleni")) {

                    Zaposleni zaposleni = (Zaposleni) inObj.readObject();
                    query = "SELECT korisnickoIme, lozinka FROM Login WHERE idZaposlenog = '" + zaposleni.getIdZaposlenog() + "'";
                    resultset = statement.executeQuery(query);

                    if (!resultset.next()) {
                        odgovor = "nepostoji";
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    } else {
                        String korisnickoIme = resultset.getString("korisnickoIme");
                        String lozinka = resultset.getString("lozinka");
                        Login login = new Login(korisnickoIme, lozinka);
                        odgovor = "postoji";
                        outObj.writeObject(odgovor);
                        outObj.flush();
                        odgovor = login;
                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }
                }
                //ZATVARANJE KONEKCIJE SA BAZOM
                try {

                    connection.close();
                    System.out.println("Zatvorena je konekcija sa Bazom");

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {

                //U SLUCAJU DA NIJE OSTVARENA KONEKCIJA SA BAZOM
                //update na JavaFx application niti
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        ispis("Nije uspela konekcija sa Bazom");

                    }
                });
            } finally {

                //ZATVARANJE KONEKCIJE - ovo treba tek kad se ide na X
                if (socket != null && inObj != null && outObj != null) {

                    try {
                        socket.close();
                        inObj.close();
                        outObj.close();
                        ispis("Prekinuta veza sa klijentom: " + adresa);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}