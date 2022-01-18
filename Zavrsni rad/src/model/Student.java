package model;

import java.io.Serializable;

public class Student implements Serializable {

    private static final long serialVersionUID = 2L;
    private String brojIndeksa;
    private int idStudenta;
    private int godinaUpisa;
    public enum tipSmera { avt, asuv, eko, elite, epo, ist, net, nrt, rt };
    private String smer;
    private String ime;
    private String prezime;
    public enum tipFinansiranja { budzet, saf };
    public String finansiranje;
    private String adresa;
    private String email;
    private String brojTelefona;

    public Student(int idStudenta, int godinaUpisa, tipSmera tipSm, String ime, String prezime, tipFinansiranja tipFin, String adresa, String email, String brojTelefona) {

        this.idStudenta = idStudenta;
        this.godinaUpisa = godinaUpisa;
        this.smer = tipSm.toString();
        this.ime = ime;
        this.prezime = prezime;
        this.finansiranje = tipFin.toString();
        this.adresa = adresa;
        this.email = email;
        this.brojTelefona = brojTelefona;
        this.brojIndeksa = smer + "/" + idStudenta + "-" +godinaUpisa;
    }

    public Student(int idStudenta, int godinaUpisa, tipSmera tipSm, String ime, String prezime, tipFinansiranja tipFin) {

        this.idStudenta = idStudenta;
        this.godinaUpisa = godinaUpisa;
        this.smer = tipSm.toString();
        this.ime = ime;
        this.prezime = prezime;
        this.finansiranje = tipFin.toString();
        this.brojIndeksa = smer + "/" + idStudenta + "-" +godinaUpisa;
        //defaultno su adresa, email i brojTelefona null - za String ako ne dodelim

    }

    public String getBrojIndeksa() {
        return brojIndeksa;
    }

    public int getIdStudenta() {
        return idStudenta;
    }

    public int getGodinaUpisa() {
        return godinaUpisa;
    }

    public String getSmer() {
        return smer;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getFinansiranje() {
        return finansiranje;
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