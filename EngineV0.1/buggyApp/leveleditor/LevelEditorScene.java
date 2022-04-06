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
		for(Entity entities: EntityManager.world_entities.values()) {
			entities.update(dt);
		}
		
	}
		
	@Override
	public void render(Renderer renderer) {

		if(main_camera!= null) {
			for(Entity entityRenderable: EntityManager.world_entities.values()) {		
				addToRenderList(entityRenderable);
			}
			renderer.render(this);
			renderList.clear();
		}
				
	}

	@Override
	public void close() {
	}

}
