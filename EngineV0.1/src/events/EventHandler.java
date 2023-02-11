package events;

import java.util.ArrayList;

public class EventHandler {
	
	public static ArrayList<Event> eventQueue = new ArrayList<>();
	
	public static void handle(EventLevel level) {
		for(Event e: eventQueue) {
			if(e.getLevel() == level) {
				if(e.handle()) {
					e.onEvent();
					eventQueue.remove(e);
				}
				break;
			}
			break;
		}
	}
	
	public static void queue(Event e) {
		eventQueue.add(e);
	}
}