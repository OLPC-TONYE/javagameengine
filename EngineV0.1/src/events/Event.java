package events;

enum EventType
{
	None,
	Wait,
};

public abstract class Event {
	
	
	String name;
	EventType type;
	EventLevel level;
	
	protected boolean ishandling;
	protected Object result;
	
	public Event(String name, EventType type, EventLevel level) {
		this.name = name; 
		this.type = type;
		this.level = level;
	}
	
	public abstract void onEvent();
	public abstract boolean handle();
	
	public EventLevel getLevel() {
		return this.level;
	}
	
	public EventType getType() {
		return this.type;
	}
	
	public boolean isHandled() {
		return ishandling;
	}

}