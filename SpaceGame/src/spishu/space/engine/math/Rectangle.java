package spishu.space.engine.math;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;


/**
 * Extension of the Shape2D class designed to speed up processing by using a minimal amount of
 * axes. Also contains a constructor which takes the dimensions.
 * 
 * @author SpinyCucumber
 */
public class Rectangle extends Shape {

	private Rectangle(List<Vec2> vertices) {
		super(vertices);
	}
	
	@Override
	public List<Vec2> normals() {
		List<Vec2> axes = new ArrayList<Vec2>();
		Vec2 n = vertices.get(1).sub(vertices.get(0)).normalize();
		axes.add(n);
		axes.add(n.perp());
		return axes;
	}
	
	@Override
	public Rectangle rotate(float angle) {
		return new Rectangle(super.rotate(angle).vertices);
	}
	
	@Override
	public Rectangle translate(Vec2 d) {
		return new Rectangle(super.translate(d).vertices);
	}
	
	/**
	 * Calls texCoords and vertices.
	 */
	public void texturedQuad() {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		vertices.get(0).glVertex();
		GL11.glTexCoord2f(1, 0);
		vertices.get(1).glVertex();
		GL11.glTexCoord2f(1, 1);
		vertices.get(2).glVertex();
		GL11.glTexCoord2f(0, 1);
		vertices.get(3).glVertex();
		GL11.glEnd();
	}
	
	public static Rectangle fromDim(Vec2 d) {
		Vec2 hd = d.invScale(2), p = new Vec2(-hd.x, hd.y);
		List<Vec2> vertices = new ArrayList<Vec2>();
		vertices.add(hd.negate());
		vertices.add(p.negate());
		vertices.add(hd);
		vertices.add(p);
		return new Rectangle(vertices);
	}
	
	public static Rectangle fromAABB(AABB aabb) {
		List<Vec2> vertices = new ArrayList<Vec2>();
		vertices.add(aabb.corner1);
		vertices.add(new Vec2(aabb.corner2.x, aabb.corner1.y));
		vertices.add(aabb.corner2);
		vertices.add(new Vec2(aabb.corner1.x, aabb.corner2.y));
		return new Rectangle(vertices);
	}

	@Override
	public String toString() {
		return "Rectangle [vertices=" + vertices + "]";
	}

}
