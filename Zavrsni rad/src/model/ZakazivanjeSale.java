package model;

import java.sql.*;

public class ZakazivanjeSale {

    private int idSale;
    private int idPredmeta;
    private Date datum;
    private Time vremePocetka;
    private Time vremeKraja;

    public ZakazivanjeSale(int idSale, int idPredmeta, Date datum, Time vremePocetka, Time vremeKraja) {

        this.idSale = idSale;
        this.idPredmeta = idPredmeta;
        this.datum = datum;
        this.vremePocetka = vremePocetka;
        this.vremeKraja = vremeKraja;

    }

    public int getIdSale() {
        return idSale;
    }

    public int getIdPredmeta() {
        return idPredmeta;
    }

    public Date getDatum() {
        return datum;
    }

    public Time getVremePocetka() {
        return vremePocetka;
    }

    public Time getVremeKraja() {
        return vremeKraja;
    }

}