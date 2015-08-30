package spishu.space.engine.math;

/**
 * A utiliy class that can be used to perform matrix operations as well as AABB operations (with the AABB methods).
 * 
 * @author SpinyCucumber
 * 
 */
public class Matrix2 {

	public Vec2 x, y;
	
	public Matrix2(Vec2 x, Vec2 y) {
		this.x = x;
		this.y = y;
	}
	
	public static Matrix2 fromAngle(float rad) {
		float cos = (float) Math.cos(rad), sin = (float) Math.sin(rad);
		return new Matrix2(new Vec2(cos, -sin), new Vec2(sin, cos));
	}
	
	public Matrix2 transpose() {
		return new Matrix2(new Vec2(x.x, y.x), new Vec2(x.y, y.y));
	}
	
	public Matrix2 translateAABB(Vec2 t) {
		return new Matrix2(x.add(t), y);
	}
	
	public boolean overlapAABB(Matrix2 o) {
		return xRangeAABB().overlap1D(o.xRangeAABB()) > 0 &&
				yRangeAABB().overlap1D(o.yRangeAABB()) > 0;
	}
	
	public Vec2 xRangeAABB() {
		return new Vec2(x.x, x.x + y.x);
	}
	
	public Vec2 yRangeAABB() {
		return new Vec2(x.y, x.y + y.y);
	}

	@Override
	public String toString() {
		return "Matrix2 [x=" + x + ", y=" + y + "]";
	}
	
}
