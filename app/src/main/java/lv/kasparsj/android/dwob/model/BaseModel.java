package lv.kasparsj.android.dwob.model;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.List;

import lv.kasparsj.android.dwob.App;
import lv.kasparsj.android.dwob.feed.LoadFeedTask;
import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.android.util.Objects;

abstract public class BaseModel {

    protected SharedPreferences settings;

    protected String description;
    protected long pubDate; // last time updated

    public BaseModel() {
        settings = App.applicationContext.getSharedPreferences();
        load();
    }

    protected void load() {
        String ns = getSaveNS();
        description = settings.getString(ns + "description", "");
        pubDate = settings.getLong(ns+"pubDate", 0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        description = value;
    }

    public long getPubDate() {
        return pubDate;
    }

    public void setPubDate(long date) {
        this.pubDate = date;
    }

    public String getHtml() {
        return description;
    }

    public boolean isOutdated() {
        return new Date().getTime() - pubDate >= App.DAY_IN_MILLIS;
    }

    public void setLoading(boolean isLoading) {
        String ns = getSaveNS();
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(ns+"loading", isLoading);
        editor.commit();
    }

    abstract protected String getSaveNS();

    public String getNSKey(String key) {
        return getSaveNS()+key;
    }

    public void setLoading(boolean isLoading, boolean success) {
        if (success) {
            save(isLoading);
        }
        else {
            setLoading(isLoading);
        }
    }

    protected void save(boolean isLoading) {
        String ns = getSaveNS();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ns+"description", description);
        editor.putLong(ns + "pubDate", pubDate);
        editor.putBoolean(ns + "loading", isLoading);
        editor.putBoolean(ns+"success", true);
        editor.commit();
    }

    abstract public void update();

    protected void update(final SaxFeedParser feedParser) {
        new LoadFeedTask(this, feedParser).execute();
    }

    public void update(List<? extends FeedItem> feedItems) {
        FeedItem feedItem = feedItems.get(0);
        long date = feedItem.getDate().getTime();
        String description = feedItem.getDescription();
        if (getPubDate() != date || !Objects.equals(getDescription(), description)) {
            setDescription(description);
            setPubDate(date);
        }
    }

    protected void updateWidgets(Class... widgetClasses) {
        Context context = App.applicationContext;
        for (Class widgetClass : widgetClasses) {
            ComponentName componentName = new ComponentName(context, widgetClass);
            int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
            if (ids.length > 0) {
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.setComponent(componentName);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                context.sendBroadcast(intent);
            }
        }
    }

    public boolean isLoaded() {
        return description.length() > 0;
    }
}