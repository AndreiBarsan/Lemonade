package com.siegedog.lemonade.entities;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.physics.AABB;

public class BasicMole extends Moleman {
	
	static final float WALKING_SPEED = 30.0f;
	static final float JETPACK_SPEED = 28.0f;
	
	public BasicMole(float x, float y) {
		super(EggGame.R.spriteAsAnimatedSprite("moleman02"),
				EggGame.R.spriteAsAnimatedSprite("moleman02-falling"),
				EggGame.R.spriteAsAnimatedSprite("moleman02-dead"), 
				new AABB(x, y, 22, 52), 
				new Vector2(-16.0f, 0.0f), 
				150);
		
		float width = 512;
		float border = 32;
		float chosenX = (float) (border + Math.random() * (width - border * 2));
		
		// For the jetpack jump effect 
		float overTheTop = 10.0f + (float) (Math.random() * 15.0f);

		if(chosenX > x) {
			sprite.flip(false, false);
		} else {
			sprite.flip(true, false);
		}
		
		// TODO: not hack this shit
		float walkwayY = -110.0f;
		addAction(Actions.sequence(
			Actions.moveTo(chosenX, y, Math.abs(x - chosenX) / WALKING_SPEED, Interpolation.pow2),
			Actions.delay(0.5f),
			new Action() { public boolean act(float delta) { state = State.Jetpacking; return true; } },
			Actions.moveTo(chosenX, walkwayY + overTheTop, Math.abs(y - walkwayY - overTheTop) / JETPACK_SPEED, Interpolation.linear),
			Actions.moveTo(chosenX, walkwayY, overTheTop / JETPACK_SPEED, Interpolation.pow2)
		));
	}

}
