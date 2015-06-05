package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.SharedPreferences;

public class PaliWord {

    private static PaliWord instance;

    private Context context;
    private SharedPreferences settings;

    public PaliWord(App applicationContext) {
        context = applicationContext;
        settings = applicationContext.getSharedPreferences();
        load();
    }

    public static PaliWord getInstance() {
        if (instance == null) {
            instance = new PaliWord(App.applicationContext);
        }
        return instance;
    }

    private void load() {
    }
}
