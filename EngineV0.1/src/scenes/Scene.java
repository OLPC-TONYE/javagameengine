package scenes;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import renderer.Renderer;

public abstract class Scene {
	
	public String title;
	
	public Entity primaryCamera;
	
	public boolean useAmbient = true;
	
	public List<Entity> lights = new ArrayList<>();
	public List<Entity> cameras = new ArrayList<>();
	
	public List<Entity> renderList = new ArrayList<>();
		
	public abstract void init();
	public abstract void close();
	public abstract void update(double dt);
	public abstract void render(Renderer renderer);
	
	public void setCamera(Entity camera) {
		if(camera != null) {
			primaryCamera = camera;
		}
	}
	
	public void addLight(Entity light) {
		if(light.isLight()) {
			lights.add(light);
		}
	}
	
	protected void addToRenderList(Entity entity) {
		if(entity != null) {
			renderList.add(entity);
		}
	}
	
}
