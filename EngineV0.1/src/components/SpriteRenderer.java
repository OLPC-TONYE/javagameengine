package components;

import org.joml.Vector3f;

import annotations.ColourField;
import annotations.HideIfNull;
import annotations.SkipField;
import assets.Material;
import assets.Mesh;
import assets.Sprite;
import managers.AssetManager;
import managers.EngineManager;

public class SpriteRenderer extends Component {
	
	
	private Sprite sprite;
	
	@ColourField
	@HideIfNull(fieldName = "sprite")
	private Vector3f colour = new Vector3f(1);
	
	@SkipField
	private Mesh mesh;
	
	private Material spriteMaterial = AssetManager.defaultMeshMaterial;
	
	
	private transient SpriteRenderer lastState;
	
	
	@Override
	public void prepare() {
		if(firstTimeStart) {
			updateSprite();
			firstTimeStart = false;
			copyTo(lastState);
		}
		
	}

	private void updateSprite() {
		if(sprite != null) {
			
			determineSpriteShape();
			
			sprite.calculateTileMap();
			determineUVs();
		}
	}
	
	private void determineUVs() {
		if(sprite.isTilemap()) {
			mesh.setTextureUVs(sprite.getCurrentTile());
		}else {
			mesh.setTextureUVs(EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS);
		}
		mesh.update();
	}

	private void determineSpriteShape() {
		if(mesh == null) {
			switch(sprite.getShape()) {
			
				case Circle:
					break;
				case Square:
					this.mesh = new Mesh().copy(AssetManager.defaultSquareMesh);
					break;
				case Triangle:
					break;
				default:
					break;
				
			}
		}
	}
	
	@Override
	public void update(double dt) {
		
		// check if state has changed
		
		if(!this.equals(lastState)) {	
			modify();
			
			if(sprite != null) {		
				updateSprite();
			}			
		}		
	}
	
	public int getTextureID() {
		return AssetManager.getTexture(sprite.getTextureName()).getTextureID();
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	/**
	 * @return the spriteColour
	 */
	public Vector3f getSpriteColour() {
		return colour;
	}

	/**
	 * @param spriteColour the spriteColour to set
	 */
	public void setSpriteColour(Vector3f spriteColour) {
		this.colour = spriteColour;
	}

	public Mesh getMesh() {
		return this.mesh;
	}
	
	/**
	 * @return the spriteMaterial
	 */
	public Material getMaterial() {
		return spriteMaterial;
	}

	/**
	 * @param spriteMaterial the spriteMaterial to set
	 */
	public void setMaterial(Material spriteMaterial) {
		this.spriteMaterial = spriteMaterial;
	}

	public String getTexture() {
		return sprite.getTextureName();
	}
	
	public void copyTo(SpriteRenderer to) {
		to = new SpriteRenderer();
		to.mesh = this.mesh;
		to.colour = this.colour;
		to.sprite = this.sprite;
		to.spriteMaterial = this.spriteMaterial;
	}
	
	public void modify() {
		this.lastState = new SpriteRenderer();
		lastState.mesh = new Mesh().copy(mesh);
		lastState.colour.set(this.colour);
		lastState.sprite = new Sprite().copy(this.sprite);
		lastState.spriteMaterial = this.spriteMaterial;
	}

	@Override
	public boolean equals(Object to) {
		
		if(to == null) return false;
		if(!(to instanceof SpriteRenderer)) return false;
		
		SpriteRenderer component = (SpriteRenderer) to;
		
		Sprite sprite = component.getSprite();
		
		if(this.sprite != null) {
			if(sprite == null) return false;
			if(!sprite.equals(this.getSprite())) return false;
		}
		
		return true;		
	}
	
}
