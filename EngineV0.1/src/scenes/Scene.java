package scenes;

import java.util.ArrayList;

import engine.EntityManager;
import entities.Entity;
import renderer.Renderer;

public abstract class Scene {
	
	
	public String title;	
	public String n_mainCamera;
	public ArrayList<String> renderableEntities = new ArrayList<String>();
	public ArrayList<String> cameras = new ArrayList<String>();
		
	public abstract void init();
	public abstract void close();
		
	public abstract void update(double dt);
	public abstract void render(Renderer renderer);	
	
	public String currentCamera() {
		return n_mainCamera;	
	}
	
	public String[] getCamerasList() {
		return cameras.toArray(new String[cameras.size()]);
	}
	
	public void findCameras() {
		for(Entity camera:EntityManager.entities.values()) {
			if(camera.isCamera()) {
				String n_camera = camera.getName();
				if(!cameras.contains(n_camera)) {
					cameras.add(camera.getName());
				}	
			}
		}
		if(!cameras.isEmpty()) {
			n_mainCamera = cameras.get(0);
		}
	}
	
}
