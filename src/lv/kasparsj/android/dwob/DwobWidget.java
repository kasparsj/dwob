/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lv.kasparsj.android.dwob;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.TextView;

public class DwobWidget extends AppWidgetProvider {
	
	private PendingIntent service = null;  
	
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
    	final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
    	  
        final Calendar TIME = Calendar.getInstance();  
        TIME.set(Calendar.MINUTE, 5);  
  
        final Intent i = new Intent(context, UpdateService.class);  
  
        if (service == null)  
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);  
  
        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), DwobApp.UPDATE_INTERVAL, service); 
    }
    
    @Override  
    public void onDisabled(Context context)  
    {  
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
  
        m.cancel(service);  
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
    	else {
    		super.onReceive(context, intent);
    	}
	}
    
	public static class UpdateService extends Service {
        @Override
        public void onStart(Intent intent, int startId) {
            LoadFeedTask task = new LoadFeedTask(this, null);
            task.execute();
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't need to bind to this service
            return null;
        }
    }
}
