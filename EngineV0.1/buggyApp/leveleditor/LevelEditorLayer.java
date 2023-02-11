package leveleditor;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import assets.Scene;
import components.ScriptComponent;
import components.Transform;
import entities.Drawable;
import entities.Entity;
import gui.Guizmos;
import gui.ImGuiLayer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiButtonFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import interfaces.AssetBrowser;
import interfaces.EntityExplorer;
import interfaces.FileExplorer;
import interfaces.InspectorPanel;
import listeners.KeyListener;
import listeners.MouseListener;
import main.Application;
import main.Layer;
import main.Window;
import managers.EngineManager;
import managers.EntityManager;
import opengl.Framebuffer;
import opengl.VertexArrayObject;
import renderer.Renderer3D;
import renderer.RendererDebug;
import scenes.SceneLoader;
import scripting.EntityScript;
import tools.Maths;

public class LevelEditorLayer extends Layer
{

	Renderer3D renderer;
	RendererDebug renderer2;
	
	Framebuffer viewport;

//	Scenes will now be changed to assets
//	Will need to make te editor run with null Scene
	public Scene currentScene = new Scene();
	
	public String selectedEntity;
	public String selectedAsset;
	
	Guizmos guizmo;

	VertexArrayObject lines;
	VertexArrayObject camera;
		
	float[] pixelData;
	float[] guizmo_pixelData = new float[3];
	
	Entity editorCamera;
	Entity light;

	@Override
	public void attach() {
		viewport = new Framebuffer(1024, 600);
		renderer = new Renderer3D();
		renderer2 = new RendererDebug();

		guizmo = new Guizmos();
		guizmo.init();

		SceneLoader.ready();

		editorCamera = EngineManager.createCamera();
		editorCamera.start();

		currentScene.setPrimaryCamera(editorCamera);

		editorCamera.addComponent(new ScriptComponent());
		ScriptComponent controller = editorCamera.getComponent(ScriptComponent.class);
		controller.bind(new CameraController());

		lines = Drawable.makeGridlines(0, 10.0f, 1.0f, -0.5f);
	}

	@Override
	public void detach() {
		renderer.clear();
		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.0f);
		viewport.cleanup();
	}

	@Override
	public void render() {
		
		// bind the framebuffer
		viewport.bind();
		
		// clear the frame
		renderer.clear();
		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.0f);
		
		// draw gridlines
		renderer2.drawLines(editorCamera, lines, new Vector3f(1, 1, 1), new Vector3f(), new Vector3f(), new Vector3f(1));
		
		// render scene
		if(currentScene.primaryCamera != null) {
			renderer.render(currentScene);
			renderer2.render(currentScene);
			
			// render guizmo if active
			guizmo.render(currentScene.primaryCamera);
		}
		
		
		pixelData = viewport.readPixelData(1, (int) viewportMouseHoverX, (int) viewportMouseHoverY);
		guizmo_pixelData = viewport.readPixelData(2, (int) viewportMouseHoverX, (int) viewportMouseHoverY);
		
		viewport.unbind();
		
		// render the interfaces
		renderInterfaces();
	}

	@Override
	public void update(double dt) {
		// update the camera
		editorCamera.update(dt);
		
		// update the scene
		for(Entity entity: currentScene.getEntities()) {
			entity.update(dt);
		}
		
		// update the guizmo
		guizmo.update(dt, guizmo_pixelData);
		
		// play scene (on key "z")
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_Z)) {
			Application.get().pileOnTop(new LevelTestLayer());
		}
	}

	private void renderInterfaces() {
		beginWorkspace();
	}

	AssetBrowser assetBrowser = new AssetBrowser();
	InspectorPanel entityInspector = new InspectorPanel();
	EntityExplorer entityExplorer = new EntityExplorer();
	FileExplorer fileExplorer = new FileExplorer("Open Scene...");
	
	String print = null;
	private void beginWorkspace() {
		
		// DOCKSPACE
		ImGuiLayer.addMainDockSpace();

		mainMenu();
		
		beginScreen();
		
		
		assetBrowser.render(this);
			
		entityInspector.render(this);
		entityExplorer.render(this);

		ImGui.showDemoWindow();
		
		if(!ImGuiLayer.resumed) ImGui.end();
	}
	
	
	private void mainMenu() {
		if(ImGui.beginMenuBar()) {
			
			if(ImGui.beginMenu("File")) {
				
				if(ImGui.menuItem("New")) {
					
				}
				
				if(ImGui.menuItem("Open")) {
					SceneLoader.load(fileExplorer.fetchFile());
				}
				
				if(ImGui.menuItem("Save")) {
					fileExplorer.saveFile(SceneLoader.saveData(), "bug");
				}
				
				if(!ImGuiLayer.resumed) {
					ImGui.endMenu();
				}				
			}
			
			if(ImGui.beginMenu("View")) {
				
				if(ImGui.menuItem("Scene Lights", "", currentScene.useSceneLights)) {
					currentScene.useSceneLights = !currentScene.useSceneLights;
				}
				
				ImGui.endMenu();
			}
			
			if(ImGui.beginMenu("About")) {
				ImGui.endMenu();
			}
			
			if(!ImGuiLayer.resumed) {
				ImGui.endMenuBar();
			}
			
		}
	}

	Vector2f viewportSize;
	Vector2f viewportCursor;
	Vector2f viewportPos;
	float viewportMouseHoverX;
	float viewportMouseHoverY;

	/**
	 * ViewPort (using Frame Buffer).
	 */
	private void beginScreen() {
		ImGui.setNextWindowSize(viewport.getFramBufferWidth(), viewport.getFramBufferHeight(), ImGuiCond.Always);

		ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
		ImGui.begin("Game", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoTitleBar);

		float cursorPosX = ImGui.getMousePosX();
		float cursorPosY = ImGui.getMousePosY();

		float sizeY = ImGui.getWindowSizeY();

		float windowSizeX = ImGui.getContentRegionAvailX();
		float windowSizeY = ImGui.getContentRegionAvailY();

		float posX = ImGui.getWindowPosX();
		float posY = ImGui.getWindowPosY();

		float tabSizeY = sizeY - ImGui.getContentRegionAvailY();

		float ndsX = ((cursorPosX - posX) / windowSizeX) * 2 - 1;
		float ndsY = 1 - 2 * ((cursorPosY - (tabSizeY + posY)) / windowSizeY);

		ndsX = Maths.clamp(-1, 1, ndsX);
		ndsY = Maths.clamp(-1, 1, ndsY);
		// Calculate Size of Window
		ImVec2 windowSize = new ImVec2();
		ImGui.getContentRegionAvail(windowSize);
		windowSize.x -= ImGui.getScrollX();
		windowSize.y -= ImGui.getScrollY();

		float aspectWidth = windowSize.x;
		float aspectHeight = aspectWidth / Window.get().TargetAspectRatio();
		if (aspectHeight > windowSize.x) {
			aspectHeight = windowSize.y;
			aspectWidth = aspectHeight * Window.get().TargetAspectRatio();
		}
		ImVec2 aspectSize = new ImVec2(aspectWidth, aspectHeight);

		float viewportX = ((windowSize.x / 2.0f) - (aspectSize.x / 2.0f)) + ImGui.getCursorPosX();
		float viewportY = ((windowSize.y / 2.0f) - (aspectSize.y / 2.0f)) + ImGui.getCursorPosY();

		ImGui.setCursorPos(viewportX, viewportY);

		viewportPos = new Vector2f(ImGui.getWindowPosX(), ImGui.getWindowPosY());
		viewportCursor = new Vector2f(ImGui.getWindowPosX() + ImGui.getCursorPosX(),
				ImGui.getWindowPosY() + ImGui.getCursorPosY());
		viewportSize = new Vector2f(ImGui.getWindowSizeX(), ImGui.getWindowSizeY());

		viewportMouseHoverX = ((cursorPosX - viewportCursor.x) / aspectWidth) * viewport.getFramBufferWidth();
		viewportMouseHoverY = ((aspectHeight - (cursorPosY - viewportCursor.y)) / aspectHeight)
				* viewport.getFramBufferHeight();

		ImGui.popStyleVar();
		ImGui.popStyleVar();

		ImGui.image(viewport.getTexture(0), aspectWidth, aspectHeight, 0, 1, 1, 0);

		if (ImGui.isItemHovered()) {
			if (ImGui.isMouseClicked(ImGuiButtonFlags.MouseButtonLeft)) {
				selectEntity();
			}
		}
		ImGui.end();
	}

	private void selectEntity() {
		
		if(guizmo_pixelData[0] >= 1 | guizmo_pixelData[1] >= 1 | guizmo_pixelData[2] >= 1) {
			
		}else {
			
			if (EntityManager.world.getFirstMap().containsValue((int) pixelData[0])) {
				Entity entity = EntityManager.getEntityById((int) pixelData[0]);
				this.selectedEntity = entity.getName();
				this.guizmo.attachTo(entity);
			} else {
				this.guizmo.dettach();
			}
		}
		
	}

}

class CameraController extends EntityScript
{
	
	private Transform cameraTransform;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(double dt) {
		
		// A minor bug (refer to 2022-04-22)
		cameraTransform = getComponent(Transform.class);
		float speed = (float) (1 * dt);
		
		if(MouseListener.mouseButtonDown(1) && MouseListener.isDragging() ) {
			moveCamera(speed * 0.25 * MouseListener.getDx(), speed * 0.25 * MouseListener.getDy(), 0);
		}
		
		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_W)) {
			cameraTransform.translateZ(speed);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_S)) {
			cameraTransform.translateZ(-speed);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) {

		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_A)) {

		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_Q)) {
			moveCamera(0, 0, speed);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_E)) {
			moveCamera(0, 0, -speed);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
			moveCamera(speed, 0, 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
			moveCamera(-speed, 0, 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
			moveCamera(0, speed, 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_UP)) {
//			Remember camera moves opposite
			moveCamera(0, -speed, 0);
		}

	}
	
	private void moveCamera(double dx, double dy, double dz) {
		
		Vector3f pos = cameraTransform.getPosition();
		Vector3f rot = cameraTransform.getRotation();
		
		double r = pos.distance(0, 0, 0);
		double theta = Math.acos(pos.y/r);
		double phi = Math.atan2(-pos.z, pos.x);
		
		theta += dy;
		phi += dx;
		r += dz;
		
		if(phi > 180) {
			phi = -(phi-180);
		}
				
		theta = Maths.clamp(0, Math.PI, theta);	
				
		float x = (float) (r * Math.sin(theta) * Math.cos(phi));
		float z = (float) (-r * Math.sin(theta) * Math.sin(phi));
		float y = (float) (r * Math.cos(theta));
		
		cameraTransform.setPosition(new Vector3f(x, y, z));
		
		float theta_deg = (float)Math.toDegrees(theta);
		if(theta_deg <= 90) {
			rot.x = 90 - theta_deg ;
		}else if (theta_deg > 90) {
			theta_deg -= 90;
			rot.x = -theta_deg ;
		}
		
		float phi_deg = (float)Math.toDegrees(phi);
		if(phi_deg <= 0 && phi_deg >= -90) {
			rot.y = -(90 - Math.abs(phi_deg));
		}else if(phi_deg <= -90 && phi_deg >= -180) {
			rot.y = (Math.abs(phi_deg)-90);
		}else if(phi_deg >= 0 && phi_deg <= 90) {
			rot.y = -(90 + (Math.abs(phi_deg)));
		}else if(phi_deg >= 90 && phi_deg <= 180) {
			rot.y = (90 + (90 -(Math.abs(phi_deg)-90)));
		}

	}

}
