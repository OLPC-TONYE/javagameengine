package leveleditor;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.EngineManager;
import engine.EntityManager;
import entities.Drawable;
import entities.Entity;
import entitiesComponents.Component;
import entitiesComponents.MeshComponent;
import entitiesComponents.ScriptComponent;
import entitiesComponents.SpriteRenderer;
import entitiesComponents.Transform;
import events.EventHandler;
import events.EventLevel;
import events.ImGuiLayerRenderEvent;
import gui.FileExplorer;
import gui.Guizmos;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiButtonFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiMouseCursor;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import listeners.KeyListener;
import listeners.MouseListener;
import main.Application;
import main.Layer;
import main.Window;
import opengl.Framebuffer;
import opengl.Texture;
import opengl.VertexArrayObject;
import renderer.Renderer3D;
import renderer.RendererDebug;
import scenes.Scene;
import scenes.SceneLoader;
import scripting.EntityScript;
import tools.Maths;

public class LevelEditorLayer extends Layer
{

	Renderer3D renderer;
	RendererDebug renderer2;
	
	Framebuffer screen;

	Scene levelScene = new LevelEditorScene();
	String selectedEntity;
	
	Guizmos guizmo;

	VertexArrayObject lines;
	VertexArrayObject camera;
	
	float[] pixelData;
	float[] guizmo_pixelData = new float[3];
	
	Entity editorCamera;
	@Override
	public void attach() {

		screen = new Framebuffer(1024, 600);
		renderer = new Renderer3D();
		renderer2 = new RendererDebug();

		guizmo = new Guizmos();
		guizmo.init();

		SceneLoader.ready();

		editorCamera = EngineManager.createCamera();
		editorCamera.start();

		System.out.println("Level Loaded");

		levelScene.init();
		levelScene.setCamera(editorCamera);

		editorCamera.addComponent(new ScriptComponent());
		ScriptComponent controller = editorCamera.getComponent(ScriptComponent.class);
		controller.bind(new CameraController());

		lines = Drawable.makeGridlines(0, 10.0f, 1.0f, -0.5f);
	}

	@Override
	public void detach() {
		levelScene.close();
		renderer.clear();
		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.0f);
		screen.cleanup();
	}

	@Override
	public void render() {
		screen.bind();
		renderer.clear();
		renderer.clearColour(0.06f, 0.06f, 0.06f, 0.0f);
		renderer2.drawLines(levelScene.main_camera, lines, new Vector3f(1, 1, 1), new Vector3f(), new Vector3f(), new Vector3f(1));
		levelScene.render(renderer);
		levelScene.render(renderer2);
		guizmo.render(levelScene.main_camera);
		pixelData = screen.readPixelData(1, (int) viewportMouseHoverX, (int) viewportMouseHoverY);
		guizmo_pixelData = screen.readPixelData(2, (int) viewportMouseHoverX, (int) viewportMouseHoverY);
		screen.unbind();
		renderInterfaces();
	}

	@Override
	public void update(double dt) {
		editorCamera.update(dt);
		levelScene.update(dt);
		guizmo.update(dt, guizmo_pixelData);
		if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_Z)) {
			Application.get().pileOnTop(new LevelTestLayer());
		}
	}

	private void renderInterfaces() {
		beginDockspace();
	}

	private void beginDockspace() {
		// DOCKSPACE
		int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

		ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.FirstUseEver);
		ImGui.setNextWindowSize(Window.get().width(), Window.get().height());
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0f);
		ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
		windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize
				| ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

		ImGui.begin("DockspaceBackground", new ImBoolean(true), windowFlags);
		ImGui.popStyleVar(2);

		int dockspace_id = ImGui.getID("ActualDockspace");
		ImGui.dockSpace(dockspace_id);

		mainMenu();
		
		beginScreen();
		beginAssetExplorer();

		beginEntityInspector();
		beginEntityExplorer();

		EventHandler.handle(EventLevel.ImGuiLayerRender);

		ImGui.showDemoWindow();
		// For Dock space
		ImGui.end();
	}
	
	private void mainMenu() {
		if(ImGui.beginMenuBar()) {
			
			if(ImGui.beginMenu("File")) {
				
				if(ImGui.menuItem("New")) {
					
				}
				
				if(ImGui.menuItem("Open")) {
					
				}
				
				if(ImGui.menuItem("Save")) {
					
				}
				
				ImGui.endMenu();
			}
			
			if(ImGui.beginMenu("View")) {
				
				ImGui.endMenu();
			}
			
			ImGui.endMenuBar();
		}
	}

	Vector2f viewportSize;
	Vector2f viewportCursor;
	Vector2f viewportPos;
	float viewportMouseHoverX;
	float viewportMouseHoverY;

	/**
	 * Game Screen (using Framebuffer).
	 */
	private void beginScreen() {
		ImGui.setNextWindowSize(screen.getFramBufferWidth(), screen.getFramBufferHeight(), ImGuiCond.Always);

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

		viewportMouseHoverX = ((cursorPosX - viewportCursor.x) / aspectWidth) * screen.getFramBufferWidth();
		viewportMouseHoverY = ((aspectHeight - (cursorPosY - viewportCursor.y)) / aspectHeight)
				* screen.getFramBufferHeight();

		ImGui.popStyleVar();
		ImGui.popStyleVar();

		ImGui.image(screen.getTexture(0), aspectWidth, aspectHeight, 0, 1, 1, 0);

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
			for (Entity entity : EntityManager.world_entities.values()) {
				int id = EntityManager.getId(entity.getName());
				if ((int) pixelData[0] == id) {
					this.selectedEntity = entity.getName();
					this.guizmo.attachTo(entity);
					break;
				} else {
					this.guizmo.dettach();
				}
			}
		}
		
	}

	private void beginAssetExplorer() {
		ImGui.begin("Assets");
		for (Texture texture : EngineManager.textureAssets.values()) {

			float WindowPosX = ImGui.getWindowPosX();
			float WindowSizeX = ImGui.getWindowSizeX();
			float ItemSpacingX = ImGui.getStyle().getItemSpacingX();
			float windowX2 = WindowPosX + WindowSizeX;

			ImGui.imageButton(texture.getTextureID(), 100, 100);
			if (ImGui.beginDragDropSource()) {
				ImGui.setDragDropPayload("payload_type", texture.getName());
				ImGui.text("Drag started : " + texture.getName());
				ImGui.endDragDropSource();
			}

			float lastPosX = ImGui.getItemRectMaxX();
			float nextPosX = lastPosX + ItemSpacingX + 100;

			if (nextPosX < windowX2) {
				ImGui.sameLine();
			}

		}

		ImGui.end();
	}

	String explorer_deleteFlag = null;

	private void showChildren(String parent) {
		if (EntityManager.hasChildren(parent)) {
			for (String child : EntityManager.getChildrenOf(parent)) {
				boolean open = ImGui.treeNodeEx(child, ImGuiTreeNodeFlags.OpenOnArrow);
				if (ImGui.beginDragDropSource()) {
					ImGui.setDragDropPayload("child", child);
					ImGui.text(child);
					ImGui.endDragDropSource();
				}
				if (ImGui.beginDragDropTarget()) {
					Object payload_check = ImGui.acceptDragDropPayload("child", ImGuiDragDropFlags.AcceptPeekOnly);
					boolean check = true;
					if (payload_check != null) {
						check = EntityManager.isAbove( (String)payload_check, child);				
						payload_check = null;
					}
					if(check) {
						ImGui.setMouseCursor(ImGuiMouseCursor.NotAllowed);
					}
					if(!check) {
						Object payload = ImGui.acceptDragDropPayload("child");
						if (payload != null) {
							String childaccept = (String) ImGui.acceptDragDropPayload("child");
							EntityManager.makeChildOf(childaccept, child);
							payload = null;
						}
					}				
					ImGui.endDragDropTarget();
				}
				if (ImGui.beginPopupContextItem(ImGuiPopupFlags.MouseButtonRight)) {
					if (ImGui.menuItem("Delete")) {
						explorer_deleteFlag = child;
					}
					ImGui.endPopup();
				}
				if (open) {
					showChildren(child);
					ImGui.treePop();
				}

			}
		}
	}

	private void beginEntityExplorer() {
		ImGui.begin("EntityExplorer");

		if (ImGui.button("Open")) {
			FileExplorer.open();
			EventHandler.queue(new FetchFileEvent());
		}

		for (String root : EntityManager.hierarchy.keySet()) {
			if (!EntityManager.hasParent(root)) {
				boolean open = ImGui.treeNodeEx(root, ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.OpenOnArrow);
				if (ImGui.beginDragDropSource()) {
					ImGui.setDragDropPayload("child", root);
					ImGui.text(root);
					ImGui.endDragDropSource();
				}
				if (ImGui.beginDragDropTarget()) {
					Object payload_check = ImGui.acceptDragDropPayload("child", ImGuiDragDropFlags.AcceptPeekOnly);
					boolean check = true;
					if (payload_check != null) {
						check = EntityManager.isAbove( (String)payload_check, root);
										
						payload_check = null;
					}
					if(check) {
						ImGui.setMouseCursor(ImGuiMouseCursor.NotAllowed);
					}
					if(!check) {
						Object payload = ImGui.acceptDragDropPayload("child");
						if (payload != null) {
							String childaccept = (String) ImGui.acceptDragDropPayload("child");
							EntityManager.makeChildOf(childaccept, root);
							payload = null;
						}
					}				
					ImGui.endDragDropTarget();
				}
				if (ImGui.beginPopupContextItem(ImGuiPopupFlags.MouseButtonRight)) {
					if (ImGui.menuItem("Delete")) {
						explorer_deleteFlag = root;
					}
					ImGui.endPopup();
				}
				if (open) {
					showChildren(root);
					ImGui.treePop();
				}
			}
		}

		if (explorer_deleteFlag != null) {
			boolean success = EntityManager.remove(explorer_deleteFlag);
			if (!success) {
				System.out.println("Failed to Remove " + explorer_deleteFlag);
			} else {
				this.selectedEntity = null;
			}
			explorer_deleteFlag = null;
		}

		if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.MouseButtonRight | ImGuiPopupFlags.NoOpenOverItems)) {

			if (ImGui.beginMenu("Add Entity")) {

				if (ImGui.menuItem("Empty Entity")) {
					Entity new_Entity = new Entity();
					new_Entity.addComponent(new Transform());
					boolean success = EntityManager.add(new_Entity, "Empty Entity");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					this.selectedEntity = new_Entity.getName();
				}
				
				if (ImGui.menuItem("Camera 2D")) {
					Entity new_Entity = EngineManager.create2DCamera();
					boolean success = EntityManager.add(new_Entity, "2D Camera");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					this.selectedEntity = new_Entity.getName();
				}
				
				if (ImGui.menuItem("Camera 3D")) {
					Entity new_Entity = EngineManager.create3DCamera();
					boolean success = EntityManager.add(new_Entity, "3D Camera");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					this.selectedEntity = new_Entity.getName();
				}

				if (ImGui.menuItem("Sprite")) {
					Entity new_Entity = new Entity();
					new_Entity.addComponent(new Transform());
					new_Entity.addComponent(new SpriteRenderer("spritesheet"));
					boolean success = EntityManager.add(new_Entity, "Sprite");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					this.selectedEntity = new_Entity.getName();
				}

				if (ImGui.menuItem("Cube")) {
					Entity new_Entity = new Entity();
					new_Entity.addComponent(new Transform());
					MeshComponent m = new MeshComponent();
					m.setTexture("white");
					new_Entity.addComponent(m);
					boolean success = EntityManager.add(new_Entity, "Cube");
					if (!success) {
						System.out.println("Failed to Add " + new_Entity.getName());
					}
					new_Entity.start();
					this.selectedEntity = new_Entity.getName();
				}

				ImGui.endMenu();
			}

			ImGui.endPopup();
		}

		ImGui.end();
	}

	/**
	 * 
	 * EntityInspector shows the details of the current entity selected
	 * 
	 */
	private void beginEntityInspector() {
		ImGui.begin("EntityInspector");

		if (this.selectedEntity != null) {
			ImGui.text(selectedEntity);
			Entity entity = EntityManager.world_entities.get(selectedEntity);

			if (ImGui.beginPopupContextWindow(ImGuiPopupFlags.MouseButtonRight)) {

				if (ImGui.beginMenu("Add Component")) {

					if (ImGui.menuItem("Sprite Renderer")) {

					}

					ImGui.endMenu();
				}

				ImGui.endPopup();
			}

			for (Component component : entity.getComponents()) {

				if (ImGui.collapsingHeader(component.getClass().getSimpleName(), ImGuiTreeNodeFlags.DefaultOpen)) {
					component.UI();
				}

			}

		}

		ImGui.end();
	}

}

class FetchFileEvent extends ImGuiLayerRenderEvent
{

	public FetchFileEvent() {
		super("FetchFileName");
	}

	@Override
	public void onEvent() {
		if (FileExplorer.onClose == 1) {
			EngineManager.getTextureWithPath(FileExplorer.getFetchedFile().getAbsolutePath());
		}
	}

	@Override
	public boolean handle() {

		FileExplorer.render();
		return FileExplorer.onClose > 0;
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
