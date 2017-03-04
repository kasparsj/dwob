package lv.kasparsj.android.content;

import android.content.Context;
import android.content.SharedPreferences;

public class PrivatePreferences
{
    public static String FILENAME = "DwobPrefsFile";

    private Context context;
    private String ns;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PrivatePreferences(Context context) {
        this(context, "");
    }

    public PrivatePreferences(Context context, String ns) {
        this.context = context;
        this.ns = ns;
        sharedPreferences = context.getSharedPreferences(FILENAME, 0);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(ns + key, defaultValue);
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(ns + key, defaultValue);
    }

    public SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = sharedPreferences.edit();
        }
        return editor;
    }

    public void commit() {
        if (editor != null) {
            editor.commit();
            editor = null;
        }
    }

    public void putBoolean(String key, boolean value) {
        getEditor().putBoolean(ns + key, value);
    }

    public void putString(String key, String value) {
        getEditor().putString(ns + key, value);
    }

    public void putLong(String key, long value) {
        getEditor().putLong(ns + key, value);
    }
}
