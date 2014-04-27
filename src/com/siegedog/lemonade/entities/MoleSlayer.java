package com.siegedog.lemonade.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.siegedog.egglib.Dude;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.physics.AABB;
import com.siegedog.egglib.physics.Collision;
import com.siegedog.egglib.util.Log;

/**
 * The guy you play as. He shoots lasers from his eyes to kill mole people.
 * @author SiegeDog
 */
public class MoleSlayer extends Dude {

	
	private static final int[] LASER_DAMAGE = new int[] { 100, 150, 250, 400 };
	private static final int[] LASER_COST = new int[] { 0, 1000, 2000, 3000 };
	
	private static final int LASER_WIDTH = 8;
	// How deep should the beam be drawn inside (on top of) the enemy
	private static final float BEAM_PENETRATION = 10.0f;
	
	private boolean paused = false;

	private float cooldownTotal = 0.5f;
	private float cooldownLeft;

	private Vector2 eyeOffset = new Vector2(8.0f, 26.0f);
	
	private int laserLevel = 0;
	
	public MoleSlayer(float x, float y) {
		super(EggGame.R.spriteAsAnimatedSprite("player"), new AABB(new Vector2(x, y), new Vector2(32, 32)));
		
		cooldownLeft = cooldownTotal;
	}

	@Override
	public void act(float delta) {
		
		if(! paused) {
			if(Gdx.input.isKeyPressed(Keys.LEFT)) {
				physics.velocity.x -= 300.0f * delta;
			}
			else if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
				physics.velocity.x += 300.0f * delta;
			}
			else {
				physics.velocity.x *= 0.85f;
			}
			
			physics.velocity.x = MathUtils.clamp(physics.velocity.x, -150.0f, 150.0f);
			
			cooldownLeft = Math.max(cooldownLeft - delta, 0.0f);
			
			if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.Z) || Gdx.input.isKeyPressed(Keys.SPACE)) {
				if(cooldownLeft == 0.0f) {
					cooldownLeft = cooldownTotal;
					
					// find enemy directly underneath
					
					float pseudoRayHeight = 500.0f;
					AABB pseudoRay = new AABB(getX() + eyeOffset.x, getY() + eyeOffset.y - pseudoRayHeight, LASER_WIDTH, pseudoRayHeight);
					
					Moleman top = null;
					float maxY = -pseudoRayHeight;
					for(Actor a : screen.getLayer("gameplay").getChildren()) {
						if(! (a instanceof Moleman)) continue;
						
						Moleman mm = (Moleman) a;
						if(! mm.isAttackable()) continue;
						
						Collision c = mm.physics.getAABB().intersects(pseudoRay);
						if(Collision.NONE != c) {
							Log.D("HIT:" + mm);
							// We hit something
							if(mm.getY() > maxY) {
								maxY = mm.getY();
								top = mm;
							}
						}
					}
					
					// TODO: fiddle around here if you want beams that shoot 
					// through enemies!
					// Deal damage to whatever we hit
					if(null != top) {
						top.takeDamage(getLaserDamage());
					}
					
					
					screen.addDude(new LaserBlast(pseudoRay.getX(), pseudoRay.getY() + pseudoRayHeight, -Math.abs(getY() - maxY) - BEAM_PENETRATION));
				}
			}
		}
		
		super.act(delta);
	}

	public void pause() {
		paused = true;
		physics.active = false;
	}
	
	public void unpause() {
		paused = false;
		physics.active = true;
	}
	
	public int getLaserDamage() {
		return LASER_DAMAGE[laserLevel];
	}
}
