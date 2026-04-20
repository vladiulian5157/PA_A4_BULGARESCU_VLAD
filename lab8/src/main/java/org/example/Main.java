package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

public class Main extends JFrame {
    private DrawingPanel canvas;
    private JSpinner sizeSpinner;

    public Main() {
        setTitle("Maze Designer Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel configPanel = new JPanel();
        sizeSpinner = new JSpinner(new SpinnerNumberModel(10, 5, 50, 1));
        JButton btnInit = new JButton("Draw Grid");
        btnInit.addActionListener(e -> canvas.initGrid((int)sizeSpinner.getValue()));
        configPanel.add(new JLabel("Maze Size:"));
        configPanel.add(sizeSpinner);
        configPanel.add(btnInit);

        canvas = new DrawingPanel();

        JPanel controlPanel = new JPanel();
        JButton btnCreate = new JButton("Create (Random)");
        JButton btnSave = new JButton("Save");
        JButton btnExport = new JButton("Export PNG");
        JButton btnExit = new JButton("Exit");

        btnCreate.addActionListener(e -> randomize());
        btnSave.addActionListener(e -> saveMaze());
        btnExport.addActionListener(e -> exportPNG());
        btnExit.addActionListener(e -> System.exit(0));

        controlPanel.add(btnCreate);
        controlPanel.add(btnSave);
        controlPanel.add(btnExport);
        controlPanel.add(btnExit);

        add(configPanel, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void randomize() {
        if (canvas.grid == null) return;
        Random r = new Random();
        for (int i = 0; i < canvas.rows; i++) {
            for (int j = 0; j < canvas.cols; j++) {
                // Randomly remove one of the four walls
                canvas.grid[i][j].walls[r.nextInt(4)] = false;
            }
        }
        canvas.repaint();
    }

    private void saveMaze() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("maze_save.dat"))) {
            out.writeObject(canvas.grid);
            JOptionPane.showMessageDialog(this, "Progress saved to maze_save.dat");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving maze.");
        }
    }

    private void exportPNG() {
        BufferedImage img = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        canvas.paint(img.getGraphics());
        try {
            ImageIO.write(img, "png", new File("maze_screenshot.png"));
            JOptionPane.showMessageDialog(this, "Saved as maze_screenshot.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}