package eventfulSearch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventfulModel {
	private static final SimpleDateFormat Formatter = new SimpleDateFormat("ddMMyyyy");
	
	private String street;
	private String city;
	private String state;
	private int radius;
	private Date startDate;
	private Date endDate;	
	private String category;
	
	private double longitude;
	private double latitude;
	private List<Event> events = new ArrayList<Event>();
	
	private EventfulModel(String street, String city, String state, int radius, Date startDate, Date endDate, String category) {
		this.street = street;
		this.city = city;
		this.state = state;
		this.radius = radius;
		this.startDate = startDate;
		this.endDate = endDate; 
		this.category = category;
	}

	public static EventfulModel buildEventfulModel(String[] inputArgs) {
		String street, city, state;
		// parsing address. assumption is address is separated by ",", following the format 'street', 'city', 'state'; this assumption is not specified in doc
		String [] address = inputArgs[0].split(",");
		if (address.length == 3){
			street = address[0].trim();
			city = address[1].trim();
			state = address[2].trim();
		} else {
			throw new EventfulSearchException("Address should be of format: 'street', 'city', 'state'");
		}
		
		// parsing radius
		int radius = 0;
		try {
			radius = Integer.parseInt(inputArgs[1].trim());
			if(radius < 0 || radius > 300) 
				throw new EventfulSearchException("Radius should be between 0 to 300");
		} catch (NumberFormatException e) {
			throw new EventfulSearchException("Radius is not an integer", e);
		}
		
		// parsing start and end date
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = EventfulModel.Formatter.parse(inputArgs[2].trim());
			endDate = EventfulModel.Formatter.parse(inputArgs[3].trim());
			
			//TODO: there are many validations that needs to be done according to the specifications
		} catch (ParseException e) {
			throw new EventfulSearchException("start date or end date does not follow the format ddMMyyyy", e);
		}
		
		String category = "Music"; // default value for category
		if(inputArgs.length == 5) { 
			category = inputArgs[4];
			if(!category.equals("Music") || !category.equals("Sports") || !category.equals("Performing Arts")) {
				throw new EventfulSearchException("category must be Music, Sports or Performing Arts");
			}
		}
		
		return new EventfulModel(street, city, state, radius, startDate, endDate, category);
	}
	
	// call google Map API
	public void generateLatLong() {
//		GoogleAddressProcessor addressQuery = new GoogleAddressProcessor(this.getStreet(), this.getCity(), this.getState());
		GoogleAddressProcessor addressQuery = new GoogleAddressProcessor();
		if (addressQuery.isValidAddress(this.getStreet(), this.getCity(), this.getState())){
			try {
//				this.longitude = Double.valueOf(addressQuery.getLongitude());
//				this.latitude = Double.valueOf(addressQuery.getLatitude());
				this.longitude = addressQuery.getLongitude();
				this.latitude = addressQuery.getLatitude();
			} catch (NumberFormatException e) {
				throw new EventfulSearchException("Lat and Long need to be of double datatype", e);
			}
		} else {
			throw new EventfulSearchException("Invalid address, validation by addressProcessor failed.");
		}
	}
	
	// call Eventful API
	public void populateEvents() {
		String strStartDate = Formatter.format(this.getStartDate());
		String strEndDate = Formatter.format(this.getEndDate());
		String units = "KM";
		EventfulProcessor eventQuery = new EventfulProcessor();
		this.events = eventQuery.findEvents(Double.toString(longitude), Double.toString(latitude), Integer.toString(radius), strStartDate, strEndDate, category, units);
	}
	
	
	// getter and setter
	public int getRadius() {
		return radius;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<Event> getEvents() {
		return events;
	}
	
	public String toString(){
		String str = "";
		str = str.concat("Address:    " + this.street + "\n");
		str = str.concat("            " + this.city + "\n");
		str = str.concat("            " + this.state + "\n");
		str = str.concat("Radius:     " + this.radius + "KM"+ "\n");
		str = str.concat("Start Date: " + this.startDate + "\n");
		str = str.concat("End Date  : " + this.endDate + "\n");
		return str;
	}
	
}
