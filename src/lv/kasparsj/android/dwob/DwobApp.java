package lv.kasparsj.android.dwob;

import lv.kasparsj.android.dwob.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class DwobApp extends Application {
	
	private static final String PREFS_NAME = "DwobPrefsFile";
	private static Pattern AUDIO_PATTERN;
	private static Pattern SOURCE_PATTERN;
	private String feed_url;
	private String title;
	private String description;
	private List<String> original;
	private List<String> translation;
	private String source;
	private String audio;
	private long updated; // last time updated
	private boolean loading = false;
	private boolean helpOnStart;
	
	public void onCreate() {
		// load saved data
		SharedPreferences settings = getSharedPreferences();
		feed_url = settings.getString("feed_url", getString(R.string.feed_url));
		compileParsePatterns();
		setTitle(settings.getString("title", getString(R.string.app_name)));
		setDescription(settings.getString("description", ""));
		updated = settings.getLong("updated", 0);
		helpOnStart = settings.getBoolean("helpOnStart", true);
	}
	
	private void compileParsePatterns() {
		if (feed_url.equals(getString(R.string.feed_url_es))) {
			AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_es), Pattern.CASE_INSENSITIVE);
			SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_es), Pattern.CASE_INSENSITIVE);
		}
		else if (feed_url.equals(getString(R.string.feed_url_pt))) {
			AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_pt), Pattern.CASE_INSENSITIVE);
			SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_pt), Pattern.CASE_INSENSITIVE);
		}
		else if (feed_url.equals(getString(R.string.feed_url_it))) {
			AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_it), Pattern.CASE_INSENSITIVE);
			SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_it), Pattern.CASE_INSENSITIVE);
		}
		else {
			AUDIO_PATTERN = Pattern.compile(getString(R.string.audio_pattern_en), Pattern.CASE_INSENSITIVE);
			SOURCE_PATTERN = Pattern.compile(getString(R.string.source_pattern_en), Pattern.CASE_INSENSITIVE);
		}
	}
	
	public String getFeedUrl() {
		return feed_url;
	}
	
	public void setFeedUrl(String url) {
		feed_url = url;
		compileParsePatterns();
		update();
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putString("feed_url", feed_url);
		editor.commit();
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
		return new Date().getTime() - updated >= getResources().getInteger(R.integer.update_period);
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
			updated = new Date().getTime();
			SharedPreferences.Editor editor = getSharedPreferences().edit();
			editor.putString("title", title);
			editor.putString("description", description);
			editor.putLong("updated", updated);
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
