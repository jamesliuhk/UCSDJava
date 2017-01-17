package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	final int buttonWidth = 110;
	final int buttonHight = 50;
	final int buttonX = 10;
	final int buttonY = 20;
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 130, 0, 750, 600, new Microsoft.RoadProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			m.setId(feature.getId());
			//System.out.println("Marker id : " + m.getAirportId());
			//System.out.println("Fearture id : " + feature.getId());
			airportList.add(m);
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		//routeList = new ArrayList<Marker>();
		//int test = 0;
		for(ShapeFeature route : routes) {
			/*
			if(test > 500)
			{
				break;
			}
			
			test ++;
			*/
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			/*if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}*/
			
			for(Marker airport : airportList)
			{
				AirportMarker airportMarker = (AirportMarker) airport;
				
				//System.out.println(airportMarker.getAirportId() + "," + Integer.toString(source));
				//System.out.println("source id : " + Integer.toString(source));
								
				if((Integer.parseInt(airportMarker.getAirportId()) == source) 
						|| (Integer.parseInt(airportMarker.getAirportId()) == dest))
				{
					SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
					airportMarker.addRounte(sl);
				}
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
			
			//System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			//routeList.add(sl);
		}
		
		//for testing
		/*
		int large = 0;
		int medium = 0;
		int small = 0;
		for(Marker airport : airportList)
		{
			AirportMarker airportMarker = (AirportMarker) airport;
			if(airportMarker.totalRoutes() > 15 )
			{
				//System.out.println(airportMarker.getProperty("name") + " : " + airportMarker.totalRoutes());
				large ++;
			}
			else if(airportMarker.totalRoutes() < 3)
			{
				small ++;
			}
			else
			{
				medium ++;
			}
		}
		System.out.println("large :  " + large);
		System.out.println("medean :  " + medium);
		System.out.println("small :  " + small);
		*/
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		addControl();
		
	}
	
	void addControl()
	{
		fill(color(150,150,0));
		rect(buttonX,buttonY,buttonWidth,buttonHight);
		rect(buttonX,buttonY + (buttonHight +10) * 1,buttonWidth,buttonHight);
		rect(buttonX,buttonY + (buttonHight +10) * 2,buttonWidth,buttonHight);
		rect(buttonX,buttonY + (buttonHight +10) * 3,buttonWidth,buttonHight);
		
		
		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("Large Airport", 20, 40);
		
		text("Medium Airport", 20, 100);
		text("Small Airport", 20, 160);
		text("All Airport", 20, 220);
	}
	
	@Override
	public void mouseClicked()
	{
		if(clickPos() == 1)
		{
			for(Marker airport : airportList)
			{
				AirportMarker airportMarker = (AirportMarker) airport;
				airportMarker.setHidden(true);
				if(airportMarker.totalRoutes() > 15 )
				{
					//System.out.println(airportMarker.getProperty("name") + " : " + airportMarker.totalRoutes());
					airportMarker.setHidden(false);
				}
			}
		}
		else if(clickPos() == 2)
		{
			for(Marker airport : airportList)
			{
				AirportMarker airportMarker = (AirportMarker) airport;
				airportMarker.setHidden(true);
				if(airportMarker.totalRoutes() < 15 && airportMarker.totalRoutes() > 2)
				{
					//System.out.println(airportMarker.getProperty("name") + " : " + airportMarker.totalRoutes());
					airportMarker.setHidden(false);
				}
			}
		}
		else if(clickPos() == 3)
		{
			for(Marker airport : airportList)
			{
				AirportMarker airportMarker = (AirportMarker) airport;
				airportMarker.setHidden(true);
				if(airportMarker.totalRoutes() < 2)
				{
					//System.out.println(airportMarker.getProperty("name") + " : " + airportMarker.totalRoutes());
					airportMarker.setHidden(false);
				}
			}
		}
		else if(clickPos() == 4)
		{
			for(Marker airport : airportList)
			{
				AirportMarker airportMarker = (AirportMarker) airport;
				airportMarker.setHidden(false);
			}
		}
		else // clickPos == -1
		{
			return;
		}
	}
	
	private int clickPos()
	{
		if(mouseX < buttonX + buttonWidth && mouseX > buttonX 
				&& mouseY > buttonY + (buttonHight + 10) *0 && mouseY < buttonY + (buttonHight + 10) *0 + buttonHight)
		{
			return 1;
		}
		else if(mouseX < buttonX + buttonWidth && mouseX > buttonX 
				&& mouseY > buttonY + (buttonHight + 10) *1 && mouseY < buttonY + (buttonHight + 10) *1 + buttonHight)
		{
			return 2;
		}
		else if(mouseX < buttonX + buttonWidth && mouseX > buttonX 
				&& mouseY > buttonY + (buttonHight + 10) *2 && mouseY < buttonY + (buttonHight + 10) *2 + buttonHight)
		{
			return 3;
		}
		else if(mouseX < buttonX + buttonWidth && mouseX > buttonX 
				&& mouseY > buttonY + (buttonHight + 10) *3 && mouseY < buttonY + (buttonHight + 10) *3 + buttonHight)
		{
			return 4;
		}
		else
		{
			return -1;
		}
	}
	

}
