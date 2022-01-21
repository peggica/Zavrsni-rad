package server;

import javafx.application.*;
import javafx.event.EventHandler;
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

/** Klase Server i ServerThread namenjene za podizanje servera i prihvatanje konekcija klijenata
 *  i razmenu zahteva/odgovora sa njima prikaz u JavaFx formi
 *  @author Biljana Stanojevic  */

public class Server extends Application {

    private static Connection connection = null;
    private TextArea areaIspis = new TextArea();
    private ServerSocket serverSocket;

    public static void main(String[] args) {

        launch(args);

    }

    /** Dopisuje novi red u TextArea za prikaz uspesno podignutnog servera i ostvarenih novih konekcija na formi    */
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

        Thread sqlKonekcijaThread = new Thread(new SQLKonekcijaThread(connection));
        //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
        sqlKonekcijaThread.setDaemon(true);
        sqlKonekcijaThread.start();

        serverStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {

                Thread sqlZatvaranjeThread = null;
                try {
                    sqlZatvaranjeThread = new Thread(new SQLZatvaranjeThread(connection));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                sqlZatvaranjeThread.setDaemon(true);
                sqlZatvaranjeThread.start();
            }
        });

    }

    private class SQLKonekcijaThread extends Thread {

        private Connection conn;

        SQLKonekcijaThread(Connection connection) throws SQLException {

            this.conn = connection;
        }
        @Override
        public void run() {
            try {

                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/studentskasluzba", "root", "");
                Server.connection = conn;
                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                /*SQLOsveziThread sqlOsveziThread = new SQLOsveziThread(conn);
                sqlOsveziThread.setDaemon(true);
                sqlOsveziThread.start();*/

                //update na JavaFx application niti
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        ispis("Ostvarena konekcija sa Bazom");

                    }
                });

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    private class SQLZatvaranjeThread extends Thread {

        private Connection conn;

        SQLZatvaranjeThread(Connection connection) throws SQLException {

            this.conn = connection;
        }

        @Override
        public void run() {
            try {

                conn.close();
                System.out.println("Zatvorena je konekcija sa Bazom");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

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
                    Thread acceptedThread = new Thread(new ServerSocketThreadPrihvatanje(socket, socket.getInetAddress().getHostName()));
                    //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                    acceptedThread.setDaemon(true);
                    acceptedThread.start();

                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    private class ServerSocketThreadPrihvatanje extends Thread {

        private Socket socket = null;
        private ObjectInputStream inObj;
        private ObjectOutputStream outObj;
        private Object zahtev;
        private Object odgovor;
        private String adresa;

        ServerSocketThreadPrihvatanje(Socket socket, String adresa) {

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
                        query = "SELECT * FROM Login WHERE korisnickoIme = '" + korisnickoIme + "' AND lozinka = '" + lozinka + "'";
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
                                    Zaposleni zaposleni = new Zaposleni(idZaposlenog, Zaposleni.tipZaposlenog.valueOf(pozicija), ime, prezime, adresa, email, telefon);
                                    odgovor = "zaposleni";
                                    outObj.writeObject(odgovor);
                                    outObj.flush();
                                    odgovor = zaposleni;
                                    outObj.writeObject(odgovor);
                                    outObj.flush();
                                }

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
                                    Student student = new Student(idStudenta, godinaUpisa, Student.tipSmera.valueOf(smer), ime, prezime, Student.tipFinansiranja.valueOf(finansiranje), adresa, email, telefon);
                                    odgovor = "student";
                                    outObj.writeObject(odgovor);
                                    outObj.flush();
                                    odgovor = student;
                                    outObj.writeObject(odgovor);
                                    outObj.flush();
                                }

                            } else {
                                //onda je korisnik sluzbe i saljem mu podatke za formu
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
                                    Student student = new Student(idStudenta, godinaUpisa, Student.tipSmera.valueOf(smer), ime, prezime, Student.tipFinansiranja.valueOf(finansiranje), adresa, email, telefon);
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
                                    Zaposleni zaposleni = new Zaposleni(idZaposlenog, Zaposleni.tipZaposlenog.valueOf(pozicija), ime, prezime, adresa, email, telefon);
                                    odgovor = zaposleni;
                                    outObj.writeObject(odgovor);
                                    outObj.flush();
                                }

                                odgovor = "svipredmeti";
                                outObj.writeObject(odgovor);
                                outObj.flush();
                                query = "SELECT * FROM Predmet";
                                resultset = statement.executeQuery(query);

                                while (resultset.next()) {
                                    int idPredmeta = resultset.getInt("idPredmeta");
                                    String naziv = resultset.getString("naziv");
                                    String studijskiSmer = resultset.getString("studijskiSmer");
                                    int semestar = resultset.getInt("semestar");
                                    int espb = resultset.getInt("Espb");
                                    //TODO: POSLATI I PROFESORE NEKAKO + UPIT*
                                    Predmet predmet = new Predmet(idPredmeta, naziv, Predmet.tipSmera.valueOf(studijskiSmer), semestar, espb);
                                    odgovor = predmet;
                                    outObj.writeObject(odgovor);
                                    outObj.flush();
                                }

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
                                    if(oprema.equals("/")) {
                                        oprema = "ništa";
                                    }
                                    Sala sala = new Sala(idSale, naziv, brojMesta, Sala.tipOpreme.valueOf(oprema));
                                    odgovor = sala;
                                    outObj.writeObject(odgovor);
                                    outObj.flush();
                                }
                                outObj.writeObject("kraj");
                            }
                        }
                    } else if(zahtev.equals("zaposleni")) {
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

                    }

            } catch (IOException | SQLException | ClassNotFoundException e) {

                e.printStackTrace();
            } finally {

                //ZATVARANJE KONEKCIJE - ovo treba tek kad se ide na X
                if(socket != null) {

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