package model;

import java.io.Serializable;

public class Predmet implements Serializable {

    private static final long serialVersionUID = 5L;
    private int idPredmeta;
    private String naziv;
    public enum tipSmera { avt, asuv, eko, elite, epo, ist, net, nrt, rt }
    private String studijskiSmer;
    private int semestar;
    private int espb;

    public Predmet(int idPredmeta, String naziv, tipSmera smer, int semestar, int espb) {

        this.idPredmeta = idPredmeta;
        this.naziv = naziv;
        this.studijskiSmer = smer.toString();
        this.semestar = semestar;
        this.espb = espb;

    }

    public Predmet(int idPredmeta, String naziv) {

        this.idPredmeta = idPredmeta;
        this.naziv = naziv;
        //defaultno je studijskiSMer null - za String ako ne dodelim, a semestar i espb 0

    }

    public int getIdPredmeta() {
        return idPredmeta;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getStudijskiSmer() {
        return studijskiSmer;
    }

    public int getSemestar() {
        return semestar;
    }

    public int getEspb() {
        return espb;
    }

}