package leveleditor;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.EngineManager;
import engine.EntityManager;
import entities.Drawable;
import entities.Entity;
import entitiesComponents.CameraComponent;
import entitiesComponents.Component;
import entitiesComponents.MeshRenderer;
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
import imgui.type.ImInt;
import imgui.type.ImString;
import listeners.KeyListener;
import loaders.LevelLoader;
import main.Application;
import main.Layer;
import main.Window;
import maths.Maths;
import opengl.Framebuffer;
import opengl.Texture;
import opengl.VertexArrayObject;
import renderer.Renderer;
import renderer.RendererDebug;
import scenes.Scene;
import scripting.EntityScript;

public class LevelEditorLayer extends Layer
{

	Renderer renderer;
	Framebuffer screen;

	Scene levelScene = new LevelEditorScene();
	String selectedEntity;
	RendererDebug renderer2;

	Guizmos guizmo;

	VertexArrayObject lines;
	float[] pixelData;

	@Override
	public void attach() {

		screen = new Framebuffer(1024, 600);
		renderer = new Renderer();
		renderer2 = new RendererDebug();

		guizmo = new Guizmos();
		guizmo.init();

		LevelLoader.ready();

		Entity camera = new Entity();
		camera.setName("Editor Camera");
		CameraComponent n = new CameraComponent();
		n.setCameraProjection(0);
		n.setNEAR_PLANE(0.01f);
		n.setFAR_PLANE(1000f);
		n.setHeight(320);
		n.setWidth(510);
		n.setFOV(70);

		camera.addComponent(n);
		camera.addComponent(new Transform(new Vector3f(0, 0, 1)));
		camera.start();

		EntityManager.add(camera);
		System.out.println("Level Loaded");

		levelScene.init();
		levelScene.setCamera(camera.getName());

		EntityManager.entities.get(levelScene.main_camera).addComponent(new ScriptComponent());
		ScriptComponent controller = EntityManager.entities.get(levelScene.main_camera)
				.getComponent(ScriptComponent.class);
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
		renderer2.drawLines(EntityManager.entities.get(levelScene.main_camera), lines, new Vector3f(1, 1, 1));
		levelScene.render(renderer);
		guizmo.render(EntityManager.entities.get(levelScene.main_camera));
		pixelData = screen.readPixelData(1, (int) viewportMouseHoverX, (int) viewportMouseHoverY);
		screen.unbind();
		renderInterfaces();
	}

	@Override
	public void update(double dt) {
		levelScene.update(dt);
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

		beginScreen();
		beginAssetExplorer();

		beginEntityInspector();
		beginEntityExplorer();

		EventHandler.handle(EventLevel.ImGuiLayerRender);

		ImGui.showDemoWindow();
		// For Dock space
		ImGui.end();
	}

	float cursorPosX;
	float cursorPosY;
	float sizeX;
	float sizeY;
	float posX;
	float posY;
	float ndsX;
	float ndsY;
	float gameX;
	float gameY;

	float menuSizeX;
	float menuSizeY;

	float windowSizeX;
	float windowSizeY;

	float tabSizeY;

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

		cursorPosX = ImGui.getMousePosX();
		cursorPosY = ImGui.getMousePosY();

		sizeX = ImGui.getWindowSizeX();
		sizeY = ImGui.getWindowSizeY();

		windowSizeX = ImGui.getContentRegionAvailX();
		windowSizeY = ImGui.getContentRegionAvailY();

		posX = ImGui.getWindowPosX();
		posY = ImGui.getWindowPosY();

		tabSizeY = sizeY - ImGui.getContentRegionAvailY();

		ndsX = ((cursorPosX - posX) / windowSizeX) * 2 - 1;
		ndsY = 1 - 2 * ((cursorPosY - (tabSizeY + posY)) / windowSizeY);

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

		for (Entity entity : EntityManager.entities.values()) {

			if (!entity.isCamera()) {
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

	ImString new_entityName = new ImString();
	ImInt new_entityType = new ImInt();

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

				}

				if (ImGui.menuItem("Sprite")) {
					Entity new_Entity = new Entity();
					new_Entity.addComponent(new Transform(new Vector3f(0, 0, -1)));
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
					new_Entity.addComponent(new Transform(new Vector3f(0, 0, -1)));
					MeshRenderer m = new MeshRenderer();
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
			Entity entity = EntityManager.entities.get(selectedEntity);

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

	private double angleXZ;
	private double angleYZ;

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

		Transform transform = getComponent(Transform.class);
		float rate = (float) (50 * dt);

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_W)) {
			angleXZ = 360;
			double radian = Math.toRadians(angleXZ);
			transform.getPosition().set((float) Math.sin(radian), 0f, (float) Math.cos(radian));
			transform.getRotation().set(0, 0, 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_S)) {
			angleXZ = 180;
			double radian = Math.toRadians(angleXZ);
			transform.getPosition().set((float) Math.sin(radian), 0f, (float) Math.cos(radian));
			transform.getRotation().set(0, 180, 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)) {
			angleXZ = 90;
			double radian = Math.toRadians(angleXZ);
			transform.getPosition().set((float) Math.sin(radian), 0f, (float) Math.cos(radian));
			transform.getRotation().set(0, 270, 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_A)) {
			angleXZ = 270;
			double radian = Math.toRadians(angleXZ);
			transform.getPosition().set((float) Math.sin(radian), 0f, (float) Math.cos(radian));
			transform.getRotation().set(0, 90, 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_Q)) {
			Vector3f position = transform.getPosition();
			transform.setPosition(position.add(0, 0, rate));
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_E)) {
			Vector3f position = transform.getPosition();
			transform.setPosition(position.sub(0, 0, rate));
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
			angleXZ += rate;
			double radian = Math.toRadians(angleXZ);
			transform.getPosition().set((float) 5 * Math.sin(radian), 0f, (float) 5 * Math.cos(radian));
			transform.getRotation().set(0, (360 - angleXZ), 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
			angleXZ -= rate;
			double radian = Math.toRadians(angleXZ);
			transform.getPosition().set((float) 5 * Math.sin(radian), 0f, (float) 5 * Math.cos(radian));
			transform.getRotation().set(0, (360 - angleXZ), 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
			angleYZ -= rate;
			double radian = Math.toRadians(angleYZ);
			transform.getPosition().set(0f, (float) Math.sin(radian), (float) 5 * Math.cos(radian));
			transform.getRotation().set((angleYZ), 0, 0);
		}

		if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_UP)) {
			angleYZ += rate;
			double radian = Math.toRadians(angleYZ);
			transform.getPosition().set(0f, (float) Math.sin(radian), (float) Math.cos(radian));
			transform.getRotation().set((angleYZ), 0, 0);
		}

	}

}
