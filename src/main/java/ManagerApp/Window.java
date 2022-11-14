/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ManagerApp;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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
        queryPanel.setToolTipText(null);
        
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
                leido += 80;
                User userTemp = new User(user, password);
                System.out.println(user);
                UsernameField.addItem(Arrays.toString(user).replaceAll("[, | \\u005B | \\u005D]", ""));
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
            StringBuffer sb = new StringBuffer((String) UsernameField.getSelectedItem());
            sb.setLength(20);
            char tempUser[] = sb.toString().toCharArray();
            sb = new StringBuffer(PasswordField.getText());
            sb.setLength(20);
            char tempPass[] = sb.toString().toCharArray();
            if (user.confirmUser(tempUser, tempPass)) {
                AppScreen1.setVisible(false);
                LoginPanel.setVisible(false);
                logged[0] = (String) UsernameField.getSelectedItem();
                logged[1] = PasswordField.getText();
                refreshMenu.setEnabled(true);
                logoutMenu.setEnabled(true);
                UsernameField.setSelectedIndex(-1);
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
            for (int i = 0; i < selectedNode.getChildCount(); i++) {
                selectedNode.remove(0);
            }
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
            
            if (true) {
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

        jFrame1 = new javax.swing.JFrame();
        newTablePanel = new javax.swing.JScrollPane();
        newTable = new javax.swing.JTable();
        CreateTableScreen = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        TableInput = new javax.swing.JTable();
        CancelTableButton = new javax.swing.JButton();
        CreateTableButton = new javax.swing.JButton();
        LayeredPane = new javax.swing.JLayeredPane();
        LoginPanel = new javax.swing.JPanel();
        TitleLabel = new javax.swing.JLabel();
        SubLabel = new javax.swing.JLabel();
        LoginButton = new javax.swing.JButton();
        PasswordField = new javax.swing.JPasswordField();
        NewUserButton = new javax.swing.JButton();
        UsernameField = new javax.swing.JComboBox<>();
        UserLabel = new javax.swing.JLabel();
        PasswordLabel = new javax.swing.JLabel();
        Sessions = new javax.swing.JTabbedPane();
        AppScreen = new javax.swing.JPanel();
        SplitPane = new javax.swing.JSplitPane();
        RightPane = new javax.swing.JPanel();
        ScriptsPanel = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        queryPanel = new javax.swing.JTextArea();
        OutPane = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        LeftPane = new javax.swing.JPanel();
        JTreePane = new javax.swing.JScrollPane();
        FileTree = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        RefreshButton = new javax.swing.JButton();
        runQueryButton = new javax.swing.JButton();
        dataCreate = new javax.swing.JButton();
        tableCreate = new javax.swing.JButton();
        viewCreate = new javax.swing.JButton();
        userCreate = new javax.swing.JButton();
        AppScreen1 = new javax.swing.JPanel();
        AppNameLabel = new javax.swing.JLabel();
        SplitPane1 = new javax.swing.JSplitPane();
        LeftPane1 = new javax.swing.JScrollPane();
        FileTree1 = new javax.swing.JTree();
        RightPane1 = new javax.swing.JPanel();
        CreateUserScreen = new javax.swing.JPanel();
        TitleLabel1 = new javax.swing.JLabel();
        SubLabel1 = new javax.swing.JLabel();
        CreateUserButton = new javax.swing.JButton();
        CreatePasswordField = new javax.swing.JPasswordField();
        CancelCreateButton = new javax.swing.JButton();
        UserLabel1 = new javax.swing.JLabel();
        PasswordLabel1 = new javax.swing.JLabel();
        CreateUserField = new javax.swing.JTextField();
        HostnameLabel = new javax.swing.JLabel();
        HostnameField = new javax.swing.JTextField();
        PathChooserButton = new javax.swing.JButton();
        DatabaseAliasField = new javax.swing.JTextField();
        DBAliasLabel = new javax.swing.JLabel();
        MenuBar = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        refreshMenu = new javax.swing.JMenuItem();
        logoutMenu = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        newTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        newTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newTableKeyTyped(evt);
            }
        });
        newTablePanel.setViewportView(newTable);

        CreateTableScreen.setPreferredSize(new java.awt.Dimension(160, 100));

        TableInput.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Field", "Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane5.setViewportView(TableInput);
        if (TableInput.getColumnModel().getColumnCount() > 0) {
            TableInput.getColumnModel().getColumn(0).setResizable(false);
            TableInput.getColumnModel().getColumn(1).setResizable(false);
        }

        CancelTableButton.setText("Cancel");

        CreateTableButton.setText("Create");
        CreateTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateTableButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CreateTableScreenLayout = new javax.swing.GroupLayout(CreateTableScreen);
        CreateTableScreen.setLayout(CreateTableScreenLayout);
        CreateTableScreenLayout.setHorizontalGroup(
            CreateTableScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CreateTableScreenLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(CreateTableScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(CreateTableScreenLayout.createSequentialGroup()
                        .addComponent(CancelTableButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CreateTableButton))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );
        CreateTableScreenLayout.setVerticalGroup(
            CreateTableScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CreateTableScreenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CreateTableScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CreateTableButton)
                    .addComponent(CancelTableButton))
                .addGap(22, 22, 22))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        LoginPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LoginPanelMouseClicked(evt);
            }
        });

        TitleLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        TitleLabel.setText("Database Manager Tool");
        TitleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TitleLabelMouseClicked(evt);
            }
        });

        SubLabel.setText("Login");

        LoginButton.setText("Login");
        LoginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginButtonActionPerformed(evt);
            }
        });

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

        NewUserButton.setText("Create a new user");
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

        UsernameField.setEditable(true);

        UserLabel.setText("Username");

        PasswordLabel.setText("Password");

        javax.swing.GroupLayout LoginPanelLayout = new javax.swing.GroupLayout(LoginPanel);
        LoginPanel.setLayout(LoginPanelLayout);
        LoginPanelLayout.setHorizontalGroup(
            LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginPanelLayout.createSequentialGroup()
                .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoginPanelLayout.createSequentialGroup()
                        .addGap(478, 478, 478)
                        .addComponent(TitleLabel))
                    .addGroup(LoginPanelLayout.createSequentialGroup()
                        .addGap(584, 584, 584)
                        .addComponent(SubLabel))
                    .addGroup(LoginPanelLayout.createSequentialGroup()
                        .addGap(456, 456, 456)
                        .addGroup(LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(LoginButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(UsernameField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(UserLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PasswordField)
                            .addComponent(PasswordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(NewUserButton, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))))
                .addContainerGap(533, Short.MAX_VALUE))
        );
        LoginPanelLayout.setVerticalGroup(
            LoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginPanelLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(TitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SubLabel)
                .addGap(85, 85, 85)
                .addComponent(UserLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(UsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PasswordLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(LoginButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NewUserButton)
                .addContainerGap(296, Short.MAX_VALUE))
        );

        LayeredPane.setLayer(LoginPanel, javax.swing.JLayeredPane.DRAG_LAYER);
        LayeredPane.add(LoginPanel);
        LoginPanel.setBounds(0, 0, 1280, 720);

        AppScreen.setMaximumSize(new java.awt.Dimension(1280, 720));
        AppScreen.setPreferredSize(new java.awt.Dimension(1280, 720));

        SplitPane.setDividerLocation(150);

        RightPane.setPreferredSize(new java.awt.Dimension(200, 679));

        ScriptsPanel.setDividerLocation(500);
        ScriptsPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        queryPanel.setColumns(20);
        queryPanel.setRows(5);
        queryPanel.setTabSize(4);
        queryPanel.setToolTipText("");
        queryPanel.setPreferredSize(new java.awt.Dimension(160, 100));
        jScrollPane2.setViewportView(queryPanel);

        ScriptsPanel.setTopComponent(jScrollPane2);

        outputArea.setEditable(false);
        outputArea.setColumns(20);
        outputArea.setRows(5);
        jScrollPane4.setViewportView(outputArea);

        OutPane.addTab("Output", jScrollPane4);

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(resultTable);

        OutPane.addTab("Result", jScrollPane3);

        ScriptsPanel.setRightComponent(OutPane);

        javax.swing.GroupLayout RightPaneLayout = new javax.swing.GroupLayout(RightPane);
        RightPane.setLayout(RightPaneLayout);
        RightPaneLayout.setHorizontalGroup(
            RightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ScriptsPanel)
        );
        RightPaneLayout.setVerticalGroup(
            RightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ScriptsPanel, javax.swing.GroupLayout.Alignment.TRAILING)
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
            .addComponent(JTreePane, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );
        LeftPaneLayout.setVerticalGroup(
            LeftPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LeftPaneLayout.createSequentialGroup()
                .addComponent(JTreePane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
        );

        SplitPane.setLeftComponent(LeftPane);

        RefreshButton.setText("Refresh");
        RefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshButtonActionPerformed(evt);
            }
        });

        runQueryButton.setText("Run Query");
        runQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runQueryButtonActionPerformed(evt);
            }
        });

        dataCreate.setText("Create Database");
        dataCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataCreateActionPerformed(evt);
            }
        });

        tableCreate.setText("Create Table");
        tableCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableCreateActionPerformed(evt);
            }
        });

        viewCreate.setText("Create View");
        viewCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewCreateActionPerformed(evt);
            }
        });

        userCreate.setText("Create User");
        userCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userCreateActionPerformed(evt);
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(runQueryButton)
                        .addGap(18, 18, 18)
                        .addComponent(dataCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tableCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(viewCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 539, Short.MAX_VALUE)))
                .addContainerGap())
        );
        AppScreenLayout.setVerticalGroup(
            AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AppScreenLayout.createSequentialGroup()
                .addGroup(AppScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RefreshButton)
                    .addComponent(runQueryButton)
                    .addComponent(dataCreate)
                    .addComponent(tableCreate)
                    .addComponent(viewCreate)
                    .addComponent(userCreate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE))
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

        CreateUserScreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CreateUserScreenMouseClicked(evt);
            }
        });

        TitleLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        TitleLabel1.setText("Database Manager Tool");
        TitleLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TitleLabel1MouseClicked(evt);
            }
        });

        SubLabel1.setText("Create user");

        CreateUserButton.setText("Create user");
        CreateUserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateUserButtonActionPerformed(evt);
            }
        });

        CreatePasswordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                CreatePasswordFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                CreatePasswordFieldFocusLost(evt);
            }
        });
        CreatePasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreatePasswordFieldActionPerformed(evt);
            }
        });
        CreatePasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CreatePasswordFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                CreatePasswordFieldKeyTyped(evt);
            }
        });

        CancelCreateButton.setText("Cancel");
        CancelCreateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CancelCreateButtonMouseClicked(evt);
            }
        });
        CancelCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelCreateButtonActionPerformed(evt);
            }
        });

        UserLabel1.setText("Username");

        PasswordLabel1.setText("Password");

        CreateUserField.setPreferredSize(new java.awt.Dimension(64, 22));
        CreateUserField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateUserFieldActionPerformed(evt);
            }
        });

        HostnameLabel.setText("Hostname");

        HostnameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HostnameFieldActionPerformed(evt);
            }
        });

        PathChooserButton.setText("...");
        PathChooserButton.setPreferredSize(new java.awt.Dimension(64, 22));

        DatabaseAliasField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DatabaseAliasFieldActionPerformed(evt);
            }
        });

        DBAliasLabel.setText("Database alias");

        javax.swing.GroupLayout CreateUserScreenLayout = new javax.swing.GroupLayout(CreateUserScreen);
        CreateUserScreen.setLayout(CreateUserScreenLayout);
        CreateUserScreenLayout.setHorizontalGroup(
            CreateUserScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CreateUserScreenLayout.createSequentialGroup()
                .addGroup(CreateUserScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CreateUserScreenLayout.createSequentialGroup()
                        .addGap(478, 478, 478)
                        .addComponent(TitleLabel1))
                    .addGroup(CreateUserScreenLayout.createSequentialGroup()
                        .addGap(574, 574, 574)
                        .addComponent(SubLabel1))
                    .addGroup(CreateUserScreenLayout.createSequentialGroup()
                        .addGap(456, 456, 456)
                        .addGroup(CreateUserScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CreateUserField, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(UserLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CreatePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PasswordLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(CreateUserScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(DatabaseAliasField)
                                .addGroup(CreateUserScreenLayout.createSequentialGroup()
                                    .addGroup(CreateUserScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(HostnameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(HostnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(DBAliasLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(PathChooserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(CreateUserScreenLayout.createSequentialGroup()
                                .addComponent(CancelCreateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CreateUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(533, Short.MAX_VALUE))
        );
        CreateUserScreenLayout.setVerticalGroup(
            CreateUserScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CreateUserScreenLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(TitleLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SubLabel1)
                .addGap(36, 36, 36)
                .addComponent(UserLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CreateUserField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(PasswordLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CreatePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(HostnameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CreateUserScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HostnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PathChooserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(DBAliasLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DatabaseAliasField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72)
                .addGroup(CreateUserScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CancelCreateButton)
                    .addComponent(CreateUserButton))
                .addContainerGap(196, Short.MAX_VALUE))
        );

        LayeredPane.setLayer(CreateUserScreen, javax.swing.JLayeredPane.DRAG_LAYER);
        LayeredPane.add(CreateUserScreen);
        CreateUserScreen.setBounds(0, 0, 1280, 720);

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

    private void LoginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginButtonActionPerformed
        
        log();
    }//GEN-LAST:event_LoginButtonActionPerformed

    private void PasswordFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PasswordFieldFocusGained
        if (PasswordField.getText().equals("password")) {
            PasswordField.setText("");
        }
    }//GEN-LAST:event_PasswordFieldFocusGained

    private void PasswordFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PasswordFieldFocusLost
        if (PasswordField.getText().equals("")) {
            PasswordField.setText("password");
        }
    }//GEN-LAST:event_PasswordFieldFocusLost

    private void LoginPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LoginPanelMouseClicked
        TitleLabel.grabFocus();
    }//GEN-LAST:event_LoginPanelMouseClicked

    private void TitleLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TitleLabelMouseClicked
        TitleLabel.grabFocus();
    }//GEN-LAST:event_TitleLabelMouseClicked

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

        /* JTextField username = new JTextField("");
        JTextField password = new JTextField("");
        jtextfield
        JPanel panel = new JPanel(new GridLayout(0,1));
  
        panel.add(new JLabel("Field 1:"));
        panel.add(field1);
        panel.add(new JLabel("Field 2:"));
        panel.add(field2);
        int result = JOptionPane.showConfirmDialog(null, panel, "Test",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);*/
        Object[] message = {
            "Username:", username,
            "Password:", password,
            "Host name:", hostname,
            "Database Alias:", dataBaseAlias,};
        
        JOptionPane.showMessageDialog(new JPanel(new GridLayout(0, 1)), message, "Create user", JOptionPane.PLAIN_MESSAGE);
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

    private void runQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runQueryButtonActionPerformed
        if (queryPanel.getText().isBlank()) {
            return;
        }
        String selected = queryPanel.getSelectedText();
        String sb = selected;
        if (selected == null) {
            sb = queryPanel.getText();
        }
        String querys[] = sb.split(";");
        System.out.println(Arrays.toString(querys));
        for (String query : querys) {
            if (query.toUpperCase().startsWith("SELECT")) {
                try {
                    ResultSet res = basePrueba.query(query);
                    outputArea.setText(outputArea.getText() + "\n" + "Success Executing SQL");
                    DefaultTableModel modelo = (DefaultTableModel) resultTable.getModel();
                    for (int i = 0; i < res.getMetaData().getColumnCount(); i++) {
                        modelo.addColumn(res.getMetaData().getColumnName(i + 1));
                    }
                    while (res.next()) {
                        Vector v = new Vector();
                        for (int i = 0; i < res.getMetaData().getColumnCount(); i++) {
                            v.add(res.getObject(i + 1));
                        }
                        modelo.addRow(v);
                    }
                    OutPane.setSelectedIndex(1);
                } catch (SQLException ex) {
                    OutPane.setSelectedIndex(0);
                    outputArea.setText(outputArea.getText() + "\n" + ex.toString());
                }
            } else if (query.toUpperCase().startsWith("USE")) {
                String baseName = query.substring(4);
                for (DataBase db : dbs) {
                    if (db.getAlias().equalsIgnoreCase(baseName)) {
                        basePrueba = db;
                        OutPane.setSelectedIndex(0);
                        outputArea.setText(outputArea.getText() + "\n" + "Using database " + baseName);
                    }
                }
            } else {
                try {
                    basePrueba.execute(query);
                    OutPane.setSelectedIndex(0);
                    outputArea.setText(outputArea.getText() + "\n" + "Success Executing SQL");
                } catch (SQLException ex) {
                    OutPane.setSelectedIndex(0);
                    outputArea.setText(outputArea.getText() + "\n" + ex.toString());
                }
            }
        }
    }//GEN-LAST:event_runQueryButtonActionPerformed
    
    private void tableCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableCreateActionPerformed
        int option = JOptionPane.showConfirmDialog(null, "Visual Creation?", "Table Create", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == 0) {
            JTextField tableName = new JTextField();
            JTextField pk = new JTextField();
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Field");
            model.addColumn("Type");
            model.setRowCount(1);
            newTable.setModel(model);
            
            Object[] message = {
                "Table Name:", tableName,
                "Fields:", newTablePanel,
                "Host name:", pk};
            
            JOptionPane.showMessageDialog(new JPanel(new GridLayout(0, 1)), message, "Create Table", JOptionPane.PLAIN_MESSAGE);
            
            String newTableName = tableName.getText();
            String primaryKey = pk.getText();
            Vector fields = new Vector();
            Vector types = new Vector();
            for (int i = 0; i < model.getRowCount(); i++) {
                fields.add(newTable.getValueAt(0, i));
                types.add(newTable.getValueAt(1, i));
            }
            System.out.println(fields);
            System.out.println(types);
            String creation = "CREATE TABLE " + newTableName + " (";
            for (int i = 0; i < fields.size(); i++) {
                creation = creation.concat((String)fields.elementAt(i) + " " + (String)types.elementAt(i) + ", ");
            }
            creation = creation.concat(" PRIMARY KEY("+primaryKey+"));");
            
            try {
                basePrueba.execute(creation);
            } catch (SQLException ex) {
                Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else if (option == 1) {
            String creation = """
                          CREATE TABLE table_name
                          (
                              column_name {< datatype> | COMPUTED BY (< expr>) | domain}
                                  [DEFAULT { literal | NULL | USER}] [NOT NULL]
                              ...
                              CONSTRAINT constraint_name
                                  PRIMARY KEY (column_list),
                                  UNIQUE      (column_list),
                                  FOREIGN KEY (column_list) REFERENCES other_table (column_list),
                                  CHECK       (condition),
                              ...
                          );
                          """;
            
            queryPanel.setText(queryPanel.getText() + "\n" + creation);
        }
    }//GEN-LAST:event_tableCreateActionPerformed

    private void viewCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewCreateActionPerformed
        int option = JOptionPane.showConfirmDialog(null, "Visual Creation?", "View Create", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == 0) {
            JTextField username = new JTextField();
            JTextField password = new JPasswordField();
            JTextField dataBaseAlias = new JTextField();
            JTextField hostname = new JTextField();
            
            Object[] message = {
                "Username:", username,
                "Password:", password,
                "Host name:", hostname,
                "Database Alias:", dataBaseAlias,};
            
            JOptionPane.showMessageDialog(new JPanel(new GridLayout(0, 1)), message, "Create database", JOptionPane.PLAIN_MESSAGE);
        } else if (option == 1) {
            String creation = """
                          CREATE VIEW view_name ( view_column, ...)
                          AS
                          /* write select statement here */
                          WITH CHECK OPTION;
                          """;
            
            queryPanel.setText(queryPanel.getText() + "\n" + creation);
        }
    }//GEN-LAST:event_viewCreateActionPerformed

    private void userCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userCreateActionPerformed
        int option = JOptionPane.showConfirmDialog(null, "Visual Creation?", "Table Create", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == 0) {
            JTextField username = new JTextField();
            JTextField password = new JPasswordField();
            JTextField firstName = new JTextField();
            JTextField middleName = new JTextField();
            JTextField lastName = new JTextField();
            JComboBox admin = new JComboBox();
            admin.addItem("NO");
            admin.addItem("YES");
            
            Object[] message = {
                "Username:", username,
                "Password:", password,
                "First Name:", firstName,
                "Middle Name:", middleName,
                "Last Name:", lastName,
                "Admin Role:", admin};
            
            JOptionPane.showMessageDialog(new JPanel(new GridLayout(0, 1)), message, "Create user", JOptionPane.PLAIN_MESSAGE);
            
            String creation = "CREATE USER " + username.getText().toUpperCase() + " PASSWORD '" + password.getText() + "' ";
            if (!firstName.getText().isBlank()) {
                creation = creation.concat(" FIRSTNAME '" + firstName.getText() + "' ");
            }
            if (!middleName.getText().isBlank()) {
                creation = creation.concat(" MIDDLENAME '" + middleName.getText() + "' ");
            }
            if (!lastName.getText().isBlank()) {
                creation = creation.concat(" LASTNAME '" + lastName.getText() + "' ");
            }
            if (admin.getSelectedIndex() == 1) {
                creation = creation.concat(" GRANT ADMIN ROLE");
            }
            creation = creation.concat(";");
            
            try {
                basePrueba.executeUpdate(creation);
                outputArea.setText(outputArea.getText() + "\nSuccess Executing SQL");
            } catch (SQLException ex) {
                OutPane.setSelectedIndex(0);
                outputArea.setText(outputArea.getText() + "\n" + ex.toString());
                System.out.println(ex);
            }
        } else if (option == 1) {
            String creation = """
                          CREATE USER username PASSWORD 'password'
                             [FIRSTNAME 'firstname']
                             [MIDDLENAME 'middlename']
                             [LASTNAME 'lastname']
                             [GRANT ADMIN ROLE]
                          """;
            
            queryPanel.setText(queryPanel.getText() + "\n" + creation);
        }
    }//GEN-LAST:event_userCreateActionPerformed

    private void dataCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataCreateActionPerformed
        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        JTextField dataBaseAlias = new JTextField();
        JTextField hostname = new JTextField();
        
        Object[] message = {
            "Username:", username,
            "Password:", password,
            "Host name:", hostname,
            "Database Alias:", dataBaseAlias,};
        
        JOptionPane.showMessageDialog(new JPanel(new GridLayout(0, 1)), message, "Create database", JOptionPane.PLAIN_MESSAGE);
        DataBase temporal = new DataBase(((JTextField) message[5]).getText(), ((JTextField) message[7]).getText(),
                ((JTextField) message[1]).getText(), ((JTextField) message[3]).getText());
        
        try {
            temporal.create();
        } catch (SQLException e) {
            System.out.println(e);
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
    }//GEN-LAST:event_dataCreateActionPerformed

    private void newTableKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newTableKeyTyped
        DefaultTableModel model = (DefaultTableModel)newTable.getModel();
        if (evt.getKeyChar() == '\n') {
            model.setRowCount(model.getRowCount() + 1);
        }
        if (evt.getKeyChar() == 127) {
            int selectedRow = newTable.getSelectedRow();
            model.removeRow(selectedRow);
            newTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }//GEN-LAST:event_newTableKeyTyped

    private void TitleLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TitleLabel1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TitleLabel1MouseClicked

    private void CreateUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateUserButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CreateUserButtonActionPerformed

    private void CreatePasswordFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CreatePasswordFieldFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_CreatePasswordFieldFocusGained

    private void CreatePasswordFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CreatePasswordFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_CreatePasswordFieldFocusLost

    private void CreatePasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreatePasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CreatePasswordFieldActionPerformed

    private void CreatePasswordFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CreatePasswordFieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CreatePasswordFieldKeyPressed

    private void CreatePasswordFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CreatePasswordFieldKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_CreatePasswordFieldKeyTyped

    private void CreateUserScreenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CreateUserScreenMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_CreateUserScreenMouseClicked

    private void CancelCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelCreateButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CancelCreateButtonActionPerformed

    private void CancelCreateButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CancelCreateButtonMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_CancelCreateButtonMouseClicked

    private void CreateUserFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateUserFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CreateUserFieldActionPerformed

    private void HostnameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HostnameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_HostnameFieldActionPerformed

    private void DatabaseAliasFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DatabaseAliasFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DatabaseAliasFieldActionPerformed

    private void CreateTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateTableButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CreateTableButtonActionPerformed

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
    private javax.swing.JButton CancelCreateButton;
    private javax.swing.JButton CancelTableButton;
    private javax.swing.JPasswordField CreatePasswordField;
    private javax.swing.JButton CreateTableButton;
    private javax.swing.JPanel CreateTableScreen;
    private javax.swing.JButton CreateUserButton;
    private javax.swing.JTextField CreateUserField;
    private javax.swing.JPanel CreateUserScreen;
    private javax.swing.JLabel DBAliasLabel;
    private javax.swing.JTextField DatabaseAliasField;
    private javax.swing.JTree FileTree;
    private javax.swing.JTree FileTree1;
    private javax.swing.JTextField HostnameField;
    private javax.swing.JLabel HostnameLabel;
    private javax.swing.JScrollPane JTreePane;
    private javax.swing.JLayeredPane LayeredPane;
    private javax.swing.JPanel LeftPane;
    private javax.swing.JScrollPane LeftPane1;
    private javax.swing.JButton LoginButton;
    private javax.swing.JPanel LoginPanel;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JButton NewUserButton;
    private javax.swing.JTabbedPane OutPane;
    private javax.swing.JPasswordField PasswordField;
    private javax.swing.JLabel PasswordLabel;
    private javax.swing.JLabel PasswordLabel1;
    private javax.swing.JButton PathChooserButton;
    private javax.swing.JButton RefreshButton;
    private javax.swing.JPanel RightPane;
    private javax.swing.JPanel RightPane1;
    private javax.swing.JSplitPane ScriptsPanel;
    private javax.swing.JTabbedPane Sessions;
    private javax.swing.JSplitPane SplitPane;
    private javax.swing.JSplitPane SplitPane1;
    private javax.swing.JLabel SubLabel;
    private javax.swing.JLabel SubLabel1;
    private javax.swing.JTable TableInput;
    private javax.swing.JLabel TitleLabel;
    private javax.swing.JLabel TitleLabel1;
    private javax.swing.JLabel UserLabel;
    private javax.swing.JLabel UserLabel1;
    private javax.swing.JComboBox<String> UsernameField;
    private javax.swing.JButton dataCreate;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenuItem logoutMenu;
    private javax.swing.JTable newTable;
    private javax.swing.JScrollPane newTablePanel;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JTextArea queryPanel;
    private javax.swing.JMenuItem refreshMenu;
    private javax.swing.JTable resultTable;
    private javax.swing.JButton runQueryButton;
    private javax.swing.JButton tableCreate;
    private javax.swing.JButton userCreate;
    private javax.swing.JButton viewCreate;
    // End of variables declaration//GEN-END:variables
}
