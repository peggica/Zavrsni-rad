package model;

import javafx.collections.ObservableList;

import java.io.Serializable;

public class Zaposleni implements Serializable {

    private static final long serialVersionUID = 11L;
    private int idZaposlenog;
    public enum tipZaposlenog { profesor, asistent, saradnik }
    private String pozicija;
    private String ime;
    private String prezime;
    private String adresa;
    private String email;
    private String brojTelefona;

    public Zaposleni(int idZaposlenog, tipZaposlenog tip, String ime, String prezime, String adresa, String email, String brojTelefona) {

        this.idZaposlenog = idZaposlenog;
        this.pozicija = tip.toString();
        this.ime = ime;
        this.prezime = prezime;
        this.adresa = adresa;
        this.email = email;
        this.brojTelefona = brojTelefona;

    }

    public Zaposleni(int idZaposlenog, tipZaposlenog tip, String ime, String prezime) {

        this.idZaposlenog = idZaposlenog;
        this.pozicija = tip.toString();
        this.ime = ime;
        this.prezime = prezime;
        //defaultno su adresa, email i brojTelefona null - za String ako ne dodelim

    }

    public int getIdZaposlenog() {
        return idZaposlenog;
    }

    public String getPozicija() {
        return pozicija;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getEmail() {
        return email;
    }

    public String getBrojTelefona() {
        return brojTelefona;
    }

    //METODA KOJA GENERISE NOVI ID (+1) ZA TU POZICIJU
    public static int idNovogZaposlenog(ObservableList<Zaposleni> zaposleni, String pozicija) {

        int idZaposlenog = 0;
        int brojNadjenih = (int) zaposleni.stream().filter(z -> z.getPozicija().equals(pozicija)).count();
        if (pozicija.equals("profesor")) {
            idZaposlenog = 1000;
        } else if (pozicija.equals("asistent")) {
            idZaposlenog = 2000;
        } else {
            idZaposlenog = 3000;
        }
        idZaposlenog += brojNadjenih + 1;
        return idZaposlenog;

    }

}