package controller;

import application.Main;

public abstract class AbstractController {
	protected Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }

}
	