package entitiesComponents;

import engine.EngineManager;
import entities.Sprite;
import opengl.VertexArrayObject;

public class SpriteRenderer extends Component {

	private Sprite sprite;
	private VertexArrayObject mesh;
		
	public SpriteRenderer(Sprite sprite) {
		this.sprite = sprite;
	}
	
	@Override
	public void prepare() {
		if(entity.getComponent(TextureComponent.class)!= null) {
			TextureComponent texture = entity.getComponent(TextureComponent.class);
			texture.start();
			mesh = EngineManager.loadToVAO(EngineManager.ENGINE_SPRITE_SQUARE, EngineManager.ENGINE_SPRITE_SQUARE_INDICES, texture.getTextureCoords());
		}else {
			mesh = EngineManager.loadToVAO(EngineManager.ENGINE_SPRITE_SQUARE, EngineManager.ENGINE_SPRITE_SQUARE_INDICES, EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS);
		}
	}
	
	@Override
	public void update() {

		if(entity.ifModified()) {
			if(entity.getComponent(TextureComponent.class)!= null) {
				TextureComponent texture = entity.getComponent(TextureComponent.class);
				texture.update();
				mesh.modifyVertexBufferObject("textCords", texture.getTextureCoords());
			}
			
		}
		
	}
	
	public Sprite getSprite() {
		return this.sprite;
	}
	
	public VertexArrayObject getMesh() {
		return this.mesh;
	}
}
