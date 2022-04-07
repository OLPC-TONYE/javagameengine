package listeners;

import org.lwjgl.glfw.GLFW;

public class MouseListener {
	private static MouseListener instance;
	private double scrollX, scrollY;
	private double xPos, yPos, lastY, lastX;
	private double startDragX, startDragY, dragX, dragY;
	
	private int mouseButtonDown= 0;
	
	private boolean mouseButtonPressed[] = new boolean[3];
	private boolean isDragging;

	private MouseListener() {
		this.scrollX = 0;
		this.scrollY = 0;
		this.xPos = 0;
		this.yPos = 0;
		this.lastY = 0;
		this.lastX = 0;
	}
	
	public static MouseListener get() {
		if (MouseListener.instance == null) {
			instance = new MouseListener();
		}
		
		return instance;
	}
	
	public static void mousePosCallback(long window, double xpos, double ypos) {
		if(get().mouseButtonDown >= 1) {
			get().isDragging = true;
		}
		get().lastX = get().xPos;
		get().lastY = get().yPos;
		get().xPos = xpos;
		get().yPos = ypos;
		
		if(!get().isDragging) {
			get().startDragX = get().xPos;
			get().startDragY = get().yPos;
		}	
		
		get().dragX = get().xPos - get().startDragX;
		get().dragY = get().yPos - get().startDragY;
	}
	
	public static void mouseButtonCallback(long window, int button, int action, int mods) {
		if(action == GLFW.GLFW_PRESS) {
			get().mouseButtonDown++;
			if (button < get().mouseButtonPressed.length) {
				get().mouseButtonPressed[button] = true;
			}
		}else if (action == GLFW.GLFW_RELEASE) {
			get().mouseButtonDown--;
			if (button < get().mouseButtonPressed.length) {
				get().mouseButtonPressed[button] = false;
				get().isDragging = false;
			}
		}
	}
	
	public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
		get().scrollX = xOffset;
		get().scrollY = yOffset;
	}
	
	public static void endFrame() {
		get().scrollX = 0;
		get().scrollY = 0;
		get().lastX = get().xPos;
		get().lastY = get().yPos;
	}
	
	public static float getX() {
		return (float)get().xPos;
	}
	
	public static float getY() {
		return (float)get().yPos;
	}
	
	public static float getDx() {
		return (float)(get().lastX - get().xPos);
	}
	
	public static float getDy() {
		return (float)(get().lastY - get().yPos);
	}
	
	public static float getDragX() {
		return (float)get().dragX;
	}
	
	public static float getDragY() {
		return (float)get().dragY;
	}
	
	public static float getScrollX() {
		return (float)get().scrollX;
	}
	
	public static float getScrollY() {
		return (float)get().scrollY;
	}
	
	public static boolean isDragging() {
		return get().isDragging;
	}
	
	public static boolean isClicked() {
		return get().isDragging;
	}
	
	public static boolean isReleased() {
		return get().isDragging;
	}
	
	public static boolean mouseButtonDown(int button) {
		if (button < get().mouseButtonPressed.length) {
			return get().mouseButtonPressed[button];
		}else {
			return false;
		}
	}
	
	
	
}
