package server;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

/** Klase Server i ServerThread namenjene za podizanje servera i prihvatanje konekcija klijenata, i razmenu zahteva/odgovora sa njima
 *  prikaz u JavaFx formi
 *  @author Biljana Stanojevic  */

public class Server extends Application {

    TextArea areaIspis = new TextArea();
    ServerSocket serverSocket;

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

    }

    private class ServerThread extends Thread {

        private static final int TCP_PORT = 9000;
        private int count = 0;

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
                    count++;

                    //pokretanje nove niti da ne bi doslo do blokiranja od strane praznog inputstreama
                    Thread acceptedThread = new Thread(new ServerSocketAcceptedThread(socket, count));
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
        private Object zahtev;
        private Object odgovor;
        private int count = 0;

        ServerSocketAcceptedThread(Socket socket, int count) {

            this.socket = socket;
            this.count = count;
        }

        @Override
        public void run() {

            try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            ispis("Ostvarena konekcija sa klijentom: " + count);
                        }
                    });

                    while (true) {

                        //RAZMENA SA KLIJENTOM
                        zahtev = in.readLine();
                        //TODO: proveriti u bazi da li postoji korisnik za primljene podatke
                        if (zahtev == null) break;
                        //TODO: poslati podatke o korisniku klijentu ako postoji
                        out.println();

                    }

                //ZATVARANJE KONEKCIJE
                in.close();
                out.close();
                socket.close();
                ispis("Klijent "  + count + " je prekinuo vezu");

            } catch (IOException e) {

                e.printStackTrace();

            }
        }

    }
}