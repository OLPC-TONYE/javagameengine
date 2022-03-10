package entitiesComponents;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import engine.EngineManager;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImInt;
import opengl.VertexArrayObject;

public class SpriteRenderer extends Component {

	private Vector3f colour;
	private VertexArrayObject mesh;
	
	private String textureName;
	private float[] textureCords;
	
//	TileMap
	private boolean isTilemap;
	private List<float[]> tilemap;
	private int number_of_tiles;
	private int current_tile;
	private int tile_width;
	private int tile_height;
	private int spacing;
		
	public SpriteRenderer(Vector3f colour) {
		this.colour = colour;
	}
	
	public SpriteRenderer(String texture) {
		this.textureName = texture;
		this.colour = new Vector3f(1,1,1);
	}
	
	@Override
	public void prepare() {
		if(firstTimeStart) {
			if(colour == null) {
				colour = new Vector3f(1,1,1);
			}
			
			if(isTilemap) {
				calculateTileMap(tile_width, tile_height, number_of_tiles, spacing);
				this.textureCords = getFromTilemap(current_tile);
			}else {
				this.textureCords = EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS;
			}
			
			mesh = EngineManager.loadToVAO(EngineManager.ENGINE_SPRITE_SQUARE, EngineManager.ENGINE_SPRITE_SQUARE_INDICES, textureCords);
			
		}
		firstTimeStart = false;
	}
	
	@Override
	public void update() {

		if(entity.ifModified()) {
			if(isTilemap) {
				calculateTileMap(tile_width, tile_height, number_of_tiles, spacing);
				this.textureCords = getFromTilemap(current_tile);
			}else {
				this.textureCords = EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS;
			}
			mesh.modifyVertexBufferObject("textureCords", textureCords);
		}
		
	}
	
	public int getTextureID() {
		return EngineManager.getTexture(textureName).getTextureID();
	}
	
	public float[] getFromTilemap(int index) {
		if(index > tilemap.size()-1) {
			index = 0;
			this.current_tile = 0;
		}
		return this.tilemap.get(index);
	}
	
	public Vector3f getColour() {
		return this.colour;
	}
	
	public void toggleSpritesheet() {
		this.isTilemap = !isTilemap;
	}

	public VertexArrayObject getMesh() {
		return this.mesh;
	}
	
	public void calculateTileMap(int tileWidth, int tileHeight, int num, int spacing) {
		
		this.tilemap = new ArrayList<>();
		this.number_of_tiles = num;
		this.isTilemap = true;
		
		this.tile_width = tileWidth;
		this.tile_height = tileHeight;
		this.spacing = spacing;
		
		if(num <= 0) {
			this.number_of_tiles = 1;
		}
		
		if(tileWidth <= 0) {
			this.tile_width = 1;
		}
		if(tileHeight <= 0) {
			this.tile_height = 1;
		}
				
		int imageWidth = EngineManager.getTexture(textureName).getWidth();
		int imageHeight= EngineManager.getTexture(textureName).getHeight();
		
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
	public void UI() {
		// UI Settings
		Vector3f val = colour;
		float[] imFloat = {val.x, val.y, val.z};
		if(ImGui.colorEdit3("Colour", imFloat, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop)) {
			this.colour.set(imFloat);
		}
		
		// Variables
		ImInt sprTotal = new ImInt(this.number_of_tiles);
		ImInt sprHeight = new ImInt(this.tile_height);
		ImInt sprWidth = new ImInt(this.tile_width);
		
		if(ImGui.checkbox("TileMap", isTilemap)) {
			toggleSpritesheet();
			this.entity.modified();
		}
		
		if (isTilemap) {
			// 
			ImGui.pushID("Number of Tiles");
			ImGui.text("Number of Tiles:");
			ImGui.sameLine();
			if (ImGui.inputInt("", sprTotal)) {
				this.number_of_tiles = sprTotal.get();
				this.entity.modified();
			}
			ImGui.popID();
			
			ImGui.pushID("Tile Height");
			ImGui.text("Tile Height:");
			ImGui.sameLine();
			if (ImGui.inputInt("", sprHeight)) {
				this.tile_height = sprHeight.get();
				this.entity.modified();
			}
			ImGui.popID();
			
			ImGui.pushID("Tile Width");
			ImGui.text("Tile Width:");
			ImGui.sameLine();
			if (ImGui.inputInt("", sprWidth)) {
				this.tile_width = sprWidth.get();
				this.entity.modified();
			} 
			ImGui.popID();
			
			if(number_of_tiles > 0) {
				ImGui.pushID("Tile Preview");
				ImGui.text("Preview: ");
				ImGui.sameLine();
				float[] tile_cords = tilemap.get(current_tile);
				if(ImGui.imageButton(getTextureID(), 
						30, 30, tile_cords[0], tile_cords[1], tile_cords[4], tile_cords[5])) {
	            	ImGui.openPopup("Tilemap");
	            }
	    		ImGui.setNextWindowSize(500, 240);
	            if(ImGui.beginPopup("Tilemap")) {
	            	
	            	//--------------------------------------
	            	if(ImGui.button("X")) {
	            		ImGui.closeCurrentPopup();
	            	}
	            	// 
	            	float WindowPosX = ImGui.getWindowPosX();
	            	float WindowSizeX = ImGui.getWindowSizeX();
	            	float ItemSpacingX = ImGui.getStyle().getItemSpacingX();
	            	float windowX2 = WindowPosX + WindowSizeX;
	            	
	            	for(int i=0; i < number_of_tiles; i++) {
	            		
	            		float[] tile_cords1 = tilemap.get(i);
	            		
	            		// Change ID For each icon
	            		ImGui.pushID(i);
	            		if(ImGui.imageButton(getTextureID(), 
	            				100, 100, tile_cords1[0], tile_cords1[1], tile_cords1[4], tile_cords1[5])) {
	            			this.current_tile = i;
	            			this.entity.modified();
	            		}
	            		ImGui.popID();
	            		// Change ID For each icon -------------
	            		
	            		float lastPosX = ImGui.getItemRectMaxX();
	            		float nextPosX = lastPosX + ItemSpacingX + 100;
	            		
	            		if(i + 1 < number_of_tiles && nextPosX < windowX2) {
	            			ImGui.sameLine();
	            		}
	            	}
	            	ImGui.endPopup();
	            	
	            }
				ImGui.popID();
			}
			
		}

//        	
//        } else {
//			
//			ImGui.imageButton(Texture.getTextureID(), 100, 100, 0, 0, 1, 1);                	
//			
//		}
		
	}

	public String getTexture() {
		return this.textureName;
	}
}
