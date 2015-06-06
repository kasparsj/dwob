package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

abstract public class BaseModel {

    protected Context context;
    protected SharedPreferences settings;

    protected String description;
    protected long pubDate; // last time updated

    public BaseModel(App applicationContext) {
        context = applicationContext;
        settings = applicationContext.getSharedPreferences();
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

    public boolean isLoaded() {
        return description.length() > 0;
    }
}
