package model;

import java.io.Serializable;
import java.sql.*;

public class ZakazivanjeSale implements Serializable {

    private static final long serialVersionUID = 9L;
    private int idSale;
    private int idPredmeta;
    private int idZaposlenog;
    private static Date datum;
    private Time vremePocetka;
    private Time vremeKraja;

    public ZakazivanjeSale(int idSale, int idPredmeta, int idZaposlenog, Date datum, Time vremePocetka, Time vremeKraja) {

        this.idSale = idSale;
        this.idPredmeta = idPredmeta;
        this.idZaposlenog = idZaposlenog;
        this.datum = datum;
        this.vremePocetka = vremePocetka;
        this.vremeKraja = vremeKraja;

    }

    public ZakazivanjeSale(Date datum, Time vremePocetka, Time vremeKraja) {
        this.datum = datum;
        this.vremePocetka = vremePocetka;
        this.vremeKraja = vremeKraja;
    }

    public ZakazivanjeSale(Date datum, Time vremePocetka) {
        this.datum = datum;
        this.vremePocetka = vremePocetka;
    }

    public int getIdSale() {
        return idSale;
    }

    public int getIdPredmeta() {
        return idPredmeta;
    }

    public int getIdZaposlenog() {
        return idZaposlenog;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Time getVremePocetka() {
        return vremePocetka;
    }

    public void setVremePocetka(Time vremePocetka) {
        this.vremePocetka = vremePocetka;
    }

    public Time getVremeKraja() {
        return vremeKraja;
    }

    public void setVremeKraja(Time vremeKraja) {
        this.vremeKraja = vremeKraja;
    }

}