package opengl;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

import managers.EngineManager;

public class Texture {
	
	private int textureID;
	
	private String filepath;
	private String name;
	
	// Image Properties
	private int width, height;

	public Texture(String file_path, String filename) {
		this.filepath = file_path;
		this.name = filename;
		loadTexture(filepath);
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, textureID);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getTextureID() {	
		return this.textureID;
	}
	
//	Image Properties
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public String getFilepath() {return filepath;}
	public String getName() {return name;}

	
	public void loadTexture(String filepath) {
		
		this.textureID = glGenTextures();
		EngineManager.addTexture(textureID);
		
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); 
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); 
        
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);
        
        if(image != null) {
        	
        	this.width = width.get(0);
        	this.height = height.get(0);
        	
        	if(channels.get(0) == 3) {
        		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
        	}
        	else if (channels.get(0) == 4) {
        		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        	}
        	else {
        		assert false: "Error: (Texture) Insufficent/Excessive number of channels";
        	}
        	
        }else {
        	assert false : " Error: (Texture) Could not load image '" + filepath +"'";
        }
        
        stbi_image_free(image);
	}
	
	
}
