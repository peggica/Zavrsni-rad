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

    public Sala(int idSale, String naziv, int brojMesta, tipOpreme tip) {

        this.idSale = idSale;
        this.naziv = naziv;
        this.brojMesta = brojMesta;
        this.oprema = tip.toString();

    }

    public Sala(int idSale, String naziv, int brojMesta) {

        this.idSale = idSale;
        this.naziv = naziv;
        this.brojMesta = brojMesta;
        //defaultno je oprema null jer nije nista setovano iz enum

    }

    public int getIdSale() {
        return idSale;
    }

    public String getNaziv() {
        return naziv;
    }

    public int getBrojMesta() {
        return brojMesta;
    }

    public String getOprema() {
        return oprema;
    }

}