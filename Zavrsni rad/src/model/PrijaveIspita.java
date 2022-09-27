package model;

import java.io.Serializable;
import java.sql.Date;

public class PrijaveIspita implements Serializable {

    private static final long serialVersionUID = 5L;
    private int idPredmeta;
    private int idStudenta;
    public enum tipSmera { avt, asuv, eko, elite, epo, is, net, nrt, rt };
    private String smer;
    private int godinaUpisa;
    private int idRoka;
    private Date datum;

    public PrijaveIspita(int idPredmeta, int idStudenta, tipSmera tip, int godinaUpisa, int idRoka) {

        this.idPredmeta = idPredmeta;
        this.idStudenta = idStudenta;
        this.smer = tip.toString();
        this.godinaUpisa = godinaUpisa;
        this.idRoka = idRoka;

    }

    public int getIdPredmeta() {
        return idPredmeta;
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