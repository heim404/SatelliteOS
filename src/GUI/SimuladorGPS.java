package GUI;

import Satelite.SO.SistemaGPS;
import Satelite.Middlewere;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Deque;
import java.util.List;

public class SimuladorGPS extends JFrame {

    private JLabel latitudeLabel, longitudeLabel;
    private JTextField latitudeField, longitudeField;
    private GPSCanvas gpsCanvas;
    private Middlewere middlewere;
    private Timer simulacaoTimer;
    private JTextArea statusTextArea;

    public SimuladorGPS(Middlewere middlewere) {
        this.middlewere = middlewere;

        setTitle("Simulador GPS");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        latitudeLabel = new JLabel("Latitude:");
        longitudeLabel = new JLabel("Longitude:");
        latitudeField = new JTextField(10);
        longitudeField = new JTextField(10);


        latitudeField.setEditable(false);
        longitudeField.setEditable(false);

        gpsCanvas = new GPSCanvas();

        setLayout(new BorderLayout());

        latitudeLabel = new JLabel("Latitude:");
        longitudeLabel = new JLabel("Longitude:");


        statusTextArea = new JTextArea(20, 15);
        statusTextArea.setEditable(false);


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(latitudeLabel);
        inputPanel.add(latitudeField);
        inputPanel.add(longitudeLabel);
        inputPanel.add(longitudeField);

        add(inputPanel, BorderLayout.NORTH);
        add(gpsCanvas, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout());
        statusPanel.add(new JScrollPane(statusTextArea));
        add(statusPanel, BorderLayout.EAST);

        // Cria um Timer para simular as coordenadas
        simulacaoTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simularCoordenadas();
            }
        });
        simulacaoTimer.start(); // Iniciar o Timer

        // Botão "Save to Logs"
        JButton saveToLogsButton = new JButton("Save to Logs");
        saveToLogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Chama o método para salvar no arquivo
                middlewere.writeToFile();
                JOptionPane.showMessageDialog(null, "Logs salvos com sucesso!");
            }
        });

        // Botão "Set Auto Write to File"
        JButton setAutoWriteToFileButton = new JButton("Set Auto Write to File");
        setAutoWriteToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                middlewere.setAutoWriteToFile();
                JOptionPane.showMessageDialog(null, "Escrita de logs ativada/desativada.");
            }
        });

        // Botão "Set Auto Save Off"
        JButton setAutoSaveOffButton = new JButton("Set Auto Save Off");
        setAutoSaveOffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                middlewere.setAutoSaveOff();
                JOptionPane.showMessageDialog(null, "Autosave Ativado/desativado.");
            }
        });

        // Botão "Set Propulsor On 1"
        JButton setPropulsorOn1Button = new JButton("Ligar Propulsor de Fora");
        setPropulsorOn1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                middlewere.setPropulsorOn("FORA");
                JOptionPane.showMessageDialog(null, "Propulsor de Fora ligado.");
            }
        });

        // Botão "Set Propulsor On 2"
        JButton setPropulsorOn2Button = new JButton("Ligar Propulsor de Dentro");
        setPropulsorOn2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                middlewere.setPropulsorOn("DENTRO");
                JOptionPane.showMessageDialog(null, "Propulsor de Dentro ligado.");
            }
        });

        JButton showHistoryButton = new JButton("Show History");
        showHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHistory();
            }
        });

        // Adiciona os botões à interface
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveToLogsButton);
        buttonPanel.add(setAutoWriteToFileButton);
        buttonPanel.add(setAutoSaveOffButton);
        buttonPanel.add(setPropulsorOn1Button);
        buttonPanel.add(setPropulsorOn2Button);
        buttonPanel.add(showHistoryButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Adiciona o segundo menu para verificar o arquivo de logs
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem checkLogsItem = new JMenuItem("Check Logs");
        checkLogsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Ler o conteúdo do arquivo de logs e exibir apenas os últimos 10 logs
                    List<String> logs = Files.readAllLines(Path.of("logs.txt"));
                    int startIndex = Math.max(0, logs.size() - 10);
                    List<String> last10Logs = logs.subList(startIndex, logs.size());

                    // Exibe os últimos 10 logs em uma caixa de diálogo
                    StringBuilder logsText = new StringBuilder();
                    for (String log : last10Logs) {
                        logsText.append(log).append("\n");
                    }

                    JOptionPane.showMessageDialog(null, "Últimos 10 logs:\n" + logsText.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao ler o arquivo de logs.");
                }
            }
        });

        fileMenu.add(checkLogsItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void simularCoordenadas() {
        statusTextArea.append("Status:Get Location Request Sent\n");
        SistemaGPS coordinates = middlewere.getLocation();
        statusTextArea.append("Status: Got Response\n");


        double valorX = coordinates.getX();
        double valorY = coordinates.getY();

        // Atualiza os campos de texto com as coordenadas simuladas
        DecimalFormat df = new DecimalFormat("#.#####");
        latitudeField.setText(df.format(valorX));
        longitudeField.setText(df.format(valorY));

        // Atualiza a posição do satélite no Canvas
        gpsCanvas.updateSatellitePosition(coordinates);

    }

    private void showHistory() {
        try {
            statusTextArea.append("Status:Get History Request Sent\n");
            Deque<SistemaGPS> historyDeque = middlewere.getHistory();
            statusTextArea.append("Status: Got Response\n");

            // Display the history in a new window
            StringBuilder historyText = new StringBuilder();
            for (SistemaGPS sistemaGPS : historyDeque) {
                historyText.append("Latitude: ").append(sistemaGPS.getX()).append("\n");
                historyText.append("Longitude: ").append(sistemaGPS.getY()).append("\n");
                historyText.append("\n");
            }

            JTextArea historyTextArea = new JTextArea(historyText.toString());
            JScrollPane scrollPane = new JScrollPane(historyTextArea);

            // Create a new JFrame for displaying history
            JFrame historyFrame = new JFrame("History");
            historyFrame.setSize(400, 300);
            historyFrame.setLocationRelativeTo(null);
            historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            historyFrame.add(scrollPane);
            historyFrame.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching history.");
        }
    }

    class GPSCanvas extends Canvas {

        private double satelliteLatitude;
        private double satelliteLongitude;

        public void updateSatellitePosition(SistemaGPS coordinates) {
            // Obtém a posição do satélite usando as coordenadas fornecidas
            this.satelliteLatitude = coordinates.getX();
            this.satelliteLongitude = coordinates.getY();

            // Redesenha o Canvas
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            // Desenha o satélite na posição atual
            int x = (int) ((satelliteLongitude + 180) * getWidth() / 360);
            int y = (int) ((90 - satelliteLatitude) * getHeight() / 180);

            g.setColor(Color.RED);
            g.fillOval(x - 5, y - 5, 10, 10);
        }
    }
}
