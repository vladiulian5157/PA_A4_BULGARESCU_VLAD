package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DrawingPanel extends JPanel {
    public Cell[][] grid;
    public int rows, cols;
    private final int panelSize = 600;

    public DrawingPanel() {
        setPreferredSize(new Dimension(panelSize, panelSize));
        setBackground(Color.WHITE);

        // Homework: Manual Editing (Toggle walls on click)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (grid == null) return;
                int cellSize = panelSize / Math.max(rows, cols);
                int c = e.getX() / cellSize;
                int r = e.getY() / cellSize;

                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                    // Toggles the right wall of the cell you clicked
                    grid[r][c].walls[1] = !grid[r][c].walls[1];
                    repaint();
                }
            }
        });
    }

    public void initGrid(int size) {
        this.rows = size;
        this.cols = size;
        grid = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        int cellSize = panelSize / Math.max(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * cellSize;
                int y = i * cellSize;
                if (grid[i][j].walls[0]) g2.drawLine(x, y, x + cellSize, y);
                if (grid[i][j].walls[1]) g2.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
                if (grid[i][j].walls[2]) g2.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
                if (grid[i][j].walls[3]) g2.drawLine(x, y, x, y + cellSize);
            }
        }
    }
}