package managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import entities.Entity;
import tools.Dictionary;

public class EntityManager {
	
	enum EntityType {
		SPRITE("2D Object"),
		CUBE("3D Object");
		
		String objecttype;
		
		EntityType(String objecttype){
			this.objecttype = objecttype;
		}

	}

	public static Dictionary<String, Integer, Entity, ArrayList<String>> world = new Dictionary<>();
	
	public static boolean isParentOf(String nameOfEntity, String nameOfChild) {
		List<String> children = world.getFromThirdMap(nameOfEntity);
		if(children.contains(nameOfChild)) {
			return true;
		}
		return false;
	}
	
	public static boolean isChildOf(String nameOfEntity, String nameOfParent) {
		List<String> children = world.getFromThirdMap(nameOfParent);
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
		List<String> children = world.getFromThirdMap(nameOfEntity);
		if(children.size()>0) {
			return true;
		}
		return false;
	}
	
	public static boolean hasParent(String nameOfEntity) {
		for(String parent: world.keySet()) {
			if(parent == nameOfEntity) {
				continue;
			}
			
			if(!hasChildren(parent)) {
				continue;
			}
			
			List<String> children = world.getFromThirdMap(parent);
			
			if(children.contains(nameOfEntity)) {
				return true;
			}
		}
		return false;
	}
	
	public static List<String> getChildrenOf(String nameOfEntity) {
		return world.getFromThirdMap(nameOfEntity);
	}
	
	public static String getParentOf(String nameOfEntity) {
		for(String parent: world.keySet()) {
			if(parent == nameOfEntity) {
				continue;
			}
			
			if(!hasChildren(parent)) {
				continue;
			}
			
			List<String> children = world.getFromThirdMap(parent);
			
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
		for(String root: world.keySet()) {
			if(!hasParent(root)) {
				System.out.print(root+" - ");
				showChildren(root);
				System.out.print("\n");
			}
		}
		System.out.println(world.getThirdMap());
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
		while(world.containsKey(newName)) {
			count++;
			newName = name+" "+count;
		}
		return newName;
	}
	
	private static int getAvailIdentifier() {
		int entityId = 1;
		while(world.getFirstMap().containsValue(entityId)) {
			entityId++;
		}
		return entityId;
	}
	
	public static boolean add(Entity entity) {
		String entityName = getAvailName(entity.getName());
		if(!world.containsKey(entityName)) {
			addEntity(entity, entityName);
			return true;
		}
		return false;
	}
	
	public static boolean add(Entity entity, String name) {
		String entityName = getAvailName(name);
		if(!world.containsKey(entityName)) {
			entity.setName(entityName);
			addEntity(entity, entityName);
			return true;
		}
		return false;
	}
	
	private static void addEntity(Entity entity, String entityName) {
		world.put(entityName, getAvailIdentifier(), entity, new ArrayList<>());
	}
	
	public static boolean remove(String nameOfEntity) {
		if(world.containsKey(nameOfEntity)) {
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
		world.remove(nameOfEntity);
	}
	
	public static Entity getEntityById(int entityId) {
		for(Entry<String, Integer> entry: world.getFirstMap().entrySet()) {
			
			int id = entry.getValue();
			if(id == entityId) {
				return world.getFromSecondMap(entry.getKey());
			}
		}
		return null;
	}
	
	public static Collection<Entity> getWorldEntities() {
		
		return world.getSecondMap().values();
	}
	
	public static int getId(String entityname) {
		if(world.containsKey(entityname)) {
			return world.getFromFirstMap(entityname);
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

	public static void reset() {
		world.clear();
	}
}
