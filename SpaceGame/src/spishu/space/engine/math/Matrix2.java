package spishu.space.engine.math;

/**
 * 2x2 Mathematical Matrix
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
	
	public static Matrix2 fromVec(Vec2 vec) {
		return new Matrix2(vec.perp(), vec);
	}
	
	public Matrix2 transpose() {
		return new Matrix2(new Vec2(x.x, y.x), new Vec2(x.y, y.y));
	}

	@Override
	public String toString() {
		return "Matrix2 [x=" + x + ", y=" + y + "]";
	}
	
}
