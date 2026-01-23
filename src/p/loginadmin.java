package p;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.util.ArrayList;
import connection.sqlcon;
import p.menup;

public class loginadmin extends JFrame {

    private final String IMG_FOLDER = "/images/";
    private DefaultTableModel tableModel;
    private ArrayList<String[]> listeUtilisateurs;
    private JTextField tfUser, tfPass;
    private JComboBox<String> cbRole; 
    private JLabel lblCardUsers; 
    private JLabel lblStockPercent; 
    private JLabel lblChiffreAffaire; 
    private JLabel lblValeurStock;    
    private JLabel lblCardFournisseurs; // Nouveau : Label pour les fournisseurs
    private JTable table;

    public loginadmin() {
        setTitle("ADMINISTRATION - SYSTÈME PHARMACIE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 850);
        getContentPane().setBackground(new Color(45, 45, 45)); 
        setLayout(null);

        // --- 1. CHARGEMENT DES DONNÉES ---
        listeUtilisateurs = sqlcon.getListeUtilisateurs();
        
        // --- 2. HEADER ---
        JPanel pnlHeader = new JPanel(null);
        pnlHeader.setBackground(new Color(30, 30, 30));
        pnlHeader.setBounds(0, 0, 1250, 60);
        add(pnlHeader);

        JLabel lblHeader = new JLabel("ADMINISTRATION - SYSTÈME PHARMACIE", SwingConstants.CENTER);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setBounds(0, 10, 1250, 40);
        pnlHeader.add(lblHeader);

        JButton btnLogout = new JButton("DÉCONNEXION");
        btnLogout.setBounds(1080, 12, 140, 35);
        btnLogout.setBackground(new Color(231, 76, 60)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLogout.addActionListener(e -> {
            int x = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment vous déconnecter ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (x == JOptionPane.YES_OPTION) {
                this.dispose();
                menup menuWindow = new menup(); 
                menuWindow.getFrame().setVisible(true); 
            }
        });
        pnlHeader.add(btnLogout);

        // --- 3. CARTES DE STATISTIQUES ---
        int cardW = 250, cardH = 260, startX = 60, spacing = 45;
        
        // ÉTAT DES STOCKS
        add(createRoundedCard("ÉTAT DES STOCKS", sqlcon.getPourcentageStock(), null, startX, 90, cardW, cardH, Color.WHITE, new Color(230, 126, 34)));
        
        // CHIFFRE D'AFFAIRES
        add(createRoundedCard("CHIFFRE D'AFFAIRES", sqlcon.getChiffreAffaire(), null, startX + (cardW + spacing), 90, cardW, cardH, new Color(35, 35, 35), null));
        
        // FOURNISSEURS (Maintenant dynamique via sqlcon.getNombreFournisseurs)
        add(createRoundedCard("FOURNISSEURS", sqlcon.getNombreFournisseurs(), "fournisuer.PNG", startX + (cardW + spacing) * 2, 90, cardW, cardH, new Color(60, 60, 60), Color.WHITE));
        
        // UTILISATEURS
        JPanel cardU = createRoundedCard("UTILISATEURS", getTexteCompteur(), "liste utlisateur.png", startX + (cardW + spacing) * 3, 90, cardW, cardH, Color.WHITE, Color.BLACK);
        add(cardU);

        // --- 4. SECTION GESTION ---
        RoundedPanel pnlGestion = new RoundedPanel(30, new Color(35, 35, 35));
        pnlGestion.setBounds(startX, 380, 545, 400);
        pnlGestion.setLayout(null);
        add(pnlGestion);

        JLabel lblGestTitle = new JLabel("GESTION DES UTILISATEURS");
        lblGestTitle.setForeground(Color.WHITE);
        lblGestTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblGestTitle.setBounds(25, 15, 300, 25);
        pnlGestion.add(lblGestTitle);

        tfUser = addInput(pnlGestion, "NOM D'UTILISATEUR", 25, 60, 490);
        tfPass = addInput(pnlGestion, "MOT DE PASSE", 25, 135, 490);
        
        JLabel lblRoleLabel = new JLabel("RÔLE / TYPE");
        lblRoleLabel.setForeground(new Color(200, 200, 200));
        lblRoleLabel.setBounds(25, 210, 200, 20);
        pnlGestion.add(lblRoleLabel);

        String[] roles = { "admin", "pharmacien" };
        cbRole = new JComboBox<>(roles);
        cbRole.setBounds(25, 235, 490, 35);
        cbRole.setBackground(new Color(50, 50, 50));
        cbRole.setForeground(Color.WHITE);
        cbRole.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                BasicArrowButton arrow = new BasicArrowButton(BasicArrowButton.SOUTH, new Color(50, 50, 50), Color.WHITE, Color.WHITE, Color.WHITE);
                arrow.setBorder(BorderFactory.createEmptyBorder());
                return arrow;
            }
        });
        pnlGestion.add(cbRole);

        // --- BOUTONS CRUD ---
        JButton btnAdd = createBtn("AJOUTER", new Color(46, 204, 113), 25, 330);
        btnAdd.addActionListener(e -> {
            String user = tfUser.getText().trim();
            if(user.isEmpty() || tfPass.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Champs vides !");
                return;
            }
            sqlcon.ajouterUtilisateur(user, tfPass.getText(), cbRole.getSelectedItem().toString());
            actualiserInterface();
        });

        JButton btnMod = createBtn("MODIFIER", new Color(52, 152, 219), 155, 330);
        btnMod.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                String id = table.getValueAt(row, 3).toString();
                sqlcon.modifierUtilisateur(id, tfUser.getText(), tfPass.getText(), cbRole.getSelectedItem().toString());
                actualiserInterface();
            }
        });

        JButton btnDel = createBtn("SUPPRIMER", new Color(231, 76, 60), 285, 330);
        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                sqlcon.supprimerUtilisateur(table.getValueAt(row, 3).toString());
                actualiserInterface();
            }
        });

        JButton btnAct = createBtn("ACTUALISER", new Color(230, 126, 34), 415, 330);
        btnAct.addActionListener(e -> actualiserInterface());

        pnlGestion.add(btnAdd); pnlGestion.add(btnMod); pnlGestion.add(btnDel); pnlGestion.add(btnAct);

        // --- 5. SECTION LISTE ---
        RoundedPanel pnlTableCont = new RoundedPanel(30, new Color(35, 35, 35));
        pnlTableCont.setBounds(startX + 590, 380, 545, 400);
        pnlTableCont.setLayout(new BorderLayout());
        pnlTableCont.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(pnlTableCont);

        tableModel = new DefaultTableModel(null, new String[]{"N°", "Utilisateur", "Type / Rôle", "Clé"});
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setBackground(new Color(35, 35, 35));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 126, 34));
        
        table.getColumnModel().getColumn(3).setMinWidth(0);
        table.getColumnModel().getColumn(3).setMaxWidth(0);
        table.getColumnModel().getColumn(3).setWidth(0);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                tfUser.setText(table.getValueAt(row, 1).toString()); 
                cbRole.setSelectedItem(table.getValueAt(row, 2).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(35, 35, 35));
        pnlTableCont.add(scrollPane, BorderLayout.CENTER);

        actualiserInterface();
        setLocationRelativeTo(null);
    }

    private void actualiserInterface() {
        listeUtilisateurs = sqlcon.getListeUtilisateurs();
        tableModel.setRowCount(0);
        for (int i = 0; i < listeUtilisateurs.size(); i++) {
            String[] u = listeUtilisateurs.get(i);
            tableModel.addRow(new Object[]{ (i + 1), u[0], u[1], u[2] });
        }
        
        // --- MISE À JOUR DYNAMIQUE DES CARTES ---
        if(lblCardUsers != null) lblCardUsers.setText(getTexteCompteur());
        if(lblStockPercent != null) lblStockPercent.setText(sqlcon.getPourcentageStock());
        if(lblChiffreAffaire != null) lblChiffreAffaire.setText(sqlcon.getChiffreAffaire());
        if(lblValeurStock != null) lblValeurStock.setText(sqlcon.getValeurTotalStock());
        if(lblCardFournisseurs != null) lblCardFournisseurs.setText(sqlcon.getNombreFournisseurs()); // Mise à jour ici
        
        tfUser.setText(""); tfPass.setText("");
    }

    private String getTexteCompteur() {
        int n = listeUtilisateurs.size();
        return n + (n > 1 ? " UTILISATEURS" : " UTILISATEUR");
    }

    private JTextField addInput(JPanel p, String label, int x, int y, int w) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setBounds(x, y, 200, 20);
        p.add(lbl);
        JTextField tf = new JTextField();
        tf.setBounds(x, y + 25, w, 35);
        tf.setBackground(new Color(50, 50, 50));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        p.add(tf);
        return tf;
    }

    private JPanel createRoundedCard(String title, String val, String path, int x, int y, int w, int h, Color bg, Color valCol) {
        RoundedPanel card = new RoundedPanel(40, bg);
        card.setBounds(x, y, w, h);
        card.setLayout(null);
        
        JLabel lblT = new JLabel(title, SwingConstants.CENTER);
        lblT.setForeground(new Color(130, 130, 130));
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblT.setBounds(0, 20, w, 25);
        card.add(lblT);

        if (title.equals("CHIFFRE D'AFFAIRES")) {
            lblChiffreAffaire = new JLabel(val, SwingConstants.CENTER);
            lblChiffreAffaire.setFont(new Font("Segoe UI", Font.BOLD, 38));
            lblChiffreAffaire.setForeground(new Color(46, 204, 113)); 
            lblChiffreAffaire.setBounds(0, 75, w, 50);
            card.add(lblChiffreAffaire);

            JLabel lblSep = new JLabel("ACHATS FOURNISSEURS :", SwingConstants.CENTER);
            lblSep.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblSep.setForeground(Color.GRAY);
            lblSep.setBounds(0, 135, w, 20);
            card.add(lblSep);

            lblValeurStock = new JLabel(sqlcon.getValeurTotalStock(), SwingConstants.CENTER);
            lblValeurStock.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lblValeurStock.setForeground(new Color(231, 76, 60)); 
            lblValeurStock.setBounds(0, 155, w, 40);
            card.add(lblValeurStock);
            
        } else {
            if (path != null) {
                try {
                    java.net.URL imgURL = getClass().getResource(IMG_FOLDER + path);
                    if (imgURL != null) {
                        Image img = new ImageIcon(imgURL).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        JLabel lblImg = new JLabel(new ImageIcon(img));
                        lblImg.setBounds((w - 100) / 2, 60, 100, 100);
                        card.add(lblImg);
                    }
                } catch (Exception e) {}
            }
            JLabel lblV = new JLabel(val, SwingConstants.CENTER);
            lblV.setFont(new Font("Segoe UI", Font.BOLD, path == null ? 55 : 24));
            lblV.setForeground(valCol);
            lblV.setBounds(0, path == null ? 95 : 190, w, 50);
            card.add(lblV);

            // MEMORISATION DES LABELS
            if(title.equals("UTILISATEURS")) this.lblCardUsers = lblV;
            if(title.equals("ÉTAT DES STOCKS")) this.lblStockPercent = lblV;
            if(title.equals("FOURNISSEURS")) this.lblCardFournisseurs = lblV; // Mémorisé ici
        }
        
        return card;
    }

    private JButton createBtn(String txt, Color bg, int x, int y) {
        JButton b = new JButton(txt);
        b.setBounds(x, y, 110, 40);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new loginadmin().setVisible(true));
    }
}
