package entitiesComponents;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.joml.Vector3f;

import entities.Entity;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;


public abstract class Component {
	
	public transient Entity entity = null;
	
	public boolean firstTimeStart = true;
	
	public abstract void prepare();
	public abstract void update(double dt);

	public void start() {
		prepare();
		this.firstTimeStart = false;
	}
	
	public void stop() {
		this.firstTimeStart = true;
	}

	public void UI() {
		try {
			for(Field field: this.getClass().getDeclaredFields()) {
				
				boolean isTransient  = Modifier.isTransient(field.getModifiers());
	        	boolean isPrivate = Modifier.isPrivate(field.getModifiers());
	        	boolean isProtected = Modifier.isProtected(field.getModifiers());
	        	
	        	if(isPrivate | isProtected) {
	        		field.setAccessible(true);
	        	}
	        	
	        	if(isTransient) {
	        		continue;
	        	}
				
				Class<?> type = field.getType();
	        	String name = field.getName();
				Object value = field.get(this);
						
//            	===== ColourPicker UI ============================
            	if(name.contains("colour")) {
            		Vector3f val = (Vector3f)value;
            		float[] imFloat = {val.x, val.y, val.z};
            		if(ImGui.colorEdit3(name, imFloat, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop)) {
            			val.set(imFloat);
            		}
            	}
            	
//				===== Integers =================================
            	else if(type == int.class) {
            		int val = (int)value;
            		int[] imInt = {val};
            		if(ImGui.dragInt(name, imInt)) {
            			field.set(this, imInt[0]);
            		}
            	}
            	
//            	===== Vector3f ==================================
            	else if(type == Vector3f.class) {
            		Vector3f val = (Vector3f)value;
            		float[] imFloat = {val.x, val.y, val.z};
            		if(ImGui.inputFloat3(name, imFloat)) {
            			val.set(imFloat);
            		}
            	}
				
				
				if(isPrivate| isProtected) {
	        		field.setAccessible(false);
	        	}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
