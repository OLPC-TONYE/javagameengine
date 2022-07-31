package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import assets.Mesh;
import managers.EngineManager;
import opengl.VertexArrayObject;

public class MeshLoader {
	
	static String defaultpath = "assets/models/";
	
	public static Mesh loadFromObj(String filename) {
		String filepath = defaultpath+filename+".obj";
		FileReader fr = null;
		try {
			fr = new FileReader(new File(filepath));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't load file!");
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] positionsArray = null;
		float[] normalArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		try {
						
			while(true) {
				line = reader.readLine();
				String[] currentline = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentline[1]), Float.parseFloat(currentline[2]), Float.parseFloat(currentline[3]));
					vertices.add(vertex);
				}
				else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currentline[1]), Float.parseFloat(currentline[2]));
					textures.add(texture);
				}
				else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentline[1]), Float.parseFloat(currentline[2]), Float.parseFloat(currentline[3]));
					normals.add(normal);

				}
				else if (line.startsWith("f ")) {
					textureArray = new float[vertices.size()*2];
					normalArray = new float[vertices.size()*3];
					break;
				}
			}
			
			while(line!=null) {
				if(!line.startsWith("f ")) {
					line = reader.readLine();
					continue;
				}
				String[] currentline = line.split(" ");
				String[] vertex1 = currentline[1].split("/");
				String[] vertex2 = currentline[2].split("/");
				String[] vertex3 = currentline[3].split("/");
				
				processVertex(vertex1,indices,textures,normals,textureArray,normalArray);
				processVertex(vertex2,indices,textures,normals,textureArray,normalArray);
				processVertex(vertex3,indices,textures,normals,textureArray,normalArray);
				line = reader.readLine();
			}
			reader.close();
				
		}catch(Exception e) {
		 	e.printStackTrace();
		}
		
		positionsArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		
		int vertexPointer = 0;
		for(Vector3f vertex:vertices) {
			positionsArray[vertexPointer++] = vertex.x;
			positionsArray[vertexPointer++] = vertex.y;
			positionsArray[vertexPointer++] = vertex.z;
		}
		
		for(int i=0;i<indices.size();i++) {
			indicesArray[i] = indices.get(i);
		}
		
		return new Mesh(filename, positionsArray, textureArray, normalArray, indicesArray);
	}
	
	public static VertexArrayObject loadRawObjModel(String filename) {
		String filepath = defaultpath+filename+".obj";
		FileReader fr = null;
		try {
			fr = new FileReader(new File(filepath));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't load file!");
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] positionsArray = null;
		float[] normalArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		try {
						
			while(true) {
				line = reader.readLine();
				String[] currentline = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentline[1]), Float.parseFloat(currentline[2]), Float.parseFloat(currentline[3]));
					vertices.add(vertex);
				}
				else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currentline[1]), Float.parseFloat(currentline[2]));
					textures.add(texture);
				}
				else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentline[1]), Float.parseFloat(currentline[2]), Float.parseFloat(currentline[3]));
					normals.add(normal);

				}
				else if (line.startsWith("f ")) {
					textureArray = new float[vertices.size()*2];
					normalArray = new float[vertices.size()*3];
					break;
				}
			}
			
			while(line!=null) {
				if(!line.startsWith("f ")) {
					line = reader.readLine();
					continue;
				}
				String[] currentline = line.split(" ");
				String[] vertex1 = currentline[1].split("/");
				String[] vertex2 = currentline[2].split("/");
				String[] vertex3 = currentline[3].split("/");
				
				processVertex(vertex1,indices,textures,normals,textureArray,normalArray);
				processVertex(vertex2,indices,textures,normals,textureArray,normalArray);
				processVertex(vertex3,indices,textures,normals,textureArray,normalArray);
				line = reader.readLine();
			}
			reader.close();
				
		}catch(Exception e) {
		 	e.printStackTrace();
		}
		
		positionsArray = new float[vertices.size()*3];
		indicesArray = new int[indices.size()];
		
		int vertexPointer = 0;
		for(Vector3f vertex:vertices) {
			positionsArray[vertexPointer++] = vertex.x;
			positionsArray[vertexPointer++] = vertex.y;
			positionsArray[vertexPointer++] = vertex.z;
		}
		
		for(int i=0;i<indices.size();i++) {
			indicesArray[i] = indices.get(i);
		}
		
		return EngineManager.loadToVAO(positionsArray, textureArray, normalArray, indicesArray);
	}
	
	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1])-1);
		textureArray[currentVertexPointer*2] = currentTex.x;
		textureArray[currentVertexPointer*2+1] = 1 - currentTex.y;
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
		normalsArray[currentVertexPointer*3] = currentNorm.x;
		normalsArray[currentVertexPointer*3+1] = currentNorm.y;
		normalsArray[currentVertexPointer*3+2] = currentNorm.z;
		
	}

}
