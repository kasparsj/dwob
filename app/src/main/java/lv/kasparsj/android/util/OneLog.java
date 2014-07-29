package lv.kasparsj.android.util;

public class OneLog {

    public static String TAG = OneLog.class.getName();

    private OneLog() {
    }

    public static int v(String msg) {
        return android.util.Log.v(TAG, msg);
    }

    public static int v(String msg, Throwable tr) {
        return android.util.Log.v(TAG, msg, tr);
    }

    public static int d(String msg) {
        return android.util.Log.d(TAG, msg);
    }

    public static int d(String msg, Throwable tr) {
        return android.util.Log.d(TAG, msg, tr);
    }

    public static int i(String msg) {
        return android.util.Log.i(TAG, msg);
    }

    public static int i(String msg, Throwable tr) {
        return android.util.Log.i(TAG, msg, tr);
    }

    public static int w(String msg) {
        return android.util.Log.w(TAG, msg);
    }

    public static int w(String msg, Throwable tr) {
        return android.util.Log.w(TAG, msg, tr);
    }

    public static int w(Throwable tr) {
        return android.util.Log.w(TAG, tr);
    }

    public static int e(String msg) {
        return android.util.Log.e(TAG, msg);
    }

    public static int e(String msg, Throwable tr) {
        return android.util.Log.e(TAG, msg, tr);
    }
}
