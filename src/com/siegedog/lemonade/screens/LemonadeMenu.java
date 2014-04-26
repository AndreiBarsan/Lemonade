package com.siegedog.lemonade.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.siegedog.egglib.Dude;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.GameScreen;
import com.siegedog.egglib.juice.UIFLabel;
import com.siegedog.egglib.juice.UIFLabel.Fade;
import com.siegedog.egglib.physics.PointShape;

public class LemonadeMenu extends GameScreen {
	
	private BitmapFont menuFont;
	private Dude walkway;
	private float elapsed;
	
	@Override
	public void init(EggGame game) {
		super.init(game, 2);
		super.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		menuFont = EggGame.R.font("menuFont");
		
		float width = Gdx.graphics.getWidth() / 2.0f;
		float height = Gdx.graphics.getHeight() / 2.0f;
		Fade fadeIn = new Fade(10.0f, Interpolation.exp10);
		Fade fadeOut = new Fade(0.75f, Interpolation.exp5In);
		UIFLabel hello;
		addDude("ui", hello = new UIFLabel(
				"Rise of the\nMOLE PEOPLE", 
				menuFont,
				new Vector2(0.0f, height - 120.0f),
				width,
				fadeIn,
				fadeOut
			));
		
		addDude("gameplay", walkway = new Dude(EggGame.R.spriteAsAnimatedSprite("walkway"), new PointShape(0.0f, height - 80.0f)));
		System.out.println(hello.getState());
		hello.show();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		elapsed += delta;
		
		int width = (int) (Gdx.graphics.getWidth() / 2.0);
	}

}
