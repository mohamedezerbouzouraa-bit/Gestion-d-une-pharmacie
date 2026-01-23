package p;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import connection.sqlcon;
public class login extends JFrame {

    private JPanel contentPane, mainPanel;
    private CardLayout cardLayout;
    
    private Color colorBg = new Color(18, 18, 18);
    private Color colorSidebar = new Color(28, 28, 28);
    private Color colorAccent = new Color(46, 204, 113); 
    private Color colorCard = new Color(35, 35, 35);
    private Color colorInput = new Color(50, 50, 50);
    private Color colorDanger = new Color(231, 76, 60); 
    private int seuilAlerte = 10; // Seuil par défaut
    private JLabel lblAlertM; // Pour le texte "X Produits en alerte"
    private JLabel lblAlertSidebarCount; // Optionnel : si vous voulez un petit badge
    public login() {
        setTitle("PHARMA-PRO : DASHBOARD PHARMACIEN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);

        contentPane = new JPanel(null);
        contentPane.setBackground(colorBg);
        setContentPane(contentPane);

        // --- 1. CRÉATION DE LA SIDEBAR (Doit être faite AVANT d'y ajouter des trucs) ---
        JPanel sidebar = new JPanel(null);
        sidebar.setBounds(0, 0, 280, 850);
        sidebar.setBackground(colorSidebar);
        contentPane.add(sidebar);

        JLabel lblLogo = new JLabel("PHARMA SYSTEM");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(colorAccent);
        lblLogo.setBounds(30, 30, 220, 30);
        sidebar.add(lblLogo);

        // --- 2. BOUTONS DU MENU (Sidebar) ---
        String[] menuItems = {"Dashboard", "Inventaire", "Ventes", "Commandes"};
        String[] icons = {"\uD83D\uDCCA", "\uD83D\uDCE6", "\uD83D\uDED2", "\uD83D\uDE9B"};
        
        int yMenu = 120;
        for (int i = 0; i < menuItems.length; i++) {
            final String name = menuItems[i];
            JButton btn = createMenuBtn(icons[i] + " " + name, yMenu);
            btn.addActionListener(e -> cardLayout.show(mainPanel, name));
            sidebar.add(btn);
            yMenu += 60;
        }

        // --- 3. PANEL D'ALERTE DANS LA SIDEBAR ---
        JPanel pnlAlertSidebar = new JPanel(null);
        pnlAlertSidebar.setBounds(10, 730, 260, 80);
        pnlAlertSidebar.setBackground(new Color(231, 76, 60, 40)); 
        pnlAlertSidebar.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, colorDanger)); 
        sidebar.add(pnlAlertSidebar); // Ajouté à sidebar (qui existe maintenant)

        JLabel lblAlertT = new JLabel("(!) ALERTES STOCK");
        lblAlertT.setForeground(colorDanger);
        lblAlertT.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAlertT.setBounds(15, 15, 200, 20);
        pnlAlertSidebar.add(lblAlertT);

        // lblAlertM est une variable de classe déclarée en haut de ton code
        lblAlertM = new JLabel(sqlcon.getCountAlertes(seuilAlerte) + " Produits en alerte !"); 
        lblAlertM.setForeground(Color.WHITE);
        lblAlertM.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAlertM.setBounds(15, 40, 200, 20);
        pnlAlertSidebar.add(lblAlertM);

        // --- 4. HEADER (Zone de droite) ---
        JLabel lblWelcome = new JLabel("Bienvenue, Pharmacien");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setBounds(310, 25, 400, 40);
        contentPane.add(lblWelcome);

        JButton btnLogout = new JButton("DÉCONNEXION");
        btnLogout.setBounds(1120, 25, 140, 40);
        btnLogout.setBackground(colorDanger);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ACTION DE DÉCONNEXION
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Voulez-vous vraiment vous déconnecter ?", 
                "DÉCONNEXION", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Ferme le dashboard actuel
                
                // Ouvre à nouveau la fenêtre menup
                menup fenetreLogin = new menup();
                fenetreLogin.getFrame().setVisible(true);
            }
        });
        contentPane.add(btnLogout);

        // --- 5. MAIN PANEL (Zone de contenu) ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBounds(310, 100, 960, 700);
        mainPanel.setBackground(colorBg);
        contentPane.add(mainPanel);

        // Ajout des différentes vues
        mainPanel.add(createDashboardView(), "Dashboard");
        mainPanel.add(createInventaireView(), "Inventaire");
        mainPanel.add(createVenteView(), "Ventes");
        mainPanel.add(createCommandeView(), "Commandes");
    }


    private JPanel createDashboardView() {
        JPanel p = new JPanel(null);
        p.setName("DashboardPanel");
        p.setBackground(colorBg);
        
        // 1. RÉCUPÉRATION DES DONNÉES
        int nbAlertes = sqlcon.getCountAlertes(seuilAlerte);
        int nbCommandes = sqlcon.getNbCommandesJour(); 
        String totalVentes = sqlcon.getVentesJour();
        java.util.ArrayList<String[]> detailsAlertes = sqlcon.getAlertesStockDetail(seuilAlerte);

        // --- SECTION 1 : CARTES STATISTIQUES ---
        p.add(createStatCard("Ventes du jour", totalVentes, 0, 0, 300, 140, colorAccent));
        p.add(createStatCard("Commandes du jour", String.valueOf(nbCommandes), 325, 0, 300, 140, new Color(52, 152, 219)));
        p.add(createStatCard("Alertes Rupture", String.valueOf(nbAlertes), 650, 0, 310, 140, colorDanger));

        // --- SECTION 2 : HISTORIQUE AVEC FILTRE ---
        JLabel lblTableTitle = new JLabel("HISTORIQUE DES VENTES");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setForeground(Color.WHITE);
        lblTableTitle.setBounds(0, 180, 250, 30);
        p.add(lblTableTitle);

        // --- CHANGEMENT : Dates réelles et recherche personnalisée ---
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String aujourdhui = java.time.LocalDate.now().format(fmt);
        String hier = java.time.LocalDate.now().minusDays(1).format(fmt);

        String[] options = {"Tout l'historique", "Aujourd'hui ("+aujourdhui+")", "Hier ("+hier+")", "Choisir une date..."};
        JComboBox<String> comboFiltre = new JComboBox<>(options);
        comboFiltre.setBounds(370, 180, 255, 30);
        comboFiltre.setBackground(colorCard);
        comboFiltre.setForeground(Color.WHITE);
        p.add(comboFiltre);

        RoundedPanel pnlTable = new RoundedPanel(25, colorCard);
        pnlTable.setBounds(0, 220, 625, 460);
        pnlTable.setLayout(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        p.add(pnlTable);

        String[] cols = {"DATE", "NOM DU CLIENT", "TOTAL DÉPENSÉ", "ACTION"};
        DefaultTableModel modelVentes = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tableVentes = new JTable(modelVentes);
        styleDarkTable(tableVentes);
        tableVentes.getColumnModel().getColumn(0).setPreferredWidth(90);

        // Chargement initial des données
        java.util.ArrayList<Object[]> toutesVentes = sqlcon.getHistoriqueGroupeParClient();
        for (Object[] v : toutesVentes) { modelVentes.addRow(v); }

        // --- LOGIQUE DU FILTRE MISE À JOUR ---
        comboFiltre.addActionListener(e -> {
            int index = comboFiltre.getSelectedIndex();
            modelVentes.setRowCount(0);
            String dateRecherche = "";

            if (index == 0) { // Tout
                for (Object[] v : toutesVentes) modelVentes.addRow(v);
                return;
            } else if (index == 1) { // Aujourd'hui
                dateRecherche = aujourdhui;
            } else if (index == 2) { // Hier
                dateRecherche = hier;
            } else if (index == 3) { // Recherche manuelle
                dateRecherche = JOptionPane.showInputDialog(p, "Entrez la date (AAAA-MM-JJ) :", aujourdhui);
            }

            if (dateRecherche != null && !dateRecherche.isEmpty()) {
                for (Object[] v : toutesVentes) {
                    if (v[0].toString().equals(dateRecherche)) {
                        modelVentes.addRow(v);
                    }
                }
            }
        });

        // --- ÉCOUTEUR DE CLIC ---
        tableVentes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tableVentes.getSelectedRow();
                if (row != -1) {
                    String nomClient = tableVentes.getValueAt(row, 1).toString();
                    String detailInfo = sqlcon.getDetailsClient(nomClient);
                    
                    JDialog dialog = new JDialog();
                    dialog.setTitle("Facture : " + nomClient);
                    dialog.setModal(true);
                    dialog.setSize(380, 450);
                    dialog.setLocationRelativeTo(p);

                    JPanel content = new JPanel(new BorderLayout());
                    content.setBackground(colorCard);
                    content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                    JLabel title = new JLabel("DÉTAILS DES ACHATS");
                    title.setForeground(colorAccent);
                    title.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
                    content.add(title, BorderLayout.NORTH);

                    JTextArea area = new JTextArea(detailInfo);
                    area.setBackground(colorCard);
                    area.setForeground(Color.WHITE);
                    area.setFont(new Font("Monospaced", Font.PLAIN, 13)); 
                    area.setEditable(false);
                    
                    JScrollPane scroll = new JScrollPane(area);
                    scroll.setBorder(null);
                    scroll.getViewport().setBackground(colorCard);
                    content.add(scroll, BorderLayout.CENTER);

                    JButton btnFermer = new JButton("FERMER");
                    btnFermer.setBackground(colorAccent);
                    btnFermer.setForeground(Color.WHITE);
                    btnFermer.addActionListener(ev -> dialog.dispose());
                    content.add(btnFermer, BorderLayout.SOUTH);

                    dialog.add(content);
                    dialog.setVisible(true);
                }
            }
        });

        JScrollPane sp = new JScrollPane(tableVentes);
        sp.setBorder(null);
        sp.getViewport().setBackground(colorCard);
        pnlTable.add(sp, BorderLayout.CENTER);

        // --- SECTION 3 : STOCK CRITIQUE ---
        RoundedPanel pnlRup = new RoundedPanel(25, colorCard);
        pnlRup.setBounds(650, 180, 310, 500);
        pnlRup.setLayout(null);
        p.add(pnlRup);
        
        JLabel lblR = new JLabel("STOCK CRITIQUE (< " + seuilAlerte + ")");
        lblR.setForeground(colorDanger);
        lblR.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblR.setBounds(25, 25, 250, 25);
        pnlRup.add(lblR);

        JButton btnSetSeuil = new JButton("⚙");
        btnSetSeuil.setBounds(250, 20, 45, 30);
        btnSetSeuil.setBackground(colorCard);
        btnSetSeuil.setForeground(Color.WHITE);
        btnSetSeuil.setBorder(null);
        btnSetSeuil.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ajuster le seuil :", seuilAlerte);
            if (input != null && !input.isEmpty()) {
                try {
                    seuilAlerte = Integer.parseInt(input);
                    refreshDashboard(); 
                } catch (Exception ex) {}
            }
        });
        pnlRup.add(btnSetSeuil);

        StringBuilder htmlList = new StringBuilder("<html><body style='font-family:Segoe UI; color:#bbb;'>");
        for (String[] alert : detailsAlertes) {
            int qte = Integer.parseInt(alert[1]); 
            String color = (qte <= 0) ? "#ff4d4d" : "#f39c12";
            htmlList.append("<br>• ").append(alert[0]).append(" : <font color='")
                    .append(color).append("'>Stock: ").append(qte).append("</font><br>");
        }
        htmlList.append("</body></html>");

        JLabel list = new JLabel(htmlList.toString());
        list.setBounds(25, 70, 260, 400);
        list.setVerticalAlignment(SwingConstants.TOP);
        pnlRup.add(list);
        
        return p;
    }
    public void refreshDashboard() {
        // 1. Sauvegarder quel onglet était ouvert (pour ne pas être téléporté au Dashboard)
        String currentCard = "Dashboard";
        for (Component comp : mainPanel.getComponents()) {
            if (comp.isVisible()) {
                currentCard = comp.getName(); // On récupère le nom (ex: "Ventes")
            }
        }

        // 2. Mise à jour des alertes sidebar
        int nbAlertes = sqlcon.getCountAlertes(seuilAlerte);
        if (lblAlertM != null) {
            lblAlertM.setText(nbAlertes + " Produits en alerte !");
        }

        // 3. Rechargement complet des données
        mainPanel.removeAll(); 
        
        // On recrée les vues : cela relance les requêtes SQL (Fusion des ventes, etc.)
        mainPanel.add(createDashboardView(), "Dashboard");
        mainPanel.add(createInventaireView(), "Inventaire");
        mainPanel.add(createVenteView(), "Ventes");
        mainPanel.add(createCommandeView(), "Commandes");

        // 4. Rafraîchir l'affichage graphique
        mainPanel.revalidate();
        mainPanel.repaint();
        
        // 5. Rester sur l'onglet où on était
        cardLayout.show(mainPanel, currentCard);
    }
private String produitSelectionne = ""; 

private JPanel createInventaireView() {
    JPanel p = new JPanel(null);
    p.setName("InventairePanel");
    p.setBackground(colorBg);

    // --- 1. BARRE DE RECHERCHE (CONSERVÉE ET FIXÉE) ---
    JTextField txtSearch = new JTextField("Rechercher un médicament...");
    txtSearch.setBounds(0, 0, 960, 45);
    txtSearch.setBackground(colorCard);
    txtSearch.setForeground(Color.GRAY);
    txtSearch.setCaretColor(Color.WHITE);
    txtSearch.setFont(new Font("Segoe UI", Font.ITALIC, 14));
    txtSearch.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(60,60,60)),
        BorderFactory.createEmptyBorder(0, 15, 0, 15)
    ));

    txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (txtSearch.getText().equals("Rechercher un médicament...")) {
                txtSearch.setText("");
                txtSearch.setForeground(Color.WHITE);
                txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        }
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (txtSearch.getText().isEmpty()) {
                txtSearch.setForeground(Color.GRAY);
                txtSearch.setText("Rechercher un médicament...");
                txtSearch.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            }
        }
    });
    p.add(txtSearch);

    // --- 2. FORMULAIRE D'ÉDITION ---
    RoundedPanel pnlTools = new RoundedPanel(20, colorCard);
    pnlTools.setBounds(0, 65, 960, 110);
    pnlTools.setLayout(null);
    p.add(pnlTools);

    String[] labels = {"NOM DU PRODUIT", "AJOUTER QUANTITÉ", "PRIX UNITAIRE (DT)"};
    JTextField[] fields = new JTextField[3];
    int xPos = 20;
    for(int i=0; i<3; i++) {
        JLabel lbl = new JLabel(labels[i]);
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setBounds(xPos, 15, 150, 20);
        pnlTools.add(lbl);

        fields[i] = new JTextField();
        fields[i].setBounds(xPos, 40, (i==0 ? 250 : 120), 35); 
        fields[i].setBackground(colorInput);
        fields[i].setForeground(Color.WHITE);
        fields[i].setCaretColor(Color.WHITE);
        fields[i].setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        pnlTools.add(fields[i]);
        xPos += (i==0 ? 270 : 140); 
    }

    JButton btnSave = new JButton("SAUVEGARDER");
    btnSave.setBounds(xPos, 40, 180, 35);
    btnSave.setBackground(colorAccent);
    btnSave.setForeground(Color.WHITE);
    pnlTools.add(btnSave);

    // --- BOUTON SUPPRIMER (CONSERVÉ) ---
    JButton btnDelete = new JButton("SUPPRIMER");
    btnDelete.setBounds(790, 40, 150, 35); 
    btnDelete.setBackground(colorDanger);
    btnDelete.setForeground(Color.WHITE);
    pnlTools.add(btnDelete);

    // --- 3. TABLEAU ---
    String[] cols = {"NOM DU PRODUIT", "EN STOCK", "PRIX UNITAIRE"};
    DefaultTableModel model = new DefaultTableModel(cols, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    JTable table = new JTable(model);
    styleDarkTable(table);
    
    Runnable loadData = () -> {
        model.setRowCount(0);
        for (Object[] row : sqlcon.getInventaire()) model.addRow(row);
    };
    loadData.run();

    // --- 4. INTERACTIONS ---
    table.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
            int row = table.getSelectedRow();
            if (row != -1) {
                produitSelectionne = table.getValueAt(row, 0).toString();
                fields[0].setText(produitSelectionne);
                fields[0].setEditable(true); // Autorise le changement de nom
                fields[1].setText("0"); 
                String prixStr = table.getValueAt(row, 2).toString().replace(" DT", "").replace(",", ".");
                fields[2].setText(prixStr);
            }
        }
    });

    // SAUVEGARDER (Sans message de confirmation)
 // DANS createInventaireView()
    btnSave.addActionListener(e -> {
        try {
            String nouveauNom = fields[0].getText();
            int qteAajouter = Integer.parseInt(fields[1].getText());
            float prix = Float.parseFloat(fields[2].getText());

            boolean success = produitSelectionne.isEmpty() 
                ? sqlcon.ajouterProduit(nouveauNom, qteAajouter, prix)
                : sqlcon.updateProduitTotal(produitSelectionne, nouveauNom, qteAajouter, prix);

            if(success) {
                loadData.run(); // Rafraîchit le tableau de l'inventaire
                
                // --- LA LIGNE MANQUANTE ÉTAIT ICI ---
                refreshDashboard(); // Met à jour la sidebar ET le dashboard central
                // ------------------------------------

                fields[0].setText(""); 
                fields[1].setText(""); 
                fields[2].setText("");
                produitSelectionne = "";
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Utile pour voir s'il y a une erreur de format
        }
    });
    //suprimer

    btnDelete.addActionListener(e -> {
        int row = table.getSelectedRow();
        if(row != -1) {
            String nom = table.getValueAt(row, 0).toString();
            if(sqlcon.supprimerProduit(nom)) {
                loadData.run();
                refreshDashboard(); // Rafraîchir les alertes ici aussi
                fields[0].setText(""); fields[1].setText(""); fields[2].setText("");
                produitSelectionne = "";
            }
        }
    });

    // RECHERCHE DYNAMIQUE
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    table.setRowSorter(sorter);
    txtSearch.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
            String val = txtSearch.getText();
            if (val.isEmpty() || val.equals("Rechercher un médicament...")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + val));
            }
        }
    });

    JScrollPane sp = new JScrollPane(table);
    sp.setBounds(0, 190, 960, 475);
    sp.setBorder(null);
    sp.getViewport().setBackground(colorBg);
    p.add(sp);

    return p;
}

private JPanel createVenteView() {
    JPanel p = new JPanel(null);
    p.setBackground(colorBg);

    // --- PANEL DE GAUCHE (SAISIE) ---
    RoundedPanel form = new RoundedPanel(30, colorCard);
    form.setBounds(20, 20, 450, 620);
    form.setLayout(null);
    p.add(form);

    JLabel t = new JLabel("CAISSE RAPIDE");
    t.setForeground(colorAccent);
    t.setFont(new Font("Segoe UI", Font.BOLD, 20));
    t.setBounds(30, 20, 200, 30);
    form.add(t);

    // --- CHAMP MÉDICAMENT ---
    JLabel lblNom = new JLabel("NOM DU MÉDICAMENT");
    lblNom.setForeground(Color.GRAY);
    lblNom.setBounds(30, 70, 200, 20);
    form.add(lblNom);

    JTextField txtNom = new JTextField();
    txtNom.setBounds(30, 95, 390, 40);
    styleInput(txtNom);
    form.add(txtNom);

 // --- POPUP DE SUGGESTIONS CORRIGÉE ---
    JPopupMenu suggestionMenu = new JPopupMenu();
    txtNom.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent e) {
            String input = txtNom.getText().trim(); // Nettoie la saisie utilisateur
            suggestionMenu.removeAll();
            if (input.length() >= 2) {
                java.util.List<String> suggestions = sqlcon.getSuggestionsMedicament(input);
                for (String s : suggestions) {
                    // On utilise trim() sur le résultat de la DB pour être sûr
                    String cleanSuggestion = s.trim(); 
                    JMenuItem item = new JMenuItem(cleanSuggestion);
                    item.addActionListener(ae -> {
                        txtNom.setText(cleanSuggestion); // Remplit sans espace
                        suggestionMenu.setVisible(false);
                    });
                    suggestionMenu.add(item);
                }
                if (suggestions.size() > 0) {
                    suggestionMenu.show(txtNom, 0, txtNom.getHeight());
                    txtNom.requestFocus();
                }
            } else {
                suggestionMenu.setVisible(false);
            }
        }
    });

    // --- CHAMP QUANTITÉ ---
    JLabel lblQte = new JLabel("QUANTITÉ");
    lblQte.setForeground(Color.GRAY);
    lblQte.setBounds(30, 150, 200, 20);
    form.add(lblQte);

    JTextField txtQte = new JTextField();
    txtQte.setBounds(30, 175, 390, 40);
    styleInput(txtQte);
    form.add(txtQte);

    // --- CHAMP CLIENT ---
    JLabel lblClient = new JLabel("NOM DU CLIENT");
    lblClient.setForeground(Color.GRAY);
    lblClient.setBounds(30, 230, 200, 20);
    form.add(lblClient);

    JTextField txtClient = new JTextField("");
    txtClient.setBounds(30, 255, 390, 40);
    styleInput(txtClient);
    form.add(txtClient);

    JButton btnAddList = new JButton("AJOUTER AU PANIER");
    btnAddList.setBounds(30, 320, 390, 45);
    btnAddList.setBackground(new Color(52, 152, 219));
    btnAddList.setForeground(Color.WHITE);
    btnAddList.setFocusPainted(false);
    form.add(btnAddList);

    // --- PANEL DE DROITE (PANIER) ---
    RoundedPanel pnlCart = new RoundedPanel(30, colorCard);
    pnlCart.setBounds(490, 20, 450, 620);
    pnlCart.setLayout(new BorderLayout());
    pnlCart.setBorder(new EmptyBorder(20, 20, 20, 20));

    String[] cols = {"PRODUIT", "QTE", "PRIX UNIT", "TOTAL"};
    DefaultTableModel modelCart = new DefaultTableModel(cols, 0);
    JTable tableCart = new JTable(modelCart);
    styleDarkTable(tableCart);
    JScrollPane spCart = new JScrollPane(tableCart);
    spCart.getViewport().setBackground(colorCard);
    spCart.setBorder(null);
    pnlCart.add(spCart, BorderLayout.CENTER);

    JPanel pnlBottom = new JPanel(new BorderLayout());
    pnlBottom.setBackground(colorCard);
    
    JLabel lblTot = new JLabel("TOTAL : 0.000 DT");
    lblTot.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTot.setForeground(Color.WHITE);
    pnlBottom.add(lblTot, BorderLayout.NORTH);

    JButton btnValider = new JButton("VALIDER LA VENTE");
    btnValider.setPreferredSize(new Dimension(0, 50));
    btnValider.setBackground(colorAccent);
    btnValider.setForeground(Color.WHITE);
    btnValider.setFont(new Font("Segoe UI", Font.BOLD, 16));
    pnlBottom.add(btnValider, BorderLayout.SOUTH);

    pnlCart.add(pnlBottom, BorderLayout.SOUTH);
    p.add(pnlCart);

 // --- ACTIONS CORRIGÉES ---

    btnAddList.addActionListener(e -> {
        String nom = txtNom.getText().trim();
        String qteStr = txtQte.getText().trim();

        if(!nom.isEmpty() && !qteStr.isEmpty()) {
            try {
                int qteDemandee = Integer.parseInt(qteStr);
                float prixUnit = sqlcon.getPrixProduit(nom); 
                int stockDisponible = sqlcon.getStockProduit(nom);

                if (prixUnit <= 0) {
                	alerteStyle("Produit '" + nom + "' introuvable ou prix non défini !", "DANGER");
                } else if (qteDemandee > stockDisponible) {
                	alerteStyle("Stock insuffisant ! (Disponible : " + stockDisponible + ")", "DANGER");
                } else {
                    float totalLigne = qteDemandee * prixUnit;
                    
                    // IMPORTANT : Utiliser Locale.US pour forcer le point (.) comme séparateur
                    modelCart.addRow(new Object[]{
                        nom, 
                        qteDemandee, 
                        String.format(java.util.Locale.US, "%.3f", prixUnit) + " DT", 
                        String.format(java.util.Locale.US, "%.3f", totalLigne) + " DT"
                    });
                    
                    updateTotalGlobal(modelCart, lblTot);
                    txtNom.setText(""); 
                    txtQte.setText("");
                }
            } catch (NumberFormatException ex) {
            	alerteStyle("La quantité doit être un nombre entier valide !", "DANGER");
            }
        }
    });

    btnValider.addActionListener(e -> {
        if (modelCart.getRowCount() == 0) {
        	alerteStyle("Le panier est vide !", "DANGER");
            return;
        }
        
        String nomClient = txtClient.getText().trim();
        // Si vide, on assigne un client par défaut ou on demande une saisie
        if (nomClient.isEmpty()) {
            nomClient = "Passant"; 
        }

        try {
            int idClient = sqlcon.getIdClientByName(nomClient);

            for (int i = 0; i < modelCart.getRowCount(); i++) {
                String nomProd = modelCart.getValueAt(i, 0).toString();
                int qte = Integer.parseInt(modelCart.getValueAt(i, 1).toString());
                
                // Enregistrement SQL
                sqlcon.enregistrerVente(nomProd, qte, idClient);
                sqlcon.diminuerStock(nomProd, qte);
            }

            alerteStyle("Vente enregistrée avec succès pour : " + nomClient, "SUCCESS");
            
            // RÉINITIALISATION COMPLÈTE
            modelCart.setRowCount(0); 
            txtClient.setText(""); // Vide le champ client pour la prochaine vente
            lblTot.setText("TOTAL : 0.000 DT");
            
            refreshDashboard(); 
            
        } catch (Exception ex) {
        	alerteStyle("Erreur système : " + ex.getMessage(), "DANGER");
        }
    });
    return p;

}

// Fonction utilitaire pour le calcul du total
private void updateTotalGlobal(DefaultTableModel model, JLabel label) {
    float total = 0;
    for (int i = 0; i < model.getRowCount(); i++) {
        String val = model.getValueAt(i, 3).toString().replace(" DT", "");
        total += Float.parseFloat(val);
    }
    label.setText(String.format("TOTAL : %.3f DT", total));
}

// Fonction utilitaire pour le style des champs
private void styleInput(JTextField tf) {
    tf.setBackground(colorInput);
    tf.setForeground(Color.WHITE);
    tf.setCaretColor(Color.WHITE);
    tf.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
}
private JPanel createCommandeView() {
    JPanel p = new JPanel(null);
    p.setBackground(colorBg);
    p.setName("Commandes");

    JLabel lblTitle = new JLabel("GESTION DES COMMANDES FOURNISSEURS");
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
    lblTitle.setForeground(Color.WHITE);
    lblTitle.setBounds(0, 0, 500, 30);
    p.add(lblTitle);

    RoundedPanel pnlForm = new RoundedPanel(25, colorCard);
    pnlForm.setBounds(0, 50, 350, 610);
    pnlForm.setLayout(null);
    p.add(pnlForm);

    String[] fieldsText = {"Médicament", "Quantité", "Prix Estime (DT)", "Nom Fournisseur"};
    JTextField[] inputs = new JTextField[4];
    int yF = 60;
    for(int i=0; i<4; i++) {
        JLabel l = new JLabel(fieldsText[i]);
        l.setForeground(Color.GRAY);
        l.setBounds(20, yF, 200, 20);
        pnlForm.add(l);
        inputs[i] = new JTextField();
        inputs[i].setBounds(20, yF+25, 310, 35);
        styleInput(inputs[i]); 
        pnlForm.add(inputs[i]);
        yF += 85;
    }

    JButton btnPasser = new JButton("PASSER LA COMMANDE");
    btnPasser.setBounds(20, 540, 310, 45);
    btnPasser.setBackground(new Color(52, 152, 219));
    btnPasser.setForeground(Color.WHITE);
    btnPasser.setFont(new Font("Segoe UI", Font.BOLD, 12));
    btnPasser.setBorderPainted(false);
    pnlForm.add(btnPasser);

    // --- Tableau (AJOUT COLONNE QTÉ) ---
    // Index : 0:Médicament, 1:Fournisseur, 2:QTÉ, 3:TOTAL, 4:STATUT, 5:ID_HIDDEN
    String[] cols = {"MÉDICAMENT", "FOURNISSEUR", "QTÉ", "TOTAL", "STATUT", "ID_HIDDEN"};
    DefaultTableModel modelCmd = new DefaultTableModel(cols, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    
    for (Object[] ligne : sqlcon.getCommandesFournisseurs()) {
        modelCmd.addRow(ligne);
    }

    JTable tableCmd = new JTable(modelCmd);
    styleDarkTable(tableCmd);
    
    // --- RENDERING COULEUR ORANGE (Index décalé à 4 pour STATUT) ---
    tableCmd.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
            l.setForeground(new Color(241, 196, 15)); 
            l.setHorizontalAlignment(SwingConstants.CENTER);
            return l;
        }
    });

    // Cacher la colonne ID (Index décalé à 5)
    tableCmd.getColumnModel().getColumn(5).setMinWidth(0);
    tableCmd.getColumnModel().getColumn(5).setMaxWidth(0);
    
    // Ajuster la largeur de la colonne QTÉ
    tableCmd.getColumnModel().getColumn(2).setPreferredWidth(50);

    JScrollPane spCmd = new JScrollPane(tableCmd);
    spCmd.setBounds(370, 50, 590, 540);
    spCmd.setBorder(BorderFactory.createEmptyBorder());
    spCmd.getViewport().setBackground(colorBg);
    p.add(spCmd);

    // --- LOGIQUE BOUTON ---
    btnPasser.addActionListener(e -> {
        if(!inputs[0].getText().trim().isEmpty()) {
            sqlcon.ajouterCommandeFournisseur(
                inputs[3].getText(), inputs[0].getText(), 
                Integer.parseInt(inputs[1].getText()), Float.parseFloat(inputs[2].getText())
            );
            refreshDashboard();
            for(JTextField tf : inputs) tf.setText("");
            inputs[0].requestFocusInWindow(); 
        }
    });

    // Bouton REÇUE (Index ID est 5)
    JButton btnRecue = new JButton("MARQUER COMME REÇUE");
    btnRecue.setBounds(370, 610, 285, 45);
    btnRecue.setBackground(colorAccent);
    btnRecue.setForeground(Color.WHITE);
    btnRecue.addActionListener(e -> {
        int row = tableCmd.getSelectedRow();
        if(row != -1) {
            int id = (int) modelCmd.getValueAt(row, 5); // Index 5
            sqlcon.marquerCommandeRecue(id);
            refreshDashboard();
        }
    });
    p.add(btnRecue);

    // Bouton ANNULER (Index ID est 5)
    JButton btnAnnuler = new JButton("ANNULER LA COMMANDE");
    btnAnnuler.setBounds(675, 610, 285, 45);
    btnAnnuler.setBackground(colorDanger);
    btnAnnuler.setForeground(Color.WHITE);
    btnAnnuler.addActionListener(e -> {
        int row = tableCmd.getSelectedRow();
        if(row != -1) {
            int id = (int) modelCmd.getValueAt(row, 5); // Index 5
            sqlcon.updateStatutCommande(id, "ANNULÉE");
            refreshDashboard();
        }
    });
    p.add(btnAnnuler);

    return p;
}
    private void styleDarkTable(JTable t) {
        t.setRowHeight(45);
        t.setBackground(colorCard); // Doit correspondre exactement au fond du panel
        t.setForeground(new Color(220, 220, 220));
        t.setGridColor(new Color(60, 60, 60)); // Grille très sombre
        t.setShowVerticalLines(false); // Design plus moderne sans barres verticales
        t.setSelectionBackground(new Color(46, 204, 113, 80));
        t.setBorder(null); // Sécurité supplémentaire
        
        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(25, 25, 25));
        header.setForeground(colorAccent);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
    }

    private void addLabeledInput(JPanel p, String label, int x, int y) {
        JLabel l = new JLabel(label);
        l.setForeground(Color.GRAY);
        l.setBounds(x, y, 200, 20);
        p.add(l);
        JTextField tf = new JTextField();
        tf.setBounds(x, y + 25, 390, 40);
        tf.setBackground(colorInput);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        p.add(tf);
    }

    private JButton createMenuBtn(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(20, y, 240, 50);
        btn.setBackground(colorSidebar);
        btn.setForeground(Color.LIGHT_GRAY);
        btn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private JPanel createStatCard(String title, String val, int x, int y, int w, int h, Color accent) {
        RoundedPanel card = new RoundedPanel(25, colorCard);
        card.setBounds(x, y, w, h);
        card.setLayout(null);
        JLabel lblT = new JLabel(title);
        lblT.setForeground(Color.GRAY);
        lblT.setBounds(20, 20, 200, 20);
        card.add(lblT);
        JLabel lblV = new JLabel(val);
        lblV.setForeground(Color.WHITE);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblV.setBounds(20, 50, 250, 40);
        card.add(lblV);
        JPanel line = new JPanel();
        line.setBackground(accent);
        line.setBounds(0, h-6, w, 6);
        card.add(line);
        return card;
    }
    private void showCustomDetailsDialog(String nomClient, String detailsTexte) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Détails des achats : " + nomClient);
        dialog.setModal(true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(colorCard); // Utilise votre couleur sombre
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel lblTitle = new JLabel("ACHATS DE : " + nomClient.toUpperCase());
        lblTitle.setForeground(colorAccent);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        content.add(lblTitle, BorderLayout.NORTH);

        // Zone de texte pour les produits
        JTextArea txtArea = new JTextArea(detailsTexte);
        txtArea.setBackground(colorCard);
        txtArea.setForeground(Color.WHITE);
        txtArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtArea.setEditable(false);
        txtArea.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(txtArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(colorCard);
        content.add(scroll, BorderLayout.CENTER);

        // Bouton Fermer
        JButton btnClose = new JButton("FERMER");
        btnClose.setBackground(colorAccent);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dialog.dispose());
        content.add(btnClose, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.setVisible(true);
    }
 // Copie ce bloc tout en bas de ta classe
    private void alerteStyle(String message, String type) {
        // Définition de la couleur selon le type
        Color bg = new Color(52, 152, 219); // Bleu par défaut (INFO)
        if (type.equals("SUCCESS")) bg = new Color(46, 204, 113); // Vert
        if (type.equals("DANGER")) bg = new Color(231, 76, 60);  // Rouge

        JDialog d = new JDialog();
        d.setUndecorated(true); // Supprime les bords Windows
        d.setSize(350, 60);
        d.setLocationRelativeTo(this); // Centre sur l'application

        // Création du panneau arrondi
        RoundedPanel p = new RoundedPanel(20, bg);
        p.setLayout(new BorderLayout());
        
        JLabel txt = new JLabel(message, SwingConstants.CENTER);
        txt.setForeground(Color.WHITE);
        txt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(txt, BorderLayout.CENTER);

        d.add(p);
        
        // Timer pour fermer après 1.5 seconde
        new javax.swing.Timer(1500, e -> d.dispose()).start();
        d.setVisible(true);
    }

 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new login().setVisible(true));
    }
}
