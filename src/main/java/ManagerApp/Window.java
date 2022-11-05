/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ManagerApp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author bounc
 */
public class Window extends javax.swing.JFrame {

    /**
     * Creates new form Window
     */
    String logged[] = {"", ""};
    private DefaultTreeModel modelo;
    DefaultMutableTreeNode selectedNode;
    DataBase basePrueba;
    ArrayList<DataBase> dbs = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();

    public Window() {
        initComponents();
        this.setLocationRelativeTo(null);
        modelo = (DefaultTreeModel) FileTree.getModel();
        refreshMenu.setEnabled(false);
        logoutMenu.setEnabled(false);

        try ( RandomAccessFile file = new RandomAccessFile("info.bin", "rw")) {
            long leido = 0;
            while (leido < file.length()) {
                file.seek(0);
                char user[] = new char[20];
                char password[] = new char[20];
                for (int i = 0; i < 20; i++) {
                    user[i] = file.readChar();
                }
                for (int i = 0; i < 20; i++) {
                    password[i] = file.readChar();
                }
                leido += 20;
                User userTemp = new User(user, password);
                users.add(userTemp);
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

        try ( RandomAccessFile file = new RandomAccessFile("data.bin", "rw")) {
            while (file.getFilePointer() < file.length()) {
                file.seek(0);
                String path = file.readUTF();
                String alias = file.readUTF();
                String user = file.readUTF();
                String password = file.readUTF();
                DataBase tempBase = new DataBase(path, alias, user, password);
                dbs.add(tempBase);
                modelo.insertNodeInto(new DefaultMutableTreeNode(alias),
                        (DefaultMutableTreeNode) modelo.getRoot(),
                        ((DefaultMutableTreeNode) modelo.getRoot()).getChildCount());
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    private void log() {
        for (User user : users) {
            StringBuffer sb = new StringBuffer(UsernameField.getText());
            sb.setLength(20);
            char tempUser[] = sb.toString().toCharArray();
            sb = new StringBuffer(PasswordField.getText());
            sb.setLength(20);
            char tempPass[] = sb.toString().toCharArray();
            if (user.confirmUser(tempUser, tempPass)) {
                AppScreen1.setVisible(false);
                LoginPanel.setVisible(false);
                logged[0] = UsernameField.getText();
                logged[1] = PasswordField.getText();
                refreshMenu.setEnabled(true);
                logoutMenu.setEnabled(true);
                UsernameField.setText("Username");
                PasswordField.setText("Password");
            }
        }
    }

    private void loadDataBase(String dataBaseAlias) {
        if (dbs.isEmpty()) {
            return;
        }
        try {
            int arrayDBIndex = -1;
            for (int i = 0; i < dbs.size(); i++) {
                /*System.out.println("ALIAS " + dataBaseAlias);
                System.out.println("ALIAS2 " + dbs.get(i).getAlias());*/
                if (dataBaseAlias.equals(dbs.get(i).getAlias())) {
                    arrayDBIndex = i;
                    //System.out.println("HOLAAA");
                }
            }
            if (arrayDBIndex == -1) {
                //System.out.println("Esto debe salir");
                return;
            }
            int indice = FileTree.getRowForPath(FileTree.getSelectionPath());
            int dataBaseIndex = FileTree.getRowForPath(FileTree.getNextMatch(dataBaseAlias, 0, Position.Bias.Forward));
            //System.out.println("Indice ..." + dataBaseIndex);
            FileTree.setSelectionRow(dataBaseIndex);
            selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
            //System.out.println(selectedNode);
            if (modelo.getChildCount(selectedNode) == 0) {
                String listas[] = {"Tables", "Views", "Packages", "Saved Processes", "Functions", "Secuences", "Triggers", "Indexes", "Users"};

                for (String nodo : listas) {
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(nodo);
                    modelo.insertNodeInto(n, selectedNode, selectedNode.getChildCount());
                }
                FileTree.setModel(modelo);
            }

            FileTree.expandRow(dataBaseIndex);
            //System.out.println("PATH " + FileTree.getRowForPath(FileTree.getNextMatch("Tables", dataBaseIndex, Position.Bias.Forward)));
            FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Tables", dataBaseIndex, Position.Bias.Forward)));
            selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();

            if (modelo.getChildCount(selectedNode) == 0) {
                //System.out.println(selectedNode);
                basePrueba = dbs.get(arrayDBIndex);
                //System.out.println(basePrueba.getAlias());
                // <editor-fold defaultstate="collapsed" desc="Carga de Tablas">
                // Carga de Tablas
                ResultSet res = basePrueba.query("""
                                                    select rdb$relation_name
                                                    from rdb$relations
                                                    where rdb$view_blr is null
                                                    and (rdb$system_flag is null or rdb$system_flag = 0);
                                                """);
                while (res.next()) {
                    ResultSet res1 = basePrueba.query("select rdb$field_name from rdb$relation_fields where rdb$relation_name like '" + res.getNString(1) + "%';");
                    //System.out.println(res.getNString(1));
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    FileTree.setSelectionRow(FileTree.getRowForPath(FileTree.getNextMatch("Tables", dataBaseIndex, Position.Bias.Forward)));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    //System.out.println(selectedNode.toString());
                    while (res1.next()) {
                        //System.out.println(res1.getNString(1));
                        DefaultMutableTreeNode temporal = new DefaultMutableTreeNode(res1.getNString(1));
                        n.insert(temporal, n.getChildCount());
                    }
                    modelo.insertNodeInto(n, selectedNode, 0);
                    FileTree.setModel(modelo);
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
                while (res.next()) {
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
                while (res.next()) {
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
                while (res.next()) {
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
                while (res.next()) {
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
                while (res.next()) {
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
                while (res.next()) {
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
                while (res.next()) {
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
                while (res.next()) {
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(res.getNString(1));
                    selectedNode = (DefaultMutableTreeNode) FileTree.getLastSelectedPathComponent();
                    modelo.insertNodeInto(n, selectedNode, 0);
                }
                // </editor-fold>

            } else {
                FileTree.setSelectionRow(indice);
            }
            FileTree.collapseRow(dataBaseIndex);
            FileTree.clearSelection();
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
        RefreshButton = new javax.swing.JButton();
        AppScreen1 = new javax.swing.JPanel();
        AppNameLabel = new javax.swing.JLabel();
        SplitPane1 = new javax.swing.JSplitPane();
        LeftPane1 = new javax.swing.JScrollPane();
        FileTree1 = new javax.swing.JTree();
        RightPane1 = new javax.swing.JPanel();
        MenuBar = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        refreshMenu = new javax.swing.JMenuItem();
        logoutMenu = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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

        LayeredPane.setLayer(LoginPanel, javax.swing.JLayeredPane.DRAG_LAYER);
        LayeredPane.add(LoginPanel);
        LoginPanel.setBounds(0, 0, 1280, 720);

        AppScreen.setMaximumSize(new java.awt.Dimension(1280, 720));
        AppScreen.setPreferredSize(new java.awt.Dimension(1280, 720));

        SplitPane.setDividerLocation(150);

        RightPane.setPreferredSize(new java.awt.Dimension(200, 679));

        javax.swing.GroupLayout RightPaneLayout = new javax.swing.GroupLayout(RightPane);
        RightPane.setLayout(RightPaneLayout);
        RightPaneLayout.setHorizontalGroup(
            RightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1114, Short.MAX_VALUE)
        );
        RightPaneLayout.setVerticalGroup(
            RightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 661, Short.MAX_VALUE)
        );

        SplitPane.setRightComponent(RightPane);

        LeftPane.setMinimumSize(new java.awt.Dimension(100, 100));
        LeftPane.setPreferredSize(new java.awt.Dimension(200, 100));

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

        javax.swing.GroupLayout LeftPaneLayout = new javax.swing.GroupLayout(LeftPane);
        LeftPane.setLayout(LeftPaneLayout);
        LeftPaneLayout.setHorizontalGroup(
            LeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(JTreePane, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
        );
        LeftPaneLayout.setVerticalGroup(
            LeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeftPaneLayout.createSequentialGroup()
                .addComponent(JTreePane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
        );

        SplitPane.setLeftComponent(LeftPane);

        RefreshButton.setText("Refresh");
        RefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AppScreenLayout = new javax.swing.GroupLayout(AppScreen);
        AppScreen.setLayout(AppScreenLayout);
        AppScreenLayout.setHorizontalGroup(
            AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppScreenLayout.createSequentialGroup()
                .addGroup(AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1263, Short.MAX_VALUE)
                    .addGroup(AppScreenLayout.createSequentialGroup()
                        .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        AppScreenLayout.setVerticalGroup(
            AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppScreenLayout.createSequentialGroup()
                .addComponent(RefreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
        );

        Sessions.addTab("Session", AppScreen);

        LayeredPane.setLayer(Sessions, javax.swing.JLayeredPane.MODAL_LAYER);
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

        jMenu3.setText("Session");

        refreshMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        refreshMenu.setText("Refresh");
        refreshMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshMenuActionPerformed(evt);
            }
        });
        jMenu3.add(refreshMenu);

        logoutMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        logoutMenu.setText("Log out");
        logoutMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutMenuActionPerformed(evt);
            }
        });
        jMenu3.add(logoutMenu);

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
            "Database Alias:", dataBaseAlias,};

        JOptionPane.showMessageDialog(new JPanel(), message);
        DataBase temporal = new DataBase(((JTextField) message[5]).getText(), ((JTextField) message[7]).getText(),
                ((JTextField) message[1]).getText(), ((JTextField) message[3]).getText());

        try {
            temporal.getConnection();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error de Conexion", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < modelo.getChildCount(modelo.getRoot()); i++) {
            if (modelo.getChild(modelo.getRoot(), i).toString().equals(temporal.getAlias())) {
                JOptionPane.showMessageDialog(null, "Error: El alias ya esta en uso", "Error de registro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        char usuario[] = new char[20];
        char contra[] = new char[20];

        User userTemp = new User(logged[0].toCharArray(), logged[1].toCharArray());

        StringBuffer sb = new StringBuffer(((JTextField) message[1]).getText());
        sb.setLength(20);
        usuario = sb.toString().toCharArray();
        userTemp.setUser(usuario);

        sb = new StringBuffer(((JTextField) message[3]).getText());
        sb.setLength(20);
        contra = sb.toString().toCharArray();
        userTemp.setPassword(contra);

        boolean exists = false;
        for (User user : users) {
            if (user.confirmUser(usuario, contra)) {
                exists = user.confirmUser(usuario, contra);
                break;
            }
        }

        if (!exists) {
            try ( RandomAccessFile file = new RandomAccessFile("info.bin", "rw")) {
                file.seek(file.length());
                file.writeChars(Arrays.toString(usuario).replaceAll("[, | \\u005B | \\u005D]", ""));
                file.writeChars(Arrays.toString(contra).replaceAll("[, | \\u005B | \\u005D]", ""));
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Archivo local no encontrado", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error al abrir archivo local", JOptionPane.ERROR_MESSAGE);
            }
        }

        try ( RandomAccessFile file = new RandomAccessFile("data.bin", "rw")) {
            file.seek(file.length());
            file.writeUTF(temporal.getPath());
            file.writeUTF(temporal.getAlias());
            file.writeUTF(((JTextField) message[1]).getText());
            file.writeUTF(((JTextField) message[3]).getText());
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Archivo local no encontrado", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error al abrir archivo local", JOptionPane.ERROR_MESSAGE);
        }

        users.add(new User(usuario, contra));
        FileTree.setSelectionRow(0);
        DefaultMutableTreeNode n = new DefaultMutableTreeNode(((JTextField) message[7]).getText());
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

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        for (int i = 0; i < modelo.getChildCount(modelo.getRoot()); i++) {
            FileTree.collapsePath(FileTree.getNextMatch(modelo.getChild(modelo.getRoot(), i).toString(), 0, Position.Bias.Forward));
        }
        for (int i = 0; i < modelo.getChildCount(modelo.getRoot()); i++) {
            loadDataBase(modelo.getChild(modelo.getRoot(), i).toString());
        }
    }//GEN-LAST:event_RefreshButtonActionPerformed

    private void logoutMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutMenuActionPerformed
        LoginPanel.setVisible(true);
        refreshMenu.setEnabled(false);
        logoutMenu.setEnabled(false);
    }//GEN-LAST:event_logoutMenuActionPerformed

    private void refreshMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshMenuActionPerformed
        for (int i = 0; i < modelo.getChildCount(modelo.getRoot()); i++) {
            FileTree.collapsePath(FileTree.getNextMatch(modelo.getChild(modelo.getRoot(), i).toString(), 0, Position.Bias.Forward));
        }
        for (int i = 0; i < modelo.getChildCount(modelo.getRoot()); i++) {
            loadDataBase(modelo.getChild(modelo.getRoot(), i).toString());
        }
    }//GEN-LAST:event_refreshMenuActionPerformed

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
    private javax.swing.JButton RefreshButton;
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
    private javax.swing.JMenuItem logoutMenu;
    private javax.swing.JMenuItem refreshMenu;
    // End of variables declaration//GEN-END:variables
}
