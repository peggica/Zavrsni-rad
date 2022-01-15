package server.model;

public class Sala {

    private enum tipOpreme { racunari { public String toString() { return "računari"; }}, nista { public String toString() { return "/"; }}};
    private int idSale;
    private String naziv;
    private int brojMesta;
    private String oprema;

    public Sala(int idSale, String naziv, int brojMesta, tipOpreme tip) {

        this.idSale = idSale;
        this.naziv = naziv;
        this.brojMesta = brojMesta;
        this.oprema = tip.toString();

    }

    public Sala(int idSale, String naziv, int brojMesta) {

        this.idSale = idSale;
        this.naziv = naziv;
        this.brojMesta = brojMesta;
        //defaultno je oprema null jer nije nista setovano iz enum

    }

    public int getIdSale() {
        return idSale;
    }

    public String getNaziv() {
        return naziv;
    }

    public int getBrojMesta() {
        return brojMesta;
    }

    public String getOprema() {
        return oprema;
    }

}