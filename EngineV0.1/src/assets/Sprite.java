package assets;

import java.util.ArrayList;
import java.util.List;

import annotations.ShowIfValueBool;
import annotations.SkipField;
import managers.AssetManager;

public class Sprite extends Asset {
		
	private String textureName = "white";
	
	private SpriteType shape = SpriteType.Square;
	
//	TileMap
	private boolean isTileMap;
	
	@ShowIfValueBool(fieldname = "isTileMap", value = true)
	private List<float[]> tilemap = new ArrayList<>();
	
	@SkipField
	@ShowIfValueBool(fieldname = "isTileMap", value = true)
	private int current_tile = 1;
	
	@ShowIfValueBool(fieldname = "isTileMap", value = true)
	private int
	number_of_tiles,
	tile_width = 16,
	tile_height = 16,
	spacing;

	/**
	 * Sprite
	 * 
	 * <p>
	 * used by SpriteRenderer Component
	 * 
	 */
	public Sprite() {
		super("Sprite", AssetType.Sprite);
		
	}

	/**
	 * Sprite
	 * 
	 * <p>
	 * used by SpriteRenderer Component
	 * 
	 * @param name
	 */
	public Sprite(String name) {
		super(name, AssetType.Sprite);
	}

	public String getTextureName() {
		return textureName;
	}

	public void setTextureName(String textureName) {
		this.textureName = textureName;
	}	

	public SpriteType getShape() {
		return shape;
	}

	public void setShape(SpriteType shape) {
		this.shape = shape;
	}

	public boolean isTilemap() {
		return isTileMap;
	}

	public void toggleTileMap() {
		this.isTileMap = !isTileMap;
		calculateTileMap();
	}
	
	public void toggleTileMap(boolean isTileMap) {
		this.isTileMap = isTileMap;
		calculateTileMap();
	}
	
	public float[] getFromTileMap(int index) {
	
		validateSprite();
		calculateTileMap();
		return tilemap.get(index);
	}
	
	public float[] getCurrentTile() {
		if(isTileMap) {
			validateSprite();
		}
		return tilemap.get(current_tile-1);
	}

	private void validateSprite() {
		if(number_of_tiles <= 0) number_of_tiles = 1;
		if(current_tile>number_of_tiles||current_tile<=0) current_tile = 1;
	}

	public List<float[]> getTilemap() {
		if(isTileMap) {
			validateSprite();
		}		
		return tilemap;
	}

	public void setTilemap(List<float[]> tilemap) {
		this.tilemap = tilemap;
	}

	public int getCurrentTileIndex() {
		return current_tile;
	}

	public void setCurrentTile(int current_tile) {
		this.current_tile = current_tile;
		calculateTileMap();
	}

	public int getNumberOfTiles() {
		return number_of_tiles;
	}

	public void setNumberOfTiles(int number_of_tiles) {
		this.number_of_tiles = number_of_tiles;
		calculateTileMap();
	}

	public int getTileWidth() {
		return tile_width;
	}

	public void setTileWidth(int tile_width) {
		this.tile_width = tile_width;
		calculateTileMap();
	}

	public int getTileHeight() {
		return tile_height;
	}

	public void setTileHeight(int tile_height) {
		this.tile_height = tile_height;
		calculateTileMap();
	}

	public int getTileSpacing() {
		return spacing;
	}

	public void setTileSpacing(int spacing) {
		this.spacing = spacing;
		calculateTileMap();
	}
	
	public void calculateTileMap() {
		
		this.tilemap = new ArrayList<>();
		
		if(this.isTileMap != true) return;
		
		if(number_of_tiles <= 0) {
			this.number_of_tiles = 1;
		}
		
		if(tile_width <= 0) {
			this.tile_width = 1;
		}
		if(tile_height <= 0) {
			this.tile_height = 1;
		}
				
		int imageWidth = AssetManager.getTexture(textureName).getWidth();
		int imageHeight= AssetManager.getTexture(textureName).getHeight();
		
		//=================================
		// Get All Sprites From SpriteSheet
		//=================================
		
		//Start From First Row
		int currentX = 0;
		int currentY = 0;
		
		for(int i=0; i < number_of_tiles; i++) {
			float topY = (currentY + tile_width) / (float)imageHeight;
			float rightX = (currentX + tile_height) / (float)imageWidth;
			float leftX = currentX / (float)imageWidth;
			float bottomY = currentY / (float)imageHeight;
			
			// Get Texture Cords
			float[] textureCoords = { 	leftX,	bottomY, 
										leftX,	topY,
										rightX,	topY, 	
										rightX,	bottomY};
			// Add To Array
			this.tilemap.add(textureCoords);
			
			currentX += tile_width + spacing;
			if(currentX >= imageWidth) {
				currentX = 0;
				currentY -= tile_height + spacing;
			}
		}
	}

	@Override
	public Sprite copy(Asset from) {
		if(from == null) return null;
		if(!(from instanceof Sprite)) return null;
		
		Sprite sprite = (Sprite) from;
		
		this.isTileMap = sprite.isTilemap();
		this.current_tile = sprite.getCurrentTileIndex();
		this.number_of_tiles = sprite.getNumberOfTiles();
		this.spacing = sprite.getTileSpacing();
		this.textureName = sprite.getTextureName();
		this.tile_height = sprite.getTileHeight();
		this.tile_width = sprite.getTileWidth();
		return this;
	}

	@Override
	public boolean equals(Object to) {
		
		if(to == null) return false;
		if(!(to instanceof Sprite)) return false;
		
		Sprite sprite = (Sprite) to;
		
		if(this.textureName != sprite.textureName) return false;
		
		if(this.isTileMap != sprite.isTileMap) return false;
		if(this.current_tile != sprite.current_tile) return false;
		if(this.number_of_tiles != sprite.number_of_tiles) return false;
		if(this.spacing != sprite.spacing) return false;
		if(this.tile_height != sprite.tile_height) return false;
		if(this.tile_width != sprite.tile_width) return false;
		
		return true;
	}	
}
