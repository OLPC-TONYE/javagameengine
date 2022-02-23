#version 120

precision mediump float;

uniform sampler2D textureSampler;
uniform int hasTexture;

varying vec3 passColour;
varying vec2 passTextureCords;

void main() {

	if(hasTexture == 0) {
		gl_FragColor = vec4(passColour, 1.0);
	}else {
		gl_FragColor = vec4(passColour, 1.0) * texture(textureSampler, passTextureCords);
	}
	
}