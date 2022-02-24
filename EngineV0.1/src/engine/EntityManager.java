package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import entities.Entity;

public class EntityManager {
	
	enum EntityType {
		SPRITE("Object2D"),
		CUBE("Object3D");
		
		String objecttype;
		
		EntityType(String objecttype){
			this.objecttype = objecttype;
		}

	}

	public static Map<String, Entity> entities = new HashMap<>();
	
	
	public static String createEntity(String name) {
		String newName = name;
		int i = 0;
		while(entities.containsKey(newName)) {
			i++;
			newName = newName+" "+i;
		}
		return newName;		
	}

	public static String[] getEntityTypes() {
		ArrayList<String> all = new ArrayList<>();
		for(EntityType type: EntityType.values()) {
			all.add(type.name());
		}
		return all.toArray(new String[all.size()]);
	}
}
