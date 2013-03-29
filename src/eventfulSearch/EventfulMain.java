package eventfulSearch;

import java.util.Iterator;
import java.util.List;

public class EventfulMain {
	
	public static void main(String[] args) {
//		String[] inputArgs = args[0].split("|"); 
		String[] inputArgs = "4788 Hazel Street, Burnaby, BC | 103 | 29032013 | 29042013".split("\\|");
//		System.out.println("inputArgs Length: " + inputArgs.length);
//		for(int i=0; i<inputArgs.length; i++) {
//			System.out.println(i + " " + inputArgs[i]);
//		}
		
		
		List<Event> events;
		if(inputArgs.length != 5 && inputArgs.length != 4) {
			throw new EventfulSearchException("Program expects 5 inputs in vertical-bar separated format, in the following form: \n" +
					"{Address} | {Radius} | {Start Date} | {End Date} | {Category}");
		}
		else{
			try{
				EventfulModel model = EventfulModel.buildEventfulModel(inputArgs);
				model.generateLatLong();
				model.populateEvents();
				events = model.getEvents();
				
				System.out.println(model.toString());
				
				printEvents(events);
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
		
	}
	
	public static void printEvents(List <Event> events){
		for(Iterator<Event> event = events.iterator(); event.hasNext();){
			Event evnt = event.next();
			System.out.print("Title: " + evnt.getTitle().replace("{e_name}amp;", "&") + " | ");
			System.out.print("Venue: " + evnt.getVenue() + " | ");
			int totalArtist = evnt.getArtists().size(); 
			if(totalArtist != 0){
				if(totalArtist > 1){
					System.out.print("Artists: ");
				}
				else{
					System.out.print("Artist: ");
				}
			}
			
			List<String> artists = evnt.getArtists();
			for(Iterator<String> arst = artists.iterator(); arst.hasNext();){
				String currArst = arst.next();
				if(arst.hasNext()){
					System.out.print(currArst + ", ");
				}
				else{
					System.out.print(currArst + " | ");
				}
			}
			System.out.println("Event Date: " + evnt.getDate());
		}
	}
	

}
