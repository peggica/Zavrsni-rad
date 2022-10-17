package model;

import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.Calendar;

public class Student implements Serializable {

    private static final long serialVersionUID = 9L;
    private int idStudenta;
    private int godinaUpisa;
    public enum tipSmera { avt, asuv, eko, elite, epo, ist, net, nrt, rt }
    private String smer;
    private String ime;
    private String prezime;
    public enum tipFinansiranja { budzet, saf }
    public String finansiranje;
    private String adresa;
    private String email;
    private String brojTelefona;
    private String brojIndeksa;
    private boolean vidljiv;

    public Student(int idStudenta, int godinaUpisa, tipSmera tipSm, String ime, String prezime, tipFinansiranja tipFin, String adresa, String email, String brojTelefona, boolean vidljiv) {

        this.idStudenta = idStudenta;
        this.godinaUpisa = godinaUpisa;
        this.smer = tipSm.toString();
        this.brojIndeksa = this.smer + "/" + this.idStudenta + "-" + String.valueOf(this.godinaUpisa).substring(2);
        this.ime = ime;
        this.prezime = prezime;
        this.finansiranje = tipFin.toString();
        this.adresa = adresa;
        this.email = email;
        this.brojTelefona = brojTelefona;
        this.vidljiv = vidljiv;
    }

    public Student(int idStudenta, int godinaUpisa, tipSmera tipSm, String ime, String prezime, tipFinansiranja tipFin) {

        this.idStudenta = idStudenta;
        this.godinaUpisa = godinaUpisa;
        this.smer = tipSm.toString();
        this.ime = ime;
        this.prezime = prezime;
        this.finansiranje = tipFin.toString();
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

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getFinansiranje() {
        return finansiranje;
    }

    public void setFinansiranje(tipFinansiranja tipFin) {
        this.finansiranje = tipFin.toString();
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBrojTelefona() {
        return brojTelefona;
    }

    public void setBrojTelefona(String brojTelefona) {
        this.brojTelefona = brojTelefona;
    }

    public String getBrojIndeksa() {
        return brojIndeksa;
    }

    public String getImePrezime() {
        return ime + " " + prezime;
    }

    public void setVidljiv(boolean vidljiv) {
        this.vidljiv = vidljiv;
    }

    public boolean isVidljiv() {
        return vidljiv;
    }

    public int getSemestar(int brojUpisa) {
        //ovo je za sada za osnovne studije
        int semestar = 0;   //ukoliko ne upiše sledeću godinu - semestar je 0
        int mesec = Calendar.getInstance().get(Calendar.MONTH); //0-11
        int godina = Calendar.getInstance().get(Calendar.YEAR);
        int godinaUpisa = getGodinaUpisa();

        if (brojUpisa <= 3) {
            //ukoliko ne obnavlja godinu
            if ((godina - godinaUpisa) <= brojUpisa) {
                if (mesec >= 2 && mesec <= 9) {     //od marta do septembra, 1,2,3 godine razlike = 2,4,6 semestar
                    semestar = brojUpisa * 2;
                } else if (mesec <= 1 || mesec >= 10) {    //januar i februar ili oktobar, novembar i decembar 1,2,3 godine razlike = 1,3,5 semestar
                    semestar = brojUpisa * 2 + 1;
                }
            //ukoliko obnavlja godinu
            } else {
                //fiksno na 2,4,6 semestar
                semestar = brojUpisa * 2;
            }
        }
        return semestar;
    }

    //METODA KOJA VRACA NOVI ID (+1) ZA TU GODINU UPISA I TAJ SMER
    public static int idNovogStudenta(ObservableList<Student> studenti, String smer, int godinaUpisa) {

        int brojNadjenih = (int)studenti.stream().filter(s -> s.getSmer().equals(smer) && s.getGodinaUpisa() == godinaUpisa).count();
        return brojNadjenih + 1;

    }

}