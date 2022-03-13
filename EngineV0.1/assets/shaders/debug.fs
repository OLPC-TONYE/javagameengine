#version 120

uniform vec3 colour;

void main() {

	gl_FragData[0] = vec4(colour, 1.0);
	
	gl_FragData[1] = vec4(0,0,0,0);
	
	gl_FragData[2] = vec4(0,0,0,0);
}