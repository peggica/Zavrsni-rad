package model;

public class RaspodelaPredmeta {

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