#version 120

uniform sampler2D textureSampler;
uniform int hasTexture;

uniform vec3 colour;
varying vec2 passTextureCords;

uniform float entityId;

void main() 
{
	vec4 colours = vec4(colour, 1.0);
	vec4 tex_colour;
	
	if(hasTexture == 0) {
		gl_FragData[0] = colours;
	}else {
		tex_colour = vec4(colour, 1.0) * texture(textureSampler, passTextureCords);
		gl_FragData[0] = tex_colour;
	}
	
	if(tex_colour.a < 0.5) 
	{
		discard;
	}
	
	gl_FragData[1] = vec4(entityId, 1.0, 1.0, 1.0);
	
	gl_FragData[2] = vec4(0, 0, 0, 0);
}