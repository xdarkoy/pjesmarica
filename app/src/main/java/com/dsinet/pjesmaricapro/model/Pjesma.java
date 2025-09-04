package com.dsinet.pjesmaricapro.model;

public class Pjesma {
    public String _autor = "";
    public String _naslov = "";
    public String _tekst = "";

    public String getAutor() {
        return _autor;
    }

    public void setAutor(String autor) {
        this._autor = autor;
    }

    public String getNaslov() {
        return _naslov;
    }

    public void setNaslov(String Naslov) {
        this._naslov = Naslov;
    }

    public String getTekst() {
        return _tekst;
    }

    public void setTekst(String Tekst) {
        this._tekst = Tekst;
    }
}
