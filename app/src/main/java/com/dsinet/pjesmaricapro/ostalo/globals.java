package com.dsinet.pjesmaricapro.ostalo;

import static com.dsinet.pjesmaricapro.R.color.dsColor;
import static com.dsinet.pjesmaricapro.R.color.white;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dsinet.pjesmaricapro.db.DbMain;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class globals extends Application {
    private static Context cxMain;

    public globals(Context cx)
    {
        cxMain = cx;
    }
    public final Integer BrojStavki = 4;
    public String BrojZapisa() {return getParam("BrojZapisa");}
    public String BrojLicence() {
        return getParam("BrojLicence");
    }
    public String Code() {
        return getParam("Code");
    }

    public String BojaAplikacije() {
        String value = getParam("BojaAplikacije");
        if (value == null || value.isEmpty()) {
            return "#FFFFFF"; // default fallback
        }
        return value;
    }

    public String BojaPjesmarnika() {
        return getParam("BojaPjesmarnika");
    }
    public String BojaFonta() {
        return getParam("BojaFonta");
    }
    public String VelicinaFonta() {
        return getParam("VelicinaFonta");
    }

    @SuppressLint("ResourceType")
    public String getParam(String param)
    {
        String tmpvalue = new DbMain(cxMain).getSetting(param);
        if (tmpvalue == null) tmpvalue = "";

        switch (param)
        {
            case "BojaAplikacije":
                if (tmpvalue.isEmpty())
                {
                    int colorInt = ContextCompat.getColor(cxMain, dsColor);
                    tmpvalue = hexStringColor(colorInt);
                    setParam(param, tmpvalue);
                }
                break;
            case "BojaPjesmarnika":
                if (tmpvalue.isEmpty())
                {
                    int colorInt = ContextCompat.getColor(cxMain, dsColor);
                    tmpvalue = hexStringColor(colorInt);
                    setParam(param, tmpvalue);
                }
                break;
            case "BojaFonta":
                if (tmpvalue.isEmpty())
                {
                    int colorInt = ContextCompat.getColor(cxMain, white);
                    tmpvalue = hexStringColor(colorInt);
                    setParam(param, tmpvalue);
                }
                break;
            case "VelicinaFonta":
                if (tmpvalue.isEmpty() || tmpvalue.equals("0"))
                {
                    tmpvalue = "14f";
                    setParam(param, tmpvalue);
                }
                break;
            case "Licencirano":
                if (tmpvalue == null || tmpvalue.equals("0") || tmpvalue.isEmpty())
                {
                    tmpvalue = "6";
                    setParam(param, tmpvalue);
                }
                break;
            case "BrojZapisa":
                tmpvalue = String.valueOf(new DbMain(cxMain).brojZapisa());
                setParam(param, tmpvalue);
                break;
        }

        return tmpvalue;
    }

    public void setParam(String param, String value)
    {
        new DbMain(cxMain).updateSetting(param, value);
    }

    public String hexStringColor(int color) {
        // sicherstellen, dass wir #RRGGBB zurückgeben
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public void setTopBarColor(AppCompatActivity ac)
    {
        if (ac.getSupportActionBar() != null) {
            try {
                String colorHex = new globals(cxMain).BojaAplikacije();
                ac.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorHex)));
            } catch (Exception ignored) {
                // falls parse fehlschlägt, nicht crashen
            }
        }
    }

    public String getID()
    {
        String dbCode = Code();

        if (dbCode == null || dbCode.isEmpty())
        {
            dbCode = UUID.randomUUID().toString().substring(0, 6);
            setParam("Code", dbCode);
        }
        return dbCode;
    }
    public int boolToInt(boolean b) {
        return b ? View.VISIBLE : View.INVISIBLE;
    }
}
