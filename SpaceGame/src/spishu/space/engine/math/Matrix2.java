package spishu.space.engine.math;


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

	@Override
	public String toString() {
		return "Matrix2 [x=" + x + ", y=" + y + "]";
	}
	
}
