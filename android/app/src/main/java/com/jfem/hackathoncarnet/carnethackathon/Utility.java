package com.jfem.hackathoncarnet.carnethackathon;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by LaQuay on 18/03/2017.
 */

public class Utility {
    public static Snackbar showSnackBar(View view, String text, String actionText, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.setAction(actionText, onClickListener);
        snackbar.show();

        return snackbar;
    }

    public static void closeSnackBar(Snackbar snackbar) {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }
}
