package lv.kasparsj.android.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.android.util.Objects;

public class NetworkStateReceiver extends BroadcastReceiver {

	private NetworkInfo.State state;

	public NetworkStateReceiver() {
		this(NetworkInfo.State.UNKNOWN);
	}

	public NetworkStateReceiver(NetworkInfo.State state) {
		super();
		this.state = state;
	}

	public NetworkInfo.State getState() {
		return state;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			if (Objects.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION) && intent.getExtras() != null) {
				NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
				if (ni != null) {
					NetworkInfo.State state = ni.getState();
					if (state != this.state) {
						this.state = state;
						onChange(state);
					}
					onReceive(state);
				}
			}
		}
	}

	protected void onReceive(NetworkInfo.State state) {

	}

	protected void onChange(NetworkInfo.State state) {

	}
}
