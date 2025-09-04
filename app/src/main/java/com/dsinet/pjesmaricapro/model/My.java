package com.dsinet.pjesmaricapro.model;

public class My {
    public String _ID = "";
    public String _naslov= "";
    public String _pjesma= "";


    public String getID(){
        return _ID;
    }

    public void setID(String ID) {
        this._ID = ID;
    }

    public String getNaslov() {
        return _naslov;
    }

    public void setNaslov(String Naslov) {
        this._naslov = Naslov;
    }

    public String getPjesma() {
        return _pjesma;
    }

    public void setPjesma(String Pjesma) {
        this._pjesma = Pjesma;
    }
}
