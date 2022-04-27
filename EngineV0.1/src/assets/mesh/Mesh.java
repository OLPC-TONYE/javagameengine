package assets.mesh;

import assets.Asset;
import assets.AssetType;
import engine.EngineManager;
import opengl.VertexArrayObject;

public class Mesh extends Asset{
	
	float[] vertices;
	float[] textureUVs;
	float[] normals;
	int[] indices;
	
	Material material = new Material("material");	
	transient VertexArrayObject vertexArray;
	
	public Mesh() {
		super("Mesh", AssetType.Mesh);
	}
	
	/**
	 * @param vertices
	 * @param textureUVs
	 * @param normals
	 * @param indices
	 */
	public Mesh(String name, float[] vertices, float[] textureUVs, float[] normals, int[] indices) {
		super(name, AssetType.Mesh);
		this.vertices = vertices;
		this.textureUVs = textureUVs;
		this.normals = normals;
		this.indices = indices;
	}
	
	/**
	 * @param vertices
	 * @param textureUVs
	 * @param normals
	 * @param indices
	 */
	public Mesh(float[] vertices, float[] textureUVs, float[] normals, int[] indices) {
		super("Mesh", AssetType.Mesh);
		this.vertices = vertices;
		this.textureUVs = textureUVs;
		this.normals = normals;
		this.indices = indices;
	}
		
	public void setData(float[] vertices, float[] textureUVs, float[] normals, int[] indices) {
		this.vertices = vertices;
		this.textureUVs = textureUVs;
		this.normals = normals;
		this.indices = indices;
	}

	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

	public void setTextureUVs(float[] textureUVs) {
		this.textureUVs = textureUVs;
	}

	public void setNormals(float[] normals) {
		this.normals = normals;
	}

	public void setIndices(int[] indices) {
		this.indices = indices;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureUVs() {
		return textureUVs;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public VertexArrayObject getVertexArray() {
		if(vertexArray == null) {
			EngineManager.loadToVAO(this);
		}
		return vertexArray;
	}

	public void setVertexArray(VertexArrayObject vertexArray) {
		this.vertexArray = vertexArray;
	}

	@Override
	public void copy(Asset from) {
		if(from == null) return;
		if(!(from instanceof Mesh)) return;
		
		Mesh mesh = (Mesh) from;
		
		this.indices = mesh.getIndices();
		this.vertices = mesh.getVertices();
		this.textureUVs = mesh.getTextureUVs();
		this.normals = mesh.getNormals();
		this.material = new Material("");
		this.material.copy(mesh.getMaterial());
	}

}
