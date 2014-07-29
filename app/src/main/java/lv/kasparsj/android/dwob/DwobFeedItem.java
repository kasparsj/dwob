package lv.kasparsj.android.dwob;

import lv.kasparsj.android.feed.FeedItem;

public class DwobFeedItem extends FeedItem
{
    private String translator;
    private String listenLink;
    private String bookLink;
    private String tipitakaLink;
    private String pali;
    private String source;
    private String translated;

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
        listenLink = value;
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

    public String getPali() {
        return pali;
    }

    public void setPali(String value) {
        pali = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        source = value;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String value) {
        translated = value;
    }

    @Override
    public DwobFeedItem copy() {
        DwobFeedItem copy = copy(DwobFeedItem.class);
        copy.translator = translator;
        copy.listenLink = listenLink;
        copy.bookLink = bookLink;
        copy.tipitakaLink = tipitakaLink;
        copy.pali = pali;
        copy.source = source;
        copy.translated = translated;
        return copy;
    }
}
