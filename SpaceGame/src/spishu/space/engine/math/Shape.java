package spishu.space.engine.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.assets.CollisionResult;


/**
 * A more complex geometrical class, usually used to detect collisions, but has many applications.
 * Utilizes the Vector2D class heavily. Ported from the CodingClub project.
 * 
 * @author SpinyCucumber
 * 
 */
public class Shape {

	protected List<Vec2d> vertices; //Using list for flexibility.
	
	public Shape(List<Vec2d> vertices) {
		this.vertices = vertices;
	}
	
	public Shape(Vec2d...vertices) { //Convenience
		this(Arrays.asList(vertices));
	}

	public List<Vec2d> getVertices() {
		return vertices;
	}

	/**
	 * Translate shape by moving all vertices. Faster than a matrix.
	 * @param d Translation vector
	 * @return Translated shape
	 */
	public Shape translate(Vec2d d) { //Most methods follow this format
		List<Vec2d> newVertices = new ArrayList<Vec2d>();
		for(Vec2d vertex : vertices) newVertices.add(vertex.add(d));
		return new Shape(newVertices);
	}
	
	/**
	 * Apply a 2x2 transformation matrix.
	 * @param mat Matrix
	 * @return Transformed shape
	 */
	public Shape transform(Matrix2d mat) {
		List<Vec2d> newVertices = new ArrayList<Vec2d>();
		for(Vec2d vertex : vertices) newVertices.add(vertex.mul(mat));
		return new Shape(newVertices);
	}
	
	/**
	 * Generate rotation matrix and calls transform.
	 * Translate shape first to obtain new pivot point.
	 * @param a Rotation in radians.
	 * @return Rotated shape.
	 */
	public Shape rotate(float a) {
		return transform(Matrix2d.fromVec(Vec2d.fromAngle(a)));
	}
	
	/**
	 * Scales vertices element-wise.
	 * Useful for shrinking or growing shapes to fit a specified space.
	 */
	public Shape divDim(Vec2d dim) {
		List<Vec2d> newVertices = new ArrayList<Vec2d>();
		for(Vec2d vertex : vertices) newVertices.add(vertex.divDim(dim));
		return new Shape(newVertices);
	}
	
	/**
	 * Subdivides each edge into 2,
	 * effectively generating a new shape with 2 times the number of vertices.
	 * <p> "Simulates" detail.
	 * @return Subdivided shape
	 */
	public Shape subdivide() {
		List<Vec2d> edges = edges(), newVertices = new ArrayList<Vec2d>();
		for(int i = 0; i < vertices.size(); i++) {
			Vec2d p = vertices.get(i), edge = edges.get(i);
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
	public Vec2d min() {
		
		float x = vertices.get(0).x, y = vertices.get(0).y;
		for(Vec2d vertex : vertices) {
			x = Math.min(x, vertex.x);
			y = Math.min(y, vertex.y);
		}
		return new Vec2d(x, y);
		
	}
	
	/**
	 * @return Bottom-right bounds in shape.
	 */
	public Vec2d max() {
		
		float x = vertices.get(0).x, y = vertices.get(0).y;
		for(Vec2d vertex : vertices) {
			x = Math.max(x, vertex.x);
			y = Math.max(y, vertex.y);
		}
		return new Vec2d(x, y);
		
	}
	
	/**
	 * Obtains the centroid (average of all vertices)
	 * by taking the midpoint of the top-left bounds and bottom-right bounds.
	 * @return Centroid
	 */
	public Vec2d center() {
		return min().midpoint(max());
	}
	
	/**
	 * Calculates edge, or difference between next vertex. (right)
	 */
	public Vec2d edge(int index) {
		Vec2d a = vertices.get(index), b = vertices.get((index+1) % vertices.size());
		return b.sub(a);
	}
	
	/**
	 * Returns a list of all edges.
	 */
	public List<Vec2d> edges() {
		List<Vec2d> edges = new ArrayList<Vec2d>();
		for(int i = 0; i < vertices.size(); i++) edges.add(edge(i));
		return edges;
	}
	
	/**
	 * Get the axes for testing by normalizing the vectors perpendicular the the edges.
	 */
	public List<Vec2d> normals() {
		List<Vec2d> normals = new ArrayList<Vec2d>();
		for(Vec2d edge : edges()) normals.add(edge.normalize().perp());
		return normals;
	}
	
	/**
	 * Project the shape onto a 1-dimensional surface, like creating a shadow.
	 * @return Range of projection, represented by a 2d vector.
	 */
	public Vec2d project(Vec2d axis) {
		float min = axis.dot(vertices.get(0)), max = min;
		for(int i = 1; i < vertices.size(); i++) {
			float p = axis.dot(vertices.get(i));
			if(p < min) min = p;
			else if(p > max) max = p;
		}
		Vec2d projection = new Vec2d(min, max);
		return projection;
	}

	/**
	 * Test for collisions using the Seperating-Axis Theorem. The theorem states that if all
	 * of the overlaps of the shadows of the shapes when projected onto their individual
	 * axes do not overlap, then the shapes are not colliding.
	 * @param b Shape to test against
	 */
	public CollisionResult checkCollision(Shape b) {
		
		Set<Vec2d> axes = new HashSet<Vec2d>();
		axes.addAll(this.normals());
		axes.addAll(b.normals());
		Iterator<Vec2d> iterator = axes.iterator();
		
		Vec2d normal = iterator.next();
		float depth = project(normal).overlap1D(b.project(normal));
		if(depth < 0) return null;
		
		while(iterator.hasNext()) {
			
			Vec2d axis = iterator.next(), p1 = project(axis), p2 = b.project(axis);
			float ol = p1.overlap1D(p2);
			
			if (ol < 0) return null;
			if(ol < depth) {
				depth = ol;
				normal = axis;
			}
			
		}
		Vec2d d = center().sub(b.center());
		if(normal.dot(d) > 0) normal = normal.negate();

		return new CollisionResult(normal, depth);
		
	}
	
	/**
	 * Recursively decomposes shape into a set of convex shapes.
	 * @return Set of convex shapes
	 */
	public Set<Shape> decompose() {
		
		List<Vec2d> normals = normals();
		Set<Shape> shapes = new HashSet<Shape>();
		
		for(int i0 = 0; i0 < vertices.size(); i0++) { //Find first concave vertex
			
			int n = (i0-1) % vertices.size();
			if(n<0) n += vertices.size(); //Subtracting requires modulo, not % (remainder)
			Vec2d left = normals.get(n), right = normals.get(i0);
			
			if(left.cross(right)<0) { //Vertex is concave, begin searching for split point.
				
				Vec2d vertex = vertices.get(i0);
				for(int i1 = i0+1; i1 < i0+vertices.size(); i1++) {
					
					n = i1%vertices.size();
					Vec2d dir = vertices.get(n).sub(vertex).normalize();
					
					if(dir.dot(left) <= 0) { //Found split point

						for(Shape shape : split(Math.min(i0, n), Math.max(i0, n))) { //Decompose children and add them
							shapes.addAll(shape.decompose());
						}
						return shapes;
						
					}
					
				}
				
			}
			
		}
		
		shapes.add(this); //If no concave point found, shape is convex, and return self.
		return shapes;

	}
	
	public Set<Shape> split(int beginIndex, int endIndex) {
		Set<Shape> shapes = new HashSet<Shape>();
		List<Vec2d> vert1 = vertices.subList(beginIndex, endIndex+1), vert2 = new ArrayList<Vec2d>(vertices);
		vert2.removeAll(vertices.subList(beginIndex+1, endIndex));
		shapes.add(new Shape(vert1));
		shapes.add(new Shape(vert2));
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
		for(Vec2d vertex : vertices) vertex.glVertex();
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
	
	public Shape copy() {
		return new Shape(vertices);
	}

	public static void main(String[] args) {
		Shape s = new Shape(new Vec2d(-1,1), new Vec2d(1,0), new Vec2d(-1,-1), new Vec2d(0,0));
		System.out.println(String.format("Starting shape: %s", s));
		System.out.println(String.format("Decomposed shapes: %s", s.decompose()));
	}
	
}

