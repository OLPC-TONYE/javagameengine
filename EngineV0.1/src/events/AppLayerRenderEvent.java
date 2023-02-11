package events;

public abstract class AppLayerRenderEvent extends Event
{

	public AppLayerRenderEvent(String name) {
		super(name, EventType.Wait, EventLevel.ApplicationLayerRender);
	}
	
}
