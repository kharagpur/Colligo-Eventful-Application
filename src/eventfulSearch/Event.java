package eventfulSearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

	private String title;
	private String venue;
	private List<String> artists = new ArrayList<String>();
	private Date date;
	
	public Event() {}
	
	public Event(String title, String venue, Date date) {
		this.title = title;
		this.venue = venue;
		this.date = date;
	}
	
	public void addArtist(String artist) {
		this.artists.add(artist);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public List<String> getArtists() {
		return artists;
	}
	public void setArtists(List<String> artists) {
		this.artists = artists;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
