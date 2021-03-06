package itacademy.com.project30permissionmaps.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

public final class AndroidUtils {

    public static void showShortToast(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, String msg, int length) {
        Toast.makeText(context, msg, length).show();
    }

    public static void showShortSnackBar(Activity activity, String msg) {
        showSnackBar(activity, msg, Snackbar.LENGTH_SHORT);
    }

    public static void showLongSnackBar(Activity activity, String msg) {
        showSnackBar(activity, msg, Snackbar.LENGTH_LONG);
    }

    private static void showSnackBar(Activity activity, String msg, int length) {
        Snackbar.make(activity.findViewById(android.R.id.content), msg, length).show();
    }
}
