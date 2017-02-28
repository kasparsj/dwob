package lv.kasparsj.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenStateReceiver extends BroadcastReceiver {
	
	private boolean screenOff = false;

	public boolean isScreenOff() {
		return screenOff;
	}
	
	public void onReceive(Context context, Intent intent) {
		screenOff = !intent.getAction().equals(Intent.ACTION_SCREEN_ON);
	}
}
