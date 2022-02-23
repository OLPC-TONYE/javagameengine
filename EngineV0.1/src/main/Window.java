package main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;


import listeners.KeyListener;
import listeners.MouseListener;

public class Window {
	
	private int width, height;
	private String title;
	public long glfwWindow;
			
	private static Window window = null;
		
	private Window(String title) {
		this.width = 1080;
		this.height = 720;
		this.title = title;
	}

	public long create() {
		// Setup Error Callback
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW
		if(!glfwInit()) {
			throw new IllegalStateException("Unable to initailize GLFW");
		}
		
		// GLFW Configuration
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
		
		// Create Window
		glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
		
		if(glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create the Window");
		}
				
		// Set Mouse & Keyboard Listeners
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		
		// Make the OpenGL context
		glfwMakeContextCurrent(glfwWindow);
		
		// ENABLE V-SYNC
		glfwSwapInterval(1);
		
		// MAKE THE WINDOW VISIBLE
		glfwShowWindow(glfwWindow);
		
		GL.createCapabilities();
				
		glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		
		return glfwWindow;
	}
	
	public void closeWindow() {
		glfwFreeCallbacks(glfwWindow);
		GLFW.glfwDestroyWindow(glfwWindow);
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}
	
	public void swapBuffers() {
		GLFW.glfwSwapBuffers(glfwWindow);
	}
	
	public void changeTitle(String title) {
		CharSequence newTitle = title;
		GLFW.glfwSetWindowTitle(glfwWindow, newTitle);
	}
	
	public int width() {
		IntBuffer width = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(glfwWindow, width, null);
		return width.get(0);
	}

	public int height() {
		IntBuffer height = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(glfwWindow, null, height);
		return height.get(0);
	}
	
	public float TargetAspectRatio() {
		return 16f /9f;
	}

	public static Window get() {
		if(Window.window == null) {
			Window.window = new Window("BUGGY ENGINE");
		}
		return Window.window;
	}

	
}
