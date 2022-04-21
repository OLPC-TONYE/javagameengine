package scenes;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import renderer.Renderer;

public abstract class Scene {
	
	public String title;
	
	public Entity main_camera;
	public Entity main_light;
	
	public List<Entity> renderList = new ArrayList<>();
		
	public abstract void init();
	public abstract void close();
	public abstract void update(double dt);
	public abstract void render(Renderer renderer);
	
	public void setCamera(Entity camera) {
		if(camera != null) {
			main_camera = camera;
		}
	}
	
	public void setMainLight(Entity light) {
		this.main_light = light;
	}
	
	public void addLight(Entity light) {
		
	}
	
	protected void addToRenderList(Entity entity) {
		if(entity != null) {
			renderList.add(entity);
		}
	}
	
}
