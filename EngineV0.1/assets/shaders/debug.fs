#version 120

precision mediump float;
varying vec3 passColour;

void main() {

	gl_FragColor = vec4(passColour, 1.0);
	
}