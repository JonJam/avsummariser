package uk.ecs.gdp.avsummariser.model.facialdetection;

import java.util.Observable;
import java.util.Observer;

import uk.ecs.gdp.avsummariser.view.AVSummaryView;

public class CharacterDetectedObserver implements Observer {
	AVSummaryView view;

	public void update(Observable o, Object arg) {
		VideoCharacter vChar = (VideoCharacter) arg;
		view.getFrame().getCharacterPanel().addCharacter(vChar);
	}

	public void setView(AVSummaryView view) {
		this.view = view;
		System.out.println("view set");
	}

}
