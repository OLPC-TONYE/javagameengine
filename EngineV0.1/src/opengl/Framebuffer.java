package opengl;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
//import static org.lwjgl.opengl.GL11.GL_LINEAR;
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
import static org.lwjgl.opengl.GL11.glDeleteTextures;
//import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import main.Window;

public class Framebuffer {
	
	private int framebuffer;
	
	private int[] textures = new int[4];
	private int depthTexture;
	
	private int depthBuffer;
	
	protected int width;
	protected int height;
			
	public Framebuffer(int width, int height) {
		this.width = width;
		this.height = height;
		createFrameBuffer();
		addTextureAttachment(0, GL_RGBA, GL_RGBA, width, height);
		addTextureAttachment(1, GL_RGB32F, GL_RGB, width, height);
		addTextureAttachment(2, GL_RGB32F, GL_RGB, width, height);
		addDepthTexture(width, height);
		addDepthBufferAttachment(width, height);
		validate();
		unbind();
	}
	
	public int getFrameBuffer() {
		return framebuffer;
	}
	
	public int getTexture(int attachment) {
		return textures[attachment];
	}
	
	public int getFramBufferWidth() {
		return this.width;
	}
	
	public int getFramBufferHeight() {
		return this.height;
	}
	
	public float[] readPixelData(int index, int x, int y) {
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0 +  index);
		float[] pixels = new float[4];
		GL11.glReadPixels(x, y, 1, 1, GL14.GL_RGB, GL11.GL_FLOAT, pixels);
		return pixels;
	}
	
	public void unbind() {
		//call to switch to default frame buffer
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, Window.get().width(), Window.get().height());
	}

	public void cleanup() {
		//call when closing the game
		glDeleteFramebuffers(framebuffer);
		glDeleteTextures(textures);
		glDeleteTextures(depthTexture);
		glDeleteRenderbuffers(depthBuffer);
	}
	
	public void bind(){
		glBindTexture(GL_TEXTURE_2D, 0); //To make sure the texture isn't bound
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
		glViewport(0, 0, width, height);
	}
	
	protected void createFrameBuffer() {
		framebuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
		int[] bufs = new int[] { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2};
		GL20.glDrawBuffers(bufs);
	}
	
	protected boolean validate() {
		
		if(GL30.glCheckFramebufferStatus(framebuffer) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			switch (GL30.glCheckFramebufferStatus(framebuffer)) 
			{
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
					System.err.println("Incomplete Draw Buffer");
					return false;
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
					System.err.println("Incomplete Read Buffer");
					return false;
				case GL30.GL_FRAMEBUFFER_UNSUPPORTED:
					System.err.println("UnSupported");
					return false;
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
					System.err.println("Incomplete Attachment");
					return false;
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
					System.err.println("Incomplete Multisample");
					return false;
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
					System.err.println("Incomplete Missing Attachment");
					return false;
				case GL30.GL_FRAMEBUFFER_UNDEFINED:
					System.err.println("Undefined");
					return false;
			}
		}
		return true;
	}

	protected void addTextureAttachment(int attachment, int internalFormat, int format, int width, int height) {
		textures[attachment] = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, textures[attachment]);
		
//		Create empty texture
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		
//		Set texture parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + attachment, GL_TEXTURE_2D, textures[attachment], 0);
	}
	
	
	protected void addDepthTexture(int width, int height){
		depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
	}
	
	protected void addDepthBufferAttachment(int width, int height) {
		depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
	}
}
