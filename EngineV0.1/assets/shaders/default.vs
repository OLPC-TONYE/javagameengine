#version 120

attribute vec3 position;
attribute vec2 textureCords;

uniform vec3 colour;
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

varying vec3 passColour;
varying vec2 passTextureCords;

void main()
{

	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
	passColour = colour;
	passTextureCords = textureCords;
}