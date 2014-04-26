package com.siegedog.lemonade;

import com.badlogic.gdx.Gdx;
import com.siegedog.egglib.EggGame;
import com.siegedog.egglib.GameScreen;
import com.siegedog.egglib.PreloadingScreen;
import com.siegedog.egglib.Resources;
import com.siegedog.egglib.PreloadingScreen.LoadTask;
import com.siegedog.egglib.util.Log;
import com.siegedog.lemonade.screens.LemonadeMenu;

public class LemonadeGame extends EggGame {

	@Override
	protected void startup() {		
		GameScreen next = new LemonadeMenu();
		LoadTask loadTask = new LoadTask() {
			
			@Override
			public void performLoad(Resources r) {
				r.loadFont("menuFont", "menuFont");
				r.loadAtlas("img/atlas/pack.atlas");
			}
		};
		
		setScreen(new PreloadingScreen(next, loadTask) );
	}

}
