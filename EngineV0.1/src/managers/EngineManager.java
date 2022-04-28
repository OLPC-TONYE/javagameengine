package managers;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import assets.mesh.Mesh;
import entities.Entity;
import entitiesComponents.CameraComponent;
import entitiesComponents.Transform;
import opengl.VertexArrayObject;

public class EngineManager {

	public final static int ENGINE_CAMERA_PERSPECTIVE = 0;
	public final static int ENGINE_CAMERA_ORTHOGRAPHIC = 1;
	
	public final static float[] ENGINE_SPRITE_SQUARE = { -0.5f, 0.5f, 0.0f,  -0.5f, -0.5f, 0.0f,  0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f,};
	public final static float[] ENGINE_SPRITE_SQUARE_NORMALS = { 0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,};
	public final static int[] ENGINE_SPRITE_SQUARE_INDICES ={ 0, 1, 3, 3, 1, 2,};
	public final static float[] ENGINE_SPRITE_SQUARE_TEXTURECOORDS = { 0,0, 0,1,  1,1, 1,0};
	
	static float[] positions = {			
			/* */-0.5f,0.5f,-0.5f, /* */-0.5f,-0.5f,-0.5f, /* */0.5f,-0.5f,-0.5f, /* */0.5f,0.5f,-0.5f,	
			/* */-0.5f,0.5f,0.5f, /* */-0.5f,-0.5f,0.5f, /* */0.5f,-0.5f,0.5f, /* */0.5f,0.5f,0.5f,	
			/* */0.5f,0.5f,-0.5f, /* */0.5f,-0.5f,-0.5f, /* */0.5f,-0.5f,0.5f,	/* */0.5f,0.5f,0.5f,
			/* */-0.5f,0.5f,-0.5f,	/* */-0.5f,-0.5f,-0.5f,	/* */-0.5f,-0.5f,0.5f,	/* */-0.5f,0.5f,0.5f,
			/* */-0.5f,0.5f,0.5f, /* */-0.5f,0.5f,-0.5f, /* */0.5f,0.5f,-0.5f, /* */0.5f,0.5f,0.5f,
			/* */-0.5f,-0.5f,0.5f, /* */-0.5f,-0.5f,-0.5f, /* */0.5f,-0.5f,-0.5f, /* */0.5f,-0.5f,0.5f
	};
	
	static int[] indices = {
			/* */0,1,3,	/* */3,1,2,	/* */4,5,7, /* */7,5,6, /* */8,9,11, /* */11,9,10, /* */12,13,15, /* */15,13,14, /* */16,17,19,
			/* */19,17,18, /* */20,21,23, /* */23,21,22

	};
	
	static float[] textureCoords = {
			
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,			
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,			
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,
			/* */0,0, /* */0,1, /* */1,1, /* */1,0,
			/* */0,0, /* */0,1, /* */1,1, /* */1,0
			
	};
	
	static float[] normals = {
			/* */0.0f,0.0f,-1.0f, /* */0.0f,0.0f,-1.0f, /* */0.0f,0.0f,-1.0f, /* */0.0f,0.0f,-1.0f, 
			/* */0.0f,0.0f,1.0f, /* */0.0f,0.0f,1.0f, /* */0.0f,0.0f,1.0f, /* */0.0f,0.0f,1.0f, 
			/* */1.0f,0.0f,0.0f, /* */1.0f,0.0f,0.0f, /* */1.0f,0.0f,0.0f, /* */1.0f,0.0f,0.0f, 
			/* */-1.0f,0.0f,0.0f, /* */-1.0f,0.0f,0.0f, /* */-1.0f,0.0f,0.0f, /* */-1.0f,0.0f,0.0f, 
			/* */0.0f,1.0f,0.0f, /* */0.0f,1.0f,0.0f, /* */0.0f,1.0f,0.0f, /* */0.0f,1.0f,0.0f, 
			/* */0.0f,-1.0f,0.0f, /* */0.0f,-1.0f,0.0f, /* */0.0f,-1.0f,0.0f, /* */0.0f,-1.0f,0.0f,
	};
	
	protected static List<Integer> vaos = new ArrayList<Integer>();
	protected static List<Integer> vbos = new ArrayList<Integer>();
	protected static List<Integer> textures = new ArrayList<Integer>();
	protected static List<Integer> framebuffers = new ArrayList<Integer>();
			
	public static VertexArrayObject loadToVAO(Mesh mesh) {
		VertexArrayObject create = new VertexArrayObject();
		vaos.add(create.getId());
		create.addVertexBufferObject("indices", mesh.getIndices());
		create.setCount(mesh.getIndices().length);
		create.addVertexBufferObject("positions", 0, 3, mesh.getVertices());
		create.addVertexBufferObject("textureCords", 1, 2, mesh.getTextureUVs());
		create.addVertexBufferObject("normals", 2, 3, mesh.getNormals());
		mesh.setVertexArray(create);
		return create;
	}
	
	public static VertexArrayObject loadToVAO(float[] vertices, float[] textureCords,  float[] normals, int[] indices) {
		VertexArrayObject create = new VertexArrayObject();
		vaos.add(create.getId());
		create.addVertexBufferObject("indices", indices);
		create.setCount(indices.length);
		create.addVertexBufferObject("positions", 0, 3, vertices);
		create.addVertexBufferObject("textureCords", 1, 2, textureCords);
		create.addVertexBufferObject("normals", 2, 3, normals);
		return create;
	}
	
	public static VertexArrayObject loadToVAO(float[] vertices, float[] textureCords, int[] indices) {
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
		Transform transform = new Transform();
		transform.setPosition(new Vector3f(0, 0, 10));
		camera.addComponent(transform);
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