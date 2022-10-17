package model;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalDate;

public class Zapisnik implements Serializable {

    private static final long serialVersionUID = 12L;
    private int idPredmeta;
    private Date datum;
    private int ocena;
    private int idStudenta;
    public enum tipSmera { avt, asuv, eko, elite, epo, ist, net, nrt, rt }
    private String smer;
    private int godinaUpisa;
    private int idRoka;

    public Zapisnik(int idPredmeta, int ocena, int idStudenta, tipSmera tip, int godinaUpisa, int idRoka) {
        this.idPredmeta = idPredmeta;
        this.ocena = ocena;
        this.idStudenta = idStudenta;
        this.smer = tip.toString();
        this.godinaUpisa = godinaUpisa;
        this.idRoka = idRoka;
        this.datum = Date.valueOf(LocalDate.now());
    }

    public int getIdPredmeta() {
        return idPredmeta;
    }

    public void setIdPredmeta(int idPredmeta) {
        this.idPredmeta = idPredmeta;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public int getOcena() {
        return ocena;
    }

    public void setOcena(int ocena) {
        this.ocena = ocena;
    }

    public int getIdStudenta() {
        return idStudenta;
    }

    public void setIdStudenta(int idStudenta) {
        this.idStudenta = idStudenta;
    }

    public String getSmer() {
        return smer;
    }

    public void setSmer(String smer) {
        this.smer = smer;
    }

    public int getGodinaUpisa() {
        return godinaUpisa;
    }

    public void setGodinaUpisa(int godinaUpisa) {
        this.godinaUpisa = godinaUpisa;
    }

    public int getIdRoka() {
        return idRoka;
    }

    public void setIdRoka(int idRoka) {
        this.idRoka = idRoka;
    }
}