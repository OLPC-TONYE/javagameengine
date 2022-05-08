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
		for(Entity entities: EntityManager.getWorldEntities()) {
			entities.update(dt);
		}
		
	}
		
	@Override
	public void render(Renderer renderer) {

		if(primaryCamera!= null) {
			for(Entity entity: EntityManager.getWorldEntities()) {		
				if(!entity.isCamera() && !entity.isLight()) {
					addToRenderList(entity);
				}
				if(entity.isLight()) {
					if(!useAmbient) addLight(entity);
				}
			}
			renderer.render(this);
			renderList.clear();
			lights.clear();
		}
				
	}

	@Override
	public void close() {
	}

}
