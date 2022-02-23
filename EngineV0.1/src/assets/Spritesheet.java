package assets;

import java.util.ArrayList;
import java.util.List;

import opengl.Texture;

public class Spritesheet {
	
	private Texture texture;
	
	private boolean isSpritesheet;
	private List<float[]> spritesheet;
	private int numberOfSprites;
	private int currentSprite;
	private int spriteWidth;
	private int spriteHeight;
	private int spriteSpacing;
	
	public Spritesheet(Texture texture) {
		this.texture = texture;
	}
	
	public void initSpriteSheet(int spriteWidth, int spriteHeight, int numSprites, int spacing) {
		
		this.spritesheet = new ArrayList<>();
		this.numberOfSprites = numSprites;
		this.currentSprite = 0;
		
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		
		int imageWidth = texture.getWidth();
		int imageHeight = texture.getHeight();
		
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

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public boolean isSpritesheet() {
		return isSpritesheet;
	}

	public void setSpritesheet(boolean isSpritesheet) {
		this.isSpritesheet = isSpritesheet;
	}

	public List<float[]> getSpritesheet() {
		return spritesheet;
	}

	public void setSpritesheet(List<float[]> spritesheet) {
		this.spritesheet = spritesheet;
	}
	
	public float[] getSprite() {
		return this.spritesheet.get(currentSprite);
	}

	public int getNumberOfSprites() {
		return numberOfSprites;
	}

	public void setNumberOfSprites(int numberOfSprites) {
		this.numberOfSprites = numberOfSprites;
	}

	public int getCurrentSprite() {
		return currentSprite;
	}

	public void setCurrentSprite(int currentSprite) {
		if(currentSprite < 0) {
			this.currentSprite = 0;
		}else if(currentSprite >= this.numberOfSprites) {
			this.currentSprite = 0;
		}else {
			this.currentSprite = currentSprite;
		}
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public void setSpriteWidth(int spriteWidth) {
		this.spriteWidth = spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public void setSpriteHeight(int spriteHeight) {
		this.spriteHeight = spriteHeight;
	}

	public int getSpriteSpacing() {
		return spriteSpacing;
	}

	public void setSpriteSpacing(int spriteSpacing) {
		this.spriteSpacing = spriteSpacing;
	}
}
