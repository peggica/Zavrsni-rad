package model;

import javafx.collections.ObservableList;

import java.io.Serializable;

public class Zaposleni implements Serializable {

    private static final long serialVersionUID = 13L;
    private int idZaposlenog;
    public enum tipZaposlenog { profesor, asistent, saradnik }
    private String pozicija;
    private String ime;
    private String prezime;
    private String adresa;
    private String email;
    private String brojTelefona;
    private boolean vidljiv;

    public Zaposleni() {
        this.setIme("");
        this.setPrezime("");
    }

    public Zaposleni(int idZaposlenog, tipZaposlenog tip, String ime, String prezime, String adresa, String email, String brojTelefona, boolean vidljiv) {

        this.idZaposlenog = idZaposlenog;
        this.pozicija = tip.toString();
        this.ime = ime;
        this.prezime = prezime;
        this.adresa = adresa;
        this.email = email;
        this.brojTelefona = brojTelefona;
        this.vidljiv = vidljiv;

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

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBrojTelefona() {
        return brojTelefona;
    }

    public String getImePrezime() {
        return ime + " " +prezime;
    }

    public void setBrojTelefona(String brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    public void setVidljiv(boolean vidljiv) {
        this.vidljiv = vidljiv;
    }

    public boolean isVidljiv() {
        return vidljiv;
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