#version 120

precision mediump float;

uniform sampler2D textureSampler;
uniform int hasTexture;

varying vec3 passColour;
varying vec2 passTextureCords;

uniform float entityId;

void main() 
{
	vec4 colour = vec4(passColour, 1.0);
	vec4 tex_colour;
	
	if(hasTexture == 0) {
		gl_FragData[0] = colour;
	}else {
		tex_colour = vec4(passColour, 1.0) * texture(textureSampler, passTextureCords);
		gl_FragData[0] = tex_colour;
	}
	
	if(tex_colour.a < 0.5) 
	{
		discard;
	}
	
	gl_FragData[1] = vec4(entityId, 1.0, 1.0, 1.0);
}