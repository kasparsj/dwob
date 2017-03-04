package lv.kasparsj.android.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lv.kasparsj.android.util.Objects;

public class ScreenStateReceiver extends BroadcastReceiver {

	private boolean on;

	public ScreenStateReceiver() {
		this(false);
	}

	public ScreenStateReceiver(boolean on) {
		super();
		this.on = on;
	}

	public boolean getIsOn() {
		return on;
	}

	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			boolean value = Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_ON);
			if (value != on) {
				on = value;
				onChange(on);
			}
			onReceive(value);
		}
	}

	protected void onReceive(boolean on) {

	}

	protected void onChange(boolean on) {

	}
}
