package model;

import java.io.Serializable;

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

}