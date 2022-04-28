package entitiesComponents;

import assets.Asset;
import assets.AssetType;
import assets.mesh.Mesh;
import assets.sprite.Sprite;
import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import imgui.type.ImInt;
import managers.AssetManager;
import managers.EngineManager;

public class SpriteRenderer extends Component {
	
	Mesh mesh;
	Sprite sprite;
	
	@Override
	public void prepare() {
		if(firstTimeStart) {

			if(sprite != null) {
				sprite.calculateTileMap();
				mesh.setTextureUVs(sprite.isTilemap() ? getCurrentTile(): EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS);
			}	
		}
		firstTimeStart = false;
	}
	
	@Override
	public void update(double dt) {

		if(entity.ifModified()) {
			
			if(sprite != null) {
				sprite.calculateTileMap();
				mesh.setTextureUVs(sprite.isTilemap() ? getCurrentTile(): EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS);
			}
			
			if(mesh != null)
				mesh.getVertexArray().modifyVertexBufferObject("textureCords", mesh.getTextureUVs());
		}
		
	}
	
	public int getTextureID() {
		return AssetManager.getTexture(sprite.getTextureName()).getTextureID();
	}
	
	public float[] getFromTilemap(int index) {
		if(index > sprite.getTilemap().size()-1) {
			index = 0;
			sprite.setCurrentTile(0);
		}
		return sprite.getTilemap().get(index);
	}
	
	public float[] getCurrentTile() {
		return getFromTilemap(sprite.getCurrentTile());
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public Mesh getMesh() {
		return this.mesh;
	}
	
	public String getTexture() {
		return sprite.getTextureName();
	}
	
	@Override
	public void UI() {
		// UI Settings
		//TODO: Move UI to 'leveleditor' package
		ImGui.pushID("Sprite:");
		ImGui.text("Sprite: ");
				
		ImGui.sameLine();
		
		{	// Mesh
			String previewValue = sprite != null ? sprite.getAssetName(): "" ;
			if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.NoArrowButton)) {
				ImGui.endCombo();
			}
			
		}
		
		if (ImGui.beginDragDropTarget()) {
            final Object payload = ImGui.acceptDragDropPayload("buggy_asset");
            if (payload != null && payload instanceof Asset) {
            	Asset asset = (Asset) payload;
            	if(asset.getAssetType() == AssetType.Sprite) {
            		if(sprite ==null) sprite = new Sprite();
            		sprite.copy((Sprite) asset);
            	}
            }
            ImGui.endDragDropTarget();
        }
		ImGui.popID();
		
		if(sprite != null) {
			
			ImGui.pushID("Mesh:");
			ImGui.text("Mesh: ");
					
			ImGui.sameLine();
			
			{	// Mesh
				String previewValue = mesh != null ? mesh.getAssetName(): "" ;
				if(ImGui.beginCombo("", previewValue, ImGuiComboFlags.NoArrowButton)) {
					ImGui.endCombo();
				}
				
			}
			
			if (ImGui.beginDragDropTarget()) {
	            final Object payload = ImGui.acceptDragDropPayload("buggy_asset");
	            if (payload != null && payload instanceof Asset) {
	            	Asset asset = (Asset) payload;
	            	if(asset.getAssetType() == AssetType.Mesh) {
	            		if(mesh == null) mesh = new Mesh(null, null, null, null);
	            		mesh.copy(asset);
	            	}
	            }
	            ImGui.endDragDropTarget();
	        }
			ImGui.popID();
			
			// Variables
			ImInt sprTotal = new ImInt(sprite.getNumberOfTiles());
			ImInt sprHeight = new ImInt(sprite.getTileHeight());
			ImInt sprWidth = new ImInt(sprite.getTileWidth());
			
			if(ImGui.checkbox("TileMap", sprite.isTilemap())) {
				sprite.toggleTileMap();
				this.entity.modified();
			}
			
			if (sprite.isTilemap()) {
				// 
				ImGui.pushID("Number of Tiles");
				ImGui.text("Number of Tiles:");
				ImGui.sameLine();
				if (ImGui.inputInt("", sprTotal)) {
					sprite.setNumberOfTiles(sprTotal.get());
					this.entity.modified();
				}
				ImGui.popID();
				
				ImGui.pushID("Tile Height");
				ImGui.text("Tile Height:");
				ImGui.sameLine();
				if (ImGui.inputInt("", sprHeight)) {
					sprite.setTileHeight(sprHeight.get());
					this.entity.modified();
				}
				ImGui.popID();
				
				ImGui.pushID("Tile Width");
				ImGui.text("Tile Width:");
				ImGui.sameLine();
				if (ImGui.inputInt("", sprWidth)) {
					sprite.setTileWidth(sprWidth.get());
					this.entity.modified();
				} 
				ImGui.popID();
				
				if(sprite.getNumberOfTiles() > 0) {
					ImGui.pushID("Tile Preview");
					ImGui.text("Preview: ");
					ImGui.sameLine();
					float[] tile_cords = getCurrentTile();
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
		            	
		            	for(int i=0; i < sprite.getNumberOfTiles(); i++) {
		            		
		            		float[] tile_cords1 = sprite.getTilemap().get(i);
		            		
		            		// Change ID For each icon
		            		ImGui.pushID(i);
		            		if(ImGui.imageButton(getTextureID(), 
		            				100, 100, tile_cords1[0], tile_cords1[1], tile_cords1[4], tile_cords1[5])) {
		            			sprite.setCurrentTile(i);
		            			this.entity.modified();
		            		}
		            		ImGui.popID();
		            		// Change ID For each icon -------------
		            		
		            		float lastPosX = ImGui.getItemRectMaxX();
		            		float nextPosX = lastPosX + ItemSpacingX + 100;
		            		
		            		if(i + 1 < sprite.getNumberOfTiles() && nextPosX < windowX2) {
		            			ImGui.sameLine();
		            		}
		            	}
		            	ImGui.endPopup();
		            	
		            }
					ImGui.popID();
				}
				
			}
		}
//		sprite
	}
	
}
