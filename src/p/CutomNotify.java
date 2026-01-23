package p;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class CustomNotify extends JDialog {

    public CustomNotify(JFrame parent, String title, String message, Color themeColor) {
        super(parent, true); // 'true' rend la fenêtre modale
        setUndecorated(true);
        setSize(380, 200);
        setLocationRelativeTo(parent);
        
        // Arrondir les coins de la popup
        setShape(new RoundRectangle2D.Double(0, 0, 380, 200, 30, 30));

        JPanel content = new JPanel();
        content.setBackground(new Color(30, 30, 30)); // Fond sombre élégant
        content.setBorder(BorderFactory.createLineBorder(themeColor, 2)); // Bordure de la couleur du message
        content.setLayout(null);
        getContentPane().add(content);

        // --- TITRE ---
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(themeColor);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(30, 25, 320, 30);
        content.add(lblTitle);

        // --- MESSAGE ---
        JLabel lblMsg = new JLabel("<html><div style='text-align: left;'>" + message + "</div></html>");
        lblMsg.setForeground(Color.WHITE);
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblMsg.setBounds(30, 65, 320, 60);
        lblMsg.setVerticalAlignment(SwingConstants.TOP);
        content.add(lblMsg);

        // --- BOUTON FERMER ---
        JButton btnOk = new JButton("CONTINUER");
        btnOk.setBounds(115, 140, 150, 40);
        btnOk.setBackground(themeColor);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOk.setFocusPainted(false);
        btnOk.setBorderPainted(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnOk.addActionListener(e -> dispose());
        content.add(btnOk);
    }

    // Méthode statique pour l'appeler en une seule ligne
    public static void showMsg(JFrame parent, String title, String message, Color color) {
        CustomNotify dialog = new CustomNotify(parent, title, message, color);
        dialog.setVisible(true);
    }
}
