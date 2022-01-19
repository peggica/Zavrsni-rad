package model;

import java.io.Serializable;
import java.sql.*;

public class IspitniRok implements Serializable {

    private static final long serialVersionUID = 1L;
    private int idRoka;
    private String naziv;
    private Date datumPocetka;
    private Date datumKraja;
    private boolean aktivnost;

    public IspitniRok(int idRoka, String naziv, boolean aktivnost) {

        this.idRoka = idRoka;
        this.naziv = naziv;
        this.aktivnost = aktivnost;

    }

    public IspitniRok(int idRoka, String naziv, Date datumPocetka, Date datumKraja, boolean aktivnost) {

        this.idRoka = idRoka;
        this.naziv = naziv;
        this.datumPocetka = datumPocetka;
        this.datumKraja = datumKraja;
        this.aktivnost = aktivnost;

    }


    public int getIdRoka() {
        return idRoka;
    }

    public String getNaziv() {
        return naziv;
    }

    public Date getDatumPocetka() {
        return datumPocetka;
    }

    public Date getDatumKraja() {
        return datumKraja;
    }

    public boolean isAktivnost() {
        return aktivnost;
    }
    //za dodavanje novog id iz arrayliste pročitanih.length + 1 u glavnom programu

}