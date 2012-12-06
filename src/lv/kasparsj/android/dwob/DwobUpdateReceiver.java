package lv.kasparsj.android.dwob;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class DwobUpdateReceiver extends BroadcastReceiver {
	
	public Boolean needsUpdate = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("test", "DwobUpdateReceiver::onReceive ("+intent.getAction()+")");
		if (intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION && intent.getExtras() != null) {
    		NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
    		if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
    			needsUpdate = true;
    		}
    	}
    	if (needsUpdate) {
    		needsUpdate = false;
    		((DwobApp) context.getApplicationContext()).update();
    	}
	}

}
