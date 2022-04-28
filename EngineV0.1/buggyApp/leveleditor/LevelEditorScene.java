package leveleditor;

import entities.Entity;
import managers.EntityManager;
import renderer.Renderer;
import scenes.Scene;

public class LevelEditorScene extends Scene{
			
	@Override
	public void init() {
		useAmbient = true;
	}

	@Override
	public void update(double dt) {		
		for(Entity entities: EntityManager.world_entities.values()) {
			entities.update(dt);
		}
		
	}
		
	@Override
	public void render(Renderer renderer) {

		if(primaryCamera!= null) {
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
