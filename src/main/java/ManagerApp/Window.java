/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ManagerApp;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author bounc
 */
public class Window extends javax.swing.JFrame {

    /**
     * Creates new form Window
     */
    private int cont = 0;
    private DefaultTreeModel modelo;
    DefaultMutableTreeNode selectedNode;
    DataBase basePrueba;

    public Window() {
        initComponents();
        modelo = (DefaultTreeModel) FileTree.getModel();
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
        AppScreen = new javax.swing.JPanel();
        AppNameLabel = new javax.swing.JLabel();
        SplitPane = new javax.swing.JSplitPane();
        LeftPane = new javax.swing.JScrollPane();
        FileTree = new javax.swing.JTree();
        RightPane = new javax.swing.JPanel();
        LoginPanel = new javax.swing.JPanel();
        WelcomeLabel = new javax.swing.JLabel();
        SubLabel = new javax.swing.JLabel();
        LoginButton = new javax.swing.JButton();
        UsernameField = new javax.swing.JTextField();
        PasswordField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1280, 720));
        setResizable(false);

        AppScreen.setMaximumSize(new java.awt.Dimension(1280, 720));
        AppScreen.setPreferredSize(new java.awt.Dimension(1280, 720));

        AppNameLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        AppNameLabel.setText("Database Management Tool");

        SplitPane.setDividerLocation(150);

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
        LeftPane.setViewportView(FileTree);

        SplitPane.setLeftComponent(LeftPane);

        javax.swing.GroupLayout RightPaneLayout = new javax.swing.GroupLayout(RightPane);
        RightPane.setLayout(RightPaneLayout);
        RightPaneLayout.setHorizontalGroup(
            RightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1109, Short.MAX_VALUE)
        );
        RightPaneLayout.setVerticalGroup(
            RightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 676, Short.MAX_VALUE)
        );

        SplitPane.setRightComponent(RightPane);

        javax.swing.GroupLayout AppScreenLayout = new javax.swing.GroupLayout(AppScreen);
        AppScreen.setLayout(AppScreenLayout);
        AppScreenLayout.setHorizontalGroup(
            AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AppNameLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(SplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1264, Short.MAX_VALUE)
        );
        AppScreenLayout.setVerticalGroup(
            AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AppNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SplitPane))
        );

        LayeredPane.setLayer(AppScreen, javax.swing.JLayeredPane.DRAG_LAYER);
        LayeredPane.add(AppScreen);
        AppScreen.setBounds(0, 0, 1280, 720);

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

        javax.swing.GroupLayout LoginPanelLayout = new javax.swing.GroupLayout(LoginPanel);
        LoginPanel.setLayout(LoginPanelLayout);
        LoginPanelLayout.setHorizontalGroup(
            LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginPanelLayout.createSequentialGroup()
                .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoginPanelLayout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SubLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(WelcomeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(LoginPanelLayout.createSequentialGroup()
                        .addGap(504, 504, 504)
                        .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LoginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(UsernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                .addComponent(PasswordField)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LoginPanelLayout.setVerticalGroup(
            LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginPanelLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(WelcomeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SubLabel)
                .addGap(146, 146, 146)
                .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LoginButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        LayeredPane.setLayer(LoginPanel, javax.swing.JLayeredPane.MODAL_LAYER);
        LayeredPane.add(LoginPanel);
        LoginPanel.setBounds(0, 0, 1280, 720);

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
        // TODO add your handling code here:
    }//GEN-LAST:event_UsernameFieldActionPerformed

    private void LoginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginButtonActionPerformed
        System.out.println(UsernameField.getText());
        System.out.println(PasswordField.getPassword());
        if (UsernameField.getText().equals("admin") && PasswordField.getText().equals("admin")) {
            LoginPanel.setVisible(false);
        }
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
        try {
            /*cont++;
            if (cont == 2) {
            System.out.println(FileTree.getSelectionPath());
            FileTree.expandPath(FileTree.getSelectionPath());
            FileTree.expandRow(1);
            cont = 0;
            }*/
            int indice = FileTree.getRowForPath(FileTree.getSelectionPath());
            FileTree.setSelectionRow(2);
            selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();

            if (modelo.getChildCount(selectedNode) == 0) {
                basePrueba = new DataBase("//olimpo.dscloud.biz:1433//firebird/data/", "base.fdb", "SYSDBA", "masterkey");
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
                    System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // Carga de Vistas
                FileTree.setSelectionRow(3);
                res = basePrueba.query("""
                                        select rdb$relation_name
                                        from rdb$relations
                                        where rdb$view_blr is not null
                                        and (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // Carga de Paquetes
                FileTree.setSelectionRow(4);
                res = basePrueba.query("""
                                        select RDB$PACKAGE_NAME
                                        from RDB$PACKAGES
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // Carga de Procedimientos Almacenados
                FileTree.setSelectionRow(5);
                res = basePrueba.query("""
                                        select RDB$PROCEDURE_NAME
                                        from RDB$PROCEDURES
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // Carga de Funciones
                FileTree.setSelectionRow(6);
                res = basePrueba.query("""
                                        select RDB$FUNCTION_NAME
                                        from RDB$FUNCTIONS
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // Carga de Generadores
                FileTree.setSelectionRow(7);
                res = basePrueba.query("""
                                        select RDB$GENERATOR_NAME
                                        from RDB$GENERATORS
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // Carga de Triggers
                FileTree.setSelectionRow(8);
                res = basePrueba.query("""
                                        select RDB$TRIGGER_NAME
                                        from RDB$TRIGGERS
                                        where (rdb$system_flag is null or rdb$system_flag = 0);
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // Carga de Usuarios
                FileTree.setSelectionRow(10);
                res = basePrueba.query("""
                                        select SEC$USER_NAME
                                        from SEC$USERS;
                                    """);
                cnt = 0;
                while (res.next()) {
                    cnt++;
                    System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
            } else {
                FileTree.setSelectionRow(indice);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_FileTreeMouseClicked

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
    private javax.swing.JTree FileTree;
    private javax.swing.JLayeredPane LayeredPane;
    private javax.swing.JScrollPane LeftPane;
    private javax.swing.JButton LoginButton;
    private javax.swing.JPanel LoginPanel;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JPanel RightPane;
    private javax.swing.JSplitPane SplitPane;
    private javax.swing.JLabel SubLabel;
    private javax.swing.JTextField UsernameField;
    private javax.swing.JLabel WelcomeLabel;
    // End of variables declaration//GEN-END:variables
}
