package model;

import java.io.Serializable;

public class PrijavaIspita implements Serializable {

    private static final long serialVersionUID = 4L;
    private int idPredmeta;
    private int idStudenta;
    public enum tipSmera { AVT, ASUV, EKO, ELITE, EPO, IS, NET, NRT, RT };
    private String smer;
    private int godinaUpisa;
    private int idRoka;

    public PrijavaIspita(int idPredmeta, int idStudenta, tipSmera tip, int godinaUpisa, int idRoka) {

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