package uk.ecs.gdp.avsummariser.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import uk.ecs.gdp.avsummariser.controller.AVSummaryController;
import uk.ecs.gdp.avsummariser.controller.listener.DetectCharactersActionListener;
import uk.ecs.gdp.avsummariser.model.AVSummaryModel;
import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.view.videobrowser.VideoBrowser;

/**
 * JFrame class which contains all the view content of the system.
 * 
 * @author Michael Harris
 * @author Jonathan Harrison
 * @author Samanatha Kanza
 * @verison 1
 */
public class GUIFrame extends JFrame {

	private static final String MAIN_TITLE = "AV Summariser Application";
	
	private JPanel container;
	private JPanel southPanel;
	private JPanel statusPanel;
	private JPanel summaryControlPanel;
	private JPanel shotContainer;
	
	private JTabbedPane videoDetailPane;
	private SummaryTabsPane outputTabsPane;
	
	private VideoBrowser videoBrowser;
	private SubtitleDetailPanel subtitleDetailPanel;
	private VideoPlayerPanel videoPlayerPanel;
	private ShotDetailPanel shotDetailPanel;
	private TrailerTypePanel trailerTypePanel;
	private CharacterDetailPanel characterDetailPanel;
	private SeriesSearchPanel seriesSearchPanel;
	private PersonNamesMentionedDetailPanel personNamesMentionedDetailPanel;
	private LocationNamesMentionedDetailPanel locationNamesMentionedDetailPanel;

	private JButton generateSummaryBtn;
	private JButton playSummaryBtn;
	private JButton detectCharactersBtn;
	private JButton exportSummaryBtn;

	private JLabel genreLabel;
	private JLabel durationlabel;
	private JLabel statusLabel;

	private JComboBox genreComboBox;
	private JTextField durationField;
	private Map<VideoFile, ShotDetailPanel> shotPanelMap;

	public GUIFrame(final AVSummaryModel model, final AVSummaryController controller, final AVSummaryView view) {
		this.setTitle(MAIN_TITLE);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(900, 750);

		container = new JPanel(new BorderLayout());
		container.setBackground(Color.LIGHT_GRAY);
		
		JPanel sideBarContainer = new JPanel(new BorderLayout());
		southPanel = new JPanel(new BorderLayout());
		summaryControlPanel = new JPanel(new GridLayout(1, 7));
		shotContainer = new JPanel();
		statusPanel = new JPanel(new GridLayout());
		
		videoDetailPane = new JTabbedPane();
		outputTabsPane = new SummaryTabsPane(controller);
		outputTabsPane.setPreferredSize(new Dimension(300, 250));
		
		shotPanelMap = new HashMap<VideoFile, ShotDetailPanel>();
		
		shotDetailPanel = new ShotDetailPanel();
		characterDetailPanel = new CharacterDetailPanel();
		videoBrowser = new VideoBrowser(model, view);
		videoPlayerPanel = new VideoPlayerPanel(model, view);
		seriesSearchPanel = new SeriesSearchPanel(model, view);
		subtitleDetailPanel = new SubtitleDetailPanel(controller.getVideoTabsController());
		personNamesMentionedDetailPanel = new PersonNamesMentionedDetailPanel(controller.getVideoTabsController());
		locationNamesMentionedDetailPanel = new LocationNamesMentionedDetailPanel(controller.getVideoTabsController());
		trailerTypePanel = new TrailerTypePanel(controller.getTrailerTypePaneController());
		
		statusLabel = new JLabel("Ready...");
		genreLabel = new JLabel("Genre: ");
		durationlabel = new JLabel("Duration: ");
		
		genreComboBox = new JComboBox();
		genreComboBox.setPreferredSize(new Dimension(200, 10));
		genreComboBox.setEnabled(false);
	
		durationField = new JTextField("10:00");
		
		generateSummaryBtn = new JButton("Generate Summary");
		generateSummaryBtn.setEnabled(false);
		playSummaryBtn = new JButton("Play Summary");
		playSummaryBtn.setEnabled(false);
		exportSummaryBtn = new JButton("Export Summary");
		exportSummaryBtn.setEnabled(false);
		detectCharactersBtn = new JButton("Find Characters");
		
		
		this.add(container);
		statusPanel.add(statusLabel);
		
		shotContainer.setPreferredSize(new Dimension(videoDetailPane.getWidth(), videoDetailPane.getHeight()));
		videoDetailPane.add("Video Player", videoPlayerPanel);
		videoDetailPane.add("Subtitles", subtitleDetailPanel);
		videoDetailPane.add("Shots", shotContainer);
		videoDetailPane.add("Characters", characterDetailPanel);
		videoDetailPane.add("People Mentioned", personNamesMentionedDetailPanel);
		videoDetailPane.add("Locations Mentioned",locationNamesMentionedDetailPanel);

		sideBarContainer.add(seriesSearchPanel, BorderLayout.NORTH);
		sideBarContainer.add(videoBrowser, BorderLayout.CENTER);

		container.add(sideBarContainer, BorderLayout.EAST);
		container.add(videoDetailPane, BorderLayout.CENTER);

		summaryControlPanel.add(genreLabel);
		summaryControlPanel.add(genreComboBox);
		summaryControlPanel.add(durationlabel);
		summaryControlPanel.add(durationField);
		summaryControlPanel.add(generateSummaryBtn);
		summaryControlPanel.add(playSummaryBtn);
		summaryControlPanel.add(detectCharactersBtn);
		summaryControlPanel.add(exportSummaryBtn);
		
		southPanel.add(summaryControlPanel, BorderLayout.NORTH);
		southPanel.add(outputTabsPane, BorderLayout.CENTER);
		southPanel.add(trailerTypePanel, BorderLayout.EAST);
		southPanel.add(statusPanel, BorderLayout.SOUTH);
		
		container.add(southPanel, BorderLayout.SOUTH);

		container.updateUI();
		
		//Add listeners
		detectCharactersBtn.addActionListener(new DetectCharactersActionListener(view,model));
		
		durationField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {}
			
			public void focusLost(FocusEvent e) {
				String time = durationField.getText();
				
				if(time.matches("[1-9]?[0-9]:[0-5][0-9]")){
					//Valid duration string
					
					model.setTrailerDuration(time);
				} else{
					//Not valid duration string
					
					JOptionPane.showMessageDialog(null, "Duration entered is incorrect. Please enter duration of form MM:SS");
				}
			}
		});

		generateSummaryBtn.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				view.stopVideoPlayBack();
				
				//Disable features
				generateSummaryBtn.setEnabled(false);
				playSummaryBtn.setEnabled(false);
				exportSummaryBtn.setEnabled(false);
				
				model.generateSummary(view);
			}
		});

		playSummaryBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				
				model.playSummary(view);
			}
		});			
		
		exportSummaryBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				//Disable features
				generateSummaryBtn.setEnabled(false);
				playSummaryBtn.setEnabled(false);
				exportSummaryBtn.setEnabled(false);
				
				model.exportSummary(view);
			}
		});
	}

	/**
	 * Method to get VideoBrowser object
	 * 
	 * @return VideoBrowser object
	 */
	public VideoBrowser getVideoBrowser() {
		return videoBrowser;
	}

	/**
	 * Method to get VideoPlayerPanel object
	 * 
	 * @return VideoPlayerPanel object
	 */
	public VideoPlayerPanel getVideoPlayerPanel() {
		return videoPlayerPanel;
	}

	/**
	 * Method to set status label
	 * 
	 * @param message (String object)
	 */
	public void setStatusLabel(String message) {
		statusLabel.setText(message);
	}

	/**
	 * Method to get CharacterDetailPanel
	 * 
	 * @return CharacterDetailPanel object
	 */
	public CharacterDetailPanel getCharacterPanel() {
		return characterDetailPanel;
	}

	/**
	 * Method to get ShotDetailPanel object
	 * 
	 * @param videoFile (VideoFile object)
	 * @return ShotDetailPanel object
	 */
	public ShotDetailPanel getShotPanel(VideoFile videoFile) {
		
		shotContainer.setPreferredSize(new Dimension(videoDetailPane.getWidth(), videoDetailPane.getHeight()));
		
		if (shotPanelMap.containsKey(videoFile)) {
			
			ShotDetailPanel existingShotDetailPanel = shotPanelMap.get(videoFile);
			shotContainer.removeAll();
			shotContainer.add(existingShotDetailPanel);
			existingShotDetailPanel.setPreferredSize(new Dimension(videoDetailPane.getWidth(), videoDetailPane.getHeight()));
			
			return existingShotDetailPanel;

		} else {
			
			ShotDetailPanel newShotDetailPanel = new ShotDetailPanel();
			shotPanelMap.put(videoFile, newShotDetailPanel);
			shotContainer.removeAll();
			shotContainer.add(shotPanelMap.get(videoFile));
			newShotDetailPanel.setPreferredSize(new Dimension(videoDetailPane.getWidth(), videoDetailPane.getHeight()));
		
			return newShotDetailPanel;
		}
		
	}

	/**
	 * Method to get SubtitleDetailPanel object
	 * 
	 * @return SubtitleDetailPanel object
	 */
	public SubtitleDetailPanel getSubtitlePanel() {
		return subtitleDetailPanel;
	}

	/**
	 * Method to get SummarySeriesDetailPanel object
	 * 
	 * @return SummarySeriesDetailPanel object
	 */
	public SummarySeriesDetailPanel getSummarySeriesDetailPanel() {
		return outputTabsPane.getSummarySeriesDetailPanel();
	}
	
	/**
	 * Method to get SummaryCharacterDetailPanel object
	 * 
	 * @return SummaryCharacterDetailPanel object
	 */
	public SummaryCharacterDetailPanel getSummaryCharactersDetailPanel() {
		return outputTabsPane.getSummaryCharacterDetailPanel();
	}
	
	/**
	 * Method to get SummaryLocationsDetailPanel object
	 * 
	 * @return SummaryLocationsDetailPanel object
	 */
	public SummaryLocationsDetailPanel getSummaryLocationsDetailPanel() {
		return outputTabsPane.getSummaryLocationsDetailPanel();
	}

	/**
	 * Method to get PersonNamesMentionedDetailPanel object
	 * 
	 * @return PersonNamesMentionedDetailPanel object
	 */
	public PersonNamesMentionedDetailPanel getPersonNamesDetailPanel() {
		return personNamesMentionedDetailPanel;
	}

	/**
	 * Method to get LocationNamesMentionedDetailPanel object
	 * 
	 * @return LocationNamesMentionedDetailPanel object
	 */
	public LocationNamesMentionedDetailPanel getLocationNamesDetailPanel() {
		return locationNamesMentionedDetailPanel;
	}
	
	/**
	 * Method to get SeriesSearchPanel object
	 * 
	 * @return SeriesSearchPanel object
	 */
	public SeriesSearchPanel getSeriesSearchPanel(){
		return seriesSearchPanel;
	}

	/**
	 * Method to get genre combobox object
	 * 
	 * @return JComboBox object
	 */
	public JComboBox getGenreComboBox() {
		return genreComboBox;
	}
	
	/**
	 * Method to get generate summary button object
	 * 
	 * @return JButton object
	 */
	public JButton getGenerateSummaryButton(){
		return generateSummaryBtn;
	}
	
	/**
	 * Method to get play summary button object
	 * 
	 * @return JButton object
	 */
	public JButton getPlaySummaryButton(){
		return playSummaryBtn;
	}
	
	/**
	 * Method to get export summary button object
	 * 
	 * @return JButton object
	 */
	public JButton getExportSummaryButton(){
		return exportSummaryBtn;
	}
}
