CREATE DATABASE pharmacie_db;
USE pharmacie_db;

-- 2. Table Utilisateur
CREATE TABLE utilisateur (
    cle INT PRIMARY KEY AUTO_INCREMENT,
    user VARCHAR(50) NOT NULL UNIQUE, 
    password VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL
);

-- 3. Table Stock
CREATE TABLE stock (
    nom VARCHAR(50) PRIMARY KEY,
    prix_unitaire FLOAT NOT NULL,    
    quantite_stock INT NOT NULL      
);

-- 4. Table Client
CREATE TABLE client (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL
);

-- 5. Table Fournisseur
CREATE TABLE fournisseur (
    id_fournisseur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL
);

-- 6. Table Commande Client (On garde la FK car on ne vend que ce qu'on a en stock)
CREATE TABLE commande_client (
    id_commande_client INT AUTO_INCREMENT PRIMARY KEY,
    id_client INT NOT NULL,
    nom_stock VARCHAR(50) NOT NULL,
    quantite_commande INT NOT NULL,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_client) REFERENCES client(id_client),
    FOREIGN KEY (nom_stock) REFERENCES stock(nom)
);

-- 7. Table Commande Fournisseur (CORRIGÉE : Pas de FK sur le stock ici)
CREATE TABLE commande_fournisseur (
    id_commande_fournisseur INT AUTO_INCREMENT PRIMARY KEY,
    id_fournisseur INT NOT NULL,
    nom_stock VARCHAR(50) NOT NULL, -- On peut commander un nouveau produit
    quantite_f INT NOT NULL,
    fprix_achat_total FLOAT NOT NULL, 
    date_commande_f TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    etat_commande VARCHAR(20) DEFAULT 'EN ATTENTE',
    FOREIGN KEY (id_fournisseur) REFERENCES fournisseur(id_fournisseur)
);

INSERT INTO utilisateur (user, password, type) VALUES 
('admin', 'admin', 'admin'),
('test', 'test', 'pharmacien');

INSERT INTO stock (nom, prix_unitaire, quantite_stock) VALUES 
('Paracétamol 500mg', 5.200, 120),
('Amoxicilline 1g', 14.750, 45),
('test-med', 5.200, 0);

INSERT INTO client (nom) VALUES ('Mohamed Ali'), ('Sami Ben Youssef');
INSERT INTO commande_client (id_client, nom_stock, quantite_commande) VALUES (1, 'Paracétamol 500mg', 10);

INSERT INTO commande_client (id_client, nom_stock, quantite_commande) VALUES (2, 'Amoxicilline 1g', 2);
INSERT INTO fournisseur (nom) VALUES ('Pharmacie Centrale'), ('Distributeur Alpha');
