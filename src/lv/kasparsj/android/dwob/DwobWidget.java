package lv.kasparsj.android.dwob;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.TextView;

public class DwobWidget extends AppWidgetProvider {
	
    private ScreenStateReceiver screenStateReceiver = new ScreenStateReceiver();

	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
		screenStateReceiver.runWhenOn(context, LoadFeedTask.class);
    }
	
	@Override
	public void onEnabled(Context context) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
    	context.getApplicationContext().registerReceiver(this.screenStateReceiver, filter);
	}
	
	public void onDisabled(Context context) {
		context.getApplicationContext().unregisterReceiver(this.screenStateReceiver);
	}
    
    private float getDefaultTextSize(int numLines) {
        switch (numLines) {
	    	case 1:
	    	case 2:
	    	case 3:
	    	case 4:
	    		return 16;
	    	case 5:
	    		return 13;
	    	case 6:
	    		return 11;
	    	case 7:
	    		return 9;
    	}
        return 8;
    }
    
    private int countTextViewLines(TextView textView, String[] lines, float lineWidth) {
    	int numLines = lines.length;
    	for (int i=0; i<lines.length; i++) {
        	if (textView.getPaint().measureText(lines[i]) > lineWidth) {
        		numLines++;
        	}
        }
    	return numLines;
    }
    
    private int getLinesVisible(float textSize) {
    	if (textSize > 13) return 4;
    	else if (textSize > 11) return 5;
    	else if (textSize > 9) return 6;
    	else if (textSize > 8) return 7;
    	return 8;
    }
    
    public void onReceive(Context context, Intent intent) {
    	Resources r = context.getResources();
    	DwobApp app = ((DwobApp) context.getApplicationContext());
    	Object[] translation = app.getTranslation().toArray();
    	if (intent.getAction().equals(r.getString(R.string.action_refresh)) || translation.length > 0) {
			// Retrieve latest translation
            String text = r.getString(R.string.widget_error);
            if (translation.length > 0) {
            	String html = TextUtils.join("\n<br />\n", translation).trim().replaceAll("^<br />", "").trim();
            	text = Html.fromHtml(html).toString();
            }
            // Detect numLines to display
            String[] lines = text.split("\r\n|\r|\n");
            int numLines = lines.length;
            if (numLines > 6 && text.indexOf("\n\n") > -1) {
            	String[] parts = text.split("\n\n");
            	text = parts[0].trim()+"...";
            	lines = text.split("\r\n|\r|\n");
            	numLines = lines.length;
            }
            // Measure text width, and alter numLines accordingly
            TextView textView = new TextView(context);
            textView.setTextSize(getDefaultTextSize(numLines));
            float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getDimension(R.dimen.widget_padding), r.getDisplayMetrics());
            float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getDimension(R.dimen.widget_margin), r.getDisplayMetrics());
            float lineWidth = (r.getDisplayMetrics().widthPixels - padding*2 - margin*2);
            while (numLines < countTextViewLines(textView, lines, lineWidth)) {
            	textView.setTextSize((textView.getTextSize()-.5f));
            	numLines = getLinesVisible(textView.getTextSize());
            }
            // Build an update that holds the updated widget contents
            RemoteViews updateViews;
            updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_words);
            updateViews.setTextViewText(R.id.words, text);
            updateViews.setFloat(R.id.words, "setTextSize", textView.getTextSize());
            // setOnClickPendingIntent
            Intent defineIntent = new Intent(context, DwobActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            updateViews.setOnClickPendingIntent(R.id.words, pendingIntent);
			// update Widget
			ComponentName thisWidget = new ComponentName(context, DwobWidget.class);
	        AppWidgetManager manager = AppWidgetManager.getInstance(context);
	        manager.updateAppWidget(thisWidget, updateViews);
    	}
    	
		super.onReceive(context, intent);
	}
    
    public class ScreenStateReceiver extends BroadcastReceiver {
		private boolean screenOff = false;
		private boolean pendingUpdate = false;
		private Class<?> pendingTask;
		private Object[] pendingTaskParams;
    	public void onReceive(Context context, Intent intent) {
    		if (intent.getAction() == Intent.ACTION_SCREEN_ON) {
    			screenOff = false;
    		}
    		else {
    			screenOff = true;
    			if (pendingUpdate) {
    				pendingUpdate = false;
    				run(context);
    			}
    		}
    	}
    	public void runWhenOn(Context context, Class<?> task, Object[] params) {
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
    	public void runWhenOn(Context context, Class<?> task) {
    		runWhenOn(context, task, new Object[1]);
    	}
    	private void run(Context context) {
    		Log.i("test", "running update");
    		try {
    			Object task = pendingTask.getConstructors()[0].newInstance(context, null);
    			pendingTask.getMethod("execute", new Class[] { Object[].class }).invoke(task, pendingTaskParams);
    			pendingTask = null;
    			pendingTaskParams = null;
    		} catch (Exception e) {
    			Resources r = context.getResources();
    			Log.e(r.getString(R.string.app_name), e.getMessage(), e);
    		}
    	}
    }
}
