package engine;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Entity;
import entitiesComponents.CameraComponent;
import entitiesComponents.Transform;
import opengl.Texture;
import opengl.VertexArrayObject;

public class EngineManager {

	public final static int ENGINE_CAMERA_PERSPECTIVE = 0;
	public final static int ENGINE_CAMERA_ORTHOGRAPHIC = 1;
	
	public final static float[] ENGINE_SPRITE_SQUARE = { -0.5f, 0.5f, 0.0f,  -0.5f, -0.5f, 0.0f,  0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f,};
	public final static int[] ENGINE_SPRITE_SQUARE_INDICES ={ 0, 1, 3, 3, 1, 2,};
	public final static float[] ENGINE_SPRITE_SQUARE_TEXTURECOORDS = { 0,0, 0,1,  1,1, 1,0};
	
	protected static List<Integer> vaos = new ArrayList<Integer>();
	protected static List<Integer> vbos = new ArrayList<Integer>();
	protected static List<Integer> textures = new ArrayList<Integer>();
	protected static List<Integer> framebuffers = new ArrayList<Integer>();
		
	public static Map<String, Texture> textureAssets = new HashMap<>();
	public static Map<String, Texture> iconTextureAssets = new HashMap<>();
	
	public static Texture getTexture(String textureName) {
		String path = "assets/textures/"+textureName+".png";
		File file = new File(path);
		if (EngineManager.textureAssets.containsKey(file.getAbsolutePath())) {
			return EngineManager.textureAssets.get(file.getAbsolutePath());
		}else {
			System.out.println("Texture Not Found, Creating");
			Texture texture = new Texture(path, textureName);
			EngineManager.textureAssets.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}
	
	public static Texture getTextureWithPath(String texture_path) {
		File file = new File(texture_path);
		if (EngineManager.textureAssets.containsKey(file.getAbsolutePath())) {
			return EngineManager.textureAssets.get(file.getAbsolutePath());
		}else {
			System.out.println("Texture Not Found, Creating "+file.getName().split("\\.")[0]);
			Texture texture = new Texture(texture_path, file.getName().split("\\.")[0]);
			EngineManager.textureAssets.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}
	
	public static Texture getIconTexture(String textureName) {
		String path = "res/icons/"+textureName+".png";
		File file = new File(path);
		if (EngineManager.iconTextureAssets.containsKey(file.getAbsolutePath())) {
			return EngineManager.iconTextureAssets.get(file.getAbsolutePath());
		}else {
			Texture texture = new Texture(path, textureName);
			EngineManager.iconTextureAssets.put(file.getAbsolutePath(), texture);
			return texture;
		}
	}
	
//	===============================================================
	
	public static VertexArrayObject loadToVAO(float[] vertices, int[] indices, float[] textureCords) {
		VertexArrayObject create = new VertexArrayObject();
		vaos.add(create.getId());
		create.addVertexBufferObject("indices", indices);
		create.setCount(indices.length);
		create.addVertexBufferObject("positions", 0, 3, vertices);
		create.addVertexBufferObject("textureCords", 1, 2, textureCords);
		return create;
	}
	
	public static VertexArrayObject loadToVAO(float[] vertices, int[] indices) {
		VertexArrayObject vao = new VertexArrayObject();
		vaos.add(vao.getId());
		vao.addVertexBufferObject("indices", indices);
		vao.setCount(indices.length);
		vao.addVertexBufferObject("positions", 0, 3, vertices);
		return vao;
	}
	
	public static VertexArrayObject loadToVAO(float[] positions) {
		VertexArrayObject vao = new VertexArrayObject();
		vaos.add(vao.getId());
		vao.addVertexBufferObject("position", 0, 3, positions);
		vao.setCount(positions.length/3);
		return vao;
	}
		
	public static Entity createCamera() {
		Entity camera = new Entity();
		camera.setName("Camera");
		camera.addComponent(new Transform());
		CameraComponent cp = new CameraComponent();
		cp.setCameraProjection(ENGINE_CAMERA_PERSPECTIVE);
		cp.setPerpsProperties(0.01f, 1000f, 70);
		cp.setCameraSize(510, 320);
		cp.setClearColour(0.06f, 0.06f, 0.06f, 0.0f);

		camera.addComponent(cp);
		return camera;
	}
	
	public static Entity create2DCamera() {
		Entity camera = new Entity();
		camera.setName("Camera");
		camera.addComponent(new Transform());
		CameraComponent cp = new CameraComponent();
		cp.setCameraProjection(ENGINE_CAMERA_ORTHOGRAPHIC);
		cp.setOrthoProperties(1f, -1f, 70, 5);
		cp.setCameraSize(510, 320);

		camera.addComponent(cp);
		return camera;
	}
	
	public static Entity create3DCamera() {
		Entity camera = new Entity();
		camera.setName("Camera");
		camera.addComponent(new Transform());
		CameraComponent cp = new CameraComponent();
		cp.setCameraProjection(ENGINE_CAMERA_PERSPECTIVE);
		cp.setPerpsProperties(0.01f, 1000f, 70);
		cp.setCameraSize(510, 320);
		camera.addComponent(cp);
		return camera;
	}
	
	
	public static void addVBOS(int vboID) {
		vbos.add(vboID);
	}
	
	public static void addTexture(int textureID) {
		textures.add(textureID);
	}
	
	public static void addFramebuffer(int framebufferID) {
		framebuffers.add(framebufferID);
	}
	
	public static void cleanUp() {
		for(int vao:vaos) {
			glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos) {
			glDeleteBuffers(vbo);
		}
		for(int texture:textures) {
			glDeleteTextures(texture);
		}
	}

}
