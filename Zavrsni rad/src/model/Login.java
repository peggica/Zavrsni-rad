package model;

import javafx.collections.ObservableList;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Login implements Serializable {

    private static final long serialVersionUID = 3L;
    private int idZaposlenog;
    private String korisnickoIme;
    private String lozinka;
    private int idStudenta;
    public enum tipSmera { avt, asuv, eko, elite, epo, is, net, nrt, rt };
    private String smer;
    private int godinaUpisa;

    public Login(String korisnickoIme, String lozinka) {

        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;

    }

    public Login(int idZaposlenog, String korisnickoIme, String lozinka, int idStudenta, tipSmera tip, int godinaUpisa) {

        this.idZaposlenog = idZaposlenog;
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.idStudenta = idStudenta;
        this.smer = tip.toString();
        this.godinaUpisa = godinaUpisa;

    }

    public int getIdZaposlenog() {
        return idZaposlenog;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public int getIdStudenta() {
        return idStudenta;
    }

    public String getSmer() {
        return smer;
    }

    public int getGodinaUpisa() {
        return godinaUpisa;
    }

    //METODA KOJA GENERIŠE KORISNIČKO IME
    private static String generisanjeKorisnickogImena(Object obj) {

        String korisnickoIme = null;

        if (obj instanceof Student) {

            korisnickoIme = ((Student)obj).getIme().toLowerCase() + ((Student)obj).getSmer().toLowerCase() + ((Student)obj).getIdStudenta() + (((Student)obj).getGodinaUpisa() % 100);

        } else if (obj instanceof Zaposleni) {

            if (((Zaposleni) obj).getPozicija().toLowerCase().equals("asistent") || ((Zaposleni) obj).getPozicija().toLowerCase().equals("saradnik")) {

                korisnickoIme = ((Zaposleni) obj).getPozicija().toLowerCase() + ((Zaposleni) obj).getPrezime().toLowerCase();
            } else if (((Zaposleni) obj).getPozicija().toLowerCase().equals("profesor")) {

                korisnickoIme = ((Zaposleni) obj).getPozicija().toLowerCase().substring(0, 4) + ((Zaposleni) obj).getPrezime().toLowerCase();
            }
        }
        return korisnickoIme;

    }

    //METODA KOJA GENERIŠE LOZINKU
    private static String gerisanjeLozinke(Object obj) {

        String lozinka = "";
        Random random = new Random();

        if (obj instanceof Student) {

            for (int i = 0; i < 4; i++) {
                lozinka += (char) (random.nextInt(26) + 'a');
            }

        } else if (obj instanceof Zaposleni) {

            lozinka = ((Zaposleni) obj).getPrezime().substring(0, 1).toLowerCase() + ((Zaposleni) obj).getIme().toLowerCase();
        }
        for (int i = 0; i < 4; i++) {
            lozinka += random.nextInt(10);
        }
        return lozinka;

    }

    //METODA KOJA VRAĆA NOVO KORISNIČKO IME I LOZINKU
    public static String[] getKorisnickoImeLozinka(ArrayList<Login> logini, Object obj) {

        String korisnickoIme;
        String lozinka;
        boolean jedinstvena = true;

        korisnickoIme = generisanjeKorisnickogImena(obj);
        do {
            lozinka = gerisanjeLozinke(obj);

            for (Login l : logini) {
                if (l.getLozinka().equals(lozinka)) {
                    jedinstvena = false;
                    break;
                }
            }
        } while (!jedinstvena);

        String[] noviLogin = new String[2];
        noviLogin[0] = korisnickoIme;
        noviLogin[1] = lozinka;
        return noviLogin;

    }

}