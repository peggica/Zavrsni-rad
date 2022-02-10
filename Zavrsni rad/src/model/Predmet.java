package model;

import javafx.collections.ObservableList;

import java.io.Serializable;

public class Predmet implements Serializable {

    private static final long serialVersionUID = 5L;
    private int idPredmeta;
    private String naziv;
    public enum tipSmera {avt, asuv, eko, elite, epo, ist, net, nrt, rt}//, zajednicki
    private String studijskiSmer;
    private int semestar;
    private int espb;

    public Predmet(int idPredmeta, String naziv, tipSmera smer, int semestar, int espb) {

        this.idPredmeta = idPredmeta;
        this.naziv = naziv;
        if (smer == null) {
            this.studijskiSmer = null;
        } else {
            this.studijskiSmer = smer.toString();
        }
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

    //METODA KOJA GENERISE NOVI ID (+1) ZA TAJ STUDIJSKI SMER
    public static int idNovogPredmeta(ObservableList<Predmet> predmeti, String smer) {

        int idPredmeta = 0;
        int brojNadjenih = (int) predmeti.stream().filter(p -> p.getStudijskiSmer() == smer).count();
        if(smer.equals("")) {
            idPredmeta = 10000;
        } else if(smer.equals("avt")) {
            idPredmeta = 11000;
        } else if(smer.equals("asuv")) {
            idPredmeta = 12000;
        } else if(smer.equals("eko")) {
            idPredmeta = 13000;
        } else if(smer.equals("elite")) {
            idPredmeta = 14000;
        } else if(smer.equals("epo")) {
            idPredmeta = 15000;
        } else if(smer.equals("ist")) {
            idPredmeta = 16000;
        } else if(smer.equals("net")) {
            idPredmeta = 17000;
        } else if(smer.equals("nrt")) {
            idPredmeta = 18000;
        } else if(smer.equals("rt")) {
            idPredmeta = 19000;
        }
        idPredmeta += brojNadjenih + 1;
        return idPredmeta;

    }

}