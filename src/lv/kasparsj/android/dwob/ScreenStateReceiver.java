package lv.kasparsj.android.dwob;

import lv.kasparsj.android.dwob.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

public class ScreenStateReceiver extends BroadcastReceiver {
		private boolean screenOff = false;
		private boolean pendingUpdate = false;
		private Class<?> pendingTask;
		private Object[] pendingTaskParams;
    	public void onReceive(Context context, Intent intent) {
    		if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
    			screenOff = false;
    			if (pendingUpdate) {
    				pendingUpdate = false;
    				run(context);
    			}
    		}
    		else {
    			screenOff = true;
    		}
    	}
    	public void scheduleTask(Context context, Class<?> task, Object[] params) {
    		pendingTask = task;
    		pendingTaskParams = params;
    		if (!screenOff) {
    			run(context);
    		}
    		else {
    			Log.i("test", "scheduling update");
    			pendingUpdate = true;
    		}
    	}
    	public void scheduleTask(Context context, Class<?> task) {
    		scheduleTask(context, task, new Object[1]);
    	}
    	private void run(Context context) {
    		Log.i("test", "ScreenStateReceiver::run");
    		try {
    			Object task = pendingTask.getConstructors()[0].newInstance(context);
    			pendingTask.getMethod("execute", new Class[] { Object[].class }).invoke(task, pendingTaskParams);
    			pendingTask = null;
    			pendingTaskParams = null;
    		} catch (Exception e) {
    			Resources r = context.getResources();
    			Log.e(r.getString(R.string.app_name), e.getMessage(), e);
    		}
    	}
    }