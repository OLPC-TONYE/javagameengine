package assets.sprite;

import java.util.ArrayList;
import java.util.List;

import assets.Asset;
import assets.AssetType;
import managers.AssetManager;

public class Sprite extends Asset {
		
	String textureName = "white";
	
//	TileMap
	boolean isTileMap;
	List<float[]> tilemap = new ArrayList<>();
	int current_tile = 1;
	int number_of_tiles;
	int tile_width = 16;
	int tile_height = 16;
	int spacing;

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

	public List<float[]> getTilemap() {
		return tilemap;
	}

	public void setTilemap(List<float[]> tilemap) {
		this.tilemap = tilemap;
	}

	public int getCurrentTile() {
		return current_tile;
	}

	public void setCurrentTile(int current_tile) {
		this.current_tile = current_tile;
	}

	public int getNumberOfTiles() {
		return number_of_tiles;
	}

	public void setNumberOfTiles(int number_of_tiles) {
		this.number_of_tiles = number_of_tiles;
	}

	public int getTileWidth() {
		return tile_width;
	}

	public void setTileWidth(int tile_width) {
		this.tile_width = tile_width;
	}

	public int getTileHeight() {
		return tile_height;
	}

	public void setTileHeight(int tile_height) {
		this.tile_height = tile_height;
	}

	public int getTileSpacing() {
		return spacing;
	}

	public void setTileSpacing(int spacing) {
		this.spacing = spacing;
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
	public void copy(Asset from) {
		if(from == null) return;
		if(!(from instanceof Sprite)) return;
		
		Sprite sprite = (Sprite) from;
		
		this.isTileMap = sprite.isTilemap();
		this.number_of_tiles = sprite.getNumberOfTiles();
		this.spacing = sprite.getTileSpacing();
		this.textureName = sprite.getTextureName();
		this.tile_height = sprite.getTileHeight();
		this.tile_width = sprite.getTileWidth();
	}	
}
