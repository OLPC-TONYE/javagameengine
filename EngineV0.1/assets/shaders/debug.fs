#version 120

precision mediump float;
varying vec3 passColour;

void main() {

	gl_FragData[0] = vec4(passColour, 1.0);
	
	gl_FragData[1] = vec4(0,0,0,0);
}