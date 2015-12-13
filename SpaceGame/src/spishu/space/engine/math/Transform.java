package spishu.space.engine.math;

public class Transform {
	
	public static final Transform IDENTITY = new Transform(1,0,0,
														   0,1,0,
														   0,0,1);
	
	float a0, a1, a2, b0, b1, b2, c0, c1, c2;

	public Transform(float a0, float a1, float a2, float b0, float b1, float b2, float c0, float c1, float c2) {
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		this.b0 = b0;
		this.b1 = b1;
		this.b2 = b2;
		this.c0 = c0;
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public static Transform rotation(Vec2d vec) {
		return new Transform(vec.x,vec.y,0,-vec.y,vec.x,0,0,0,1);
	}
	
	public static Transform translation(Vec2d vec) {
		return new Transform(1,0,0,0,1,0,vec.x,vec.y,1);
	}
	
	public static Transform scaling(Vec2d scale) {
		return new Transform(scale.x,0,0,0,scale.y,0,0,0,1);
	}
	
	public Vec2d apply(Vec2d vec) {
		return new Vec2d(vec.x*a0+vec.y*a1+a2,vec.x*b0+vec.y*b1+b2);
	}
	
	public Transform combine(Transform o) {
		return new Transform(a0*o.a0+a1*o.b0+a2*o.c0, a0*o.a1+a1*o.b1+a2*o.c1, a0*o.a2+a1*o.b2+a2*o.c2,
							 b0*o.a0+b1*o.b0+b2*o.c0, b0*o.a1+b1*o.b1+b2*o.c1, b0*o.a2+b1*o.b2+b2*o.c2,
							 c0*o.a0+c1*o.b0+c2*o.c0, c0*o.a1+c1*o.b1+c2*o.c1, c0*o.a2+c1*o.b2+c2*o.c2);
	}
	
	@Override
	public String toString() {
		return "Transform [a0=" + a0 + ", a1=" + a1 + ", a2=" + a2 + ", b0=" + b0 + ", b1=" + b1 + ", b2=" + b2
				+ ", c0=" + c0 + ", c1=" + c1 + ", c2=" + c2 + "]";
	}

	public static void main(String[] args) {
		Transform t0 = Transform.rotation(Vec2d.fromAngle((float) Math.toRadians(90))), t1 = IDENTITY;
		System.out.println(t0);
		System.out.println(t0.combine(t1));
	}
	
}
