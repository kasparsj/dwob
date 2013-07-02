package lv.kasparsj.android.dwob;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class Message implements Comparable<Message>{
	public static SimpleDateFormat DATE_PARSER;
	private static final int DAY_IN_MILLIS = 24*60*60*1000;
    private static String TIME_PARSER = "HH:mm:ss Z";
    private static String DATE_FORMAT = "dd/MM/yyyy";
	private String title;
	private URL link;
	private String description;
	private Date date;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title.trim();
	}
	// getters and setters omitted for brevity 
	public URL getLink() {
		return link;
	}
	
	public void setLink(String link) {
		try {
			this.link = new URL(link);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description.trim();
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(String date) {
        date = date.trim();
		while (!date.endsWith("00")){
			date += "0";
		}
        try {
            this.date = DATE_PARSER.parse(date);
        }
        catch (ParseException e) {
            try {
                String[] parts = date.split(" ");
                String datepart = new SimpleDateFormat(DATE_FORMAT).format(new Date());
                String timepart = parts[parts.length-2]+" "+parts[parts.length-1];
                this.date = new SimpleDateFormat(DATE_FORMAT +" "+ TIME_PARSER).parse(datepart+" "+timepart);
                Calendar cal = Calendar.getInstance();
                if (this.date.after(cal.getTime())) {
                	long diff = this.date.getTime() - cal.getTimeInMillis();
                	int days = (int) Math.ceil(diff / DAY_IN_MILLIS);
                	cal.setTime(this.date);
                	cal.add(Calendar.DATE, -days);
                	this.date = cal.getTime();
                }
            }
            catch (ParseException ex) {
                Log.w(Message.class.toString(), "could not parse date: "+date);
                this.date = new Date();
            }
        }
        Log.i("test", this.date.toString());
	}
	
	public Message copy(){
		Message copy = new Message();
		copy.title = title;
		copy.link = link;
		copy.description = description;
		copy.date = date;
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ");
		sb.append(title);
		sb.append('\n');
		sb.append("Date: ");
		sb.append(this.getDate());
		sb.append('\n');
		sb.append("Link: ");
		sb.append(link);
		sb.append('\n');
		sb.append("Description: ");
		sb.append(description);
		return sb.toString();
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
		Message other = (Message) obj;
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

	public int compareTo(Message another) {
		if (another == null) return 1;
		// sort descending, most recent first
		return another.date.compareTo(date);
	}
}
