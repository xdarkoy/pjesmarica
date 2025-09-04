package com.dsinet.pjesmaricapro.model;

public class Pjesme {

    public String _ID = "";
    public String _autor = "";
    public String _naslov = "";
    public String _favorit = "0";


    public String getID(){
        return _ID;
    }

    public void setID(String ID) {
        this._ID = ID;
    }

    public String getAutor() {
        return _autor;
    }

    public void setAutor(String Autor) {
        this._autor = Autor;
    }

    public String getNaslov() {
        return _naslov;
    }

    public void setNaslov(String Naslov) {
        this._naslov = Naslov;
    }

    public String getFavorit() {
        return _favorit;
    }

    public void setFavorit(String Favorit) {
        this._favorit = Favorit;
    }
}
