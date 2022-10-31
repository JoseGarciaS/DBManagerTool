/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ManagerApp;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

/**
 *
 * @author bounc
 */
public class Window extends javax.swing.JFrame {

    /**
     * Creates new form Window
     */
    String logged[] = {"",""};
    private int cont = 0;
    private DefaultTreeModel modelo;
    DefaultMutableTreeNode selectedNode;
    DataBase basePrueba;
    ArrayList<DataBase> dbs = new ArrayList<>();

    public Window() {
        initComponents();
        modelo = (DefaultTreeModel) FileTree.getModel();
    }

    private void log() {
        System.out.println(UsernameField.getText());
        System.out.println(PasswordField.getPassword());
        if (UsernameField.getText().equals("admin") && PasswordField.getText().equals("admin")) {
            LoginPanel.setVisible(false);
        }
    }

    private void loadDataBase(String dataBaseAlias) {
        try {
            int indice = FileTree.getRowForPath(FileTree.getSelectionPath());
            int dataBaseIndex = FileTree.getRowForPath(FileTree.getNextMatch(dataBaseAlias, 0, Position.Bias.Forward));
            FileTree.setSelectionRow(dataBaseIndex);
            selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
            System.out.println(selectedNode);
            if (modelo.getChildCount(selectedNode) == 0) {
                String listas[] = {"Tables", "Views", "Packages", "Saved Processes", "Functions", "Secuences", "Triggers", "Indexes", "Users"};
                
                for (String nodo : listas) {
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(nodo);
                    modelo.insertNodeInto(n, selectedNode, selectedNode.getChildCount());
                }
                FileTree.setModel(modelo);
            }
            
            FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Tables", dataBaseIndex, Position.Bias.Forward)));
            selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();

            if (modelo.getChildCount(selectedNode) == 0) {
                basePrueba = dbs.get(0);
                        
                // <editor-fold defaultstate="collapsed" desc="Carga de Tablas">
                // Carga de Tablas
                ResultSet res = basePrueba.query("""
                                                    select rdb$relation_name
                                                    from rdb$relations
                                                    where rdb$view_blr is null
                                                    and (rdb$system_flag is null or rdb$system_flag = 0);
                                                """);
                int cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Carga de Vistas">
                // Carga de Vistas
                FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Views", dataBaseIndex, Position.Bias.Forward)));
                res = basePrueba.query("""
                                        select rdb$relation_name
                                        from rdb$relations
                                        where rdb$view_blr is not null
                                        and (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Carga de Paquetes">
                // Carga de Paquetes
                FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Packages", dataBaseIndex, Position.Bias.Forward)));
                res = basePrueba.query("""
                                        select RDB$PACKAGE_NAME
                                        from RDB$PACKAGES
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Carga de Procedimientos Almacenados">
                // Carga de Procedimientos Almacenados
                FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Saved Processes", dataBaseIndex, Position.Bias.Forward)));
                res = basePrueba.query("""
                                        select RDB$PROCEDURE_NAME
                                        from RDB$PROCEDURES
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Carga de Funciones">
                // Carga de Funciones
                FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Functions", dataBaseIndex, Position.Bias.Forward)));
                res = basePrueba.query("""
                                        select RDB$FUNCTION_NAME
                                        from RDB$FUNCTIONS
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Carga de Generadores">
                // Carga de Generadores
                FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Secuences", dataBaseIndex, Position.Bias.Forward)));
                res = basePrueba.query("""
                                        select RDB$GENERATOR_NAME
                                        from RDB$GENERATORS
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Carga de Triggers">
                // Carga de Triggers
                FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Triggers", dataBaseIndex, Position.Bias.Forward)));
                res = basePrueba.query("""
                                        select RDB$TRIGGER_NAME
                                        from RDB$TRIGGERS
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Carga de Indices">
                // Carga de Indices
                FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Indexes", dataBaseIndex, Position.Bias.Forward)));
                res = basePrueba.query("""
                                        select RDB$INDEX_NAME
                                        from RDB$INDICES
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Carga de Usuarios">
                // Carga de Usuarios
                FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Users", dataBaseIndex, Position.Bias.Forward)));
                res = basePrueba.query("""
                                        select SEC$USER_NAME
                                        from SEC$USERS;
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

            } else {
                FileTree.setSelectionRow(indice);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
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

        LayeredPane = new javax.swing.JLayeredPane();
        LoginPanel = new javax.swing.JPanel();
        WelcomeLabel = new javax.swing.JLabel();
        SubLabel = new javax.swing.JLabel();
        LoginButton = new javax.swing.JButton();
        UsernameField = new javax.swing.JTextField();
        PasswordField = new javax.swing.JPasswordField();
        NewUserButton = new javax.swing.JButton();
        Sessions = new javax.swing.JTabbedPane();
        AppScreen = new javax.swing.JPanel();
        SplitPane = new javax.swing.JSplitPane();
        RightPane = new javax.swing.JPanel();
        LeftPane = new javax.swing.JPanel();
        JTreePane = new javax.swing.JScrollPane();
        FileTree = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        AppScreen1 = new javax.swing.JPanel();
        AppNameLabel = new javax.swing.JLabel();
        SplitPane1 = new javax.swing.JSplitPane();
        LeftPane1 = new javax.swing.JScrollPane();
        FileTree1 = new javax.swing.JTree();
        RightPane1 = new javax.swing.JPanel();
        MenuBar = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1280, 720));
        setResizable(false);

        LoginPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LoginPanelMouseClicked(evt);
            }
        });

        WelcomeLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        WelcomeLabel.setText("Database Manager Tool");
        WelcomeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                WelcomeLabelMouseClicked(evt);
            }
        });

        SubLabel.setText("Temporal login");

        LoginButton.setText("Login");
        LoginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginButtonActionPerformed(evt);
            }
        });

        UsernameField.setText("Username");
        UsernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                UsernameFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                UsernameFieldFocusLost(evt);
            }
        });
        UsernameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UsernameFieldActionPerformed(evt);
            }
        });

        PasswordField.setText("password");
        PasswordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                PasswordFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                PasswordFieldFocusLost(evt);
            }
        });
        PasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PasswordFieldActionPerformed(evt);
            }
        });
        PasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PasswordFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                PasswordFieldKeyTyped(evt);
            }
        });

        NewUserButton.setText("Create New Connection");
        NewUserButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NewUserButtonMouseClicked(evt);
            }
        });
        NewUserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewUserButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout LoginPanelLayout = new javax.swing.GroupLayout(LoginPanel);
        LoginPanel.setLayout(LoginPanelLayout);
        LoginPanelLayout.setHorizontalGroup(
            LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginPanelLayout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoginPanelLayout.createSequentialGroup()
                        .addComponent(NewUserButton)
                        .addGap(329, 329, 329)
                        .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LoginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(UsernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                .addComponent(PasswordField))))
                    .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(SubLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(WelcomeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(617, Short.MAX_VALUE))
        );
        LoginPanelLayout.setVerticalGroup(
            LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginPanelLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(LoginPanelLayout.createSequentialGroup()
                        .addComponent(WelcomeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SubLabel)
                        .addGap(146, 146, 146)
                        .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(NewUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LoginButton)
                .addContainerGap(350, Short.MAX_VALUE))
        );

        LayeredPane.setLayer(LoginPanel, javax.swing.JLayeredPane.PALETTE_LAYER);
        LayeredPane.add(LoginPanel);
        LoginPanel.setBounds(0, 0, 1280, 720);

        AppScreen.setMaximumSize(new java.awt.Dimension(1280, 720));
        AppScreen.setPreferredSize(new java.awt.Dimension(1280, 720));

        SplitPane.setDividerLocation(150);

        javax.swing.GroupLayout RightPaneLayout = new javax.swing.GroupLayout(RightPane);
        RightPane.setLayout(RightPaneLayout);
        RightPaneLayout.setHorizontalGroup(
            RightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1125, Short.MAX_VALUE)
        );
        RightPaneLayout.setVerticalGroup(
            RightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 683, Short.MAX_VALUE)
        );

        SplitPane.setRightComponent(RightPane);

        LeftPane.setLayout(null);

        JTreePane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JTreePaneMouseClicked(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Bases");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("BasePrueba");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Tables");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Views");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Packages");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Saved Processes");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Functions");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Secuences");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Triggers");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Indexes");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Users");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        FileTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        FileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FileTreeMouseClicked(evt);
            }
        });
        JTreePane.setViewportView(FileTree);

        LeftPane.add(JTreePane);
        JTreePane.setBounds(0, 0, 150, 400);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Field", "Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
        }

        LeftPane.add(jScrollPane1);
        jScrollPane1.setBounds(0, 400, 150, 320);

        SplitPane.setLeftComponent(LeftPane);

        javax.swing.GroupLayout AppScreenLayout = new javax.swing.GroupLayout(AppScreen);
        AppScreen.setLayout(AppScreenLayout);
        AppScreenLayout.setHorizontalGroup(
            AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitPane)
        );
        AppScreenLayout.setVerticalGroup(
            AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SplitPane))
        );

        Sessions.addTab("tab1", AppScreen);

        LayeredPane.add(Sessions);
        Sessions.setBounds(0, 0, 1280, 720);

        AppScreen1.setMaximumSize(new java.awt.Dimension(1280, 720));
        AppScreen1.setPreferredSize(new java.awt.Dimension(1280, 720));

        AppNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        AppNameLabel.setText("Database Management Tool");

        SplitPane1.setDividerLocation(150);

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Bases");
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("BasePrueba");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Tables");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Views");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Packages");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Saved Processes");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Functions");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Secuences");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Triggers");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Indexes");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Users");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        FileTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        FileTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FileTreeMouseClicked(evt);
            }
        });
        LeftPane1.setViewportView(FileTree1);

        SplitPane1.setLeftComponent(LeftPane1);

        javax.swing.GroupLayout RightPane1Layout = new javax.swing.GroupLayout(RightPane1);
        RightPane1.setLayout(RightPane1Layout);
        RightPane1Layout.setHorizontalGroup(
            RightPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1109, Short.MAX_VALUE)
        );
        RightPane1Layout.setVerticalGroup(
            RightPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 676, Short.MAX_VALUE)
        );

        SplitPane1.setRightComponent(RightPane1);

        javax.swing.GroupLayout AppScreen1Layout = new javax.swing.GroupLayout(AppScreen1);
        AppScreen1.setLayout(AppScreen1Layout);
        AppScreen1Layout.setHorizontalGroup(
            AppScreen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppScreen1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AppNameLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(SplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1264, Short.MAX_VALUE)
        );
        AppScreen1Layout.setVerticalGroup(
            AppScreen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppScreen1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AppNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SplitPane1))
        );

        LayeredPane.add(AppScreen1);
        AppScreen1.setBounds(0, 0, 1280, 720);

        jMenu2.setText("File");
        MenuBar.add(jMenu2);

        jMenu3.setText("Edit");
        MenuBar.add(jMenu3);

        setJMenuBar(MenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1280, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LayeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void UsernameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsernameFieldActionPerformed
        log();
    }//GEN-LAST:event_UsernameFieldActionPerformed

    private void LoginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginButtonActionPerformed
        log();
    }//GEN-LAST:event_LoginButtonActionPerformed

    private void UsernameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UsernameFieldFocusGained
        if (UsernameField.getText().equals("Username")) {
            UsernameField.setText("");
        }
    }//GEN-LAST:event_UsernameFieldFocusGained

    private void PasswordFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PasswordFieldFocusGained
        if (PasswordField.getText().equals("password")) {
            PasswordField.setText("");
        }
    }//GEN-LAST:event_PasswordFieldFocusGained

    private void UsernameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_UsernameFieldFocusLost
        if (UsernameField.getText().equals("")) {
            UsernameField.setText("Username");
        }
    }//GEN-LAST:event_UsernameFieldFocusLost

    private void PasswordFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PasswordFieldFocusLost
        if (PasswordField.getText().equals("")) {
            PasswordField.setText("password");
        }
    }//GEN-LAST:event_PasswordFieldFocusLost

    private void LoginPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoginPanelMouseClicked
        WelcomeLabel.grabFocus();
    }//GEN-LAST:event_LoginPanelMouseClicked

    private void WelcomeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_WelcomeLabelMouseClicked
        WelcomeLabel.grabFocus();
    }//GEN-LAST:event_WelcomeLabelMouseClicked

    private void FileTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FileTreeMouseClicked
        loadDataBase("prueba2");
    }//GEN-LAST:event_FileTreeMouseClicked

    private void NewUserButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NewUserButtonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_NewUserButtonMouseClicked

    private void NewUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewUserButtonActionPerformed

        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        JTextField dataBaseAlias = new JTextField();
        JTextField hostname = new JTextField();

        Object[] message = {
            "Username:", username,
            "Password:", password,
            "Host name:", hostname,
            "Database Alias:", dataBaseAlias,
        };
        
        JOptionPane.showMessageDialog(new JPanel(), message);
        DataBase temporal = new DataBase(((JTextField)message[5]).getText(), ((JTextField)message[7]).getText(),
                ((JTextField)message[1]).getText(), ((JTextField)message[3]).getText());
        
        FileTree.setSelectionRow(0);
        DefaultMutableTreeNode n = new DefaultMutableTreeNode(((JTextField)message[7]).getText());
        selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
        modelo.insertNodeInto(n, selectedNode, modelo.getChildCount(modelo.getRoot()));
        dbs.add(temporal);

    }//GEN-LAST:event_NewUserButtonActionPerformed


    private void PasswordFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PasswordFieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PasswordFieldKeyPressed

    private void PasswordFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PasswordFieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_PasswordFieldKeyTyped

    private void PasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PasswordFieldActionPerformed
        log();
    }//GEN-LAST:event_PasswordFieldActionPerformed

    private void JTreePaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTreePaneMouseClicked
        
    }//GEN-LAST:event_JTreePaneMouseClicked

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
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Window().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AppNameLabel;
    private javax.swing.JPanel AppScreen;
    private javax.swing.JPanel AppScreen1;
    private javax.swing.JTree FileTree;
    private javax.swing.JTree FileTree1;
    private javax.swing.JScrollPane JTreePane;
    private javax.swing.JLayeredPane LayeredPane;
    private javax.swing.JPanel LeftPane;
    private javax.swing.JScrollPane LeftPane1;
    private javax.swing.JButton LoginButton;
    private javax.swing.JPanel LoginPanel;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JButton NewUserButton;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JPanel RightPane;
    private javax.swing.JPanel RightPane1;
    private javax.swing.JTabbedPane Sessions;
    private javax.swing.JSplitPane SplitPane;
    private javax.swing.JSplitPane SplitPane1;
    private javax.swing.JLabel SubLabel;
    private javax.swing.JTextField UsernameField;
    private javax.swing.JLabel WelcomeLabel;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
