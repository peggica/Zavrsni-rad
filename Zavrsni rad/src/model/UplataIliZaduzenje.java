package model;

import java.io.Serializable;
import java.sql.Date;

public class UplataIliZaduzenje implements Serializable {

    private static final long serialVersionUID = 10L;
    private int idUplate;
    private String opis;
    private int idStudenta;
    public enum tipSmera { avt, asuv, eko, elite, epo, ist, net, nrt, rt }
    private String smer;
    private int godinaUpisa;
    private Date datum;
    private double iznos;

    public UplataIliZaduzenje(int idUplate, String opis, int idStudenta, UplataIliZaduzenje.tipSmera tipSm, int godinaUpisa, Date datum, double iznos) {
        this.idUplate = idUplate;
        this.opis = opis;
        this.idStudenta = idStudenta;
        this.smer = tipSm.toString();
        this.godinaUpisa = godinaUpisa;
        this.datum = datum;
        this.iznos = iznos;
    }

    public UplataIliZaduzenje(String opis, Date datum, double iznos) {
        this.opis = opis;
        this.datum = datum;
        this.iznos = iznos;
    }

    public String getOpis() {
        return opis;
    }

    public Date getDatum() {
        return datum;
    }

    public double getIznos() {
        return iznos;
    }
}
