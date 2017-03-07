package lv.kasparsj.android.dwob.model;

import android.content.Context;

import java.util.List;

import lv.kasparsj.android.dwob.DailyWordsLargeWidget;
import lv.kasparsj.android.dwob.feed.DailyWordsFeedItem;
import lv.kasparsj.android.dwob.feed.DailyWordsFeedParser;
import lv.kasparsj.android.dwob.DailyWordsWidget;
import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.util.Objects;

public class DailyWords extends BaseModel {

    private boolean helpOnStart;
    private boolean whatsNewOnStart;

    private String title;
    private String translated;
    private String pali;
    private String source;
    private String translator;
    private String listenLink;
    private String bookLink;
    private String tipitakaLink;

    public DailyWords(Context context) {
        super(context);
    }

    protected void load() {
        helpOnStart = settings.getBoolean("helpOnStart", true);
        whatsNewOnStart = settings.getBoolean("whatsNewOnStart", !helpOnStart);
        setLanguage(settings.getString("language", Languages.EN));
        setTitle(settings.getString("title", context.getString(R.string.app_name)));
        setTranslated(settings.getString("translated", ""));
        setPali(settings.getString("pali", ""));
        setSource(settings.getString("source", ""));
        setTranslator(settings.getString("translator", ""));
        setListenLink(settings.getString("listenLink", ""));
        setBookLink(settings.getString("bookLink", ""));
        setTipitakaLink(settings.getString("tipitakaLink", ""));
        pubDate = settings.getLong("pubDate", 0);
    }

    public boolean showHelpOnStart() {
        return helpOnStart;
    }

    public void dismissHelpOnStart() {
        helpOnStart = false;
        settings.putBoolean("helpOnStart", false);
        settings.commit();
        dismissWhatsNewOnStart();
    }

    public boolean showWhatsNewOnStart() {
        return whatsNewOnStart;
    }

    public void dismissWhatsNewOnStart() {
        whatsNewOnStart = false;
        settings.putBoolean("whatsNewOnStart", false);
        settings.commit();
    }

    @Override
    protected String getSettingsNs() {
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
        listenLink = value != null ? value.toLowerCase().replaceAll("^(.*)((http|https)://)", "$2") : null;
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

    protected void save() {
        settings.putString("title", title);
        settings.putString("translated", translated);
        settings.putString("pali", pali);
        settings.putLong("pubDate", pubDate);
        settings.putString("source", source);
        settings.putString("translator", translator);
        settings.putString("listenLink", listenLink);
        settings.putString("bookLink", bookLink);
        settings.putString("tipitakaLink", tipitakaLink);
        settings.putBoolean("success", true);
        settings.commit();
    }

    @Override
    public void update() {
        update(new DailyWordsFeedParser());
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
            save();
        }
    }

    @Override
    public void updateWidgets() {
        updateWidgets(DailyWordsWidget.class, DailyWordsLargeWidget.class);
    }

    @Override
    public boolean isLoaded() {
        return translated.length() > 0;
    }

    @Override
    public String getFeedUrl() {
        switch (language) {
            case Languages.ES:
                return context.getString(R.string.daily_words_url_es);
            case Languages.PT:
                return context.getString(R.string.daily_words_url_pt);
            case Languages.IT:
                return context.getString(R.string.daily_words_url_it);
            case Languages.ZH:
                return context.getString(R.string.daily_words_url_zh);
            case Languages.FR:
                return context.getString(R.string.daily_words_url_fr);
            default:  // en
                return context.getString(R.string.daily_words_url_en);
        }
    }
}
