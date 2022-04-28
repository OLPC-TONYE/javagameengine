package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Entity;

public class EntityManager {
	
	enum EntityType {
		SPRITE("2D Object"),
		CUBE("3D Object");
		
		String objecttype;
		
		EntityType(String objecttype){
			this.objecttype = objecttype;
		}

	}

	public static Map<String, Entity> world_entities = new HashMap<>();
	public static Map<String, Integer> ids = new HashMap<>();
	
	public static Map<String, ArrayList<String>> hierarchy = new HashMap<>();
	
	public static boolean isParentOf(String nameOfEntity, String nameOfChild) {
		List<String> children = hierarchy.get(nameOfEntity);
		if(children.contains(nameOfChild)) {
			return true;
		}
		return false;
	}
	
	public static boolean isChildOf(String nameOfEntity, String nameOfParent) {
		List<String> children = hierarchy.get(nameOfParent);
		if(children.contains(nameOfEntity)) {
			return true;
		}
		return false;
	}
	
	public static boolean isAbove(String nameOfParent, String nameOfEntity) {		
		if(hasParent(nameOfEntity)) {
			String parent = getParentOf(nameOfEntity);
			if(parent == nameOfParent) {
				return true;
			}	
			if(hasParent(parent)) {
				return isAbove(nameOfParent, parent);
			}
		}
		return false;
	}
	
	public static boolean hasChildren(String nameOfEntity) {
		List<String> children = hierarchy.get(nameOfEntity);
		if(children.size()>0) {
			return true;
		}
		return false;
	}
	
	public static boolean hasParent(String nameOfEntity) {
		for(String parent: hierarchy.keySet()) {
			if(parent == nameOfEntity) {
				continue;
			}
			
			if(!hasChildren(parent)) {
				continue;
			}
			
			List<String> children = hierarchy.get(parent);
			
			if(children.contains(nameOfEntity)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<String> getChildrenOf(String nameOfEntity) {
		return hierarchy.get(nameOfEntity);
	}
	
	public static String getParentOf(String nameOfEntity) {
		for(String parent: hierarchy.keySet()) {
			if(parent == nameOfEntity) {
				continue;
			}
			
			if(!hasChildren(parent)) {
				continue;
			}
			
			List<String> children = hierarchy.get(parent);
			
			if(children.contains(nameOfEntity)) {
				return parent;
			}
		}
		return "";
	}
	
	public static void makeChildOf(String nameOfEntity, String nameOfParent) {
		if(nameOfEntity != nameOfParent) {
			if(hasParent(nameOfEntity)) {
				getChildrenOf(getParentOf(nameOfEntity)).remove(nameOfEntity);
			}
			getChildrenOf(nameOfParent).add(nameOfEntity);
		}		
	}
	
	public static void moveToRoot(String nameOfEntity) {
		if(hasParent(nameOfEntity)) {
			getChildrenOf(getParentOf(nameOfEntity)).remove(nameOfEntity);
		}
	}
	
	public static void moveUpHierarchy(String nameOfEntity) {
		String parent = getParentOf(nameOfEntity);
		if(hasParent(parent)) {
			String parent_parent = getParentOf(parent);
			getChildrenOf(parent_parent).add(nameOfEntity);
		}else {
			getChildrenOf(parent).remove(nameOfEntity);
		}
	}
	
	public static void main(String[] args) {
		add(new Entity(), "Balls");
		add(new Entity(), "Square");
		add(new Entity(), "Circle");
		add(new Entity(), "Box");
		add(new Entity(), "Ball 1");
		add(new Entity(), "Ball 2");
		makeChildOf("Ball 1", "Balls");
		makeChildOf("Ball 2", "Balls");
		makeChildOf("Balls", "Circle");
		makeChildOf("Box", "Square");
		showTree();
		System.out.println(isAbove("Circle", "Ball 1"));
		moveUpHierarchy("Box");
		showTree();
	}
	
	public static void showTree() {
		for(String root: hierarchy.keySet()) {
			if(!hasParent(root)) {
				System.out.print(root+" - ");
				showChildren(root);
				System.out.print("\n");
			}
		}
		System.out.println(hierarchy);
	}
	
	public static void showChildren(String parent) {
		
		if(hasChildren(parent)) {
			System.out.print(" (");
			for(String child: getChildrenOf(parent)) {
				System.out.print(child+", ");
				showChildren(child);
			}
			System.out.print(")");
		}
		
	}
	
	private static String getAvailName(String name) {
		String newName = name;
		int count = 0;
		while(world_entities.containsKey(newName)) {
			count++;
			newName = name+" "+count;
		}
		return newName;
	}
	
	private static int getAvailIdentifier() {
		int entityId = 1;
		while(ids.containsValue(entityId)) {
			entityId++;
		}
		return entityId;
	}
	
	public static boolean add(Entity entity) {
		String entityName = getAvailName(entity.getName());
		if(!world_entities.containsKey(entityName)) {
			addEntity(entity, entityName);
			return true;
		}
		return false;
	}
	
	public static boolean add(Entity entity, String name) {
		String entityName = getAvailName(name);
		if(!world_entities.containsKey(entityName)) {
			entity.setName(entityName);
			addEntity(entity, entityName);
			return true;
		}
		return false;
	}
	
	private static void addEntity(Entity entity, String entityName) {
		world_entities.put(entityName, entity);
		ids.put(entityName, getAvailIdentifier());
		hierarchy.put(entityName, new ArrayList<>());
	}
	
	public static boolean remove(String nameOfEntity) {
		if(world_entities.containsKey(nameOfEntity)) {
			if(hasChildren(nameOfEntity)) {
				List<String> children = getChildrenOf(nameOfEntity);
				for(int i=0; i<children.size();i++) {
					remove(children.get(i));
				}
			}
			if(hasParent(nameOfEntity)) {
				getChildrenOf(getParentOf(nameOfEntity)).remove(nameOfEntity);
			}
			removeEntity(nameOfEntity);
			return true;
		}
		return false;
	}
	
	private static void removeEntity(String nameOfEntity) {
		world_entities.remove(nameOfEntity);
		ids.remove(nameOfEntity);
		hierarchy.remove(nameOfEntity);
	}
	
	public static int getId(String entityname) {
		if(world_entities.containsKey(entityname)) {
			return ids.get(entityname);
		}
		return 0;
	}
	
	public static String[] getEntityTypes() {
		ArrayList<String> all = new ArrayList<>();
		for(EntityType type: EntityType.values()) {
			all.add(type.name());
		}
		return all.toArray(new String[all.size()]);
	}
}
