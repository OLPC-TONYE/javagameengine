#version 120

attribute vec3 position;
attribute vec2 textureCords;
attribute vec3 normals;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 inverseViewMatrix;

varying vec2 passTextureCords;
varying vec4 passWorldPosition;

varying vec3 surfaceNormal;
varying vec3 toCameraVector;

void main()
{

	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	passTextureCords = textureCords;
	passWorldPosition = worldPosition;
	
	surfaceNormal = (transformationMatrix * vec4(normals, 0.0)).xyz;
	toCameraVector = ((inverseViewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
}