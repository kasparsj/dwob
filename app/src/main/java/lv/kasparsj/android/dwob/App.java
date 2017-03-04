package lv.kasparsj.android.dwob;

import android.app.Application;
import android.text.TextUtils;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.utils.Util;

import lv.kasparsj.android.util.OneLog;

public class App extends Application {

    public static final String PACKAGE;
    static {
        PACKAGE = App.class.getPackage().getName();
        OneLog.TAG = PACKAGE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupHockey();
    }

    private void setupHockey() {
        String appIdentifier = Util.getAppIdentifier(this);
        if (TextUtils.isEmpty(appIdentifier)) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        CrashManager.initialize(this, appIdentifier, null);
    }
}
