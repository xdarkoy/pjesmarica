package com.dsinet.pjesmaricapro.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dsinet.pjesmaricapro.model.Favoriti;
import com.dsinet.pjesmaricapro.model.Izvodjaci;
import com.dsinet.pjesmaricapro.model.My;
import com.dsinet.pjesmaricapro.model.Pjesma;
import com.dsinet.pjesmaricapro.model.Pjesme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SqlLiteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dba2.db";
    private static final String DB_PATH_SUFFIX = "/databases/";
    private final Context ctx;

    public SqlLiteDbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context.getApplicationContext();
        ensurePrepopulated(); // garantiert DB vor erstem Query
    }

    /* =================== Lifecycle/Config =================== */

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        setWriteAheadLoggingEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // leer: DB kommt aus assets
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Migrationen falls nötig
    }

    /**
     * Öffnet/erstellt DB und kopiert sie beim ersten Start aus assets.
     * (Bleibt zur Kompatibilität; ensurePrepopulated() wurde bereits im Konstruktor aufgerufen.)
     */
    public SQLiteDatabase openDataBase() throws SQLException {
        File dbFile = ctx.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            copyDataBaseFromAsset();
        }
        return SQLiteDatabase.openDatabase(
                dbFile.getPath(),
                null,
                SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY
        );
    }

    /**
     * Stellt sicher, dass die DB aus assets vorhanden ist. Falls eine existierende DB
     * vorhanden ist, aber die kritische Tabelle fehlt (z.B. leere DB), wird sie ersetzt.
     * Idempotent.
     */
    private synchronized void ensurePrepopulated() {
        File dbFile = ctx.getDatabasePath(DATABASE_NAME);

        // wenn Datei nicht existiert: einfach kopieren
        if (!dbFile.exists()) {
            copyDataBaseFromAsset();
            return;
        }

        // Wenn Datei existiert: prüfen ob Tabelle Postavke vorhanden ist.
        SQLiteDatabase checkDb = null;
        Cursor c = null;
        boolean hasTable = false;
        try {
            checkDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
            c = checkDb.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='Postavke'", null);
            hasTable = (c != null && c.moveToFirst());
        } catch (Exception ignored) {
            // Fehler beim Öffnen -> behandeln wie "keine Tabelle"
            hasTable = false;
        } finally {
            if (c != null) try { c.close(); } catch (Exception ignored) {}
            if (checkDb != null && checkDb.isOpen()) try { checkDb.close(); } catch (Exception ignored) {}
        }

        // Falls Tabelle fehlt, ersetze die Datei durch die Asset-Version.
        if (!hasTable) {
            // Lösche alte Datei (falls vorhanden) und erneut kopieren
            try {
                if (dbFile.exists()) dbFile.delete();
            } catch (Exception ignored) {}
            copyDataBaseFromAsset();
        }
    }

    private void copyDataBaseFromAsset() {
        File dir = new File(ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Could not create database directory: " + dir);
        }
        File dbFile = ctx.getDatabasePath(DATABASE_NAME);
        try (InputStream in = ctx.getAssets().open(DATABASE_NAME);
             FileOutputStream out = new FileOutputStream(dbFile)) {
            byte[] buf = new byte[8 * 1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            out.getFD().sync();
        } catch (IOException e) {
            throw new RuntimeException("Error copying database from assets", e);
        }
    }

    /* =================== Helpers =================== */

    public long getRowCount(String table) {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), table);
    }

    public int getBrojZapisa() {
        try (Cursor c = getReadableDatabase()
                .rawQuery("SELECT COUNT(*) AS BROJAC FROM Tekstovi", null)) {
            return (c.moveToFirst()) ? c.getInt(0) : 0;
        }
    }

    public int getBrojZapisaTabele(String tabela) {
        try (Cursor c = getReadableDatabase()
                .rawQuery("SELECT COUNT(*) FROM " + tabela, null)) {
            return (c.moveToFirst()) ? c.getInt(0) : 0;
        }
    }

    /* =================== Favoriti =================== */

    public ArrayList<Favoriti> getFavoriti() {
        ArrayList<Favoriti> favoriti = new ArrayList<>();
        final String sql =
                "SELECT a.ID, b.ID AS PjesmaID, c.Izvodjac, b.Pjesma " +
                        "FROM Favoriti a " +
                        "JOIN Tekstovi b ON a.ID = b.ID " +
                        "JOIN Izvodjaci c ON b.IzvodjacID = c.ID";
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, null)) {
            while (cursor.moveToNext()) {
                Favoriti ft = new Favoriti();
                ft.set_ID(cursor.getString(cursor.getColumnIndexOrThrow("ID")));
                ft.set_PjesmaId(cursor.getString(cursor.getColumnIndexOrThrow("PjesmaID")));
                ft.set_Izvodjac(cursor.getString(cursor.getColumnIndexOrThrow("Izvodjac")));
                ft.set_Pjesma(cursor.getString(cursor.getColumnIndexOrThrow("Pjesma")));
                favoriti.add(ft);
            }
        }
        return favoriti;
    }

    public void insertFavorit(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ID", id);
        db.insert("Favoriti", null, values);
    }

    public void deleteFavorit(String id) {
        getWritableDatabase().delete("Favoriti", "ID = ?", new String[]{id});
    }

    /* =================== MojePjesme =================== */

    public void insertMy(My my) {
        ContentValues v = new ContentValues();
        v.put("Naslov", my.getNaslov());
        v.put("Pjesma", my.getPjesma());
        getWritableDatabase().insert("MojePjesme", null, v);
    }

    public void updateMy(My my) {
        ContentValues v = new ContentValues();
        v.put("Naslov", my.getNaslov());
        v.put("Pjesma", my.getPjesma());
        getWritableDatabase().update("MojePjesme", v, "ID = ?", new String[]{my.getID()});
    }

    public void deleteMojaPjesma(String id) {
        getWritableDatabase().delete("MojePjesme", "ID = ?", new String[]{id});
    }

    public ArrayList<My> getMojePjesme() {
        ArrayList<My> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery("SELECT * FROM MojePjesme", null)) {
            while (c.moveToNext()) {
                My m = new My();
                m.setID(c.getString(c.getColumnIndexOrThrow("ID")));
                m.setNaslov(c.getString(c.getColumnIndexOrThrow("Naslov")));
                m.setPjesma(c.getString(c.getColumnIndexOrThrow("Pjesma")));
                list.add(m);
            }
        }
        return list;
    }

    public My getMojaPjesma(String id) {
        My m = null;
        try (Cursor c = getReadableDatabase()
                .rawQuery("SELECT * FROM MojePjesme WHERE ID = ?", new String[]{id})) {
            if (c.moveToFirst()) {
                m = new My();
                m.setID(c.getString(c.getColumnIndexOrThrow("ID")));
                m.setNaslov(c.getString(c.getColumnIndexOrThrow("Naslov")));
                m.setPjesma(c.getString(c.getColumnIndexOrThrow("Pjesma")));
            }
        }
        return m;
    }

    /* =================== Izvodjaci / Pjesme =================== */

    public ArrayList<Izvodjaci> getIzvodjaci(String pretraga) {
        ArrayList<Izvodjaci> list = new ArrayList<>();
        String sql = "SELECT ID, Izvodjac FROM Izvodjaci";
        String[] args = null;

        if (pretraga != null && !pretraga.isEmpty()) {
            sql += " WHERE Izvodjac LIKE ?";
            args = new String[]{"%" + pretraga + "%"};
        }

        try (Cursor c = getReadableDatabase().rawQuery(sql, args)) {
            while (c.moveToNext()) {
                Izvodjaci i = new Izvodjaci();
                i.setID(c.getString(c.getColumnIndexOrThrow("ID")));
                i.setNaziv(c.getString(c.getColumnIndexOrThrow("Izvodjac")));
                list.add(i);
            }
        }
        return list;
    }

    public ArrayList<Pjesme> getPjesme(int autorId, String pjesma) {
        ArrayList<Pjesme> list = new ArrayList<>();
        String sql;
        String[] args;

        if (pjesma != null && !pjesma.isEmpty()) {
            sql = "SELECT a.ID, a.IzvodjacID, b.Izvodjac, a.Pjesma, " +
                    "CASE WHEN EXISTS(SELECT 1 FROM Favoriti x WHERE x.ID = a.ID) THEN 1 ELSE 0 END AS Favorit " +
                    "FROM Tekstovi a JOIN Izvodjaci b ON a.IzvodjacID = b.ID " +
                    "WHERE a.Pjesma LIKE ?";
            args = new String[]{"%" + pjesma + "%"};
        } else {
            sql = "SELECT a.ID, a.IzvodjacID, b.Izvodjac, a.Pjesma, " +
                    "CASE WHEN EXISTS(SELECT 1 FROM Favoriti x WHERE x.ID = a.ID) THEN 1 ELSE 0 END AS Favorit " +
                    "FROM Tekstovi a JOIN Izvodjaci b ON a.IzvodjacID = b.ID " +
                    "WHERE a.IzvodjacID = ?";
            args = new String[]{String.valueOf(autorId)};
        }

        try (Cursor c = getReadableDatabase().rawQuery(sql, args)) {
            while (c.moveToNext()) {
                Pjesme p = new Pjesme();
                p.setID(c.getString(c.getColumnIndexOrThrow("ID")));
                p.setAutor(c.getString(c.getColumnIndexOrThrow("Izvodjac")));
                p.setNaslov(c.getString(c.getColumnIndexOrThrow("Pjesma")));
                p.setFavorit(c.getString(c.getColumnIndexOrThrow("Favorit")));
                list.add(p);
            }
        }
        return list;
    }

    public Pjesma getPjesma(String id) {
        Pjesma p = null;
        String sql = "SELECT b.Izvodjac, a.Pjesma AS Naslov, a.Tekst " +
                "FROM Tekstovi a JOIN Izvodjaci b ON a.IzvodjacID = b.ID " +
                "WHERE a.ID = ?";
        try (Cursor c = getReadableDatabase().rawQuery(sql, new String[]{id})) {
            if (c.moveToFirst()) {
                p = new Pjesma();
                p.setAutor(c.getString(c.getColumnIndexOrThrow("Izvodjac")));
                p.setNaslov(c.getString(c.getColumnIndexOrThrow("Naslov")));
                p.setTekst(c.getString(c.getColumnIndexOrThrow("Tekst")));
            }
        }
        return p;
    }

    /* =================== Settings =================== */

    public String getSetting(String column) {
        // Defensive: stelle sicher, dass DB vorbereitet ist (nochmal)
        ensurePrepopulated();

        String sql = "SELECT " + column + " FROM Postavke WHERE ID = 1";
        try (Cursor c = getReadableDatabase().rawQuery(sql, null)) {
            if (c.moveToFirst()) {
                String val = c.getString(0);
                return val != null ? val : "";
            }
        } catch (Exception e) {
            // fallback
        }
        return "";
    }

    public void updateSetting(String column, String value) {
        // Defensive: ensure DB present
        ensurePrepopulated();
        try {
            ContentValues v = new ContentValues();
            v.put(column, value);
            getWritableDatabase().update("Postavke", v, "ID = ?", new String[]{"1"});
        } catch (Exception e) {
            // falls Tabelle nicht existiert - ignoriere oder erstelle Tabelle falls erwünscht
        }
    }
}
