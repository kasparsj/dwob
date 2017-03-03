package lv.kasparsj.android.dwob.app;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.android.receiver.ScreenStateReceiver;
import lv.kasparsj.android.util.Objects;

public class AppService extends Service
{
    private ScreenStateReceiver screenStateReceiver = new ScreenStateReceiver();
    private PendingUpdateReceiver pendingUpdateReceiver = new PendingUpdateReceiver();

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter screenIntentFilter = new IntentFilter();
        screenIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenStateReceiver, screenIntentFilter);

        IntentFilter updateIntentFilter = new IntentFilter();
        updateIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        updateIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(pendingUpdateReceiver, updateIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Objects.equals(intent.getAction(), getString(R.string.action_update))) {
            if (screenStateReceiver.isScreenOff()) {
                pendingUpdateReceiver.setIsPendingUpdate(true);
            }
            else {
                DailyWords.getInstance().update();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(screenStateReceiver);
        unregisterReceiver(pendingUpdateReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
