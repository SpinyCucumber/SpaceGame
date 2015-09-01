package spishu.space.engine.math;

import java.util.ArrayDeque;
import java.util.Collection;

import org.lwjgl.opengl.GL11;


/**
 * Extension of the Shape2D class designed to speed up processing by using a minimal amount of
 * axes. Also contains a constructor which takes the dimensions.
 * 
 * @author SpinyCucumber
 */
public class Rectangle extends Shape {

	private Rectangle(Vec2...vec2s) {
		super(vec2s);
	}
	
	@Override
	public Collection<Vec2> axes() {
		Collection<Vec2> axes = new ArrayDeque<Vec2>();
		Vec2 n = vertices[1].sub(vertices[0]).normalize();
		axes.add(n);
		axes.add(n.perp());
		return axes;
	}
	
	@Override
	public Shape rotate(float angle) {
		return new Rectangle(super.rotate(angle).vertices);
	}
	
	@Override
	public Shape translate(Vec2 d) {
		return new Rectangle(super.translate(d).vertices);
	}
	
	public void texturedQuad() {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		vertices[0].glVertex();
		GL11.glTexCoord2f(1, 0);
		vertices[1].glVertex();
		GL11.glTexCoord2f(1, 1);
		vertices[2].glVertex();
		GL11.glTexCoord2f(0, 1);
		vertices[3].glVertex();
		GL11.glEnd();
	}
	
	public static Rectangle fromDimensions(Vec2 d) {
		Vec2 hd = d.invScale(2), p = new Vec2(-hd.x, hd.y);
		return new Rectangle(hd.negate(), p.negate(), hd, p);
	}
	
	public static Rectangle fromAABB(AABB aabb) {
		return new Rectangle(aabb.corner1, new Vec2(aabb.corner2.x, aabb.corner1.y),
				aabb.corner2, new Vec2(aabb.corner1.x, aabb.corner2.y));
	}

}
