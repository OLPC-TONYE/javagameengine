package maths;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import entitiesComponents.CameraComponent;


/**
 * {@link Maths} contains functions for calculations .

 */
public class Maths {

	public static float clamp(float min, float max, float value) {
		if(value>max) {
			value = max;
		}else if( value < min) {
			value = min;
		}
		return value;	
		
	}
	
	public static Vector3f calculateRay(float ndsX, float ndsY, CameraComponent camera) {
		Vector4f ray_clip = new Vector4f(ndsX, ndsY, -1, 1);
		Matrix4f inverseProjM = camera.getProjectionMatrix().invert();
		
		Vector4f ray_eye = ray_clip.mul(inverseProjM);
		
		Matrix4f inverseViewM = camera.getViewMatrix().invert();
		ray_eye = new Vector4f(ray_eye.x, ray_eye.y, -1, 0);
		
		ray_eye.mul(inverseViewM);
		Vector3f ray_world = new Vector3f(ray_eye.x, ray_eye.y, ray_eye.z);
		ray_world.normalize();
		return ray_world;
	}
	
	public static Vector3f calculateRayVector(float ndsX, float ndsY, CameraComponent camera) {
		Vector4f ray_clip = new Vector4f(ndsX, ndsY, -1, 1);
		Matrix4f inverseProjM = camera.getProjectionMatrix().invert();
		Matrix4f inverseViewM = camera.getViewMatrix().invert();
		Vector4f ray_eye = ray_clip.mul(inverseProjM.mul(inverseViewM));
		return new Vector3f(ray_eye.x, ray_eye.y, ray_eye.z).normalize();
	}
	
	
	/**
     * 
     * @param camera
     *          the camera component of camera
     * @param ndsX
     *          the x normalised device coordinates
     * @param ndsY
     *          the y normalised device coordinates
     * @return <code>Vector4f</code>
     */
	public static Vector4f calculateGameViewportCords(CameraComponent camera, float ndsX, float ndsY) {
        Vector4f tmp = new Vector4f(ndsX, ndsY, 0, 1);
		Matrix4f inverseProj = camera.getOrthoProjectionMatrix().invert();
		Matrix4f inverseView = camera.getViewMatrix().invert();
		return tmp.mul(inverseProj.mul(inverseView));
	}
	
	/**
     * Test whether the ray of <code>origin</code> and pointing at <code>direction</code> intersect the plane
     * given via normal <code>normal</code> and a point on the plane <code>plane</code>.
     * 
     * @param origin
     *          the origin of the ray
     * @param direction
     *          the direction of the ray
     * @param point
     *          point on the plane
     * @param normal
     *          the normal of the plane
     * @return <b>t</b> of the ray intersects the given plane; <code>-1.0f</code> otherwise
     */
	public static float calculateIntersectPlane(Vector3f origin, Vector3f direction, Vector3f point, Vector3f normal) {
        float denom = direction.dot(normal);
        if (denom < 0.0f) {
        	float d = point.dot(normal);
            float t = (d - origin.dot(normal)) / denom;
            if (t >= 0.0f)
                return t;
        }
        return -1.0f;				
	} 
	
	public static boolean intersectSurfacePlane(Vector3f plane, Vector3f position, Vector3f scale) {
		float distance = plane.distance(position);
		if(distance <= (scale.x/2) & distance <= (scale.y/2)) {
			return true;
		}
		return false;
	}
	
	public static boolean intersectEntityuPlane(Vector3f origin, Vector3f direction, Vector3f position, Vector3f scale, Vector3f normal) {
		float t = calculateIntersectPlane(origin, direction, position, normal);
		Vector3f plane = new Vector3f(direction).mul(t).add(origin);
		return intersectSurfacePlane(plane, position, scale);
	}

}
