package eventfulSearch;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class EventfulProcessor {
	
	/**
	 * URL Format based on (http://api.eventful.com/docs/events/get)
	 * http://api.evdb.com/rest/events/search?app_keys=[APPKEY]&user=[USERNAME]&user_key=[USERKEY]&id=E0-001-000278174-6
	 * NOTE: [value] the value inside the square brackets is generated dynamically based on user input.
	 */
	
	//FINAL VARIABLES
	private final String EVENTFULAPIURL = "http://api.evdb.com/rest/events/search?";
	private final String USERNAME 		= "kkw21";
	private final String USERKEY 		= "2w98kJmMhBhfVrTY";								// Developer user key
	private final String APPKEY 		= "8PFcLzLJWBzvHCwT";								// Developer application key
		
	//OPERATION
	private String constructURI(String longitude, String latitude, String radius, String startDate, String endDate, String category, String units){
		return  EVENTFULAPIURL + generateAuthURN() + generateKeywordsURN(category) + generateLocationURN(longitude, latitude, radius, units) + generateDateURN(startDate, endDate);
	}
	
	private String generateAuthURN(){
		return "app_key=" + APPKEY + "&user="+ USERNAME + "&user_key=" + USERKEY;
	}
	
	private String generateKeywordsURN(String category){
		return "&keywords=" + category;
	}
	
	private String generateLocationURN(String longitude, String latitude, String radius, String units){
		return "&where=" + latitude + "," + longitude + "&within=" + radius + "&units=" + units;
	}
	
	// TODO: consider rewrite this using regex
	private String generateDateURN(String startDate, String endDate){
		//EVENTFUL API parses date in different format YYYYMMDD00
		int sday = Integer.parseInt(""+startDate.charAt(0)+startDate.charAt(1));
		int smonth = Integer.parseInt(""+startDate.charAt(2)+startDate.charAt(3));
		int syear = Integer.parseInt(""+startDate.charAt(4)+startDate.charAt(5)+startDate.charAt(6)+startDate.charAt(7));
		int eday = Integer.parseInt(""+endDate.charAt(0)+endDate.charAt(1));
		int emonth = Integer.parseInt(""+endDate.charAt(2)+endDate.charAt(3));
		int eyear = Integer.parseInt(""+endDate.charAt(4)+endDate.charAt(5)+endDate.charAt(6)+endDate.charAt(7));
		return "&date=" + syear+smonth+sday+"00"+ "-" + eyear+emonth+eday+"00";//2013041300-2013051200";
	}
	
	public List<Event> findEvents(String longitude, String latitude, String radius, String startDate, String endDate, String category, String units){
		String uri = constructURI(longitude, latitude, radius, startDate, endDate, category, units);
		URL constructedQueryURL;
		URLConnection constructedQueryURLConnection;
		List<Event> events;
		//System.out.println("ConstructedLoginURL: " + uri);
		
		try {
			constructedQueryURL = new URL(uri);
			constructedQueryURLConnection = constructedQueryURL.openConnection();
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(constructedQueryURLConnection.getInputStream());
			Element rootNode = document.getRootElement();
			
			List<Element> eventElementList = rootNode.getChild("events").getChildren("event");
			events = new ArrayList<Event>(eventElementList.size());
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			for(Iterator<Element> eventIterator = eventElementList.iterator(); eventIterator.hasNext();) {
				Element eventElement = eventIterator.next();
				
				String title = eventElement.getChildText("title");
				String venue = eventElement.getChildText("venue_name");
//				String[] dateTime = eventElement.getChildText("start_time").split(" "); 
//				Date date = format.parse(dateTime[0]); // assuming event date is start date
				Date date = format.parse(eventElement.getChildText("start_time"));
				Event event = new Event(title, venue, date);
				
				List<Element> performers = eventElement.getChild("performers").getChildren("performer");
				for(Iterator<Element> performerIterator = performers.iterator(); performerIterator.hasNext();) {
					Element performerElement = performerIterator.next();
					String name = performerElement.getChildText("name");
					event.addArtist(name);
				}
				events.add(event);
			}
		} catch (MalformedURLException e) {
			throw new EventfulSearchException("URL for Eventful API is not well formed", e);
		} catch (IOException e) {
			throw new EventfulSearchException("IO problems", e);
		} catch (JDOMException e) {
			throw new EventfulSearchException("JDOM exception", e);
		} catch (ParseException e) {
			throw new EventfulSearchException("error parsing date", e);
		}
		
		return events;
	}
}
