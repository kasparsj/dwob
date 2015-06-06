package lv.kasparsj.android.dwob;

import android.content.SharedPreferences;

public class DailyWords extends BaseModel {

    private static DailyWords instance = null;

    private String title;
    private String translated;
    private String pali;
    private String source;
    private String audio;

    private DailyWords(App applicationContext) {
        super(applicationContext);
    }

    public static DailyWords getInstance() {
        if (instance == null) {
            instance = new DailyWords(App.applicationContext);
        }
        return instance;
    }

    protected void load() {
        String ns = getSaveNS();
        setTitle(settings.getString(ns+"title", context.getString(R.string.app_name)));
        setTranslated(settings.getString(ns+"translated", ""));
        setPali(settings.getString(ns+"pali", ""));
        pubDate = settings.getLong(ns+"pubDate", 0);
    }

    @Override
    protected String getSaveNS() {
        return "";
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

    protected void save(boolean isLoading) {
        String ns = getSaveNS();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ns+"title", title);
        editor.putString(ns+"translated", translated);
        editor.putString(ns+"pali", pali);
        editor.putLong(ns+"pubDate", pubDate);
        editor.putBoolean(ns+"loading", isLoading);
        editor.putBoolean(ns+"success", true);
        editor.commit();
    }

    public void update() {
        new LoadDailyWordsTask(context).execute();
    }

    @Override
    public boolean isLoaded() {
        return title.length() > 0;
    }
}
