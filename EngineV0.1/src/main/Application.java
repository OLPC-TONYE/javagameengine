package main;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import engine.EngineManager;
import gui.ImGuiLayer;

public class Application{

	public interface ApplicationCommands {	
		void run();
	}
	
	private static Window window;
	private static long windowReference;
	private static Application app;
	private static String appName;
	
	protected LayerPile pile;
	
	public Application(String appName) {
		Application.appName = appName;
		pile = new LayerPile();
	}
	
	public static Application get() {
		if(app == null) {
			app = new Application(appName);
		}
		return app;
	}
	
	private void init() {
		window = Window.get();
		windowReference = window.create(appName);
		ImGuiLayer.begin();
	}
	
	public void launch(ApplicationCommands commands) {
		init();
		commands.run();
		run();
	}
	
	public void pileOnTop(Layer layer) {
		get().pile.pileOntop(layer);
	}
	
	public void pushOffPile(Layer layer) {
		get().pile.pushOff(layer);
	}
	
	private void run() {

			
		final int MAX_FRAMES_PER_SECOND = 60;
		final int MAX_UPDATES_PER_SECOND = 30;
		
		final double updateOptimalTime = 1000000000 / MAX_UPDATES_PER_SECOND;
		final double frameOptimalTime = 1000000000 / MAX_FRAMES_PER_SECOND;
		
		double updateDeltaTime = 0, frameDeltaTime = 0;
		int frames = 0, updates = 0;
		
		long startTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		
		while(!glfwWindowShouldClose(windowReference)) {
			
			long currentTime = System.nanoTime();
			updateDeltaTime += (currentTime - startTime);
			frameDeltaTime += (currentTime - startTime);
			startTime = currentTime;
			
			if(updateDeltaTime >= updateOptimalTime) {
				
//				UPDATE GAME
				pile.update(updateDeltaTime / 1000000000);
				
				updates++;
				updateDeltaTime -= updateOptimalTime;
			}
					
			if(frameDeltaTime >= frameOptimalTime) {
							
				ImGuiLayer.beginFrame();
				pile.render();
				ImGuiLayer.endFrame();
				
				frames++;
				frameDeltaTime -= frameOptimalTime;
			}
	
			if(System.currentTimeMillis() - timer >= 1000) {
		
				System.out.println("UPS: "+ updates + ", FPS: " + frames);

				updates = 0;
				frames = 0;
				timer += 1000;			
			}
			
			window.update();
						
		}
		EngineManager.cleanUp();
		ImGuiLayer.end();
		pile.unstackAll();
		window.closeWindow();
		
	}
	
}
