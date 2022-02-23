package main;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import org.lwjgl.glfw.GLFW;

import engine.EngineManager;
import gui.ImGuiLayer;
import leveleditor.LevelEditorLayer;
import listeners.KeyListener;
import scenes.Layer;
import scenes.LayerPile;

public class Application{

	private static Window window = Window.get();
	private static long windowReference;
	
	private LayerPile layers = new LayerPile();
	
	public Application() {
		
	}
	
	public void launch() {
		run();
	}
	
	public void stackLayer(Layer layer){
		layers.stack(layer);
	}
	
	public void unstack() {
		layers.unstack();
	}
	
	private void update(double dt) {
		layers.update(dt);
		
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
			layers.stack(new LevelEditorLayer());
		}
	}
	
	private void render() {
		ImGuiLayer.beginFrame();
		layers.render();
		ImGuiLayer.endFrame();
	}

	@SuppressWarnings("unused")
	private void run() {
		
//		CREATE MAIN WINDOW
		windowReference = window.create();
//		PREPARE IMGUI
		ImGuiLayer.beginImGui();		
		final int MAX_FRAMES_PER_SECOND = 60;
		final int MAX_UPDATES_PER_SECOND = 60;
		
		final double updateOptimalTime = 1000000000 / MAX_UPDATES_PER_SECOND;
		final double frameOptimalTime = 1000000000 / MAX_FRAMES_PER_SECOND;
		
		double updateDeltaTime = 0;
		double frameDeltaTime = 0;
		int frames = 0;
		int updates = 0;
		
		long startTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		
		while(!glfwWindowShouldClose(windowReference)) {
			
			long currentTime = System.nanoTime();
			updateDeltaTime += (currentTime - startTime);
			frameDeltaTime += (currentTime - startTime);
			startTime = currentTime;
			
			// POLL EVENTS
			glfwPollEvents();
			
			if(updateDeltaTime >= updateOptimalTime) {
				
				
				update(updateDeltaTime/1000000000);
				
				updates++;
				updateDeltaTime -= updateOptimalTime;
			}
			
			if(frameDeltaTime >= frameOptimalTime) {
				
				render();
				
				frames++;
				frameDeltaTime -= frameOptimalTime;
			}
	
			if(System.currentTimeMillis() - timer >= 1000) {
		
//				System.out.println("UPS: "+ updates + ", FPS: " + frames);

				updates = 0;
				frames = 0;
				timer += 1000;			
			}
			
			window.swapBuffers();
						
		}
		EngineManager.cleanUp();
		ImGuiLayer.endImGui();
		layers.unstackAll();
		window.closeWindow();
		
	}
}
