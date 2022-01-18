package model;

import java.io.Serializable;

public class Zaposleni implements Serializable {

    private static final long serialVersionUID = 3L;
    private int idZaposlenog;
    public enum tipZaposlenog { profesor, asistent, saradnik };
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

}