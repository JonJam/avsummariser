package uk.ecs.gdp.avsummariser.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import uk.ecs.gdp.avsummariser.model.facialdetection.VideoCharacter;
import uk.ecs.gdp.avsummariser.model.time.TimeUtils;

/**
 * Class which shows all the additional info for each character.
 * 
 * @author Samantha Kanza
 * @version 1
 */

public class CharacterInfoPanel extends JPanel
{
	JPanel infoPanel;
	JScrollPane scrollpane1;
	VideoCharacter myChar;
	ImageContainer imgCont;
	
	public CharacterInfoPanel(VideoCharacter myChar)
	{
		this.myChar = myChar;
		this.imgCont = new ImageContainer(
				ImageUtilities.createBufferedImage(myChar.getCharImage()), 100);
		imgCont.addMouseListener(new CharacterMoreInfoListener());
		this.setLayout(new BorderLayout());
		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		scrollpane1 = new JScrollPane(infoPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane1.setPreferredSize(new Dimension(this.getHeight(), this.getHeight()));
		this.add(imgCont, BorderLayout.WEST);
		scrollpane1.setVisible(false);
		this.add(scrollpane1, BorderLayout.CENTER);
	}
	
	//adds labels for all of the timestamps, and then all the characters matching images
	public void addInfo()
	{
		infoPanel.setSize(new Dimension(infoPanel.getParent().WIDTH, (myChar.getCharImages().size()/2)*100));
		infoPanel.removeAll();
		JLabel infoLabel = new JLabel("<html> Name: " + myChar.getName() + " <br> No Appearances: " + myChar.getMatches() + " <br> Timestamps: <br> </html>");
		infoPanel.add(infoLabel);
		
		String sTimestamps = "<html>";
		int counter = 0;
		for(Long ts: myChar.getTimestamps())
		{
			if(! sTimestamps.equals("<html>"))
			{
				sTimestamps += " ";
			}
			sTimestamps += ts;
		}
		
		sTimestamps += "</html>";
		
		JLabel lTimestamps = new JLabel(sTimestamps);
		infoPanel.add(lTimestamps);
		infoPanel.updateUI();
		infoPanel.add(new MultiImageContainer(myChar.getCharImages()));
		infoPanel.updateUI();
		scrollpane1.setVisible(true);
	}
	
	//on click the character will expand to show their matching images, name and timestamps
	public class CharacterMoreInfoListener implements MouseListener
	{

		public void mouseClicked(MouseEvent e) {
			if(imgCont.contains(e.getPoint()))
			{
				System.out.println("adding Info");
				addInfo();
			}

		}

		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
}
