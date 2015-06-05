package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class DailyWords {

    private static DailyWords instance = null;

    private Context context;
    private SharedPreferences settings;

    private String title;
    private String translated;
    private String pali;
    private String source;
    private String audio;
    private long pubDate; // last time updated

    public DailyWords(App applicationContext) {
        context = applicationContext;
        settings = applicationContext.getSharedPreferences();
        load(applicationContext.getString(R.string.app_name));
    }

    public static DailyWords getInstance() {
        if (instance == null) {
            instance = new DailyWords(App.applicationContext);
        }
        return instance;
    }

    private void load(String defaultTitle) {
        setTitle(settings.getString("title", defaultTitle));
//		setDescription(settings.getString("description", ""));
        setTranslated(settings.getString("translated", ""));
        setPali(settings.getString("pali", ""));
        pubDate = settings.getLong("pubDate", 0);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtml() {
        return (translated + "\n\n" + pali).replaceAll("\n", "<br>");
    }

    public long getPubDate() {
        return pubDate;
    }

    public void setPubDate(long date) {
        this.pubDate = date;
    }

    public String getPali() {
        return pali;
    }

    public void setPali(String value) {
        pali = value;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String value) {
        translated = value;
    }

    public String getSource() {
        return source;
    }

    public boolean isOutdated() {
        return new Date().getTime() - pubDate >= App.DAY_IN_MILLIS;
    }

    public void setLoading(boolean isLoading) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("loading", isLoading);
        editor.commit();
    }

    public void setLoading(boolean isLoading, boolean success) {
        if (success) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("title", title);
            editor.putString("translated", translated);
            editor.putString("pali", pali);
            editor.putLong("pubDate", pubDate);
            editor.putBoolean("loading", isLoading);
            editor.putBoolean("success", success);
            editor.commit();
        }
        else {
            setLoading(isLoading);
        }
    }

    public void update() {
        new LoadDailyWordsTask(context).execute();
    }
}
