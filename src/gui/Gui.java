package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Network;
import model.Station;
import routing.FewestChangesRouter;
import routing.Route;
import routing.Router;
import routing.ShortestTimeRouter;

public class Gui {
    private Network network;
    private JFrame frame;
    private JComboBox<Station> from;
    private JComboBox<Station> to;
    private JRadioButton fastest;
    private JRadioButton fewest;
    private JTextArea outputText;

    public Gui(Network network) {
        this.network = network;
        this.frame = new JFrame("Journey Planner");
    }

    public void run() {
        setupFrame();
        JPanel mainPanel = setupMainPanel();
        setupRouterSelection(mainPanel);
        setupStationSelection(mainPanel);
        setupSearchButton(mainPanel);
        setupOutputArea();
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
    }

    private JPanel setupMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        frame.add(mainPanel, BorderLayout.NORTH);
        return mainPanel;
    }

    private void setupRouterSelection(JPanel mainPanel) {
        JPanel panelRadioButtons = new JPanel();
        mainPanel.add(panelRadioButtons);

        JLabel labelRadio = new JLabel("Select routing type:");
        panelRadioButtons.add(labelRadio);

        fastest = new JRadioButton("Shortest time", true);
        fewest = new JRadioButton("Fewest changes");
        panelRadioButtons.add(fastest);
        panelRadioButtons.add(fewest);

        ButtonGroup group = new ButtonGroup();
        group.add(fastest);
        group.add(fewest);
    }

    private void setupStationSelection(JPanel mainPanel) {
        JPanel panelStationSelection = new JPanel();
        mainPanel.add(panelStationSelection);

        Collection<Station> stations = network.getAllStations();

        from = new JComboBox<>(stations.toArray(new Station[0]));
        to = new JComboBox<>(stations.toArray(new Station[0]));

        panelStationSelection.setLayout(new GridLayout(2, 2, 5, 5));
        panelStationSelection.add(new JLabel("From:"));
        panelStationSelection.add(from);
        panelStationSelection.add(new JLabel("To:"));
        panelStationSelection.add(to);
    }

    private void setupSearchButton(JPanel mainPanel) {
        JPanel panelSearchButton = new JPanel();
        mainPanel.add(panelSearchButton);

        JButton search = new JButton("Search");
        search.addActionListener(e -> search());
        panelSearchButton.add(search);
    }

    private void setupOutputArea() {
        JScrollPane outputPane = new JScrollPane(); 
        frame.add(outputPane, BorderLayout.CENTER);

        outputText = new JTextArea(20, 50);
        outputPane.setViewportView(outputText);
        outputText.setEditable(false);
    }

    private void search() {
        Station fromStation = (Station) from.getSelectedItem();
        Station toStation = (Station) to.getSelectedItem();

        if (fromStation.equals(toStation)) {
            outputText.setText("Stations cannot be the same.");
            return;
        }


        Router router;

        if (fastest.isSelected()) {
            router = new ShortestTimeRouter(network); 
        }

        else {
            router = new FewestChangesRouter(network);
        }

        Route route = router.findRoute(fromStation, toStation);
        
        if (route == null) {
            outputText.setText("No route found.");
            return;
        }

        outputText.setText(route.toString());
    }
}