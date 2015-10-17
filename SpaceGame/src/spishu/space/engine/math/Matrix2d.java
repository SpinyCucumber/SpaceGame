package spishu.space.engine.math;

/**
 * 2x2 Mathematical Matrix
 * 
 * @author SpinyCucumber
 * 
 */
public class Matrix2d {

	public Vec2d x, y;
	
	public Matrix2d(Vec2d x, Vec2d y) {
		this.x = x;
		this.y = y;
	}
	
	public static Matrix2d fromVec(Vec2d vec) {
		return new Matrix2d(vec.perp(), vec);
	}
	
	public Matrix2d transpose() {
		return new Matrix2d(new Vec2d(x.x, y.x), new Vec2d(x.y, y.y));
	}

	@Override
	public String toString() {
		return "Matrix2 [x=" + x + ", y=" + y + "]";
	}
	
}
