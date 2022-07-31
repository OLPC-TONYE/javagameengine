package components;

import entities.Entity;

public abstract class Component {
	
	public transient Entity entity = null;
	
	public transient boolean firstTimeStart = true;
	
	public abstract void prepare();
	public abstract void update(double dt);

	public void start() {
		prepare();
		this.firstTimeStart = false;
	}
	
	public void stop() {
		this.firstTimeStart = true;
	}

}
