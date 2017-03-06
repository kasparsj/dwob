package lv.kasparsj.android.dwob.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import lv.kasparsj.android.content.NetworkStateReceiver;
import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.android.content.ScreenStateReceiver;
import lv.kasparsj.util.Objects;

public class AppService extends Service
{
    private DailyWords dailyWords;
    private boolean pendingUpdate = false;

    private ScreenStateReceiver screenStateReceiver;
    private NetworkStateReceiver networkStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        dailyWords = new DailyWords(this);

        setupScreenListener();
        setupNetworkListener();
    }

    private void setupScreenListener() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        screenStateReceiver = new ScreenStateReceiver(pm.isScreenOn()) {
            @Override
            protected void onChange(boolean on) {
                if (on && pendingUpdate) {
                    pendingUpdate = false;
                    dailyWords.update();
                }
            }
        };
        IntentFilter screenIntentFilter = new IntentFilter();
        screenIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenStateReceiver, screenIntentFilter);
    }

    private void setupNetworkListener() {
        networkStateReceiver = new NetworkStateReceiver() {
            @Override
            protected void onReceive(NetworkInfo.State state) {
                if (state == NetworkInfo.State.CONNECTED) {
                    if (!screenStateReceiver.getIsOn()) {
                        pendingUpdate = dailyWords.isOutdated();
                    }
                    else {
                        dailyWords.update();
                    }
                }
            }
        };
        IntentFilter updateIntentFilter = new IntentFilter();
        updateIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, updateIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (Objects.equals(intent.getAction(), getString(R.string.action_update))) {
                if (!screenStateReceiver.getIsOn()) {
                    pendingUpdate = true;
                }
                else {
                    dailyWords.update();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(screenStateReceiver);
        unregisterReceiver(networkStateReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
