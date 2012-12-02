package org.pariyatti.dwob;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (intent.getExtras() != null) {
	        NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
	        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
	        	DwobApp app = ((DwobApp) context.getApplicationContext());
	            if (new Date().getTime() - app.getUpdated() > R.integer.update_period)
	            	new LoadFeedTask(context).execute();
	        }
	     }

	}

}
