package uk.ecs.gdp.avsummariser.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import uk.ecs.gdp.avsummariser.controller.TrailerTypePaneController;

/**
 * JPanel class for trailer type panel.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class TrailerTypePanel extends JPanel{
	
	private TrailerTypePaneController controller;

	public TrailerTypePanel(final TrailerTypePaneController controller){
		this.controller = controller;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new EmptyBorder(20, 20, 20, 20) );
		
		JLabel title = new JLabel("Trailer Types:");
		this.add(title);
			
		final JRadioButton defaultButton  = new JRadioButton("Default");
		defaultButton.setSelected(true);
		this.add(defaultButton);
		
		final JRadioButton loudnessButton  = new JRadioButton("Loudness");
		this.add(loudnessButton);
		
		final JRadioButton peopleMentionedButton = new JRadioButton("People mentioned");
		this.add(peopleMentionedButton);
		
		final JRadioButton locationMentionedButton = new JRadioButton("Location mentioned");
		this.add(locationMentionedButton);
		
		ButtonGroup group = new ButtonGroup();
		group.add(defaultButton);
		group.add(loudnessButton);
		group.add(peopleMentionedButton);
		group.add(locationMentionedButton);	
	
		//Add ActionListeners
		
		ActionListener action = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(defaultButton.isSelected()){
					controller.setTrailerType("Default");
				} else if(loudnessButton.isSelected()){
					controller.setTrailerType("Loudness");
				} else if(peopleMentionedButton.isSelected()){
					controller.setTrailerType("People mentioned");
				} else if(locationMentionedButton.isSelected()){
					controller.setTrailerType("Locations mentioned");
				}
			}
		};
		
		defaultButton.addActionListener(action);
		loudnessButton.addActionListener(action);
		peopleMentionedButton.addActionListener(action);
		locationMentionedButton.addActionListener(action);
	}
}
