package tools;

import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector3f;
import org.joml.Vector4f;

import entitiesComponents.CameraComponent;
import entitiesComponents.Transform;


/**
 * {@link Maths} contains functions for calculations .

 */
public class Maths {
	
	private static Random random = new Random();

	public static float clamp(float min, float max, float value) {
		if(value>max) {
			value = max;
		}else if( value < min) {
			value = min;
		}
		return value;		
	}
	
	public static double clamp(double min, double max, double value) {
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
	
	public static Vector4f calculateGameViewportCords(CameraComponent camera, Transform object) {
        Vector4f tmp = new Vector4f(-10, 0, 0, 1);
		Matrix4f inverseProj = camera.getProjectionMatrix().invert();
		Matrix4f inverseView = camera.getViewMatrix().invert();
		Matrix4f inverseTransform = object.getTransformationMatrix().invert();
		return tmp.mul(inverseProj.mul(inverseView.mul(inverseTransform)));
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
	
	public static Matrix4f getTransformationMatrix(Vector3f position, Vector3f rotation, Vector3f scale) {
		Matrix4f transformationMatrix = new Matrix4f();
		
		transformationMatrix.identity();
		transformationMatrix.translate(position);
		transformationMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
		transformationMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
		transformationMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
		transformationMatrix.scale(scale);
		return transformationMatrix;
	}
	 
	public static Matrix4f getInvertedMatrix(Matrix4f matrix) {
		Matrix4f inverted = new Matrix4f(matrix).invert();	
		return inverted;
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
	
	
	public static Matrix4f mul(Matrix4f matrix, float scalar) {
		Matrix4f Matrix = new Matrix4f(matrix);
		
		float m00, m01, m02, m03;
	    float m10, m11, m12, m13;
	    float m20, m21, m22, m23;
	    float m30, m31, m32, m33;
	    
	    m00 = Matrix.m00() * scalar;
	    m01 = Matrix.m01() * scalar;
	    m02 = Matrix.m02() * scalar;
	    m03 = Matrix.m03() * scalar;
	    m10 = Matrix.m10() * scalar;
	    m11 = Matrix.m11() * scalar;
	    m12 = Matrix.m12() * scalar;
	    m13 = Matrix.m13() * scalar;
	    m20 = Matrix.m20() * scalar;
	    m21 = Matrix.m21() * scalar;
	    m22 = Matrix.m22() * scalar;
	    m23 = Matrix.m23() * scalar;
	    m30 = Matrix.m30() * scalar;
	    m31 = Matrix.m31() * scalar;
	    m32 = Matrix.m32() * scalar;
	    m33 = Matrix.m33() * scalar;
	    
		return Matrix.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
	}
	
	public static int randomInt(int n) {
		return random.nextInt(n);
	}
	
	public static int randomNInt() {
		int newNumber = 0;
		for(int i=0; i < 9; i++) {
			int unit = (int) (random.nextInt(9) * Math.pow(10, i));
			newNumber += unit;
		}
		return Math.abs(newNumber);
	}
}
