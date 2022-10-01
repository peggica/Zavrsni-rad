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
    private boolean aktivnaPrijava;

    public IspitniRok(int idRoka, String naziv, Date datumPocetka, Date datumKraja, boolean aktivnost, boolean aktivnaPrijava) {

        this.idRoka = idRoka;
        this.naziv = naziv;
        this.datumPocetka = datumPocetka;
        this.datumKraja = datumKraja;
        this.aktivnost = aktivnost;
        this.aktivnaPrijava = aktivnaPrijava;

    }

    public IspitniRok(String naziv, Date datumPocetka, Date datumKraja, boolean aktivnost) {

        this.naziv = naziv;
        this.datumPocetka = datumPocetka;
        this.datumKraja = datumKraja;
        this.aktivnost = aktivnost;
        this.aktivnaPrijava = aktivnaPrijava;

    }

    public int getIdRoka() {
        return idRoka;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Date getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(Date datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public Date getDatumKraja() {
        return datumKraja;
    }

    public void setDatumKraja(Date datumKraja) {
        this.datumKraja = datumKraja;
    }

    public boolean isAktivnost() {
        return aktivnost;
    }

    public void setAktivnost(boolean aktivnost) {
        this.aktivnost = aktivnost;
    }

    public boolean isAktivnaPrijava() {
        return aktivnaPrijava;
    }

    public void setAktivnaPrijava(boolean aktivnaPrijava) {
        this.aktivnaPrijava = aktivnaPrijava;
    }

}