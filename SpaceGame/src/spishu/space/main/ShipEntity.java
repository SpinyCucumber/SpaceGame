package spishu.space.main;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.assets.World;
import spishu.space.engine.assets.World.Entity;
import spishu.space.engine.lib.Texture;
import spishu.space.engine.math.Rect;
import spishu.space.engine.math.Vec2d;

/**
 * The physical ship entity. Tiles are rendered to framebuffer beforehand.
 * Similar to ShapeEntity for now.
 * @author SpinyCucumber
 *
 */
public class ShipEntity extends Entity {
	
	private Texture texture;
	private Rect bounds;
	
	public Rect getBounds() {
		return bounds;
	}
	
	@Override
	public void draw() {
		GL11.glPushMatrix();
		texture.bind();
		position.glTranslate();
		GL11.glRotatef(rotation, 0, 0, 1);
		bounds.texturedQuad();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glPopMatrix();
	}
	
	/**
	 * @param data ShipData to create ship from
	 * @param world World to add to
	 * @param velocity
	 * @param position
	 * @param mass
	 * @param rotation
	 * @param angVelocity
	 * @param restitution
	 */
	public ShipEntity(ShipData data, World world, Vec2d velocity, Vec2d position, float mass,
			float rotation, float angVelocity, float restitution) {
		world.super(velocity, position, mass, rotation, angVelocity, restitution);
		texture = data.fbo.getColorTex();
		bounds = Rect.fromDim(data.dimensions);
	}

	@Override
	public String toString() {
		return "ShipEntity [texture=" + texture + ", bounds=" + bounds
				+ ", velocity=" + velocity + ", position=" + position
				+ ", mass=" + mass + "]";
	}
	
}
