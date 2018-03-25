package com.bas.bandclient.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.bas.bandclient.BandClientApplication;

/**
 * Created by bas on 30.11.16.
 */

public class ConvertHelper {

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
