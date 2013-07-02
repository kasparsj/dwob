package lv.kasparsj.android.dwob;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class DwobApp extends Application {
	
	private static final String PREFS_NAME = "DwobPrefsFile";
    private static final int DAY_IN_MILLISECONDS = 24*60*60*1000;
	private static Pattern AUDIO_PATTERN;
	private static Pattern SOURCE_PATTERN;
    private String language;
	private String feed_url;
	private String title;
	private String description;
	private List<String> original;
	private List<String> translation;
	private String source;
	private String audio;
	private long pubDate; // last time updated
	private boolean loading = false;
	private boolean helpOnStart;
	
	public void onCreate() {
		// load saved data
		SharedPreferences settings = getSharedPreferences();
        setLanguage(settings.getString("language", DwobLanguage.EN));
		setTitle(settings.getString("title", getString(R.string.app_name)));
		setDescription(settings.getString("description", ""));
		pubDate = settings.getLong("pubDate", 0);
		helpOnStart = settings.getBoolean("helpOnStart", true);
	}

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        Message.DATE_PARSER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", new Locale(language));
        if (language.equals(DwobLanguage.ES)) {
            AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_es), Pattern.CASE_INSENSITIVE);
            SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_es), Pattern.CASE_INSENSITIVE);
            setFeedUrl(getString(R.string.feed_url_es));
        }
        else if (language.equals(DwobLanguage.PT)) {
            AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_pt), Pattern.CASE_INSENSITIVE);
            SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_pt), Pattern.CASE_INSENSITIVE);
            setFeedUrl(getString(R.string.feed_url_pt));
        }
        else if (language.equals(DwobLanguage.IT)) {
            AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_it), Pattern.CASE_INSENSITIVE);
            SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_it), Pattern.CASE_INSENSITIVE);
            setFeedUrl(getString(R.string.feed_url_it));
        }
        else if (language.equals(DwobLanguage.ZH)) {
            AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_zh), Pattern.CASE_INSENSITIVE);
            SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_zh), Pattern.CASE_INSENSITIVE);
            setFeedUrl(getString(R.string.feed_url_zh));
        }
        else if (language.equals(DwobLanguage.FR)) {
            AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_fr), Pattern.CASE_INSENSITIVE);
            SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_fr), Pattern.CASE_INSENSITIVE);
            setFeedUrl(getString(R.string.feed_url_fr));
        }
        else { // en
            AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_en), Pattern.CASE_INSENSITIVE);
            SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_en), Pattern.CASE_INSENSITIVE);
            setFeedUrl(getString(R.string.feed_url_en));
        }
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString("language", language);
        editor.commit();
    }
	
	public String getFeedUrl() {
		return feed_url;
	}
	
	public void setFeedUrl(String url) {
		feed_url = url;
		update();
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
		String[] contents = description.split("\n<br />\n");
		original = new ArrayList<String>();
    	translation = new ArrayList<String>();
    	source = "";
    	audio = "";
    	Matcher matcher;
    	for (int i=0; i<contents.length; i++) {
    		if (audio.length() == 0) {
    			original.add(contents[i]);
    			matcher = AUDIO_PATTERN.matcher(contents[i]);
        		if (matcher.find())
        			audio = matcher.group(1);
    		}
    		else {
    			matcher = SOURCE_PATTERN.matcher(contents[i]);
    			if (!matcher.find()) {
    				translation.add(contents[i]);
    			}
    			else {
    				source = contents[i];
    				break;
    			}
    		}
    	}
	}

    public long getPubDate() {
        return pubDate;
    }

    public void setPubDate(long date) {
        this.pubDate = date;
    }
	
	public List<String> getOriginal() {
		return original;
	}
	
	public List<String> getTranslation() {
		return translation;
	}
	
	public String getSource() {
		return source;
	}
	
	public boolean isOutdated() {
		return new Date().getTime() - pubDate >= DAY_IN_MILLISECONDS;
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
			editor.putString("description", description);
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
		Log.i("test", "DwobApp::update");
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
