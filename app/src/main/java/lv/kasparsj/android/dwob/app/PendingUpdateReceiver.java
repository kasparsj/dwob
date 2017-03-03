package lv.kasparsj.android.dwob.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.android.util.Objects;

public class PendingUpdateReceiver extends BroadcastReceiver {
	
	private boolean pendingUpdate = false;

	public boolean getIsPendingUpdate() {
		return pendingUpdate;
	}

	public void setIsPendingUpdate(boolean value) {
		pendingUpdate = value;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("test", "PendingUpdateReceiver::onReceive ("+intent.getAction()+")");
		DailyWords dailyWords = DailyWords.getInstance();
		if (Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION) && intent.getExtras() != null) {
    		NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
    		if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
    			pendingUpdate = dailyWords.isOutdated();
    		}
    	}
    	if (pendingUpdate) {
    		pendingUpdate = false;
			dailyWords.update();
    	}
	}

}
