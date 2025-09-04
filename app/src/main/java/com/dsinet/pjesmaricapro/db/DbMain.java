package com.dsinet.pjesmaricapro.db;

import android.content.Context;

import com.dsinet.pjesmaricapro.model.Favoriti;
import com.dsinet.pjesmaricapro.model.Izvodjaci;
import com.dsinet.pjesmaricapro.model.My;
import com.dsinet.pjesmaricapro.model.Pjesma;
import com.dsinet.pjesmaricapro.model.Pjesme;

import java.util.ArrayList;

public class DbMain {

    private final SqlLiteDbHelper dbHelper;

    public DbMain(Context context) {
        Context appCtx = context.getApplicationContext();
        this.dbHelper = new SqlLiteDbHelper(appCtx);
        // Sicherstellen, dass die Asset-DB beim ersten Start vorhanden ist:
        try {
            dbHelper.openDataBase();
        } catch (Exception ignored) {
            // Optional: Logging/Crashlytics
        }
    }

    /* =================== Zähler =================== */

    public int brojZapisa() {
        return dbHelper.getBrojZapisa();
    }

    public int getBrojZapisaTabele(String tabela) {
        return dbHelper.getBrojZapisaTabele(tabela);
    }

    /* =================== Pjesme / Izvođači =================== */

    public ArrayList<Pjesme> getPjesme(int izvodjacId, String pojam) {
        return dbHelper.getPjesme(izvodjacId, pojam);
    }

    public ArrayList<Izvodjaci> getIzvodjaci(String pretraga) {
        return dbHelper.getIzvodjaci(pretraga);
    }

    public Pjesma getPjesma(String id) {
        return dbHelper.getPjesma(id);
    }

    /* =================== Favoriti =================== */

    public ArrayList<Favoriti> getFavoriti() {
        return dbHelper.getFavoriti();
    }

    public void insertFavorit(int id) {
        dbHelper.insertFavorit(id);
    }

    public void deleteFavorit(String id) {
        dbHelper.deleteFavorit(id);
    }

    /* =================== MojePjesme =================== */

    public ArrayList<My> getMojePjesme() {
        return dbHelper.getMojePjesme();
    }

    public My getMojaPjesma(String id) {
        return dbHelper.getMojaPjesma(id);
    }

    public void insertMy(My my) {
        dbHelper.insertMy(my);
    }

    public void updateMy(My my) {
        dbHelper.updateMy(my);
    }

    public void deleteMojaPjesma(String id) {
        dbHelper.deleteMojaPjesma(id);
    }

    /* =================== Settings =================== */

    public String getSetting(String column) {
        return dbHelper.getSetting(column);
    }

    public void updateSetting(String column, String value) {
        dbHelper.updateSetting(column, value);
    }
}
