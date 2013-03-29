package eventfulSearch;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class GoogleAddressProcessor {
	
	// FINAL VARIABLES
	private final String GOOGLEAPIURL = "http://maps.googleapis.com/maps/api/geocode/xml?";	//DEFAULT GOOGLE API ADDRESS
	
	private double longitude;
	private double latitude;
	
	/**
	 * This method constructs the URI to call Google API
	 * @return final URI to call Google API
	 */
	private String constructURI(String street, String city, String state){
		// GOOGLE API EXAMPLE: http://maps.googleapis.com/maps/api/geocode/xml?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=true_or_false
		String uri = "";
		// Replace any space with +
		uri = uri.concat(GOOGLEAPIURL + "address=" + street.replace(' ', '+') + ",+"+ city.replace(' ', '+'));
		if (!state.isEmpty()){
			uri = uri.concat(",+"+ state);
		}
		uri = uri.concat("&sensor=false");
		
		return uri;
	}
	
	/**
	 * This method connects to Google API and gets the XML
	 * 
	 * If the XML <status> is "OK" then return true and get longitude and latitude else return false
	 * @param URI
	 * @return boolean value
	 */
	private boolean getXML(String URI){
		URL constructedQueryURL;
		URLConnection constructedQueryURLConnection;
		try{
			constructedQueryURL = new URL(URI);
			constructedQueryURLConnection = constructedQueryURL.openConnection();
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(constructedQueryURLConnection.getInputStream());
			// Get the root node of the document
			Element rootNode = document.getRootElement();
			List <Element> list = rootNode.getChildren("status");
			Element status = list.get(0);
			if (status.getText().matches("OK")){ // if status code is ok then get the long and lat
				// Propagate down the XML Document to get longitude and latitude
				// Get result tag
				list.clear();
				list = rootNode.getChildren("result");
				Element result = list.get(0);
				// Get geometry tag
				list.clear();
				list = result.getChildren("geometry");
				Element geometry = list.get(0);
				// Get location tag
				list.clear();
				list = geometry.getChildren("location");
				Element location = list.get(0);
				// Get lat tag
				list.clear();
				list = location.getChildren("lat");
				Element lat = list.get(0);
				// Get long tag
				list.clear();
				list = location.getChildren("lng");
				Element lng = list.get(0);
				
				// Store the long and latitude information
				this.latitude = Double.valueOf(lat.getText());
				this.longitude = Double.valueOf(lng.getText());
				
				return true;
			}
			else{ // else error in status code return false
				return false;
			}
		}
		catch (MalformedURLException e) {
			throw new EventfulSearchException("URL for Google API is not well formed", e);
		} 
		catch (IOException e) {
			throw new EventfulSearchException("IO problems", e);
		} 
		catch (JDOMException e) {
			throw new EventfulSearchException("JDOM exception", e);
		}
	}
	
	public boolean isValidAddress(String street, String city, String state){
		if (street.isEmpty() || city.isEmpty() || state.isEmpty() || !(getXML(constructURI(street, city, state)))){
			return false;
		}
		return true;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
}
