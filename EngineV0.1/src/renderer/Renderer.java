package renderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.*;

import opengl.Shader;
import scenes.Scene;

public abstract class Renderer {
	
	protected Shader shader;
	
	public Renderer() {
		prepare();
	}
	
	protected abstract void prepare();
	
	protected abstract void beginScene();
	public abstract void render(Scene scene);
	protected abstract void endScene();
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_DEPTH_BUFFER_BIT);
	}
	
	public void clearColour() {
		glClearColor(1, 1, 1, 1);
	}
	
	public void clearColour(float red, float green, float blue, float alpha) {
		glClearColor(red, green, blue, alpha);
	}
	
}
