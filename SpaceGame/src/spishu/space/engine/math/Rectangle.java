package spishu.space.engine.math;

import java.util.ArrayDeque;
import java.util.Collection;


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
	
	public static Rectangle fromDimensions(Vec2 d) {
		Vec2 hd = d.invScale(2), p = new Vec2(-hd.x, hd.y);
		return new Rectangle(hd.negate(), p.negate(), hd, p);
	}
	
	public static Rectangle fromCorners(Vec2 c1, Vec2 c2) {
		return new Rectangle(c1, new Vec2(c2.x, c1.y), c2, new Vec2(c1.x, c2.y));
	}
	
	public static Rectangle fromAABB(Matrix2 AABB) {
		return fromCorners(AABB.x, AABB.x.add(AABB.y));
	}

}
