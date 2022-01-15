package server;

import javafx.application.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.*;
import server.model.*;

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
        private Object zahtev = "";
        private String odgovor = "";
        private String adresa;

        ServerSocketThreadPrihvatanje(Socket socket, String adresa) {

            this.socket = socket;
            this.adresa = adresa;
        }

        @Override
        public void run() {

            try {

                    outObj = new ObjectOutputStream(socket.getOutputStream());
                    //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    inObj = new ObjectInputStream(socket.getInputStream());

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            ispis("Zahtev od strane klijenta: " + adresa);
                        }
                    });

                while (zahtev.toString().length() == 0) {

                    //RAZMENA SA KLIJENTOM
                    zahtev = inObj.readObject().toString();
                    System.out.println(zahtev);
                    if (zahtev.equals("login")) {

                        Login login = (Login) inObj.readObject();
                        System.out.println(login.toString());

                        //TODO: ovo u objekte ne stringove i prvo provera za login - loop ako ne unese ispravno i vraca gresku
                        //TODO: proveriti u bazi da li postoji korisnik za primljene podatke
                        String korisnickoIme = login.getKorisnickoIme();
                        String lozinka = login.getLozinka();
                        String query = "SELECT * FROM Login WHERE korisnickoIme = '" + korisnickoIme + "' AND lozinka = '" + lozinka + "'";
                        Statement statement = connection.createStatement();
                        ResultSet resultset = statement.executeQuery(query);

                        if (!resultset.next()) {

                            odgovor = "Nema podudaranja u bazi";

                        } else {
                            do {
                                System.out.println(resultset.getInt("idZaposlenog") + resultset.getInt("idStudenta") + resultset.getString("smer") + resultset.getInt("godinaUpisa"));
                                odgovor = "Uspeh";
                            } while (resultset.next());

                        }

                        outObj.writeObject(odgovor);
                        outObj.flush();
                    }

                    //System.out.println(resultset.getInt("idZaposlenog") + resultset.getInt("idStudenta") + resultset.getString("smer") + resultset.getInt("godinaUpisa"));
                    //TODO: poslati podatke o korisniku klijentu ako postoji

                }

            } catch (IOException | SQLException | ClassNotFoundException e) {

                e.printStackTrace();

            }

            finally {

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