package com.siegedog.lemonade.entities;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.physics.AABB;

public class BasicMole extends Moleman {
	
	static final float WALKING_SPEED = 60.0f;
	static final float JETPACK_SPEED = 50.0f;
	
	public BasicMole(float x, float y) {
		super(EggGame.R.spriteAsAnimatedSprite("moleman02"), new AABB(x, y, 22, 52), new Vector2(-8.0f, -10.0f), 150);
		
		float width = 512;
		float border = 32;
		float chosenX = (float) (border + Math.random() * (width - border * 2));
		
		// For the jetpack jump effect 
		float overTheTop = 10.0f + (float) (Math.random() * 15.0f);

		// TODO: not hack this shit
		float walkwayY = -110.0f;
		addAction(Actions.sequence(
			Actions.moveTo(chosenX, y, Math.abs(x - chosenX) / WALKING_SPEED, Interpolation.pow2),
			Actions.delay(0.5f),
			Actions.moveTo(chosenX, walkwayY + overTheTop, Math.abs(y - walkwayY - overTheTop) / JETPACK_SPEED, Interpolation.pow2),
			Actions.moveTo(chosenX, walkwayY, overTheTop / JETPACK_SPEED, Interpolation.pow2)
		));
	}

}
