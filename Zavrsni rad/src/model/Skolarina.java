package model;

import java.io.Serializable;

public class Skolarina implements Serializable {

    private static final long serialVersionUID = 12L;
    public enum tipSmera { avt, asuv, eko, elite, epo, ist, net, nrt, rt }
    private String smer;
    private double iznos;

    public Skolarina(Skolarina.tipSmera tipSm, double iznos) {
        this.smer = tipSm.toString();
        this.iznos = iznos;
    }

    public String getSmer() {
        return smer;
    }

    public double getIznos() {
        return iznos;
    }

}
