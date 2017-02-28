package lv.kasparsj.android.appwidget;

import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import lv.kasparsj.android.receiver.ScreenStateReceiver;
import lv.kasparsj.android.util.OneLog;

public abstract class ScreenAwareWidget extends AppWidgetProvider
{
    protected ScreenStateReceiver screenStateReceiver = new ScreenStateReceiver();
    private BroadcastReceiver screenUpdateReceiver = getUpdateReceiver();

    protected abstract BroadcastReceiver getUpdateReceiver();

    @Override
    public void onEnabled(Context context) {
        OneLog.i("ScreenAwareWidget::onEnabled");

        IntentFilter stateFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        stateFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.getApplicationContext().registerReceiver(screenStateReceiver, stateFilter);

        IntentFilter updateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        context.getApplicationContext().registerReceiver(screenUpdateReceiver, updateFilter);
    }

    public void onDisabled(Context context) {
        OneLog.i("ScreenAwareWidget::onDisabled");

        try {
            context.getApplicationContext().unregisterReceiver(screenStateReceiver);
        } catch (RuntimeException e) {
            // do nothing
        }
        try {
            context.getApplicationContext().unregisterReceiver(screenUpdateReceiver);
        } catch (RuntimeException e) {
            // do nothing
        }
    }
}
