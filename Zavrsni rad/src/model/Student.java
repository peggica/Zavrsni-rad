package model;

import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Student implements Serializable {

    private static final long serialVersionUID = 8L;
    private int idStudenta;
    private int godinaUpisa;
    public enum tipSmera { avt, asuv, eko, elite, epo, ist, net, nrt, rt }
    private String smer;
    private String ime;
    private String prezime;
    public enum tipFinansiranja { budzet, saf };
    public String finansiranje;
    private String adresa;
    private String email;
    private String brojTelefona;
    private String brojIndeksa;

    public Student(int idStudenta, int godinaUpisa, tipSmera tipSm, String ime, String prezime, tipFinansiranja tipFin, String adresa, String email, String brojTelefona) {

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

    public String getBrojIndeksa() {
        return brojIndeksa;
    }

    public String imePrezime() {
        return ime + " " + prezime;
    }

    public int getSemestar() {
        //ovo je za sada za osnovne studije
        int semestar = 6;   //najveci moguci je 6 semestar
        int godine = Calendar.getInstance().get(Calendar.YEAR) - godinaUpisa;
        int mesec = Calendar.getInstance().get(Calendar.MONTH); //0-11
        if(godine <= 3) {
            if (mesec >= 2 && mesec <= 8) {  //od marta do septembra, 1,2,3 godine razlike = 2,4,6 semestar
                semestar = godine * 2;
            } else if (mesec <= 1) {    //januar i februar, 1,2,3 godine razlike = 1,3,5 semestar
                semestar = godine * 2 - 1;
            } else if (mesec >= 9) {
                if (godine < 3) {   //oktobar, novembar, decembar, 0,1,2 godine razlike = 1,3,5 semestar
                    semestar = godine * 2 + 1;
                } else {    //oktobar, novembar, decembar, 3 godine razlike
                    semestar = 6;
                }
            }
        }
        return semestar;
    }

    //METODA KOJA VRACA NOVI ID (+1) ZA TU GODINU UPISA I TAJ SMER
    public static int idNovogStudenta(ObservableList<Student> studenti, String smer, int godinaUpisa) {

        int brojNadjenih = (int)studenti.stream().filter(s -> s.getSmer() == smer && s.getGodinaUpisa() == godinaUpisa).count();
        return brojNadjenih + 1;

    }

}