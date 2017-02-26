package lv.kasparsj.android.dwob;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.TextView;

import lv.kasparsj.android.util.OneLog;

public abstract class BaseDailyWordsWidget extends AppWidgetProvider {

    private static final int HOUR_IN_MILLIS = 60*60*1000;
	
    private ScreenStateReceiver screenStateReceiver = new ScreenStateReceiver();
    private DailyWordsUpdateReceiver screenUpdateReceiver = new DailyWordsUpdateReceiver();
	
	private PendingIntent createUpdateIntent(Context context) {
		Resources r = context.getResources();
	    Intent intent = new Intent(r.getString(R.string.action_update));
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	@Override
	public void onEnabled(Context context) {
		OneLog.i("DailyWordsWidget::onEnabled");
		
		IntentFilter stateFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		stateFilter.addAction(Intent.ACTION_SCREEN_ON);
    	context.getApplicationContext().registerReceiver(screenStateReceiver, stateFilter);
    	
		IntentFilter updateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    	context.getApplicationContext().registerReceiver(screenUpdateReceiver, updateFilter);
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), HOUR_IN_MILLIS, createUpdateIntent(context));
	}
	
	public void onDisabled(Context context) {
		OneLog.i("DailyWordsWidget::onDisabled");
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createUpdateIntent(context));
        
		try {
			context.getApplicationContext().unregisterReceiver(screenStateReceiver);
		} catch (RuntimeException e) {
			// do nothing
		}
		try {
			context.getApplicationContext().unregisterReceiver(screenUpdateReceiver);
		} catch (RuntimeException e) {
			// do nothing
		}
	}

    protected abstract float getDefaultTextSize(int numLines);
    
    private int countTextViewLines(TextView textView, String[] lines, float lineWidth, float density) {
    	int numLines = lines.length;
		for (String line : lines) {
			if (textView.getPaint().measureText(line) * density > lineWidth) {
				numLines++;
			}
		}
    	return numLines;
    }

    public void onReceive(Context context, Intent intent) {
    	OneLog.i("DailyWordsWidget::onReceive ("+intent.getAction()+")");
    	
    	DailyWords dailyWords = DailyWords.getInstance();
    	Resources r = context.getResources();
    	if (intent.getAction().equals(r.getString(R.string.action_update)) && dailyWords.isOutdated()) {
    		// don't update if screen is off
    		if (ScreenStateReceiver.screenOff) {
    			DailyWordsUpdateReceiver.pendingUpdate = true;
    		}
    		else {
				dailyWords.update();
    		}
    	}
    	
    	String translation = dailyWords.getTranslated();
    	if (intent.getAction().equals(r.getString(R.string.action_refresh)) || translation.length() > 0) {
			// Retrieve latest translation
            String text = r.getString(R.string.widget_error);
            if (translation.length() > 0) {
                text = translation.trim();
            }
            // Detect numLines to display
            String[] lines = text.split("\r\n|\r|\n");
            if (lines.length > 6 && text.contains("\n\n")) {
            	String[] parts = text.split("\n\n");
            	text = parts[0].trim()+"...";
            	lines = text.split("\r\n|\r|\n");
            }
            // Measure text width, and alter numLines accordingly
            TextView textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDefaultTextSize(lines.length));
            float width = 80 * r.getDisplayMetrics().density * 4;
            float padding = r.getDimension(R.dimen.widget_padding);
            float margin = r.getDimension(R.dimen.widget_margin);
            float lineWidth = (width - padding*2 - margin*2);
            while (lines.length < countTextViewLines(textView, lines, lineWidth, r.getDisplayMetrics().density)) {
            	textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (textView.getTextSize()-.5f));
            }
            // Build an update that holds the updated widget contents
            RemoteViews updateViews;
            updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_words);
            updateViews.setTextViewText(R.id.words, text);
            updateViews.setFloat(R.id.words, "setTextSize", textView.getTextSize());
            // setOnClickPendingIntent
            Intent defineIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            updateViews.setOnClickPendingIntent(R.id.words, pendingIntent);
			// update Widget
			ComponentName thisWidget = new ComponentName(context, getClass());
	        AppWidgetManager manager = AppWidgetManager.getInstance(context);
	        manager.updateAppWidget(thisWidget, updateViews);
    	}
    	
		super.onReceive(context, intent);
	}
}
