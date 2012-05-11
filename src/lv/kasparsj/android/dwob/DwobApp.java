package lv.kasparsj.android.dwob;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.SharedPreferences;

public class DwobApp extends Application {
	
	public static final long UPDATE_INTERVAL = 1000 * 60 * 60; // 1 hour
	
	private static final String PREFS_NAME = "DwobPrefsFile";
	private static final Pattern AUDIO_PATTERN = Pattern.compile("<a[^>]* href=\"([^\"]+)\"[^>]*>Listen</a>", Pattern.CASE_INSENSITIVE);
	private static final Pattern SOURCE_PATTERN = Pattern.compile("<a[^>]* href=\"([^\"]+)\"[^>]*>View P.li on Tipitaka.org</a>", Pattern.CASE_INSENSITIVE);
	private String title;
	private String description;
	private List<String> original;
	private List<String> translation;
	private String source;
	private String audio;
	private long updated;
	
	public void onCreate() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		setTitle(settings.getString("title", ""));
		setDescription(settings.getString("description", ""));
		setUpdated(settings.getLong("updated", 0));
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
	
	public long getUpdated() {
		return updated;
	}
	
	public void setUpdated(long updated) {
		this.updated = updated;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("title", title);
		editor.putString("description", description);
		editor.putLong("updated", updated);
		editor.commit();
	}
}
