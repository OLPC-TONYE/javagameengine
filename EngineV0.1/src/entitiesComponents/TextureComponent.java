package entitiesComponents;

import java.util.ArrayList;
import java.util.List;

import engine.EngineManager;
import imgui.ImGui;
import imgui.type.ImInt;

public class TextureComponent extends Component{
	
	private int textureID;
	private String textureName;
	
	private float[] textureCords;
	
//	Sprite sheet
	private boolean isSpritesheet;
	
//	TileMap Properties
	private List<float[]> spritesheet;
	public int numberOfSprites;
	public int currentSprite;
	private int spriteWidth;
	private int spriteHeight;
	private int spriteSpacing;
	
	public TextureComponent(String textureName) {
		this.textureName = textureName;	
	}
	
	@Override
	public void prepare() {
		if(firstTimeStart) {
			this.textureID = EngineManager.getTexture(textureName).getTextureID();
			if(isSpritesheet) {
				this.textureCords = this.spritesheet.get(currentSprite);
			}else {
				this.textureCords = EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS;
			}
		}
		firstTimeStart = false;
	}
	
	@Override
	public void update() {
		if(entity.ifModified()) {
			if(isSpritesheet) {
				initSpriteSheet(this.spriteWidth, this.spriteHeight, this.numberOfSprites, this.spriteSpacing);
				this.textureCords = this.spritesheet.get(currentSprite);
			}else {
				this.textureCords = EngineManager.ENGINE_SPRITE_SQUARE_TEXTURECOORDS;
			}
		}	
	}
	
	public int getTextureID() {
		return this.textureID;
	}
	
	public float[] getTextureCoords() {
		return this.textureCords;
	}
	
	public boolean isSpritesheet() {
		return this.isSpritesheet;
	}
	
	public void toggleSpritesheet() {
		this.isSpritesheet = !isSpritesheet;
	}

	public void getSpriteFromSheet(int spriteIndex) {
		if(spritesheet!=null) {
			if(this.currentSprite != spriteIndex) {
				if(spriteIndex >= numberOfSprites) {
					this.currentSprite = 0;
				}else {
					this.currentSprite = spriteIndex;
				} 
				this.textureCords = spritesheet.get(currentSprite);
			}
		}else {
			System.out.println("Texture Component does not contain spritesheet");
		}
		entity.modified();
	}
	
	@Override
	public void UI() {
		// UI Settings
		
		// Variables
		ImInt sprTotal = new ImInt(this.numberOfSprites);
		ImInt sprHeight = new ImInt(this.spriteHeight);
		ImInt sprWidth = new ImInt(this.spriteWidth);
		
			if (isSpritesheet) {
				// 
				ImGui.pushID("Number of Sprites");
				ImGui.text("Number of Sprites:");
				ImGui.sameLine();
				if (ImGui.inputInt("", sprTotal)) {
					this.numberOfSprites = sprTotal.get();
					this.entity.modified();
				}
				ImGui.popID();
				ImGui.pushID("Sprites Height");
				ImGui.text("Sprites Height:");
				ImGui.sameLine();
				if (ImGui.inputInt("", sprHeight)) {
					this.spriteHeight = sprHeight.get();
					this.entity.modified();
				}
				ImGui.popID();
				ImGui.pushID("Sprites Width");
				ImGui.text("Sprites Width:");
				ImGui.sameLine();
				if (ImGui.inputInt("", sprWidth)) {
					this.spriteWidth = sprWidth.get();
					this.entity.modified();
				} 
				ImGui.popID();
			}
		
		
	}

	public void initSpriteSheet(int spriteWidth, int spriteHeight, int numSprites, int spacing) {
		
		this.spritesheet = new ArrayList<>();
		this.numberOfSprites = numSprites;
		this.currentSprite = 0;
		this.isSpritesheet = true;
		
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		
		int imageWidth = EngineManager.getTexture(textureName).getWidth();
		int imageHeight= EngineManager.getTexture(textureName).getHeight();
		
		//=================================
		// Get All Sprites From SpriteSheet
		//=================================
		
		//Start From First Row
		int currentX = 0;
		int currentY = 0;
		
		for(int i=0; i < numSprites; i++) {
			float topY = (currentY + spriteHeight) / (float)imageHeight;
			float rightX = (currentX + spriteWidth) / (float)imageWidth;
			float leftX = currentX / (float)imageWidth;
			float bottomY = currentY / (float)imageHeight;
			
			// Get Texture Cords
			float[] textureCoords = { 	leftX,	bottomY, 
										leftX,	topY,
										rightX,	topY, 	
										rightX,	bottomY};
			// Add To Array
			this.spritesheet.add(textureCoords);
			
			currentX += spriteWidth + spacing;
			if(currentX >= imageWidth) {
				currentX = 0;
				currentY -= spriteHeight + spacing;
			}
		}
	}
}
