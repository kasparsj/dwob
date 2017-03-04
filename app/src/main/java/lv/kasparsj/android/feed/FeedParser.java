package lv.kasparsj.android.feed;
import java.io.InputStream;
import java.util.List;

public interface FeedParser<T extends FeedItem> {
    List<T> parse(InputStream inputStream, Class<T> itemClass);
}
