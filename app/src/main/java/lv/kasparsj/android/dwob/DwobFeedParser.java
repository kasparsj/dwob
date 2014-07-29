package lv.kasparsj.android.dwob;

import android.sax.Element;
import android.sax.EndTextElementListener;

import lv.kasparsj.android.feed.SaxFeedParser;

public class DwobFeedParser extends SaxFeedParser<DwobFeedItem>
{
    static final String DWOB = "dwob";
    static final String TRANSLATOR = "translator";
    static final String LISTEN_LINK = "listen-link";
    static final String BOOK_LINK = "book-link";
    static final String TIPITAKA_LINK = "tipitaka-link";
    static final String PALI = "pali";
    static final String SOURCE = "source";
    static final String TRANSLATED = "translated";

    public DwobFeedParser(String feedUrl) {
        super(feedUrl);
    }

    @Override
    protected void configureItem(Element item) {
        super.configureItem(item);

        configureDwob(item.getChild(DWOB));
    }

    protected void configureDwob(Element dwob) {
        dwob.getChild(TRANSLATOR).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setTranslator(body);
            }
        });
        dwob.getChild(LISTEN_LINK).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setListenLink(body);
            }
        });
        dwob.getChild(BOOK_LINK).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setBookLink(body);
            }
        });
        dwob.getChild(TIPITAKA_LINK).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setTipitakaLink(body);
            }
        });
        dwob.getChild(PALI).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setPali(body);
            }
        });
        dwob.getChild(SOURCE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setSource(body);
            }
        });
        dwob.getChild(TRANSLATED).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setTranslated(body);
            }
        });
    }
}