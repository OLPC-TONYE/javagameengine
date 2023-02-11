package leveleditor;

import entities.Entity;
import managers.EntityManager;
import renderer.Renderer;
import scenes.Scene;

public class LevelEditorScene extends Scene{
			
	@Override
	public void init() {
		useSceneLights = false;
	}

	@Override
	public void update(double dt) {		
		for(Entity entities: EntityManager.getWorldEntities()) {
			entities.update(dt);
		}
		
	}
		
	@Override
	public void render(Renderer renderer) {
				
	}

	@Override
	public void close() {
	}

}
