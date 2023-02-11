package assets;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;

public class Scene extends Asset {
	
	public Entity primaryCamera;
	
	public List<Entity> entities = new ArrayList<>();
	
	/**
	 * 
	 * Scene Settings
	 * 
	 */
	public boolean useSceneLights = false;

	public Scene() {
		super("Scene", AssetType.Scene);
	}
	
	public Scene(String name) {
		super(name, AssetType.Scene);
	}
	
	/**
	 * @return the primaryCamera
	 */
	public Entity getPrimaryCamera() {
		return primaryCamera;
	}

	/**
	 * @param primaryCamera the primaryCamera to set
	 */
	public void setPrimaryCamera(Entity primaryCamera) {
		this.primaryCamera = primaryCamera;
	}

	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void addEntities(List<Entity> entities) {
		for(Entity entity: entities) {
			this.entities.add(entity);
		}
	}
	
	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	@Override
	public Scene copy(Asset from) {
		if(from == null) return null;
		if(!(from instanceof Scene)) return null;
		
		Scene scene = (Scene) from;
		
		this.primaryCamera = scene.getPrimaryCamera();
		this.entities = scene.getEntities();
		
		this.useSceneLights = scene.useSceneLights;
		
		return this;
	}

}
