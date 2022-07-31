package entities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import components.CameraComponent;
import components.Component;
import components.LightingComponent;
import components.MeshRenderer;
import components.SpriteRenderer;

public class Entity {
	
	protected String name;
	protected transient boolean modified;
	
	protected List<Component> components = new ArrayList<Component>();
	
	public String getName(){
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public <C extends Component> C getComponent(Class<C> componentClass) {
		
		for(Component c: components) {
			if(componentClass.isAssignableFrom(c.getClass())) {
				try {
					return componentClass.cast(c);
				}catch(ClassCastException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public <C extends Component> void removeComponent(Class<C> componentClass) { 
		for(int i=0; i < components.size(); i++) {
			Component c = components.get(i);
			if(componentClass.isAssignableFrom(c.getClass())) {
				components.remove(i);
				return;
			}
		}
	}
	
	public void addComponent(Component c) {
		this.components.add(c);
		c.entity = this;
	}
	
	public List<Component> getComponents() {
		return this.components;
	}
	
	public Field[] getFields() {
		return this.getClass().getDeclaredFields();
	}
	
	public boolean ifModified() {
		return this.modified;
	}
	
	public void modified() {
		this.modified = true;
	}
	
	public void updated() {
		this.modified = false;
	}
	
	public void update(double dt) {
		for(int i=0; i < components.size(); i++) {
			components.get(i).update(dt);
		}
		this.updated();
	}
	
	public void start() {
		for(int i=0; i < components.size(); i++) {
			components.get(i).start();
		}
	}
		
	public void stop() {
		for(int i=0; i < components.size(); i++) {
			components.get(i).stop();
		}
	}
	
	public boolean isRenderable() {
		if(this.getComponent(SpriteRenderer.class) != null | this.getComponent(MeshRenderer.class) != null) {
			return true;
		}
		return false;
	}
	
	public boolean isCamera() {
		if(this.getComponent(CameraComponent.class) != null) {
			return true;
		}
		return false;
	}

	public boolean isLight() {
		if(this.getComponent(LightingComponent.class) != null) {
			return true;
		}
		return false;
	}
}
