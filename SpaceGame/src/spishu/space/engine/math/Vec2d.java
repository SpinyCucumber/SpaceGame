package spishu.space.engine.math;

import org.lwjgl.opengl.GL11;

/**
 * 2-Dimensional vector. Tons of methods.
 * 
 * @author SpinyCucumber
 *
 */
public class Vec2d {
	
	public static final Vec2d ZERO = new Vec2d(0);
	
	public static Vec2d fromAngle(float angle) {
		return new Vec2d((float) Math.sin(angle), (float) Math.cos(angle));
	}
	
	public static Vec2d randomUnit() {
		return fromAngle((float) (Math.random() * Math.PI * 2));
	}
	
	public float x, y;
	
	public Vec2d() {}
	
	public Vec2d(float d) {
		this(d, d);
	}
	
	public Vec2d(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2d add(Vec2d o) {
		return new Vec2d(x + o.x, y + o.y);
	}
	
	public float angle() {
		return (float) Math.atan2(y, x);
	}
	
	public Vec2d clone() {
		return new Vec2d(x, y);
	}
	
	public boolean contains1D(float point) {
		return point > Math.min(x, y) && point <= Math.max(x, y);
	}
	
	public float cross(Vec2d o) {
		return dot(o.perp());
	}
	
	public Vec2d mulDim(Vec2d o) {
		return new Vec2d(x * o.x, y * o.y);
	}
	
	public Vec2d divDim(Vec2d o) {
		return new Vec2d(x / o.x, y / o.y);
	}
	
	public float dot(Vec2d o) {
		return x * o.x + y * o.y;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec2d other = (Vec2d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
	public void glTexCoord() {
		GL11.glTexCoord2f(x, y);
		
	}
	
	public void glTranslate() {
		GL11.glTranslatef(x, y, 0);
	}
	
	public void glVertex() {
		GL11.glVertex2d(x, y);
	}
	
	/**
	 * GL_POINTS
	 */
	public void glPoint() {
		GL11.glBegin(GL11.GL_POINTS);
		glVertex();
		GL11.glEnd();
	}
	
	/**
	 * Convenient drawing method.
	 */
	public void lineTo(Vec2d o) {
		GL11.glBegin(GL11.GL_LINES);
		glVertex();
		o.glVertex();
		GL11.glEnd();
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	public Vec2d invScale(float s) {
		return new Vec2d(x / s, y / s);
	}
	
	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public Vec2d midpoint(Vec2d o) {
		return add(o).invScale(2);
	}
	
	public Vec2d mul(Matrix2d m) {
		return new Vec2d(dot(m.x), dot(m.y));
	}
	
	public Vec2d negate() {
		return new Vec2d(-x, -y);
	}

	public Vec2d normalize() {
		return invScale(length());
	}

	public float overlap1D(Vec2d o) {
		return Math.min(y, o.y) - Math.max(x, o.x);
	}
	
	public Vec2d perp() {
		return new Vec2d(-y, x);
	}

	public Vec2d scale(float s) {
		return new Vec2d(x * s, y * s);
	}

	public float slope() {
		return y / x;
	}

	public Vec2d sub(Vec2d o) {
		return new Vec2d(x - o.x, y - o.y);
	}

	@Override
	public String toString() {
		return "Vec2 [x=" + x + ", y=" + y + "]";
	}
	
}
