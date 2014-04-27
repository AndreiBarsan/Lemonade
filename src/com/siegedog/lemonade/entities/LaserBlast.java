package com.siegedog.lemonade.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.egglib.Doodad;
import com.siegedog.egglib.util.Log;

/**
 * Doesn't handle actual collisions and damage - these get computed instantly
 * when the player fires. The role of this class is purely aesthetic.
 * @author SiegeDog
 *
 */
public class LaserBlast extends Doodad {

	public LaserBlast(float x, float y, float height) {
		super("laserBlast", x, y);
		
		setScale(1.0f, height / sprite.getHeight());
		addAction(Actions.sequence(Actions.fadeOut(0.13f), new Action() {
			public boolean act(float delta) {
				LaserBlast.this.kill();
				return true;
			}
		}));
	}
}
