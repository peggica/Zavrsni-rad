package server;

import javafx.application.*;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.*;

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

        Thread sqlConnectionThread = new Thread(new SQLConnectionThread(connection));
        //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
        sqlConnectionThread.setDaemon(true);
        sqlConnectionThread.start();

        serverStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {

                Thread sqlCloseThread = null;
                try {
                    sqlCloseThread = new Thread(new SQLCloseThread(connection));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                sqlCloseThread.setDaemon(true);
                sqlCloseThread.start();
            }
        });

    }

    private class SQLConnectionThread extends Thread {

        private Connection conn;

        SQLConnectionThread(Connection connection) throws SQLException {

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

    private class SQLCloseThread extends Thread {

        private Connection conn;

        SQLCloseThread(Connection connection) throws SQLException {

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
                    Thread acceptedThread = new Thread(new ServerSocketAcceptedThread(socket, socket.getInetAddress().getHostName()));
                    //okoncava nit kada dodje do kraja programa - kada se izadje iz forme
                    acceptedThread.setDaemon(true);
                    acceptedThread.start();

                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    private class ServerSocketAcceptedThread extends Thread {

        private Socket socket = null;
        private BufferedReader in;
        private PrintWriter out;
        private String zahtev = "";
        private String odgovor = "";
        private String adresa;

        ServerSocketAcceptedThread(Socket socket, String adresa) {

            this.socket = socket;
            this.adresa = adresa;
        }

        @Override
        public void run() {

            try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    //update na JavaFx application niti
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            ispis("Zahtev od strane klijenta: " + adresa);
                        }
                    });

                    while (true) {

                        //RAZMENA SA KLIJENTOM
                        zahtev = in.readLine();

                        if (zahtev == null) {
                            break;
                        }

                        //TODO: ovo u objekte ne stringove i prvo provera za login - loop ako ne unese ispravno i vraca gresku
                        //TODO: proveriti u bazi da li postoji korisnik za primljene podatke
                        String korisnickoIme = zahtev.split(" ")[0];
                        String lozinka = zahtev.split(" ")[1];
                        String query = "SELECT * FROM Login WHERE korisnickoIme= '" + korisnickoIme + "' AND lozinka = '" + lozinka + "'";
                        Statement statement = connection.createStatement();
                        ResultSet resultset = statement.executeQuery(query);

                        if (!resultset.next()) {

                            odgovor = "Nema podudaranja u bazi";

                        } else {
                            do {
                                //System.out.println(resultset.getInt("idZaposlenog") + resultset.getInt("idStudenta") + resultset.getString("smer") + resultset.getInt("godinaUpisa"));
                                odgovor = "Uspeh";
                            } while (resultset.next());

                        }

                        out.println(odgovor);

                        //System.out.println(resultset.getInt("idZaposlenog") + resultset.getInt("idStudenta") + resultset.getString("smer") + resultset.getInt("godinaUpisa"));
                        //TODO: poslati podatke o korisniku klijentu ako postoji

                    }

                //ZATVARANJE KONEKCIJE
                in.close();
                out.close();
                socket.close();
                ispis("Prekinuta veza sa klijentom: " + adresa);

            } catch (IOException | SQLException e) {

                e.printStackTrace();

            }
        }

    }
}