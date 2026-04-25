package gui;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import model.Network;

public class Gui {
    private Network network;
    private JFrame frame;

    public Gui(Network network) {
        this.network = network;
        this.frame = new JFrame("Journey Planner");

        run();
    }

    public void run() {
        // Setup frame.
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Setup a main panel
        JPanel mainPanel = new JPanel();
        frame.add(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Setup router selection.
        JPanel panelRadioButtons = new JPanel();
        mainPanel.add(panelRadioButtons);

        JLabel labelRadio = new JLabel("Select routing type:");
        panelRadioButtons.add(labelRadio);
        
        JRadioButton fastest = new JRadioButton("Shortest time", true);  
        JRadioButton fewest  = new JRadioButton("Fewest changes");
        panelRadioButtons.add(fastest);
        panelRadioButtons.add(fewest);

        ButtonGroup group = new ButtonGroup();
        group.add(fastest);
        group.add(fewest);

        // Setup stations selection.
        JPanel panelStationSelection = new JPanel();
        mainPanel.add(panelStationSelection);

        JLabel labelStationSelection = new JLabel("Select which stations you want to go between");
        panelStationSelection.add(labelStationSelection);

        JComboBox<String> from = new JComboBox<>(); 
        JComboBox<String> to = new JComboBox<>(); 
        panelStationSelection.add(from);
        panelStationSelection.add(to);

        // Setup search button.
        JPanel panelSearchButton = new JPanel();
        mainPanel.add(panelSearchButton);

        JButton search = new JButton("Search");
        panelSearchButton.add(search);

        frame.setVisible(true);
    }
}