/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud.communes;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;

/**
 *
 * @author seb
 */
public class CrudCommunes extends javax.swing.JFrame {

    //Liste des communes contient un tableau de String
    private List<String[]> ilistCommunes = new ArrayList<>();
    private List<String> ilFilteredCommunes = new ArrayList<>();
    private String[] isaDepartement;
    private int iNbLines;

    private boolean isFirstLine = true;
    private boolean isLastLine = false;
    private boolean isEdited = false;
    private boolean hasPendingRecords = false;

    private String isEditMode;

    private int iiCurrentPosition = 1;

    /**
     * Creates new form CrudCommunes
     */
    public CrudCommunes() {
        initComponents();

        this.setTitle("CRUD Communes");
        this.setLocationRelativeTo(null);

        //Lecture du fichier csv des communes
        this.setListCommunes();
        //Population de la liste des communes
        this.populateDepatementCombo();

        //Affichage de la commune en cours
        this.displayCommune();
        //En mode lecture les contrôles sont désactivés
        this.setControlsEnabled();

    }

    /**
     * Lecture du fichier des communes et population d'un tableau
     */
    private void setListCommunes() {
        try {
            //---Instanciation du Filereader et du BufferedReader
            FileReader lfrFichier = new FileReader("communes_insee.csv");
            BufferedReader lbrBuffer = new BufferedReader(lfrFichier);
            String lsLigne;

            //---Instanciation d'un TreeSet pour la liste des départements
            // Liste sans doublons
            Set setDepartement = new TreeSet();

            //---Lecture du fichier ligne à ligne
            while ((lsLigne = lbrBuffer.readLine()) != null) {
                //---Chaque ligne est stockée dans un tableau
                String[] lsaCols = lsLigne.split(";");
                ilistCommunes.add(lsaCols);
                setDepartement.add(lsaCols[2]);
            }
            //---Fermeture du fichier et du buffer
            lfrFichier.close();
            lbrBuffer.close();

            //---Conversion du TreeSet en tableau statique moins couteux
            isaDepartement = (String[]) setDepartement.toArray(new String[setDepartement.size()]);

            //---Mise en memoire du nombre total de lignes dans le fichier des communes
            this.iNbLines = ilistCommunes.size() - 1;

            //---Gestion des exceptions    
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CrudCommunes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CrudCommunes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Affiche les données de la commune en cours
     */
    private void displayCommune() {
        //---Récupération des infos de la ligne en cours
        String[] lsaCols = ilistCommunes.get(this.iiCurrentPosition);

        //---Remplissage des contrôles
        jTextFieldCommune.setText(lsaCols[0]);
        jTextFieldCP.setText(lsaCols[1]);
        jTextFieldInsee.setText(lsaCols[3]);
        //---Affichage du département dans la liste
        jComboBoxDepartement.setSelectedItem(lsaCols[2]);

        //---Gestion de l'affichage et ou activation des éléments de navigation
        this.displayNavigation();
    }

    /**
     * Active ou désactive les contrôles en fonction du mode (lecture ou
     * édition)
     */
    private void setControlsEnabled() {
        //---En mode affichage, les contrôle sont grisés
        jTextFieldCommune.setEnabled(isEdited);
        jTextFieldCP.setEnabled(isEdited);
        jComboBoxDepartement.setEnabled(isEdited);
        jTextFieldInsee.setEnabled(isEdited);
    }

    /**
     * Activation conditionnelle des boutons de navigation Affichage textuel de
     * la navigation (1/n)
     */
    private void displayNavigation() {
        //---Est on en première ou dernière ligne
        this.isFirstLine = (this.iiCurrentPosition == 1);
        this.isLastLine = (this.iiCurrentPosition == this.iNbLines);

        //---Les boutons précédent et premier sont désactivés si l'on est en première position
        //  ou si l'on est en mode édition
        jButtonFirst.setEnabled(!this.isFirstLine && !this.isEdited);
        jButtonPrevious.setEnabled(!this.isFirstLine && !this.isEdited);

        //---Les boutons suivant et dernier sont désactivés si l'on est en dernière position
        //  ou si l'on est en mode édition
        jButtonLast.setEnabled(!this.isLastLine && !this.isEdited);
        jButtonNext.setEnabled(!this.isLastLine && !this.isEdited);

        //---La navigation est masquée en mode édition
        jLabelNavigation.setText((this.iiCurrentPosition) + "/" + (this.iNbLines));
        jLabelNavigation.setVisible(!isEdited);

        //---En mode édition, afficher les boutons valider et annuler
        jButtonValid.setVisible(isEdited);
        jButtonCancel.setVisible(isEdited);
    }

    /**
     * Recherche l'indice du tableau des départements correspondant à un nom de
     * département passé en argument
     *
     * @param asVille
     * @return
     */
    private int findCommuneIndex(String asVille) {
        boolean lbFound = false;
        int liPosition = 0;
        for (int i = 0; i < ilistCommunes.size() && !lbFound; i++) {
            if (ilistCommunes.get(i)[0].equals(asVille)) {
                lbFound = true;
                liPosition = i;
            }
        }
        return liPosition;
    }

/**
 * Recherche les noms de communes commençant par asSaisie
 * et peuple la Jlist du choix des communes en fonction du résultat
 * @param asSaisie 
 */
    private void populateSearchCombo(String asSaisie) {
        ilFilteredCommunes.clear();
        for (String[] item : ilistCommunes) {
            if (item[0].toUpperCase().startsWith(asSaisie.toUpperCase())) {
                ilFilteredCommunes.add(item[0]);
            }
        }
        jListRecherche.setListData(ilFilteredCommunes.toArray());
        //List<String> filteredCommunes = new ArrayList<>();
    }

    /**
     * Remplissage de la combo des départements
     */
    private void populateDepatementCombo() {
        jComboBoxDepartement.removeAllItems();
        for (Object item : isaDepartement) {
            jComboBoxDepartement.addItem(item);
        }
    }

    /**
     * Effacement des données dans les contrôles
     */
    private void clearForm() {
        jTextFieldCommune.setText("");
        jTextFieldCP.setText("");
        jTextFieldInsee.setText("");
        jComboBoxDepartement.setSelectedIndex(0);
    }

    /**
     * Test la validité de la saisie avant enregistrement des données du
     * formulaire
     *
     * @return
     */
    private boolean validateForm() {
        boolean isFormValid = true;
        boolean isFieldValid = true;
        String regExp = "";

        isFormValid = isFormValid && this.validControl(jTextFieldCommune, "^[a-zA-Z][a-zA-Z ]*$");
        isFormValid = isFormValid && this.validControl(jTextFieldCP, "^[0-9]{5}$");
        isFormValid = isFormValid && this.validControl(jTextFieldInsee, "^[0-9]{4}$");
        return isFormValid;
    }

    /**
     * Test la validité de la saisie sur un contrôle
     *
     * @param control
     * @param regExp
     * @return
     */
    private boolean validControl(JTextComponent control, String regExp) {
        boolean isValid;
        isValid = control.getText().matches(regExp);
        this.paintError(control, isValid);
        return isValid;
    }

    /**
     * Met en exergue les contrôles en erreur
     *
     * @param field
     * @param valide
     */
    private void paintError(JTextComponent field, boolean valide) {
        if (valide) {
            field.setBackground(Color.white);
        } else {
            field.setBackground(Color.red);
        }
    }

    /**
     * Renvoie une représentation csv des données du formulaire
     *
     * @return
     */
    private String getCommuneAsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(jTextFieldCommune.getText());
        sb.append(";");
        sb.append(jTextFieldCP.getText());
        sb.append(";");
        sb.append(jComboBoxDepartement.getSelectedItem());
        sb.append(";");
        sb.append(jTextFieldInsee.getText());
        return sb.toString();
    }

    /**
     * Ajoute une commune au tableau et écrit la nouvelle commune dans le
     * fichier
     */
    private void addCommune() throws IOException {
        //Ajoute la nouvelle commune au tableau
        String[] data = new String[4];
        data[0] = jTextFieldCommune.getText();
        data[1] = jTextFieldCP.getText();
        data[2] = jComboBoxDepartement.getSelectedItem().toString();
        data[3] = jTextFieldInsee.getText();
        this.ilistCommunes.add(data);
        this.iNbLines = ilistCommunes.size() - 1;

        //Ecrit les données dans le fichier
        String csvData = this.getCommuneAsCsv();
        this.saveToFile(csvData, true);
    }

    /**
     * écrit les données des communes dans le fichier csv
     *
     * @param content
     * @param appendToFile
     * @throws IOException
     */
    private void saveToFile(String content, boolean appendToFile) throws IOException {
        FileWriter communeFileWriter = new FileWriter("communes_insee.csv", appendToFile);
        BufferedWriter communeBuffer = new BufferedWriter(communeFileWriter);
        communeBuffer.write(content);
        communeBuffer.newLine();

        communeBuffer.close();
        communeFileWriter.close();
    }

    /**
     * Conversion du tableau des communes en une chaîne csv
     *
     * @return
     */
    private String getCommunesAsString() {
        StringBuilder sb = new StringBuilder();

        for (String[] item : ilistCommunes) {
            for (int i = 0; i < item.length; i++) {
                sb.append(item[i]);
                sb.append(";");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Sauvegarde des communes dans le fichier texte si des modifications 
     * (suppression ou mise à jour) ont été effectuées
     */
    private void saveCommunes() {
        if(this.hasPendingRecords){
            try {
            String data = this.getCommunesAsString();
            this.saveToFile(data, false);
        } catch (IOException ex) {
            Logger.getLogger(CrudCommunes.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldCommune = new javax.swing.JTextField();
        jTextFieldCP = new javax.swing.JTextField();
        jTextFieldInsee = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jButtonFirst = new javax.swing.JButton();
        jButtonPrevious = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jButtonLast = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jButtonCreate = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonUpdate = new javax.swing.JButton();
        jLabelNavigation = new javax.swing.JLabel();
        jComboBoxDepartement = new javax.swing.JComboBox();
        jButtonValid = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaMessage = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jTextFieldRecherche = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListRecherche = new javax.swing.JList();

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(231, 224, 216));

        jLabel1.setText("Nom");

        jLabel2.setText("CP");

        jLabel3.setText("Département");

        jLabel4.setText("INSEE");

        jTextFieldInsee.setToolTipText("");

        jButtonFirst.setText("<<");
        jButtonFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFirstActionPerformed(evt);
            }
        });

        jButtonPrevious.setText("<");
        jButtonPrevious.setMaximumSize(new java.awt.Dimension(30, 29));
        jButtonPrevious.setMinimumSize(new java.awt.Dimension(30, 29));
        jButtonPrevious.setSize(new java.awt.Dimension(30, 29));
        jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPreviousActionPerformed(evt);
            }
        });

        jButtonNext.setText(">");
        jButtonNext.setMaximumSize(new java.awt.Dimension(30, 29));
        jButtonNext.setMinimumSize(new java.awt.Dimension(30, 29));
        jButtonNext.setSize(new java.awt.Dimension(30, 29));
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });

        jButtonLast.setText(">>");
        jButtonLast.setToolTipText("");
        jButtonLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLastActionPerformed(evt);
            }
        });

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        jButtonCreate.setText("+");
        jButtonCreate.setMaximumSize(new java.awt.Dimension(30, 29));
        jButtonCreate.setMinimumSize(new java.awt.Dimension(30, 29));
        jButtonCreate.setSize(new java.awt.Dimension(30, 29));
        jButtonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateActionPerformed(evt);
            }
        });

        jButtonDelete.setText("-");
        jButtonDelete.setMaximumSize(new java.awt.Dimension(30, 29));
        jButtonDelete.setMinimumSize(new java.awt.Dimension(30, 29));
        jButtonDelete.setSize(new java.awt.Dimension(30, 29));
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonUpdate.setText("M");
        jButtonUpdate.setMaximumSize(new java.awt.Dimension(30, 29));
        jButtonUpdate.setMinimumSize(new java.awt.Dimension(30, 29));
        jButtonUpdate.setSize(new java.awt.Dimension(30, 29));
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });

        jLabelNavigation.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabelNavigation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelNavigation.setText("1/1");

        jComboBoxDepartement.setMaximumRowCount(20);

        jButtonValid.setText("Valider");
        jButtonValid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonValidActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Annuler");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jTextAreaMessage.setColumns(20);
        jTextAreaMessage.setRows(5);
        jScrollPane1.setViewportView(jTextAreaMessage);

        jTextFieldRecherche.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldRechercheKeyTyped(evt);
            }
        });

        jListRecherche.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListRecherche.setVisibleRowCount(5);
        jListRecherche.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListRechercheMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jListRecherche);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jButtonClear)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButtonCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButtonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jButtonFirst)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButtonPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButtonNext, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jButtonLast))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(6, 6, 6)
                                            .addComponent(jLabelNavigation, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(13, 13, 13))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButtonCancel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonValid))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextFieldCP, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextFieldInsee, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jComboBoxDepartement, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextFieldCommune, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 11, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldRecherche, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldRecherche, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldCommune, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldCP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxDepartement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldInsee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonValid)
                    .addComponent(jButtonCancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFirst)
                    .addComponent(jButtonPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonNext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLast))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelNavigation)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear)
                    .addComponent(jButtonCreate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateActionPerformed
        this.clearForm();
        this.isEdited = true;
        this.isEditMode = "add";
        this.displayNavigation();
        this.setControlsEnabled();
    }//GEN-LAST:event_jButtonCreateActionPerformed

    /**
     * Suppression d'une commune
     *
     * @param evt
     */
    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        this.ilistCommunes.remove(iiCurrentPosition);
        if (this.iiCurrentPosition == iNbLines) {
            this.iiCurrentPosition--;
        }
        this.iNbLines--;
        this.hasPendingRecords = true;
        this.displayCommune();
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
        this.isEdited = true;
        this.isEditMode = "update";
        this.displayNavigation();
        this.setControlsEnabled();
    }//GEN-LAST:event_jButtonUpdateActionPerformed

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        if (this.iiCurrentPosition < this.iNbLines) {
            this.iiCurrentPosition++;
            this.displayCommune();
        }
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPreviousActionPerformed
        if (this.iiCurrentPosition > 0) {
            this.iiCurrentPosition--;
            this.displayCommune();
        }
    }//GEN-LAST:event_jButtonPreviousActionPerformed

    private void jButtonFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFirstActionPerformed
        this.iiCurrentPosition = 1;
        this.displayCommune();
    }//GEN-LAST:event_jButtonFirstActionPerformed

    private void jButtonLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLastActionPerformed
        this.iiCurrentPosition = this.iNbLines;
        this.displayCommune();
    }//GEN-LAST:event_jButtonLastActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        this.clearForm();
    }//GEN-LAST:event_jButtonClearActionPerformed
    /**
     * Validation de l'édition
     *
     * @param evt
     */
    private void jButtonValidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonValidActionPerformed
        if (this.validateForm()) {
            try {
                if (this.isEditMode.equals("add")) {
                    this.addCommune();
                    this.iiCurrentPosition = this.iNbLines;
                } else {
                    String[] data = new String[4];
                    data[0] = jTextFieldCommune.getText();
                    data[1] = jTextFieldCP.getText();
                    data[2] = jComboBoxDepartement.getSelectedItem().toString();
                    data[3] = jTextFieldInsee.getText();
                    ilistCommunes.set(iiCurrentPosition, data);
                    this.hasPendingRecords = true;
                }
                this.isEdited = false;
                this.isEditMode = "";
                this.displayCommune();
                jTextAreaMessage.setText("");
                this.setControlsEnabled();
            } catch (IOException ex) {
                jTextAreaMessage.setText(ex.getMessage());
            }
        } else {
            jTextAreaMessage.setText("Le formulaire est invalide");
        }
    }//GEN-LAST:event_jButtonValidActionPerformed
    /**
     * Annulation de l'édition et retour en mode lecture
     *
     * @param evt
     */
    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.isEdited = false;
        this.isEditMode = "";
        this.displayCommune();
    }//GEN-LAST:event_jButtonCancelActionPerformed
    /**
     * Enregistrement du fichier csv à la fermeture de la fenêtre
     *
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.saveCommunes();
    }//GEN-LAST:event_formWindowClosing

    /**
     * typeAhead, filtre la liste des communes en fonction de la saisie dans le zone de recherche
     * @param evt 
     */
    private void jTextFieldRechercheKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldRechercheKeyTyped
        String lsRecherche = jTextFieldRecherche.getText();
        String saisie = String.valueOf(evt.getKeyChar());
        if(saisie.matches("[a-zA-Z]")){
            lsRecherche += saisie;
        }
        //jTextAreaMessage.setText(lsRecherche);
        if (lsRecherche.length() >= 3) {
            this.populateSearchCombo(lsRecherche);
        } else {
            jListRecherche.removeAll();
        }
    }//GEN-LAST:event_jTextFieldRechercheKeyTyped
/**
 * Accès direct depuis la liste filtrée des communes
 * @param evt 
 */
    private void jListRechercheMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListRechercheMouseClicked
        String lsVille = jListRecherche.getSelectedValue().toString();
        //recherche la position de la commune dans le tableau des communes
        iiCurrentPosition = this.findCommuneIndex(lsVille);
        this.displayCommune();
    }//GEN-LAST:event_jListRechercheMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CrudCommunes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CrudCommunes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CrudCommunes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CrudCommunes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CrudCommunes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonCreate;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonFirst;
    private javax.swing.JButton jButtonLast;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JButton jButtonPrevious;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JButton jButtonValid;
    private javax.swing.JComboBox jComboBoxDepartement;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelNavigation;
    private javax.swing.JList jList1;
    private javax.swing.JList jListRecherche;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextAreaMessage;
    private javax.swing.JTextField jTextFieldCP;
    private javax.swing.JTextField jTextFieldCommune;
    private javax.swing.JTextField jTextFieldInsee;
    private javax.swing.JTextField jTextFieldRecherche;
    // End of variables declaration//GEN-END:variables
}
