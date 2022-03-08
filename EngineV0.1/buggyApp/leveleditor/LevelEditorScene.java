package leveleditor;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.EntityManager;
import entities.Entity;
import entitiesComponents.Transform;
import listeners.KeyListener;
import renderer.Renderer;
import scenes.Scene;

public class LevelEditorScene extends Scene{
			
	@Override
	public void init() {
		
	}

	@Override
	public void update(double dt) {		
		for(Entity entities: EntityManager.entities.values()) {
			entities.update();
		}
		
		Transform camera = EntityManager.entities.get(n_mainCamera).getComponent(Transform.class);
		float rate = (float) (5 * dt);
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_W)) {
			Vector3f position = camera.getPosition();
			camera.setPosition(position.add(0, rate, 0));
		}
		
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_S)) {
			Vector3f position = camera.getPosition();
			camera.setPosition(position.sub(0, rate, 0));
		}
		
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) {
			Vector3f position = camera.getPosition();
			camera.setPosition(position.add(rate, 0, 0));
		}
		
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_A)) {
			Vector3f position = camera.getPosition();
			camera.setPosition(position.sub(rate, 0, 0));
		}
		
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
			Vector3f position = camera.getRotation();
			camera.setRotation(position.add(0, rate, 0));
		}
		
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
			Vector3f position = camera.getRotation();
			camera.setRotation(position.sub(0, rate, 0));
		}
		
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
			Vector3f position = camera.getPosition();
			camera.setPosition(position.add(0, 0, rate));
		}
		
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_UP)) {
			Vector3f position = camera.getPosition();
			camera.setPosition(position.sub(0, 0, rate));
		}
		
	}
	
	@Override
	public void render(Renderer renderer) {

		if(n_mainCamera!= null) {
			Entity current_camera = EntityManager.entities.get(n_mainCamera);
			for(Entity entityRenderable: EntityManager.entities.values()) {
				
				if(entityRenderable.isRenderable()) {
					renderer.render(current_camera, entityRenderable);
				}
				
			}

		}
		
	}

	@Override
	public void close() {
//		LevelLoader.save();
	}
	
	

}
