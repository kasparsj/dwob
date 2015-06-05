package lv.kasparsj.android.dwob;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;

import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.util.OneLog;

public class App extends Application {
	
	public static final String PREFS_NAME = "DwobPrefsFile";
    public static final int DAY_IN_MILLIS = 24*60*60*1000;
    public static App applicationContext;

    private String language;
	private String daily_words_url;
	private boolean loading = false;
	private boolean helpOnStart;

	public void onCreate() {
        super.onCreate();
        App.applicationContext = this;

		// load saved data
        SharedPreferences settings = getSharedPreferences();
        setLanguage(settings.getString("language", DwobLanguage.EN));
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
            setDailyWordsUrl(getString(R.string.daily_words_url_es));
        }
        else if (language.equals(DwobLanguage.PT)) {
            setDailyWordsUrl(getString(R.string.daily_words_url_pt));
        }
        else if (language.equals(DwobLanguage.IT)) {
            setDailyWordsUrl(getString(R.string.daily_words_url_it));
        }
        else if (language.equals(DwobLanguage.ZH)) {
            setDailyWordsUrl(getString(R.string.daily_words_url_zh));
        }
        else if (language.equals(DwobLanguage.FR)) {
            setDailyWordsUrl(getString(R.string.daily_words_url_fr));
        }
        else { // en
            setDailyWordsUrl(getString(R.string.daily_words_url_en));
        }
        if (doUpdate) {
        	DailyWords.getInstance().update();
        	SharedPreferences.Editor editor = getSharedPreferences().edit();
        	editor.putString("language", language);
        	editor.commit();
        }
    }
	
	public String getDailyWordsUrl() {
		return daily_words_url;
	}
	
	public void setDailyWordsUrl(String url) {
		daily_words_url = url;
	}

	public String getPaliWordUrl() {
        return getString(R.string.pali_word_url);
	}

    public String getDhammaVersesUrl() {
        return getString(R.string.dhamma_verses_url);
    }
	
	public SharedPreferences getSharedPreferences() {
		return getSharedPreferences(PREFS_NAME, 0);
	}
	
	public void updatePaliWord() {
		OneLog.i("App::updatePaliWord");
		new LoadPaliWordTask(getApplicationContext()).execute();
	}

	public void updateDhammaVerses() {
		OneLog.i("App::updateDhammaVerses");
		new LoadDhammaVersesTask(getApplicationContext()).execute();
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
