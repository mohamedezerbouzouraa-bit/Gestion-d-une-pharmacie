package p;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import connection.sqlcon; 

public class ajoutesql extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPasswordField passwordField; 

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ajoutesql frame = new ajoutesql();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ajoutesql() {
        setTitle("Configuration Serveur SQL");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 300);
        
        contentPane = new JPanel();
        contentPane.setBackground(new Color(45, 45, 45)); 
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblTitle = new JLabel("CONFIGURATION SQL");
        lblTitle.setForeground(new Color(255, 165, 0)); 
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(10, 25, 364, 30);
        contentPane.add(lblTitle);
        
        JLabel lblInfo = new JLabel("Entrez le mot de passe de votre serveur local :");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblInfo.setBounds(50, 80, 300, 20);
        contentPane.add(lblInfo);
        
        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(60, 60, 60));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0)));
        passwordField.setBounds(50, 110, 280, 30);
        contentPane.add(passwordField);
        
        JButton btnValider = new JButton("VÉRIFIER & LANCER");
        btnValider.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnValider.setBackground(new Color(255, 165, 0)); 
        btnValider.setForeground(Color.BLACK);
        btnValider.setFocusPainted(false);
        btnValider.setBorder(null);
        btnValider.setBounds(100, 180, 180, 40);
        contentPane.add(btnValider);
        
        JLabel lblAide = new JLabel("(Laissez vide si vous utilisez XAMPP)");
        lblAide.setForeground(new Color(150, 150, 150));
        lblAide.setFont(new Font("Tahoma", Font.ITALIC, 10));
        lblAide.setHorizontalAlignment(SwingConstants.CENTER);
        lblAide.setBounds(50, 145, 280, 20);
        contentPane.add(lblAide);

        // --- ACTION DU BOUTON CORRIGÉE ---
        btnValider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String mdp = new String(passwordField.getPassword());
                
                // 1. On teste d'abord la connexion au serveur SANS la base
                if (testerConnexionSeule(mdp)) {
                    // 2. Si le serveur répond, on lance la création/config
                    if (sqlcon.setupDatabase(mdp)) {
                        showCustomMsg("SYSTÈME CONFIGURÉ", 
                            "<b>BASE DE DONNÉES CONNECTÉE</b><br>" +
                            "Installation réussie. Lancement de l'application...", 
                            new Color(46, 204, 113)); 
                        
                        dispose(); 
                        menup window = new menup();
                        window.getFrame().setVisible(true); 
                    } else {
                        showCustomMsg("ERREUR SCRIPT", 
                            "Serveur trouvé mais impossible d'exécuter le script SQL.<br>Vérifiez le fichier pharmacie.sql.", 
                            new Color(231, 76, 60));
                    }
                } else {
                    // Si le serveur refuse le MDP dès le départ
                    showCustomMsg("ERREUR DE CONNEXION", 
                        "<b>ACCÈS REFUSÉ</b><br>" +
                        "Mot de passe incorrect ou serveur MySQL éteint.", 
                        new Color(231, 76, 60)); 
                }
            }
        });
        
        setLocationRelativeTo(null);
    }

    // Méthode de test rapide pour éviter l'erreur de base inexistante
    private boolean testerConnexionSeule(String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // On se connecte au serveur uniquement, pas à une base précise
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useSSL=false", "root", pass);
            c.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void showCustomMsg(String title, String message, Color themeColor) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(380, 160);
        dialog.setLocationRelativeTo(this);

        JPanel p = new JPanel();
        p.setBackground(new Color(25, 25, 25)); 
        p.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, themeColor)); 
        p.setLayout(null);
        dialog.getContentPane().add(p);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(themeColor);
        t.setBounds(20, 12, 340, 25);
        p.add(t);

        JLabel m = new JLabel("<html><body style='font-family:Segoe UI; color:#DCDCDC;'>" + message + "</body></html>");
        m.setBounds(20, 45, 340, 60);
        p.add(m);

        JButton b = new JButton("OK");
        b.setBounds(130, 110, 120, 35);
        b.setBackground(themeColor);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(ev -> dialog.dispose());
        p.add(b);

        dialog.setVisible(true);
    }
}
