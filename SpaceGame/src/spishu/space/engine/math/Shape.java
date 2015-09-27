package spishu.space.engine.math;

import java.util.ArrayList;
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

	protected List<Vec2> vertices; //Using list for flexibility.
	
	public Shape(List<Vec2> vertices) {
		this.vertices = vertices;
	}

	public List<Vec2> getVertices() {
		return vertices;
	}

	/**
	 * Translate shape by moving all vertices. Faster than a matrix.
	 * @param d Translation vector
	 * @return Translated shape
	 */
	public Shape translate(Vec2 d) { //Most methods follow this format
		List<Vec2> newVertices = new ArrayList<Vec2>();
		for(Vec2 vertex : vertices) newVertices.add(vertex.add(d));
		return new Shape(newVertices);
	}
	
	/**
	 * Apply a 2x2 transformation matrix.
	 * @param mat Matrix
	 * @return Transformed shape
	 */
	public Shape transform(Matrix2 mat) {
		List<Vec2> newVertices = new ArrayList<Vec2>();
		for(Vec2 vertex : vertices) newVertices.add(vertex.mul(mat));
		return new Shape(newVertices);
	}
	
	/**
	 * Generate rotation matrix and calls transform.
	 * Translate shape first to obtain new pivot point.
	 * @param a Rotation in radians.
	 * @return Rotated shape.
	 */
	public Shape rotate(float a) {
		return transform(Matrix2.fromVec(Vec2.fromAngle(a)));
	}
	
	/**
	 * Scales vertices element-wise.
	 * Useful for shrinking or growing shapes to fit a specified space.
	 */
	public Shape divDim(Vec2 dim) {
		List<Vec2> newVertices = new ArrayList<Vec2>();
		for(Vec2 vertex : vertices) newVertices.add(vertex.divDim(dim));
		return new Shape(newVertices);
	}
	
	/**
	 * Subdivides each edge into 2,
	 * effectively generating a new shape with 2 times the number of vertices.
	 * <p> "Simulates" detail.
	 * @return Subdivided shape
	 */
	public Shape subdivide() {
		List<Vec2> edges = edges(), newVertices = new ArrayList<Vec2>();
		for(int i = 0; i < vertices.size(); i++) {
			Vec2 p = vertices.get(i), edge = edges.get(i);
			newVertices.add(p.add(edge.scale(1f/3)));
			newVertices.add(p.add(edge.scale(2f/3)));
		}
		return new Shape(newVertices);
	}
	
	/**
	 * Iterative subdivision.
	 */
	public Shape subdivide(int iters) {
		Shape shape = this;
		for(int i = 0; i < iters; i++) shape = shape.subdivide();
		return shape;
	}
	
	/**
	 * @return Top-left bounds in shape.
	 */
	public Vec2 min() {
		
		float x = vertices.get(0).x, y = vertices.get(0).y;
		for(Vec2 vertex : vertices) {
			x = Math.min(x, vertex.x);
			y = Math.min(y, vertex.y);
		}
		return new Vec2(x, y);
		
	}
	
	/**
	 * @return Bottom-right bounds in shape.
	 */
	public Vec2 max() {
		
		float x = vertices.get(0).x, y = vertices.get(0).y;
		for(Vec2 vertex : vertices) {
			x = Math.max(x, vertex.x);
			y = Math.max(y, vertex.y);
		}
		return new Vec2(x, y);
		
	}
	
	/**
	 * Obtains the centroid (average of all vertices)
	 * by taking the midpoint of the top-left bounds and bottom-right bounds.
	 * @return Centroid
	 */
	public Vec2 center() {
		return min().midpoint(max());
	}
	
	/**
	 * Returns edges, or difference between adjacent vertices.
	 */
	public List<Vec2> edges() {
		List<Vec2> edges = new ArrayList<Vec2>();
		for(int i = 0; i < vertices.size(); i++) {
			Vec2 a = vertices.get(i), b = vertices.get(i+1 % vertices.size());
			edges.add(b.sub(a));
		}
		return edges;
	}
	
	/**
	 * Get the axes for testing by normalizing the vectors perpendicular the the edges.
	 */
	public List<Vec2> normals() {
		List<Vec2> normals = new ArrayList<Vec2>();
		for(Vec2 edge : edges()) normals.add(edge.normalize().perp());
		return normals;
	}
	
	/**
	 * Project the shape onto a 1-dimensional surface, like creating a shadow.
	 * @return Range of projection, represented by a 2d vector.
	 */
	public Vec2 project(Vec2 axis) {
		float min = axis.dot(vertices.get(0)), max = min;
		for(int i = 1; i < vertices.size(); i++) {
			float p = axis.dot(vertices.get(i));
			if(p < min) min = p;
			else if(p > max) max = p;
		}
		Vec2 projection = new Vec2(min, max);
		return projection;
	}

	/**
	 * Test for collisions using the Seperating-Axis Theorem. The theorem states that if all
	 * of the overlaps of the shadows of the shapes when projected onto their individual
	 * axes do not overlap, then the shapes are not colliding.
	 * @param b Shape to test against
	 */
	public CollisionResult checkCollision(Shape b) {
		
		Set<Vec2> axes = new HashSet<Vec2>();
		axes.addAll(this.normals());
		axes.addAll(b.normals());
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
	
	/**
	 * Recursively decomposes shape into a set of convex shapes.
	 * @return List of convex shapes
	 */
	public List<Shape> decompose() { //TODO
		List<Shape> shapes = new ArrayList<Shape>();
		return shapes;
	}
	
	/**
	 * Draws the shape using GL_LINE_LOOP. Convenience!
	 * @see GL11
	 */
	public void glLineLoop() {
		GL11.glBegin(GL11.GL_LINE_LOOP);
		drawVertices();
		GL11.glEnd();
	}
	
	/**
	 * Calls glVertex on each vertex.
	 * drawTexCoords is redundant because you need to call a vertex in between texCoords.
	 */
	public void drawVertices() {
		for(Vec2 vertex : vertices) vertex.glVertex();
	}
	
	@Override
	public String toString() {
		return "Shape [vertices=" + vertices + "]";
	}
	
	/**
	 * Draws vertices and texCoords in progression. Sizes must be same.
	 */
	public static void draw(Shape vertices, Shape texCoords) {
		if(vertices.vertices.size() != texCoords.vertices.size())
			throw new IllegalArgumentException("Number of vertices must be same.");
		for(int i = 0; i < vertices.vertices.size(); i++) {
			texCoords.vertices.get(i).glTexCoord();
			vertices.vertices.get(i).glVertex();
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Shape s = Rectangle.fromDimensions(new Vec2(100, 100));
		Shape r = s.rotate((float) Math.toRadians(45));
	}
	
}

