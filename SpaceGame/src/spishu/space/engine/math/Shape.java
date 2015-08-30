package spishu.space.engine.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.phys.CollisionResult;


/**
 * A more complex geometrical class, usually used to detect collisions, but has many applications.
 * Utilizes the Vector2D class heavily. Ported from the CodingClub project.
 * 
 * @author SpinyCucumber
 * 
 */
public class Shape {

	public Vec2[] vertices;
	
	public Shape(Vec2...vertices) {
		this.vertices = vertices;
	}

	public Shape translate(Vec2 d) {
		Vec2[] newVertices = new Vec2[vertices.length];
		for(int i = 0; i < vertices.length; i++) {
			newVertices[i] = vertices[i].add(d);
		}
		return new Shape(newVertices);
	}
	
	public Shape rotate(float a) {
		Vec2[] newVertices = new Vec2[vertices.length];
		Matrix2 rot = Matrix2.fromAngle(a);
		//Iterate over vertices
		for(int i = 0; i < vertices.length; i++) {
			newVertices[i] = vertices[i].multiply(rot);
		}
		return new Shape(newVertices);
	}
	
	public Shape divDim(Vec2 dim) {
		Vec2[] newVertices = new Vec2[vertices.length];
		for(int i = 0; i < vertices.length; i++) {
			newVertices[i] = vertices[i].divDim(dim);
		}
		return new Shape(newVertices);
	}
	
	public Vec2 min() {
		
		float x = vertices[0].x, y = vertices[0].y;
		for(int i = 1; i < vertices.length; i++) {
			Vec2 vertex = vertices[i];
			x = Math.min(x, vertex.x);
			y = Math.min(y, vertex.y);
		}
		return new Vec2(x, y);
		
	}
	
	public Vec2 max() {
		
		float x = vertices[0].x, y = vertices[0].y;
		for(int i = 1; i < vertices.length; i++) {
			Vec2 vertex = vertices[i];
			x = Math.max(x, vertex.x);
			y = Math.max(y, vertex.y);
		}
		return new Vec2(x, y);
		
	}
	
	/**
	 * Finds center of shape by taking the midpoint of the upper left bounds and lower right bounds.
	 */
	public Vec2 center() {
		return min().midpoint(max());
	}
	
	/**
	 * Get the axes for testing by normalizing the vectors perpendicular the the edges.
	 */
	public Collection<Vec2> axes() {
		Collection<Vec2> axes = new HashSet<Vec2>();
		for(int i = 0; i < vertices.length; i++) {
			Vec2 p1 = vertices[i];
			Vec2 p2 = vertices[(i + 1) % vertices.length];
			axes.add(p2.sub(p1).normalize().perp());
		}
		return axes;
	}
	
	/**Project the shape onto a 1-dimensional surface, like creating a shadow.*/
	public Vec2 project(Vec2 axis) {
		float min = axis.dot(vertices[0]), max = min;
		for(int i = 1; i < vertices.length; i++) {
			float p = axis.dot(vertices[i]);
			if(p < min) min = p;
			else if(p > max) max = p;
		}
		Vec2 projection = new Vec2(min, max);
		return projection;
	}

	/** Test for collisions using the Seperating-Axis Theorem. The theorem states that if all
	of the overlaps of the shadows of the shapes when projected onto their individual
	axes do not overlap, then the shapes are not colliding. This algorithm works well
	because it test collisions between any kind of shape, as long as the shape is convex.
	* @param b Shape 2
	*/
	public CollisionResult checkCollision(Shape b) {
		
		Set<Vec2> axes = new HashSet<Vec2>();
		axes.addAll(this.axes());
		axes.addAll(b.axes());
		Iterator<Vec2> iterator = axes.iterator();
		
		Vec2 normal = iterator.next();
		float depth = project(normal).overlap1D(b.project(normal));
		if(depth < 0) return null;
		
		while(iterator.hasNext()) {
			
			Vec2 axis = iterator.next(), p1 = project(axis), p2 = b.project(axis);
			float ol = p1.overlap1D(p2);
			
			if (ol < 0) return null;
			if(ol < depth) {
				depth = ol;
				normal = axis;
			}
			
		}
		Vec2 d = center().sub(b.center());
		if(normal.dot(d) > 0) normal = normal.negate();

		return new CollisionResult(normal, depth);
		
	}
	
	public List<Shape> decompose() {
		List<Shape> shapes = new ArrayList<Shape>();
		return shapes;
	}
	
	/**
	 * Draws the shape using GL_LINE_LOOP.
	 * @see GL11
	 */
	public void glLineLoop() {
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for(Vec2 vertex : vertices) vertex.glVertex();
		GL11.glEnd();
	}
	
	@Override
	public String toString() {
		return "Shape [vertices=" + Arrays.toString(vertices) + "]";
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Shape s = Rectangle.fromDimensions(new Vec2(100, 100));
		Shape r = s.rotate((float) Math.toRadians(45));
	}
	
}

