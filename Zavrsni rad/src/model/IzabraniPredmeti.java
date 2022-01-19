package model;

import java.io.Serializable;

public class IzabraniPredmeti implements Serializable {

    private static final long serialVersionUID = 2L;
    private int idStudenta;
    private int godinaUpisa;
    public enum tipSmera { avt, asuv, eko, elite, epo, ist, net, nrt, rt };
    private String smer;
    private int idPredmeta;

    public IzabraniPredmeti(int idStudenta, int godinaUpisa, tipSmera tipSm, int idPredmeta) {

        this.idStudenta = idStudenta;
        this.godinaUpisa = godinaUpisa;
        this.smer = tipSm.toString();
        this.idPredmeta = idPredmeta;

    }

    public int getIdStudenta() {
        return idStudenta;
    }

    public int getGodinaUpisa() {
        return godinaUpisa;
    }

    public String getSmer() {
        return smer;
    }

    public int getIdPredmeta() {
        return idPredmeta;
    }

}