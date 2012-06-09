package uk.ecs.gdp.avsummariser.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import uk.ecs.gdp.avsummariser.model.AVSummaryModel;

/**
 * JPanel class for series search facility
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class SeriesSearchPanel extends JPanel{

	private static final String SEARCH_INITIAL_TEXT = "Enter name of series...";
	private AVSummaryModel model;
	private AVSummaryView view;
	private JTextField searchTerms;
	private JButton searchButton;
	
	public SeriesSearchPanel(AVSummaryModel m, AVSummaryView v){
		model = m;
		view = v;
		
		this.setLayout(new BorderLayout());
		
		JPanel searchPane = new JPanel();
		
		searchTerms = new JTextField(SEARCH_INITIAL_TEXT,15);
		searchButton = new JButton("Search");
		
		searchPane.add(searchTerms);
		searchPane.add(searchButton);
		
		this.add(searchPane, BorderLayout.NORTH);
				
		//Add action listeners
		searchTerms.addMouseListener(new MouseListener(){
			
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			
			public void mouseClicked(MouseEvent e) {
				if(searchTerms.getText().equals(SEARCH_INITIAL_TEXT)){
					searchTerms.setText("");
				}
			}
		});
		
		searchTerms.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}

			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
					//Disable features
					searchTerms.setEnabled(false);
					searchButton.setEnabled(false);
					
					model.runSeriesSearch(view);
				}
			};
		
		});
		
		searchButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				//Disable features
				searchTerms.setEnabled(false);
				searchButton.setEnabled(false);
				
				model.runSeriesSearch(view);
			}
		});
	}
	
	/**
	 * Method to get search button
	 * 
	 * @return JButton object
	 */
	public JButton getSearchButton(){
		return searchButton;
	}
	
	/**
	 * Method to get text field object
	 * 
	 * @return JTextField object
	 */
	public JTextField getSearchTerms(){
		return searchTerms;
	}
}
