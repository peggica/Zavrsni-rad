package model;

import java.io.Serializable;

public class RaspodelaPredmeta implements Serializable {

    private static final long serialVersionUID = 6L;
    private int idPredmeta;
    private int idZaposlenog;

    public RaspodelaPredmeta(int idPredmeta, int idZaposlenog) {

        this.idPredmeta = idPredmeta;
        this.idZaposlenog = idZaposlenog;

    }

    public int getIdPredmeta() {
        return idPredmeta;
    }

    public int getIdZaposlenog() {
        return idZaposlenog;
    }

}