package lv.kasparsj.android.dwob.model;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import net.hockeyapp.android.CrashManager;

import java.util.Date;
import java.util.List;

import lv.kasparsj.android.content.PrivatePreferences;
import lv.kasparsj.android.dwob.feed.LoadFeedTask;
import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.util.Objects;

abstract public class BaseModel implements FeedModel {

    public static final int DAY_IN_MILLIS = 24*60*60*1000;

    protected Context context;
    protected BaseModelListener listener;
    protected PrivatePreferences settings;
    protected String language;
    protected String description;
    protected long pubDate; // last time updated

    public BaseModel(Context context) {
        this.context = context;
        settings = new PrivatePreferences(context, getSettingsNs());
        load();
    }

    public void setListener(BaseModelListener listener) {
        this.listener = listener;
    }

    public PrivatePreferences getSettings() {
        return settings;
    }

    protected void load() {
        setLanguage(settings.getString("language", Languages.EN));
        description = settings.getString("description");
        pubDate = settings.getLong("pubDate");
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String value) {
        if (!Objects.equals(language, value)) {
            boolean doUpdate = language != null;
            language = value;
            settings.putString("language", value);
            settings.commit();
            if (doUpdate) {
                update();
            }
        }
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
        return new Date().getTime() - pubDate >= DAY_IN_MILLIS;
    }

    public void setIsLoading(boolean isLoading) {
        setIsLoading(isLoading, false);
    }

    public void setIsLoading(boolean isLoading, boolean success) {
        if (listener != null) {
            listener.onLoading(isLoading, success);
        }
        if (success || !isLoaded()) {
            updateWidgets();
        }
        if (!success) {
            CrashManager.execute(context.getApplicationContext(), null);
        }
    }

    abstract protected String getSettingsNs();

    protected void save() {
        settings.putString("description", description);
        settings.putLong("pubDate", pubDate);
        settings.putBoolean("success", true);
        settings.commit();
    }

    abstract public void update();

    protected void update(final SaxFeedParser feedParser) {
        new LoadFeedTask(getFeedUrl(), this, feedParser).execute();
    }

    public void update(List<? extends FeedItem> feedItems) {
        FeedItem feedItem = feedItems.get(0);
        long date = (feedItem.getDate() != null ? feedItem.getDate() : new Date()).getTime();
        String description = feedItem.getDescription();
        if (getPubDate() != date || !Objects.equals(getDescription(), description)) {
            setDescription(description);
            setPubDate(date);
            save();
        }
    }

    public void updateWidgets() {

    }

    protected void updateWidgets(Class... widgetClasses) {
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
        return description != null && description.length() > 0;
    }

    abstract public String getFeedUrl();

    public interface BaseModelListener {
        public void onLoading(boolean isLoading, boolean success);
    }
}
