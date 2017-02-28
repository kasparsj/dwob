package lv.kasparsj.android.dwob;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.List;

import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.util.Objects;

public class DailyWords extends BaseModel {

    private static DailyWords instance = null;

    private String title;
    private String translated;
    private String pali;
    private String source;
    private String translator;
    private String listenLink;
    private String bookLink;
    private String tipitakaLink;

    private DailyWords() {
        super();
    }

    public static DailyWords getInstance() {
        if (instance == null) {
            instance = new DailyWords();
        }
        return instance;
    }

    protected void load() {
        Context context = App.applicationContext;
        String ns = getSaveNS();
        setTitle(settings.getString(ns+"title", context.getString(R.string.app_name)));
        setTranslated(settings.getString(ns+"translated", ""));
        setPali(settings.getString(ns+"pali", ""));
        setSource(settings.getString(ns+"source", ""));
        setTranslator(settings.getString(ns+"translator", ""));
        setListenLink(settings.getString(ns+"listenLink", ""));
        setBookLink(settings.getString(ns+"bookLink", ""));
        setTipitakaLink(settings.getString(ns+"tipitakaLink", ""));
        pubDate = settings.getLong(ns+"pubDate", 0);
        if (translated.length() > 0) {
            refresh();
        }
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

    public void setSource(String value) {
        source = value;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String value) {
        translator = value;
    }

    public String getListenLink() {
        return listenLink;
    }

    public void setListenLink(String value) {
        listenLink = value != null ? value.toLowerCase().replaceAll("^\\s*listen:\\s*", "") : null;
    }

    public String getBookLink() {
        return bookLink;
    }

    public void setBookLink(String value) {
        bookLink = value;
    }

    public String getTipitakaLink() {
        return tipitakaLink;
    }

    public void setTipitakaLink(String value) {
        tipitakaLink = value;
    }

    protected void save(boolean isLoading) {
        String ns = getSaveNS();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ns+"title", title);
        editor.putString(ns+"translated", translated);
        editor.putString(ns+"pali", pali);
        editor.putLong(ns+"pubDate", pubDate);
        editor.putString(ns+"source", source);
        editor.putString(ns+"translator", translator);
        editor.putString(ns+"listenLink", listenLink);
        editor.putString(ns+"bookLink", bookLink);
        editor.putString(ns+"tipitakaLink", tipitakaLink);
        editor.putBoolean(ns+"loading", isLoading);
        editor.putBoolean(ns+"success", true);
        editor.commit();
    }

    @Override
    public void update() {
        App app = App.applicationContext;
        update(app.getDailyWordsUrl());
    }

    @Override
    public void refresh() {
        Context context = App.applicationContext;
        // todo: implement saving widget state (enabled or disabled) in SharedPreferences
        // todo: broadcast intent only if it's enabled
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        ComponentName componentName = new ComponentName(context, DailyWordsWidget.class);
        intent.setComponent(componentName);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);

        intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        componentName = new ComponentName(context, LargeDailyWordsWidget.class);
        intent.setComponent(componentName);
        ids = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void update(String feedUrl) {
        Context context = App.applicationContext;
        new LoadFeedTask(this, new DailyWordsFeedParser(feedUrl), new LoadFeedTask.LoadFeedTaskListener() {
            @Override
            public void onFeedLoaded() {
                refresh();
            }
        }).execute();
    }

    @Override
    public void update(List<? extends FeedItem> feedItems) {
        // todo: currently handles only 1 item, but 7 are retrieved
        // todo: implement list of last 7 daily words
        DailyWordsFeedItem feedItem = (DailyWordsFeedItem) feedItems.get(0);
        long date = feedItem.getDate().getTime();
        String translated = feedItem.getTranslated();
        if (getPubDate() != date || !Objects.equals(getTranslated(), translated)) {
            setTitle(feedItem.getDate().toLocaleString());
            setTranslated(translated);
            setPali(feedItem.getPali());
            setSource(feedItem.getSource());
            setTranslator(feedItem.getTranslator());
            setListenLink(feedItem.getListenLink());
            setBookLink(feedItem.getBookLink());
            setTipitakaLink(feedItem.getTipitakaLink());
            setPubDate(date);
        }
    }

    @Override
    public boolean isLoaded() {
        return title.length() > 0;
    }
}
