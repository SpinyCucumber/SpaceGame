package spishu.space.engine.math;

import org.lwjgl.opengl.GL11;

public class Vec2 {
	
	public static Vec2 fromAngle(float angle) {
		return new Vec2((float) Math.sin(angle), (float) Math.cos(angle));
	}
	
	public float x, y;
	
	public Vec2() {}
	
	public Vec2(float d) {
		this(d, d);
	}
	
	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 add(Vec2 o) {
		return new Vec2(x + o.x, y + o.y);
	}
	
	public float angle() {
		return (float) Math.atan2(y, x);
	}
	
	public Vec2 clone() {
		return new Vec2(x, y);
	}
	
	public boolean contains1D(float point) {
		return point > Math.min(x, y) && point <= Math.max(x, y);
	}
	
	public float cross(Vec2 o) {
		return dot(o.perp());
	}
	
	public Vec2 divDim(Vec2 o) {
		return new Vec2(x / o.x, y / o.y);
	}
	
	public float dot(Vec2 o) {
		return x * o.x + y * o.y;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec2 other = (Vec2) obj;
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
	
	public Vec2 invScale(float s) {
		return new Vec2(x / s, y / s);
	}
	
	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public Vec2 midpoint(Vec2 o) {
		return add(o).invScale(2);
	}
	
	public Vec2 multiply(Matrix2 m) {
		return new Vec2(dot(m.x), dot(m.y));
	}
	
	public Vec2 negate() {
		return new Vec2(-x, -y);
	}

	public Vec2 normalize() {
		return invScale(length());
	}

	public float overlap1D(Vec2 o) {
		return Math.min(y, o.y) - Math.max(x, o.x);
	}
	
	public Vec2 perp() {
		return new Vec2(-y, x);
	}

	public Vec2 scale(float s) {
		return new Vec2(x * s, y * s);
	}

	public float slope() {
		return y / x;
	}

	public Vec2 sub(Vec2 o) {
		return new Vec2(x - o.x, y - o.y);
	}

	public String toString() {
		return "{" + x + ", " + y + "}";
	}
	
}
