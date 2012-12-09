package lv.kasparsj.android.dwob;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenStateReceiver extends BroadcastReceiver {
	
	public static boolean screenOff = false;
	
	public void onReceive(Context context, Intent intent) {
		Log.i("test", "ScreenStateReceiver::onReceive ("+intent.getAction()+")");
		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			screenOff = false;
		}
		else {
			screenOff = true;
		}
	}
}
