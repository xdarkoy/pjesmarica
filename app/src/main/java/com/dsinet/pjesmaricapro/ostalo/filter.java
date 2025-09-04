package com.dsinet.pjesmaricapro.ostalo;

import android.text.InputFilter;
import android.text.Spanned;

public class filter {

    protected String blockCharacterSet = "~#^|$%&*'";

    public InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
}
