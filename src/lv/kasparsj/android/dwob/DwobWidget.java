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
import android.os.IBinder;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.RemoteViews;

import lv.kasparsj.android.dwob.R;

/**
 * Define a simple widget that shows the Wiktionary "Word of the day." To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
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
    
    public void onReceive(Context context, Intent intent) {
    	if (intent.getAction().equals(LoadFeedTask.ACTION_REFRESH)) {
			RemoteViews updateViews;
			
            // Build an update that holds the updated widget contents
            updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_words);
            Object[] translation = ((DwobApp) context.getApplicationContext()).getTranslation().toArray();
            String html = "Failed loading Daily Words of Buddha";
            if (translation.length > 0)
            	html = TextUtils.join("\n<br />\n", translation).trim().replaceAll("^<br />", "").trim();
            Spanned words = Html.fromHtml(html);
            updateViews.setTextViewText(R.id.words, words);
            
            // When user clicks on widget, launch to Wiktionary definition page
            /*String definePage = res.getString(R.string.template_define_url,
                    Uri.encode(wordTitle));*/
            Intent defineIntent = new Intent(context, DwobActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            updateViews.setOnClickPendingIntent(R.id.words, pendingIntent);
			
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
