#version 120

struct Material
{
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	
	float reflectivity;
	float specularPower;
};

struct Attenuation
{
	float constant;
	float linear;
	float exponent;
};

struct DirectionalLight
{
	vec3 colour;
	vec3 position;
	vec3 direction;
	float intensity;
};

struct PointLight
{
	vec3 colour;
	vec3 position;
	float intensity;
	Attenuation att;
};

struct SpotLight
{
	vec3 colour;
	vec3 position;
	vec3 direction;
	
	float intensity;
	float cutOffAngle;
	Attenuation att;
};

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

uniform sampler2D textureSampler;

uniform float ambientLightFactor;
uniform vec3 ambientColour;

uniform Material material;

uniform DirectionalLight directionalLight;
uniform PointLight pointLight[MAX_POINT_LIGHTS];
uniform SpotLight spotLight[MAX_SPOT_LIGHTS];

varying vec2 passTextureCords;
varying vec3 surfaceNormal;
varying vec4 passWorldPosition;

varying vec3 toCameraVector;

uniform float entityId;

vec4 calcLight(DirectionalLight light) {
	
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(light.direction);
	
	float dot1 = dot(unitNormal, unitLightVector);
	float brightness = max(dot1, 0.0);
	vec4 diffuse = brightness * vec4(material.diffuse, 1) * texture(textureSampler, passTextureCords) * vec4(light.colour, 1.0) * light.intensity;
	
	vec3 unitToCameraVector = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	
	float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector), 0.0);
	specularFactor = pow(specularFactor, material.specularPower);
	vec4 specular = specularFactor * vec4(material.specular, 1) * texture(textureSampler, passTextureCords) * material.reflectivity * vec4(light.colour, 1.0) * light.intensity;
		
	return (diffuse + specular);
}

vec4 calcLight(PointLight light) {
	
	vec3 toLight = light.position - passWorldPosition.xyz;
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(toLight);
	
	float dot1 = dot(unitNormal, unitLightVector);
	float brightness = max(dot1, 0.0);
	vec4 diffuse = brightness * vec4(material.diffuse, 1) * texture(textureSampler, passTextureCords) * vec4(light.colour, 1.0) * light.intensity;
	
	vec3 unitToCameraVector = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	
	float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector), 0.0);
	specularFactor = pow(specularFactor, material.specularPower);
	vec4 specular = specularFactor * vec4(material.specular, 1) * texture(textureSampler, passTextureCords) * material.reflectivity * vec4(light.colour, 1.0) * light.intensity;
	
	float distance = length(toLight);
	float attenuationFactor = 1 / (light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance);
		
	return (diffuse + specular) * attenuationFactor;
}

vec4 calcLight(SpotLight light) {
	
	vec3 toLight = light.position - passWorldPosition.xyz;
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(toLight);
	
	float dot1 = dot(unitNormal, unitLightVector);
	float brightness = max(dot1, 0.0);
	vec4 diffuse = brightness * vec4(material.diffuse, 1) * texture(textureSampler, passTextureCords)  * vec4(light.colour, 1.0) * light.intensity;
	
	vec3 unitToCameraVector = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	
	float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector), 0.0);
	specularFactor = pow(specularFactor, material.specularPower);
	vec4 specular = specularFactor * vec4(material.specular, 1) * texture(textureSampler, passTextureCords) * material.reflectivity * vec4(light.colour, 1.0) * light.intensity;
	
	float distance = length(toLight);
	float attenuationFactor = 1 / (light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance);
	
	vec3 unitSpotDirection = normalize(light.direction);
	float spotLightDot = max(dot(unitSpotDirection, lightDirection), 0.0);	
	float spotAttenuationFactor = 1 - (1 - spotLightDot)/(1 - cos(light.cutOffAngle));	
	
	if(spotLightDot < cos(light.cutOffAngle)){
		spotAttenuationFactor = 0;
	}
	
	attenuationFactor *= spotAttenuationFactor;
	
	return (diffuse + specular) * attenuationFactor;
}

void main() 
{	
	// First calculate directional light
	vec4 lightColour = calcLight(directionalLight);
	
	//Calculate all the point lights
	for(int i=0; i < MAX_POINT_LIGHTS; i++){
		if(pointLight[i].intensity > 0){
			lightColour += calcLight(pointLight[i]);
		}
	}
	
	// Calculate all the spot lights
	for(int i=0; i < MAX_SPOT_LIGHTS; i++){
		if(spotLight[i].intensity > 0){
			lightColour += calcLight(spotLight[i]);
		}
	}
	
	vec4 ambient = vec4(material.ambient, 1) * texture(textureSampler, passTextureCords);
	vec4 ambientLight = ambient * vec4(ambientLightFactor, ambientLightFactor, ambientLightFactor, 1);
	
	gl_FragData[0] = (vec4(ambientColour, 1) * ambientLight) + lightColour ;

	if(ambient.a < 0.5) 
	{
		discard;
	}
	
	gl_FragData[1] = vec4(entityId, 1.0, 1.0, 1.0);
	
	gl_FragData[2] = vec4(0, 0, 0, 0);
}