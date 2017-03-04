package lv.kasparsj.android.feed;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

import org.xml.sax.ContentHandler;

abstract public class SaxFeedParser<T extends FeedItem> extends BaseFeedParser {

    protected List<T> feedItems;
    protected T currentFeedItem;
    protected SimpleDateFormat dateFormat;

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public List<T> parse(InputStream inputStream, Class itemClass) {
        feedItems = new ArrayList<T>();
        try {
            currentFeedItem = (T) itemClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            Xml.parse(inputStream, Xml.Encoding.UTF_8, getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return feedItems;
    }

    abstract public List<T> parse(InputStream inputStream);

    protected ContentHandler getContentHandler() {
        RootElement root = new RootElement(RSS);
        Element channel = root.getChild(CHANNEL);
        configureItem(channel.getChild(ITEM));
        return root.getContentHandler();
    }

    protected void configureItem(Element item) {
        item.setEndElementListener(new EndElementListener() {
            public void end() {
                feedItems.add((T) currentFeedItem.copy());
            }
        });
        item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setTitle(body);
            }
        });
        item.getChild(LINK).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                try {
                    currentFeedItem.setLink(body);
                }
                catch (MalformedURLException ignored) {

                }
            }
        });
        item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setDescription(body);
            }
        });
        item.getChild(PUB_DATE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentFeedItem.setDate(body, dateFormat);
            }
        });
    }
}
