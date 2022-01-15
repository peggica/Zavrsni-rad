package server.model;

public class Student {

    private int idStudenta;
    private int godinaUpisa;
    private enum tipSmera { AVT, ASUV, EKO, ELITE, EPO, IS, NET, NRT, RT };
    private String smer;
    private String ime;
    private String prezime;
    private String finansiranje;
    private String adresa;
    private String email;
    private String brojTelefona;

    public Student(int idStudenta, int godinaUpisa, tipSmera tip, String ime, String prezime, String finansiranje, String adresa, String email, String brojTelefona) {

        this.idStudenta = idStudenta;
        this.godinaUpisa = godinaUpisa;
        this.smer = tip.toString();
        this.ime = ime;
        this.prezime = prezime;
        this.finansiranje = finansiranje;
        this.adresa = adresa;
        this.email = email;
        this.brojTelefona = brojTelefona;

    }

    public Student(int idStudenta, int godinaUpisa, tipSmera tip, String ime, String prezime, String finansiranje) {

        this.idStudenta = idStudenta;
        this.godinaUpisa = godinaUpisa;
        this.smer = tip.toString();
        this.ime = ime;
        this.prezime = prezime;
        this.finansiranje = finansiranje;
        //defaultno su adresa, email i brojTelefona null - za String ako ne dodelim

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

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getFinansiranje() {
        return finansiranje;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getEmail() {
        return email;
    }

    public String getBrojTelefona() {
        return brojTelefona;
    }

}