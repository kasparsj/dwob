package lv.kasparsj.android.dwob;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.util.OneLog;

public class App extends Application {
	
	private static final String PREFS_NAME = "DwobPrefsFile";
    private static final int DAY_IN_MILLIS = 24*60*60*1000;
    private String language;
	private String feed_url;
	private boolean loading = false;
	private boolean helpOnStart;
    private long pubDate; // last time updated

    private String title;
    private String translated;
    private String pali;
    private String source;
    private String audio;
	
	public void onCreate() {
		// load saved data
		SharedPreferences settings = getSharedPreferences();
        setLanguage(settings.getString("language", DwobLanguage.EN));
		setTitle(settings.getString("title", getString(R.string.app_name)));
//		setDescription(settings.getString("description", ""));
        setTranslated(settings.getString("translated", ""));
        setPali(settings.getString("pali", ""));
		pubDate = settings.getLong("pubDate", 0);
		helpOnStart = settings.getBoolean("helpOnStart", true);
	}

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
    	boolean doUpdate = (this.language != null && this.language != language);
        this.language = language;
        FeedItem.DATE_PARSER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", new Locale(language));
        if (language.equals(DwobLanguage.ES)) {
            setFeedUrl(getString(R.string.feed_url_es));
        }
        else if (language.equals(DwobLanguage.PT)) {
            setFeedUrl(getString(R.string.feed_url_pt));
        }
        else if (language.equals(DwobLanguage.IT)) {
            setFeedUrl(getString(R.string.feed_url_it));
        }
        else if (language.equals(DwobLanguage.ZH)) {
            setFeedUrl(getString(R.string.feed_url_zh));
        }
        else if (language.equals(DwobLanguage.FR)) {
            setFeedUrl(getString(R.string.feed_url_fr));
        }
        else { // en
            setFeedUrl(getString(R.string.feed_url_en));
        }
        if (doUpdate) {
        	update();
        	SharedPreferences.Editor editor = getSharedPreferences().edit();
        	editor.putString("language", language);
        	editor.commit();
        }
    }
	
	public String getFeedUrl() {
		return feed_url;
	}
	
	public void setFeedUrl(String url) {
		feed_url = url;
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
		return new Date().getTime() - pubDate >= DAY_IN_MILLIS;
	}
	
	public SharedPreferences getSharedPreferences() {
		return getSharedPreferences(PREFS_NAME, 0);
	}
	
	public boolean isLoading() {
		return loading;
	}
	
	public void setLoading(boolean isLoading) {
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putBoolean("loading", isLoading);
		editor.commit();
	}
	
	public void setLoading(boolean isLoading, boolean success) {
		if (success) {
			SharedPreferences.Editor editor = getSharedPreferences().edit();
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
		OneLog.i("App::update");
    	new LoadFeedTask(getApplicationContext()).execute();
	}
	
	public boolean showHelpOnStart() {
		return helpOnStart;
	}
	
	public void dismissHelpOnStart() {
		helpOnStart = false;
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putBoolean("helpOnStart", false);
		editor.commit();
	}
}
