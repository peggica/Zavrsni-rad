package server.model;

public class Predmet {

    private int idPredmeta;
    private String naziv;
    private String studijskiSmer;
    private int semestar;
    private int espb;

    public Predmet(int idPredmeta, String naziv, String studijskiSmer, int semestar, int espb) {

        this.idPredmeta = idPredmeta;
        this.naziv = naziv;
        this.studijskiSmer = studijskiSmer;
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