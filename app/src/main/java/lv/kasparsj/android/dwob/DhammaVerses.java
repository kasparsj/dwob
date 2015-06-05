package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.SharedPreferences;

public class DhammaVerses {

    private static DhammaVerses instance;

    private Context context;
    private SharedPreferences settings;

    public DhammaVerses(App applicationContext) {
        context = applicationContext;
        settings = applicationContext.getSharedPreferences();
        load();
    }

    public static DhammaVerses getInstance() {
        if (instance == null) {
            instance = new DhammaVerses(App.applicationContext);
        }
        return instance;
    }

    private void load() {
    }
}
