package leveleditor;

import java.util.ArrayList;

import engine.EntityManager;
import entities.Entity;
import loaders.LevelLoader;
import renderer.Renderer;
import scenes.Scene;

public class LevelTestScene extends Scene{
	
	ArrayList<Entity> game_objects;

	@Override
	public void init() {
		// TODO Auto-generated method stub
		game_objects = new ArrayList<>();
		LevelLoader.ready();
		game_objects = LevelLoader.loadToScene();
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
		if(main_camera!= null) {
			Entity current_camera = EntityManager.entities.get(main_camera);
			for(Entity entity: game_objects) {		
				if(!entity.isCamera()) {
					renderer.render(current_camera, entity);
				}
			}

		}
	}
	
	public void findCameras() {
		ArrayList<String> cameras = new ArrayList<>();
		for(Entity camera: game_objects) {
			if(camera.isCamera()) {
				String n_camera = camera.getName();
				if(!cameras.contains(n_camera)) {
					cameras.add(camera.getName());
				}	
			}
		}
		if(!cameras.isEmpty()) {
			main_camera = cameras.get(0);
		}
	}

}
