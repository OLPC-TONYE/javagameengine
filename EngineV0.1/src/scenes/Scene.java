package scenes;

import renderer.Renderer;

public abstract class Scene {
	
	
	public String title;	
	public String main_camera;
		
	public abstract void init();
	public abstract void close();
	public abstract void update(double dt);
	public abstract void render(Renderer renderer);
	
	public void setCamera(String camera) {
		if(!camera.isEmpty()) {
			main_camera = camera;
		}
	}
	
}
