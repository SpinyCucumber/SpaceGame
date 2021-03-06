package spishu.space.engine.math;

import org.jdom2.Element;
import org.lwjgl.opengl.GL11;

public class AABB {
	
	public Vec2d corner1, corner2;
	
	public AABB(Vec2d corner1, Vec2d corner2) {
		this.corner1 = corner1;
		this.corner2 = corner2;
	}
	
	public static AABB fromXML(Element xml)	{
		return new AABB(new Vec2d(Float.parseFloat(xml.getAttributeValue("X_0")),
				Float.parseFloat(xml.getAttributeValue("Y_0"))),
				new Vec2d(Float.parseFloat(xml.getAttributeValue("X_1")),
				Float.parseFloat(xml.getAttributeValue("Y_1"))));
	}

	public AABB translate(Vec2d t) {
		return new AABB(corner1.add(t), corner2.add(t));
	}
	
	public boolean overlap(AABB o) {
		return posRange().overlap1D(o.posRange()) > 0 &&
				dimRange().overlap1D(o.dimRange()) > 0;
	}
	
	public Vec2d posRange() {
		return new Vec2d(corner1.x, corner2.x);
	}
	
	public Vec2d dimRange() {
		return new Vec2d(corner1.y, corner2.y);
	}
	
	public void glOrtho() {
		GL11.glOrtho(corner1.x, corner2.x, corner2.y, corner1.y, -1, 1);
	}
	
	public void glViewport() {
		GL11.glViewport((int) corner1.x, (int) corner1.y, (int) corner2.x, (int) corner2.y);
	}
	
	public static AABB fromDim(Vec2d half) {
		return new AABB(half.negate(), half);
	}

	@Override
	public String toString() {
		return "AABB [corner1=" + corner1 + ", corner2=" + corner2 + "]";
	}
	
}
