package lv.kasparsj.android.dwob;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("test", "NetworkStateReceiver::onReceive");
	    if (intent.getExtras() != null) {
	        NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
	        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
	        	((DwobApp) context.getApplicationContext()).update();
	        }
	     }

	}

}
