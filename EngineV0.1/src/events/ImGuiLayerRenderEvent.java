package events;

public abstract class ImGuiLayerRenderEvent extends Event
{

	public ImGuiLayerRenderEvent(String name) {
		super(name, EventType.Wait, EventLevel.ImGuiLayerRender);
	}
	
}
