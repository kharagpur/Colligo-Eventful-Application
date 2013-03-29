package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import eventfulSearch.Event;
import eventfulSearch.EventfulProcessor;

public class EventfulAPITest {
	
	public static void main(String[] args) {
		EventfulProcessor eventfulQuery = new EventfulProcessor();
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File("eventful.xml");
		
		try {
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			Element e = rootNode.getChild("event");
			List<Element> eventElementList = rootNode.getChild("events").getChildren("event");
			List<Event> events = new ArrayList<Event>(eventElementList.size());
			
			for(Iterator<Element> eventIterator = eventElementList.iterator(); eventIterator.hasNext();) {
				Element eventElement = eventIterator.next();
				String title = eventElement.getChildText("title");
				System.out.println(title);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
