#version 120

struct Material
{
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	
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

uniform sampler2D textureSampler;

uniform float ambientLightFactor;

uniform Material material;

uniform DirectionalLight directionalLight;
uniform PointLight pointLight;
uniform SpotLight spotLight;

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
	vec4 diffuse = brightness * material.diffuse * vec4(light.colour, 1.0) * light.intensity;
	
	vec3 unitToCameraVector = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	
	float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector), 0.0);
	specularFactor = pow(specularFactor, material.specularPower);
	vec4 specular = specularFactor * material.specular * material.reflectivity * vec4(light.colour, 1.0) * light.intensity;
		
	return (diffuse + specular);
}

vec4 calcLight(PointLight light) {
	
	vec3 toLight = light.position - passWorldPosition.xyz;
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(toLight);
	
	float dot1 = dot(unitNormal, unitLightVector);
	float brightness = max(dot1, 0.0);
	vec4 diffuse = brightness * material.diffuse * vec4(light.colour, 1.0) * light.intensity;
	
	vec3 unitToCameraVector = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	
	float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector), 0.0);
	specularFactor = pow(specularFactor, material.specularPower);
	vec4 specular = specularFactor * material.specular * material.reflectivity * vec4(light.colour, 1.0) * light.intensity;
	
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
	vec4 diffuse = brightness * material.diffuse * vec4(light.colour, 1.0) * light.intensity;
	
	vec3 unitToCameraVector = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	
	float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector), 0.0);
	specularFactor = pow(specularFactor, material.specularPower);
	vec4 specular = specularFactor * material.specular * material.reflectivity * vec4(light.colour, 1.0) * light.intensity;
	
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
	
	vec4 lightColour = calcLight(directionalLight);
	lightColour += calcLight(pointLight);
	lightColour += calcLight(spotLight);
	
	vec4 ambient = material.ambient * texture(textureSampler, passTextureCords);
	vec4 ambientLight = ambient * vec4(ambientLightFactor, ambientLightFactor, ambientLightFactor, 1);
	
	gl_FragData[0] = ambientLight + lightColour ;

	if(ambient.a < 0.5) 
	{
		discard;
	}
	
	gl_FragData[1] = vec4(entityId, 1.0, 1.0, 1.0);
	
	gl_FragData[2] = vec4(0, 0, 0, 0);
}