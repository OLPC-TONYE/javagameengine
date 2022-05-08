package gui;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_FLOATING;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowOpacity;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWaitEventsTimeout;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGui;
import listeners.KeyListener;
import listeners.MouseListener;
import main.Window;

public class ImGuiWindowLayer {

	long glfwWindow;
	private int width;
	private int height;
	private String title;
	
	ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    String glslVersion = "#version 120" ;
    

	/**
	 * 
	 * Creates A Sub-Window
	 * 
	 * @param title
	 * @param width
	 * @param height
	 */
	public ImGuiWindowLayer(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
		
		init();
	}

	protected void init() {
		// GLFW Configuration
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_FLOATING, GLFW_TRUE);
		
		// Create Window
		glfwWindow = glfwCreateWindow(width, height, title, NULL, Window.get().glfwWindow);
		
		if(glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create the Window");
		}
				
		// Set Mouse & Keyboard Listeners
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
		
		glfwSetWindowFocusCallback(glfwWindow, ImGuiWindowLayer::focusCallback);
		glfwSetWindowRefreshCallback(Window.get().glfwWindow, ImGuiWindowLayer::refreshCallback);
		
		// Make the OpenGL context
		glfwMakeContextCurrent(glfwWindow);
		
		// ENABLE V-SYNC
		glfwSwapInterval(1);
		
		// MAKE THE WINDOW VISIBLE
		glfwShowWindow(glfwWindow);
		
		// IMGUI Configuration
		ImGui.createContext();
    	
    	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        
        if (Window.get().glfwWindow == NULL) {
            throw new RuntimeException("Failed to find the Main GLFW window");
        }
        
        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to find the GLFW window");
        }
            	
        // ------------------------------------------------------------
        // Initialise ImGuiIO configuration
        
        final ImGuiIO io = ImGui.getIO();
        
        io.setIniFilename("desk"); 
 
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard | ImGuiConfigFlags.DockingEnable);
        
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();

        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());
        fontAtlas.addFontFromFileTTF("res/fonts/LatoItalic.ttf", 20, fontConfig);
        fontConfig.destroy();
        fontAtlas.build();
        
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);
	}
	
	public void updateWindowSpecs() {
		IntBuffer bufferWidth = BufferUtils.createIntBuffer(1);
		IntBuffer bufferHeight = BufferUtils.createIntBuffer(1);			
		glfwGetWindowSize(glfwWindow, bufferWidth, bufferHeight);
		
		width = bufferWidth.get(0);
		height = bufferHeight.get(0);
	}
	
	public void updateWindow() {
		glfwPollEvents();
		glfwSwapBuffers(glfwWindow);
	}
	
	public void destroyWindow() {
		// Cleanup GLFW and ImGUi
		imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
                
        glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		
		// Restore Mouse & Keyboard Listeners Back To Main Window
 		glfwSetCursorPosCallback(Window.get().glfwWindow, MouseListener::mousePosCallback);
 		glfwSetMouseButtonCallback(Window.get().glfwWindow, MouseListener::mouseButtonCallback);
 		glfwSetScrollCallback(Window.get().glfwWindow, MouseListener::mouseScrollCallback);
 		
 		glfwSetKeyCallback(Window.get().glfwWindow, KeyListener::keyCallback);
 		glfwSetWindowRefreshCallback(Window.get().glfwWindow, null);
 		
 		// Make the OpenGL context
 		glfwMakeContextCurrent(Window.get().glfwWindow);
	}
	
	public void beginFrame() {		
		imGuiGlfw.newFrame();
        ImGui.newFrame();
	}
	
	public void endFrame() {
		ImGui.render();
    	imGuiGl3.renderDrawData(ImGui.getDrawData());
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public boolean isClosing() {
		return glfwWindowShouldClose(glfwWindow);
	}
	
	public static void focusCallback(long glfwWindow, boolean focused) {
		if(!focused) {
			glfwFocusWindow(glfwWindow);
			glfwSetWindowOpacity(glfwWindow, 0.7f);
			glfwWaitEventsTimeout(1);
			glfwSetWindowOpacity(glfwWindow, 0.8f);
			glfwWaitEventsTimeout(1);
			glfwSetWindowOpacity(glfwWindow, 1f);
		}
	}
	
	public static void refreshCallback(long glfwWindow) {
		glfwSwapBuffers(glfwWindow);
	}
	
}
