#version 120

uniform float id;
uniform vec3 colour;

void main() 
{
	

	gl_FragData[0] = vec4(colour, 0.9);
	
	gl_FragData[1] = vec4(0, 0, 0, 0);
}