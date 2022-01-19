package model;

import java.io.Serializable;
import java.sql.*;

public class Zapisnik implements Serializable {

    private static final long serialVersionUID = 10L;
    private int idPredmeta;
    private Date datum;
    private int ocena;
    private int idStudenta;
    public enum tipSmera { AVT, ASUV, EKO, ELITE, EPO, IS, NET, NRT, RT };
    private String smer;
    private int godinaUpisa;
    private int idRoka;

    public Zapisnik(int idPredmeta, Date datum, int ocena, int idStudenta, tipSmera tip, int godinaUpisa, int idRoka) {

        this.idPredmeta = idPredmeta;
        this.datum = datum;
        this.ocena = ocena;
        this.idStudenta = idStudenta;
        this.smer = tip.toString();
        this.godinaUpisa = godinaUpisa;
        this.idRoka = idRoka;

    }

    public int getIdPredmeta() {
        return idPredmeta;
    }

    public Date getDatum() {
        return datum;
    }

    public int getOcena() {
        return ocena;
    }

    public int getIdStudenta() {
        return idStudenta;
    }

    public String getSmer() {
        return smer;
    }

    public int getGodinaUpisa() {
        return godinaUpisa;
    }

    public int getIdRoka() {
        return idRoka;
    }

}