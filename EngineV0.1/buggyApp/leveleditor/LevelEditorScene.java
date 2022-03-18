package leveleditor;

import engine.EntityManager;
import entities.Entity;
import renderer.Renderer;
import scenes.Scene;

public class LevelEditorScene extends Scene{
			
	@Override
	public void init() {
		
	}

	@Override
	public void update(double dt) {		
		for(Entity entities: EntityManager.entities.values()) {
			entities.update(dt);
		}
		
	}
	
	@Override
	public void render(Renderer renderer) {

		if(main_camera!= null) {
			Entity current_camera = EntityManager.entities.get(main_camera);
			for(Entity entityRenderable: EntityManager.entities.values()) {		
				if(!entityRenderable.isCamera()) {
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
