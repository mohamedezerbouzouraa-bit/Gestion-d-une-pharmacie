package p;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.event.*;
import connection.sqlcon;

public class menup {

	private JFrame frame;
	private int mouseX, mouseY; 
	private JTextField textField;
	private JPasswordField passwordField; 

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					menup window = new menup();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public menup() {
		initialize();
	}

	public JFrame getFrame() {
		return frame;
	}

	private void initialize() {
		frame = new JFrame();
		frame.setUndecorated(true); 
		frame.getContentPane().setBackground(new Color(60, 63, 65)); 
		frame.setBounds(100, 100, 445, 571);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null); 

		// BARRE DE TITRE
		JPanel titleBar = new JPanel();
		titleBar.setBounds(0, 0, 445, 35);
		titleBar.setBackground(new Color(45, 45, 45));
		frame.getContentPane().add(titleBar);
		titleBar.setLayout(null);

		JLabel lblTitle = new JLabel("GESTION PHARMACIE");
		lblTitle.setForeground(Color.LIGHT_GRAY);
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTitle.setBounds(10, 0, 200, 35);
		titleBar.add(lblTitle);

		JButton btnClose = new JButton("X");
		btnClose.setFocusPainted(false);
		btnClose.setBorderPainted(false);
		btnClose.setBackground(new Color(45, 45, 45));
		btnClose.setForeground(Color.WHITE);
		btnClose.setBounds(395, 0, 50, 35);
		btnClose.addActionListener(e -> System.exit(0));
		titleBar.add(btnClose);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 292, 425, 269);
		panel.setBackground(new Color(60, 63, 65));
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblUser = new JLabel("USERNAME ");
		lblUser.setFont(new Font("Arial", Font.BOLD, 14));
		lblUser.setForeground(Color.WHITE); 
		lblUser.setBounds(10, 49, 127, 38);
		panel.add(lblUser);
		
		JLabel lblPass = new JLabel("PASSWORD");
		lblPass.setForeground(Color.WHITE); 
		lblPass.setFont(new Font("Arial", Font.BOLD, 14));
		lblPass.setBounds(10, 126, 127, 30);
		panel.add(lblPass);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		textField.setCaretColor(Color.WHITE);
		textField.setForeground(Color.WHITE);
		textField.setBackground(new Color(60, 63, 65));
		textField.setBorder(new MatteBorder(0, 0, 2, 0, Color.WHITE)); 
		textField.setBounds(147, 35, 268, 38);
		panel.add(textField);
		
		passwordField = new JPasswordField();
		passwordField.setEchoChar('*'); 
		passwordField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		passwordField.setCaretColor(Color.WHITE);
		passwordField.setForeground(Color.WHITE);
		passwordField.setBackground(new Color(60, 63, 65));
		passwordField.setBorder(new MatteBorder(0, 0, 2, 0, Color.WHITE));
		passwordField.setBounds(147, 105, 268, 38);
		panel.add(passwordField);
		
		JButton btnLogin = new JButton("LOGIN");
		btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setBackground(new Color(255, 140, 0)); 
		btnLogin.setBorderPainted(false);
		btnLogin.setFocusPainted(false);
		btnLogin.setBounds(86, 188, 268, 45);
		
		// --- ACTION DU BOUTON LOGIN ---
		btnLogin.addActionListener(e -> {
		    String user = textField.getText();
		    String pass = new String(passwordField.getPassword());
		    
		    if (sqlcon.getCon() == null) { 
		        showCustomMsg("CONFIGURATION", "Le serveur SQL n'est pas prêt.<br>Ouverture de l'assistant...", new Color(52, 152, 219));
		        frame.dispose(); 
		        new ajoutesql().setVisible(true); 
		    } else {
		        String role = sqlcon.checkLogin(user, pass);
		        if (role != null) {
		           
		            showCustomMsg("SUCCÈS", "Bienvenue <b><font color='white'>" + user.toUpperCase() + "</font></b> !<br>Accès autorisé au système.", new Color(46, 204, 113));
		            frame.dispose();
		            if (role.equalsIgnoreCase("admin")) {
		            	loginadmin adminWindow = new loginadmin();
		                adminWindow.setVisible(true);
		            }
		            else {
		            	login userWindow = new login();
		                userWindow.setVisible(true);
		            	
		            }
		        } 
		            else {
		            showCustomMsg("ERREUR", "Identifiants incorrects.<br>Veuillez vérifier vos données.", new Color(231, 76, 60));
		        }
		    }
		});

		btnLogin.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				btnLogin.setBackground(new Color(204, 112, 0)); 
			}
			public void mouseExited(MouseEvent e) {
				btnLogin.setBackground(new Color(255, 140, 0));
			}
		});
		
		panel.add(btnLogin);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(10, 25, 425, 257);
		try {
			lblNewLabel.setIcon(new ImageIcon(menup.class.getResource("/images/unamed.png")));
		} catch (Exception e) {
			System.out.println("Image non trouvée");
		}
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel);

		titleBar.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		titleBar.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				frame.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
			}
		});
	}

	// --- MÉTHODE POPUP  ---
	private void showCustomMsg(String title, String message, Color themeColor) {
		JDialog dialog = new JDialog(frame, true);
		dialog.setUndecorated(true);
		dialog.setSize(360, 160); // Un peu plus large et moins haut
		dialog.setLocationRelativeTo(frame);

		JPanel p = new JPanel();
		p.setBackground(new Color(30, 30, 30)); // Fond plus sombre pour faire ressortir le blanc
		p.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, themeColor)); // Bordure nette
		p.setLayout(null);
		dialog.getContentPane().add(p);

		JLabel t = new JLabel(title);
		t.setFont(new Font("Segoe UI", Font.BOLD, 16));
		t.setForeground(themeColor);
		t.setBounds(20, 15, 320, 25);
		p.add(t);

		JLabel m = new JLabel("<html><font color='#BBBBBB'>" + message + "</font></html>");
		m.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		m.setBounds(20, 50, 320, 45);
		p.add(m);

		JButton b = new JButton("CONTINUER");
		b.setBounds(105, 110, 150, 35);
		b.setBackground(themeColor);
		b.setForeground(Color.WHITE);
		b.setFont(new Font("Segoe UI", Font.BOLD, 12));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		b.addActionListener(e -> dialog.dispose());
		p.add(b);

		dialog.setVisible(true);
	}
}
