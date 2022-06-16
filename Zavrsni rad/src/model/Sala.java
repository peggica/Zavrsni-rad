package model;

import java.io.Serializable;

public class Sala implements Serializable {

    private static final long serialVersionUID = 7L;
    public enum tipOpreme { računari, ništa {
        @Override
        public String toString() {
            return "/";
        }
    }}
    private int idSale;
    private String naziv;
    private int brojMesta;
    private String oprema;
    private boolean vidljiv;

    public Sala(int idSale, String naziv, int brojMesta, tipOpreme tip, boolean vidljiv) {

        this.idSale = idSale;
        this.naziv = naziv;
        this.brojMesta = brojMesta;
        this.oprema = tip.toString();
        this.vidljiv = vidljiv;

    }

    public Sala(int idSale, String naziv, int brojMesta, tipOpreme tip) {

        this.idSale = idSale;
        this.naziv = naziv;
        this.brojMesta = brojMesta;
        this.oprema = tip.toString();

    }


    public Sala(String naziv, int brojMesta, tipOpreme tip, boolean vidljiv) {

        this.naziv = naziv;
        this.brojMesta = brojMesta;
        this.oprema = tip.toString();
        this.vidljiv = vidljiv;
    }

    public int getIdSale() {
        return idSale;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojMesta() {
        return brojMesta;
    }

    public void setBrojMesta(int brojMesta) {
        this.brojMesta = brojMesta;
    }

    public String getOprema() {
        return oprema;
    }

    public void setOprema(tipOpreme tip) {
        this.oprema = tip.toString();
    }

    public void setVidljiv(boolean vidljiv) {
        this.vidljiv = vidljiv;
    }

    public boolean isVidljiv() {
        return vidljiv;
    }

}