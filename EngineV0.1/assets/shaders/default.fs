#version 120

precision mediump float;

uniform sampler2D textureSampler;
uniform int hasTexture;

varying vec3 passColour;
varying vec2 passTextureCords;

uniform float entityId;

void main() {

	if(hasTexture == 0) {
		gl_FragData[0] = vec4(passColour, 1.0);
	}else {
		gl_FragData[0] = vec4(passColour, 1.0) * texture(textureSampler, passTextureCords);
	}
	
	gl_FragData[1] = vec4(entityId, 1.0, 1.0, 1.0);
}