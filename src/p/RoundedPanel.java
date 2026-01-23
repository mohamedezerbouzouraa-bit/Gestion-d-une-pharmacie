package p;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int radius;
    private Color backgroundColor;

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.radius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false); // Permet de voir le fond de la fenêtre derrière les coins arrondis
    }

    @Override
    protected void paintComponent(Graphics g) {
        // On ne branche pas super.paintComponent(g) ici si on veut un fond personnalisé sans bordures natives
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessine l'ombre ou le fond
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        super.paintComponent(g);
    }
}
