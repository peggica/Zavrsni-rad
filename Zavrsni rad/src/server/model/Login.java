package server.model;

import java.io.Serializable;

public class Login implements Serializable {

    private int idZaposlenog;
    private String korisnickoIme;
    private String lozinka;
    private int idStudenta;
    private enum tipSmera { AVT, ASUV, EKO, ELITE, EPO, IS, NET, NRT, RT };
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