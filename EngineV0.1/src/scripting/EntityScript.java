package scripting;

import components.Component;
import entities.Entity;

public abstract class EntityScript {
	
	protected Entity entity;
	
	public <C extends Component> C getComponent(Class<C> c) {
		return entity.getComponent(c);
	}
	
	public abstract void onCreate();
	public abstract void onDestroy();
	public abstract void update(double dt);
	
	public void bindToEntity(Entity entity) {
		this.entity = entity;
	}
}
