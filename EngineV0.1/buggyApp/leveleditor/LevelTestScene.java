package leveleditor;

import java.util.ArrayList;

import engine.EntityManager;
import entities.Entity;
import entitiesComponents.CameraComponent;
import renderer.Renderer;
import scenes.Scene;
import scenes.SceneLoader;

public class LevelTestScene extends Scene{
	
	ArrayList<Entity> game_objects;

	@Override
	public void init() {
		// TODO Auto-generated method stub
		game_objects = new ArrayList<>();
		SceneLoader.ready();
		game_objects = SceneLoader.loadToScene();
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
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
		if(primaryCamera!= null) {
			float[] colour = primaryCamera.getComponent(CameraComponent.class).getClearColour();
			renderer.clear();
			renderer.clearColour(colour[0], colour[1], colour[2], colour[3]);
			for(Entity entityRenderable: game_objects) {		
				if(!entityRenderable.isCamera()) {
					addToRenderList(entityRenderable);
				}
			}
			renderer.render(this);
			renderList.clear();
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
			primaryCamera = EntityManager.world_entities.get(cameras.get(0));
		}
	}

}
