#version 120

attribute vec3 position;

uniform vec3 colour;
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

varying vec3 passColour;

void main()
{

	gl_Position = projectionMatrix * viewMatrix *  vec4(position, 1.0);
	passColour = colour;
}