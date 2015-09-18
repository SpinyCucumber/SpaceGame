package spishu.space.ship;

import org.lwjgl.opengl.GL11;

import spishu.space.engine.gl.Texture;
import spishu.space.engine.math.Rectangle;
import spishu.space.engine.math.Vec2;
import spishu.space.engine.phys.World;
import spishu.space.engine.phys.World.Entity;

/**
 * The physical ship entity. Tiles are rendered to framebuffer beforehand.
 * Similar to ShapeEntity for now.
 * @author SpinyCucumber
 *
 */
public class ShipEntity extends Entity {
	
	private Texture texture;
	private Rectangle bounds;
	
	public Rectangle getBounds() {
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
	public ShipEntity(ShipData data, World world, Vec2 velocity, Vec2 position, float mass,
			float rotation, float angVelocity, float restitution) {
		world.super(velocity, position, mass, rotation, angVelocity, restitution);
		texture = data.fbo.getColorTexture();
		bounds = Rectangle.fromDimensions(data.dimensions);
	}

}
