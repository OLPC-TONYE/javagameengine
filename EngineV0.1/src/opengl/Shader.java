package opengl;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils; 

public class Shader {
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private boolean usingShader;
	private boolean isLinked;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public Shader(String shaderName) {
		
		String fileVertex = "assets/shaders/"+shaderName+".vs";
		String fileFragment = "assets/shaders/"+shaderName+".fs";
		
		vertexShaderID = loadShader(fileVertex, GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fileFragment, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);		
	}
	
	public void link() {
		glLinkProgram(programID);
		isLinked = true;
		glValidateProgram(programID);
	}
	
	public void start() {
		if(!usingShader) {
			glUseProgram(programID);
			usingShader = true;
		}
	}
	
	public void stop() {
		glUseProgram(0);
		usingShader = false;
	}
	
	public void cleanUp() {
		if(usingShader) {
			stop();
		}
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		glDeleteProgram(programID);
	}
		
	public void loadFloat(String uniformName, float value) {
		if(!usingShader) {
			start();
		}
		int location = glGetUniformLocation(programID, uniformName);
		glUniform1f(location, value);
	}
	
	public void loadTexture(String uniformName, int slot) {
		if(!usingShader) {
			start();
		}
		int location = glGetUniformLocation(programID, uniformName);
		glUniform1f(location, slot);
	}
	
	public void loadVector(String uniformName, Vector3f vector) {
		if(!usingShader) {
			start();
		}
		int location = glGetUniformLocation(programID, uniformName);
		glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	public void loadBoolean(String uniformName, boolean value) {
		if(!usingShader) {
			start();
		}
		int toLoad = 0;
		if(value) {
			toLoad = 1;
		}
		int location = glGetUniformLocation(programID, uniformName);
		glUniform1i(location, toLoad);
	}
	
	public void loadMatrix(String uniformName, Matrix4f matrix) {
		start();
		matrix.get(matrixBuffer);
		int location = glGetUniformLocation(programID, uniformName);
		glUniformMatrix4fv(location, false, matrixBuffer);
	}
	
	public int getAttribLocation(String name) {
		return glGetAttribLocation(programID, name);
	}

	public void bindAttribute(int attributeLocation, String attribute) {
		if(!isLinked) {
			glBindAttribLocation(programID, attributeLocation, attribute);
		}else {
			System.err.println("Shader Already Linked");
		}
	}
	
	public void enableAttributeArray(int index) {
		glEnableVertexAttribArray(index);
	}
	
	public void disableAttributeArray(int index) {
		glDisableVertexAttribArray(index);
	}

	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) !=null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		}catch(IOException e) {
			System.err.println("Could not read file");
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);

		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE){
			System.out.println(glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader");
		}
		return shaderID;
	}
}
