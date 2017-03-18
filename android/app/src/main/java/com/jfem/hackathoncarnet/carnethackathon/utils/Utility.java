package com.jfem.hackathoncarnet.carnethackathon.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by LaQuay on 18/03/2017.
 */

public class Utility {

    public static Bitmap getBitmap(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Bitmap getScaledBitmap(Context context, int redId, int desW, int desH) {
        return Bitmap.createScaledBitmap(getBitmap(context, redId), desW, desH, false);
    }

    public static Bitmap addTextToBitmap(Context context, String textToAdd, Bitmap bitmap) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(61, 61, 61));
        paint.setTextSize((int) (20 * scale));
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        Rect bounds = new Rect();
        paint.getTextBounds(textToAdd, 0, textToAdd.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        canvas.drawText(textToAdd, x - 2, y - 12, paint);

        return bitmap;
    }

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

    public static double distanceBetween2LatLng(LatLng latLngInit, LatLng latLngEnd) {
        float[] dist = new float[1];
        Location.distanceBetween(latLngInit.latitude, latLngInit.longitude, latLngEnd.latitude, latLngEnd.longitude, dist);
        return dist[0];
    }
}
