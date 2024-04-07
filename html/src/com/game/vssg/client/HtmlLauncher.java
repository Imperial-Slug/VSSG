package com.game.vssg.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.game.vssg.VSSG;
import com.google.gwt.user.client.Window;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
                return new GwtApplicationConfiguration(1920, 1080);


        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new VSSG();
        }


        @Override
        public void exit() {
                super.exit();
                Window.alert("Thanks for playing!  Just close the browser tab to exit.");
        }


}
