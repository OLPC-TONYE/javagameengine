package events;

public abstract class AppLayerEvent extends Event
{

	public AppLayerEvent(String name) {
		super(name, EventType.Wait, EventLevel.ApplicationLayer);
	}
	
}
