package com.dsinet.pjesmaricapro.fragmenti;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dsinet.pjesmaricapro.R;
import com.dsinet.pjesmaricapro.db.DbMain;
import com.dsinet.pjesmaricapro.model.My;
import com.dsinet.pjesmaricapro.model.Pjesma;
import com.dsinet.pjesmaricapro.ostalo.globals;

public class DetailFragment extends Fragment {

    private static final float FONT_STEP_SP = 4f;
    private static final float FONT_MIN_SP  = 8f;
    private static final float FONT_MAX_SP  = 56f;

    private globals g;
    private DbMain db;
    private float txSizeSp = 16f; // default fallback

    public DetailFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g  = new globals(requireContext());
        db = new DbMain(requireContext());

        // ActionBar: Home/Up aus
        AppCompatActivity act = (AppCompatActivity) requireActivity();
        if (act.getSupportActionBar() != null) {
            act.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Fontgröße aus Settings lesen (mit Fallback)
        try {
            txSizeSp = Float.parseFloat(g.VelicinaFonta());
        } catch (Exception ignore) {
            txSizeSp = 16f;
        }
        txSizeSp = clamp(txSizeSp, FONT_MIN_SP, FONT_MAX_SP);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Hintergrundfarbe mit Fallback
        int bg;
        try { bg = Color.parseColor(g.BojaAplikacije()); }
        catch (IllegalArgumentException e) { bg = Color.WHITE; }
        view.setBackgroundColor(bg);

        TextView tekst   = view.findViewById(R.id.txText);
        ImageButton bPlus  = view.findViewById(R.id.cdPlus);
        ImageButton bMinus = view.findViewById(R.id.cdMinus);

        // Pjesmarnik-/Font-Farben setzen (mit Fallbacks)
        int paperColor, fontColor;
        try { paperColor = Color.parseColor(g.BojaPjesmarnika()); }
        catch (IllegalArgumentException e) { paperColor = Color.WHITE; }
        try { fontColor = Color.parseColor(g.BojaFonta()); }
        catch (IllegalArgumentException e) { fontColor = Color.BLACK; }

        tekst.setBackgroundColor(paperColor);
        tekst.setTextColor(fontColor);
        tekst.setMovementMethod(new ScrollingMovementMethod());
        tekst.setTextSize(TypedValue.COMPLEX_UNIT_SP, txSizeSp);

        // Args lesen
        Bundle args = getArguments();
        String isMy = args != null ? args.getString("isMy") : null;
        String id   = args != null ? args.getString("ID")   : null;

        AppCompatActivity act = (AppCompatActivity) requireActivity();

        if (id != null && !id.isEmpty() && (isMy == null || isMy.isEmpty())) {
            // Normale Pjesma
            Pjesma p = db.getPjesma(id);
            if (p != null) {
                if (act.getSupportActionBar() != null) {
                    act.getSupportActionBar().setTitle(p.getAutor() + " - " + p.getNaslov());
                }
                String cleanText = cleanText(p.getTekst());
                tekst.setText(cleanText);
            } else {
                if (act.getSupportActionBar() != null) {
                    act.getSupportActionBar().setTitle(R.string.frag_detail);
                }
                tekst.setText("");
            }
        } else if (id != null && !id.isEmpty()) {
            // Eigene Pjesma (My)
            My m = db.getMojaPjesma(id);
            if (act.getSupportActionBar() != null) {
                act.getSupportActionBar().setTitle(getString(R.string.frag_my) + ": " + (m != null ? m.getNaslov() : ""));
            }
            String cleanText = m != null ? cleanText(m.getPjesma()) : "";
            tekst.setText(cleanText);
        } else {
            // Kein valides Argument
            if (act.getSupportActionBar() != null) {
                act.getSupportActionBar().setTitle(R.string.frag_detail);
            }
            tekst.setText("");
        }

        // Fontgröße ändern + speichern
        bPlus.setOnClickListener(v -> {
            txSizeSp = clamp(txSizeSp + FONT_STEP_SP, FONT_MIN_SP, FONT_MAX_SP);
            tekst.setTextSize(TypedValue.COMPLEX_UNIT_SP, txSizeSp);
            g.setParam("VelicinaFonta", String.valueOf(txSizeSp));
        });

        bMinus.setOnClickListener(v -> {
            txSizeSp = clamp(txSizeSp - FONT_STEP_SP, FONT_MIN_SP, FONT_MAX_SP);
            tekst.setTextSize(TypedValue.COMPLEX_UNIT_SP, txSizeSp);
            g.setParam("VelicinaFonta", String.valueOf(txSizeSp));
        });
    }

    /** Ersetzt HTML-Zeilenumbrüche (<br>, <br/>, <br />) durch echte Umbrüche.
     *  Case-insensitive, trimmt außerdem überflüssige CRs. */
    private static String cleanText(String src) {
        if (src == null) return "";
        // <br>, <br/>, <br /> → \n  (case-insensitive)
        String s = src.replaceAll("(?i)<br\\s*/?>", "\n");
        // Normiere Windows-CRLF auf LF (optional)
        s = s.replace("\r\n", "\n");
        // Optional: doppelte Leerzeilen eindampfen (wenn gewünscht)
        // s = s.replaceAll("\\n{3,}", "\n\n");
        return s;
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
