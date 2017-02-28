package lv.kasparsj.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenStateReceiver extends BroadcastReceiver {
	
	public static boolean screenOff = false;
	
	public void onReceive(Context context, Intent intent) {
		Log.i("test", "ScreenStateReceiver::onReceive ("+intent.getAction()+")");
		screenOff = !intent.getAction().equals(Intent.ACTION_SCREEN_ON);
	}
}
