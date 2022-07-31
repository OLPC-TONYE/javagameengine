package leveleditor;

import java.util.ArrayList;

import org.joml.Vector4f;

import components.CameraComponent;
import entities.Entity;
import managers.EntityManager;
import renderer.Renderer;
import scenes.Scene;
import scenes.SceneLoader;

public class LevelTestScene extends Scene{
	
	ArrayList<Entity> game_objects;

	@Override
	public void init() {
		game_objects = new ArrayList<>();
		SceneLoader.ready();
		game_objects = SceneLoader.loadToScene();
	}
	
	@Override
	public void close() {
		game_objects.clear();
	}

	@Override
	public void update(double dt) {
		for(Entity entities: game_objects) {
			entities.update(dt);
		}
	}

	@Override
	public void render(Renderer renderer) {
		renderer.clear();
		renderer.clearColour(0, 0, 0, 0.4f);
		if(primaryCamera!= null) {
			Vector4f colour = primaryCamera.getComponent(CameraComponent.class).getClearColour();
			renderer.clear();
			renderer.clearColour(colour);
			for(Entity entity: game_objects) {		
				if(!entity.isCamera() && !entity.isLight()) {
					addToRenderList(entity);
				}
				if(entity.isLight()) {
					addLight(entity);
				}
			}
			renderer.render(this);
			renderList.clear();
			lights.clear();
		}
	}
	
	public void findCameras() {
		ArrayList<String> cameras = new ArrayList<>();
		for(Entity camera: game_objects) {
			if(camera.isCamera()) {
				if(!cameras.contains(camera.getName())) {
					cameras.add(camera.getName());
				}	
			}
		}
		if(!cameras.isEmpty()) {
			primaryCamera = EntityManager.world.getSecondMap().get(cameras.get(0));
		}
	}

}
