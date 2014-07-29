package lv.kasparsj.android.feed;
import java.util.List;

public interface FeedParser<T extends FeedItem> {
    List<T> parse(Class<T> itemClass);
}
