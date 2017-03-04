package lv.kasparsj.android.feed;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class FeedItem implements Comparable<FeedItem>{
	private static final int DAY_IN_MILLIS = 24*60*60*1000;
    private static String DATE_FORMAT = "dd/MM/yyyy";
    private static String TIME_FORMAT = "HH:mm:ss Z";
	private String title;
	private URL link;
	private String description;
	private Date date;

	public String getTitle() {
		return title;
	}

	public void setTitle(String value) {
		this.title = value != null ? value.trim() : null;
	}
	// getters and setters omitted for brevity 
	public URL getLink() {
		return link;
	}

	public void setLink(URL value) {
		link = value;
	}
	
	public void setLink(String value) throws MalformedURLException {
		link = value != null ? new URL(value) : null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String value) {
		description = value != null ? value.trim() : null;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date value) {
		date = value;
	}

	public void setDate(String value, SimpleDateFormat dateParser) {
		if (value != null) {
			value = value.trim();
			while (!value.endsWith("00")){
				value += "0";
			}
			try {
				if (dateParser == null) {
					dateParser = new SimpleDateFormat(DATE_FORMAT +" "+ TIME_FORMAT);
				}
				date = dateParser.parse(value);
			}
			catch (ParseException e) {
				try {
					String[] parts = value.split(" ");
					String datepart = new SimpleDateFormat(DATE_FORMAT).format(new Date());
					String timepart = parts[parts.length-2]+" "+parts[parts.length-1];
					date = new SimpleDateFormat(DATE_FORMAT +" "+ TIME_FORMAT).parse(datepart+" "+timepart);
					Calendar cal = Calendar.getInstance();
					if (date.after(cal.getTime())) {
						long diff = date.getTime() - cal.getTimeInMillis();
						int days = (int) Math.ceil(diff / DAY_IN_MILLIS);
						cal.setTime(date);
						cal.add(Calendar.DATE, -days);
						date = cal.getTime();
					}
				}
				catch (ParseException ex) {
					Log.w(FeedItem.class.toString(), "could not parse date: "+value);
					date = new Date();
				}
			}
		}
		else {
			date = null;
		}
	}
	
	protected <T extends FeedItem> T copy(Class<T> clazz){
        try {
            T copy = clazz.newInstance();
            copy.setTitle(title);
            copy.setLink(link);
            copy.setDescription(description);
            copy.setDate(date);
            return copy;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

    public FeedItem copy() {
        return copy(FeedItem.class);
    }
	
	@Override
	public String toString() {
		return "Title: " + title + '\n' +
				"Date: " + this.getDate() + '\n' +
				"Link: " + link + '\n' +
				"Description: " + description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedItem other = (FeedItem) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public int compareTo(FeedItem another) {
		if (another == null) return 1;
		// sort descending, most recent first
		return another.date.compareTo(date);
	}
}
