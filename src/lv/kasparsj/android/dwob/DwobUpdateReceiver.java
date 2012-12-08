package lv.kasparsj.android.dwob;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class DwobUpdateReceiver extends BroadcastReceiver {
	
	public static boolean pendingUpdate = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("test", "DwobUpdateReceiver::onReceive ("+intent.getAction()+")");
		DwobApp app = ((DwobApp) context.getApplicationContext());
		if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION && intent.getExtras() != null) {
    		NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
    		if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
    			pendingUpdate = app.isOutdated();
    		}
    	}
    	if (pendingUpdate) {
    		pendingUpdate = false;
    		app.update();
    	}
	}

}
