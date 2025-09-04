package com.dsinet.pjesmaricapro;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dsinet.pjesmaricapro.ostalo.globals;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CentralActivity extends AppCompatActivity {

    private static final String TAG = "CentralActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_central);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Views
        CoordinatorLayout root = findViewById(R.id.root);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        View host = findViewById(R.id.nav_host_fragment_activity_main);

        // App-Farbe (defensiv)
//        final globals g = new globals(this);
//        int appColor;
//        try { appColor = Color.parseColor(g.BojaAplikacije()); }
//        catch (Exception e) { appColor = Color.WHITE; }

        //if (host != null) host.setBackgroundColor(appColor);
        //if (navView != null) navView.setBackgroundColor(appColor);

        // Hat das Theme bereits eine Window-ActionBar?
        int[] attrs = new int[]{ android.R.attr.windowActionBar };
        TypedArray ta = getTheme().obtainStyledAttributes(attrs);
        final boolean hasWindowActionBar = ta.getBoolean(0, false);
        ta.recycle();
        Log.d(TAG, "hasWindowActionBar=" + hasWindowActionBar);

        // Kontrastberechnung
        //boolean darkBg = isColorDark(appColor);
//        int titleTextColor = darkBg ? Color.WHITE : Color.BLACK;
//        boolean wantDarkStatusBarIcons = !darkBg; // true => dunkle Statusbar-Icons

        // Wenn das Theme eine ActionBar liefert → Toolbar nicht als SupportActionBar setzen!
        if (hasWindowActionBar) {
            // Layout-Toolbar ausblenden, sonst Doppel-Anzeige
            if (toolbar != null) toolbar.setVisibility(View.GONE);

            // System-ActionBar einfärben
//            ActionBar ab = getSupportActionBar();
//            if (ab != null) {
//                ab.setBackgroundDrawable(new ColorDrawable(appColor));
//            }
        } else {
            // Kein Window-ActionBar → wir nutzen die Toolbar aus dem Layout
            if (toolbar != null) {
                toolbar.setVisibility(View.VISIBLE);
                //toolbar.setBackgroundColor(appColor);
//                toolbar.setTitleTextColor(titleTextColor);
//                if (toolbar.getNavigationIcon() != null) {
//                    toolbar.getNavigationIcon().setTint(titleTextColor);
//                }
//                if (toolbar.getOverflowIcon() != null) {
//                    toolbar.getOverflowIcon().setTint(titleTextColor);
//                }
                //toolbar.setElevation(6f * getResources().getDisplayMetrics().density);
                toolbar.bringToFront();

                // WICHTIG: Nur hier (NoActionBar-Theme) setzen — sonst Crash!
                setSupportActionBar(toolbar);
            }
        }

        // Statusbar-Farbe (wir verwenden die neue Methode nur für API 35+)
        //final int statusBarColor = manipulateColor(appColor, 0.92f); // etwas dunkler

        // set appearance (icon color) on all API levels via WindowInsetsControllerCompat
//        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
//                .setAppearanceLightStatusBars(wantDarkStatusBarIcons);

        // Insets (Toolbar oben, BottomNav unten) + API35+ Statusbar-Background handling
        if (root != null && navView != null) {
            final int tPadL = (toolbar != null) ? toolbar.getPaddingLeft() : 0;
            final int tPadT = (toolbar != null) ? toolbar.getPaddingTop() : 0;
            final int tPadR = (toolbar != null) ? toolbar.getPaddingRight() : 0;
            final int tPadB = (toolbar != null) ? toolbar.getPaddingBottom() : 0;

            final int nPadL = navView.getPaddingLeft();
            final int nPadT = navView.getPaddingTop();
            final int nPadR = navView.getPaddingRight();
            final int nPadB = navView.getPaddingBottom();

            // Holder für die dynamisch erzeugte StatusBar-Background-View (nur für API 35+)
            final View[] statusBarBgHolder = new View[1];

            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                int topInset = sys.top;

//                if (!hasWindowActionBar && toolbar != null) {
//                    // Nur wenn Toolbar sichtbar ist
//                    toolbar.setPadding(tPadL, tPadT + topInset, tPadR, tPadB);
//                }
//                navView.setPadding(nPadL, nPadT, nPadR, nPadB + sys.bottom);

                // --- API 35+ Lösung: Statusbar-Hintergrund als View hinterlegen ---
                if (Build.VERSION.SDK_INT >= 35) {
                    View sbg = statusBarBgHolder[0];
                    if (sbg == null) {
                        // neu erzeugen (Höhe = topInset)
                        sbg = new View(this);
                        sbg.setId(View.generateViewId());
                        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, topInset);
                        // füge als erstes Kind ein, damit es hinter Toolbar liegt
                        root.addView(sbg, 0, lp);
                        statusBarBgHolder[0] = sbg;
                    } else {
                        ViewGroup.LayoutParams lp = sbg.getLayoutParams();
                        if (lp != null && lp.height != topInset) {
                            lp.height = topInset;
                            sbg.setLayoutParams(lp);
                        }
                    }

                    if (statusBarBgHolder[0] != null) {
                       // statusBarBgHolder[0].setBackgroundColor(statusBarColor);
                        statusBarBgHolder[0].setVisibility(topInset > 0 ? View.VISIBLE : View.GONE);
                    }
                } else {
                    // Fallback für ältere Android-Versionen: setStatusBarColor weiterhin verwenden
                    //setStatusBarColorCompat(statusBarColor);
                }

                return insets;
            });
        } else {
            // Falls root/navView null sind: fallback für ältere Geräte
            if (Build.VERSION.SDK_INT < 35) {
                //setStatusBarColorCompat(statusBarColor);
            }
        }

        // NavController
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment missing. Prüfe activity_central.xml.");
        }
        NavController navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_search,
                R.id.navigation_favoriti,
                R.id.navigation_mojepjesme,
                R.id.navigation_postavke,
                R.id.navigation_info
        ).build();

        // ActionBar/Toolbar mit NavController verbinden
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((c, d, a) -> {
            CharSequence lbl = d.getLabel();
            if (lbl != null) {
                if (hasWindowActionBar) {
                    ActionBar ab = getSupportActionBar();
                    if (ab != null) ab.setTitle(lbl);
                } else if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(lbl);
                }
            }
        });

        // BottomNavigation + Spezialfall "Pretraga → Stack leeren"
        if (navView != null) {
            NavigationUI.setupWithNavController(navView, navController);

            navView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.navigation_search) {
                    NavOptions options = new NavOptions.Builder()
                            .setPopUpTo(navController.getGraph().getId(), true)
                            .setLaunchSingleTop(true)
                            .build();
                    navController.navigate(R.id.navigation_search, null, options);
                    return true;
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            });

            navView.setOnItemReselectedListener(item ->
                    navController.popBackStack(item.getItemId(), false));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (navHostFragment == null) return super.onSupportNavigateUp();
        NavController navController = navHostFragment.getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    // --- Helpers ---

//    private static boolean isColorDark(int color) {
//        double lum = ColorUtils.calculateLuminance(color); // 0..1
//        return lum < 0.5;
//    }

//    private static int manipulateColor(int color, float factor) {
//        int a = Color.alpha(color);
//        int r = Math.round(Color.red(color) * factor);
//        int g = Math.round(Color.green(color) * factor);
//        int b = Math.round(Color.blue(color) * factor);
//        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
//    }

//    @SuppressWarnings("deprecation")
//    private void setStatusBarColorCompat(int color) {
//        try {
//            getWindow().setStatusBarColor(color);
//        } catch (Exception ignore) {
//            // ignore
//        }
//    }
}
