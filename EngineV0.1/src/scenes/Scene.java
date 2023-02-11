package scenes;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import renderer.Renderer;

public abstract class Scene {
	
	public String title;
	
	public Entity primaryCamera;
	
	public boolean useSceneLights = false;
	
	public List<Entity> entities = new ArrayList<>();
		
	public abstract void init();
	public abstract void close();
	public abstract void update(double dt);
	public abstract void render(Renderer renderer);
	
	public void setCamera(Entity camera) {
		if(camera != null) {
			primaryCamera = camera;
		}
	}

	protected void addToRenderList(Entity entity) {
		if(entity != null) {
			entities.add(entity);
		}
	}
	
}
