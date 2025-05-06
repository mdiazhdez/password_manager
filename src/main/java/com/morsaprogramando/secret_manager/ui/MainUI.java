package com.morsaprogramando.secret_manager.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.time.Instant;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import com.morsaprogramando.secret_manager.controller.ServicesAndPasswords;
import com.morsaprogramando.secret_manager.models.StoredPassword;



public class MainUI {

    private JFrame frame;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private ServicesAndPasswords servicesAndPasswords;

    public MainUI() {
        frame = new JFrame("Keystore Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        showInitialMenu();
        new TrayManager(frame);
        frame.setVisible(true);
    }

    private void showInitialMenu() {
        mainPanel = new JPanel(new GridBagLayout());
        JButton openButton = new JButton("Open Keystore");
        JButton createButton = new JButton("Create Keystore");

        openButton.setPreferredSize(new Dimension(200, 50));
        createButton.setPreferredSize(new Dimension(200, 50));

        openButton.addActionListener(e -> openKeystore());
        createButton.addActionListener(e -> createKeystore());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(createButton, gbc);
        gbc.gridy = 1;
        mainPanel.add(openButton, gbc);

        frame.setContentPane(mainPanel);
        frame.revalidate();
    }

    private void openKeystore() {
    	 String defaultSecretFile = System.getProperty("defaultSecretFile");
     	if(defaultSecretFile != null && !defaultSecretFile.isEmpty()){
     		openKeystore(new File(defaultSecretFile));
     	}else{
     		 JFileChooser fileChooser = new JFileChooser();
             fileChooser.setDialogTitle("Open Keystore (.wsf)");
             fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("WSF Files", "wsf"));
             
             int result = fileChooser.showOpenDialog(frame);
             if (result == JFileChooser.APPROVE_OPTION) {
                 openKeystore(fileChooser.getSelectedFile());      
             }
     	}
     	
    }
    
    private void openKeystore(File selectedFile){
    	PasswordFieldWithToggle passwordComponent = new PasswordFieldWithToggle();

        int option = JOptionPane.showConfirmDialog(
            null,
            passwordComponent,
            "Password",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String masterPassword = passwordComponent.getPassword();
            
            if (masterPassword != null && !masterPassword.trim().isEmpty()) {
	        	try {
					servicesAndPasswords = ControllerUI.INSTANCE.open(selectedFile, masterPassword);				
					showKeystoreTable(); 
					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
	        }
            
        }
    }
    
    private void createKeystore() {
    	 // Crear el JFileChooser y establecer el modo de selección de directorios
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select folder to create a new file");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // Mostrar el diálogo de selección de carpeta
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            
            String fileName = JOptionPane.showInputDialog(frame, "File name","NewFile");

            if (fileName != null && !fileName.trim().isEmpty()) {                
                if (!fileName.endsWith(".wsf")) {
                    fileName += ".wsf";
                }
                
             // Mostrar un campo de texto para la contraseña (como en tu código anterior)
                PasswordFieldWithToggle passwordComponent = new PasswordFieldWithToggle();

                int option = JOptionPane.showConfirmDialog(
                    null,
                    passwordComponent,
                    "Password",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
                );

                if (option == JOptionPane.OK_OPTION) {
                    String masterPassword = passwordComponent.getPassword();

                    if (masterPassword != null && !masterPassword.trim().isEmpty()) {
                        try {
                            // Crear el archivo en el directorio seleccionado
                            File newFile = new File(selectedDirectory, fileName);
                            if (!newFile.exists()) {
                                boolean created = newFile.createNewFile();
                                if (created) {
                                    servicesAndPasswords = ControllerUI.INSTANCE.create(newFile, masterPassword);
                                    savePasswords();
                                    showKeystoreTable();
                                } else {
                                	JOptionPane.showMessageDialog(frame, "The file "+fileName+" could not be created", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                
            }
        }
    }

    private void showKeystoreTable() {
        String[] columns = {"Id", "Title", "Username", "Password", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Botones de acciones
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton copyPasswordButton = new JButton("View password");
        JButton updateButton = new JButton("Update password");
        JButton deleteButton = new JButton("Delete");
        //JButton saveButton = new JButton("Save");
        JButton quitButton = new JButton("Quit");

        buttonPanel.add(addButton);
        buttonPanel.add(copyPasswordButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        //buttonPanel.add(saveButton);
        buttonPanel.add(quitButton);

        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Eventos básicos
        addButton.addActionListener(e -> createPasswordAction());
        copyPasswordButton.addActionListener(e -> viewPasswordAction(table.getSelectedRow()));
        updateButton.addActionListener(e -> updatePasswordAction(table.getSelectedRow()));
        deleteButton.addActionListener(e -> deletePasswordAction(table.getSelectedRow()));
        //saveButton.addActionListener(e -> savePasswords());
        quitButton.addActionListener(e -> System.exit(0));

        loadDataTable();

        frame.setContentPane(tablePanel);
        frame.revalidate();
    }
    
    private void loadDataTable(){
    	 for (int i = 0; i < servicesAndPasswords.getPasswords().size(); i++) {
         	addEntry(i+1, servicesAndPasswords.getPasswords().get(i));
 		}
    }
    
    private void reloadDataTable(){
    	tableModel.setRowCount(0);
    	loadDataTable();
    }

    private void createPasswordAction() {
    	 AddCredentialsDialog dialog = new AddCredentialsDialog(frame);
         dialog.setVisible(true);
         
         if (dialog.isSucceeded()) {
        	 StoredPassword storedPassword = new StoredPassword(dialog.getTitleInput(), dialog.getUsername(), dialog.getPassword(), Instant.now());
        	 servicesAndPasswords.getPasswords().add(storedPassword);
        	 savePasswords();
        	 addEntry(servicesAndPasswords.getPasswords().size(), storedPassword);        	 
         }
    }
    
    
    private void viewPasswordAction(int index){
    	if(index == -1){
        	JOptionPane.showMessageDialog(frame, "select a record from the table", "Select record", JOptionPane.WARNING_MESSAGE);
    	}else{
    		String value = servicesAndPasswords.getPasswords().get(index).getPassword();
        	JOptionPane.showMessageDialog(frame, value, "View Password", JOptionPane.INFORMATION_MESSAGE);
    	}
    	
    }
    
    /*
    private void copyPasswordAction(int index){
    	if(index == -1){
        	JOptionPane.showMessageDialog(frame, "select a record from the table", "Select record", JOptionPane.WARNING_MESSAGE);
    	}else{
    		String value = servicesAndPasswords.getPasswords().get(index).getPassword();
    		StringSelection selection = new StringSelection(value);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
             
        	JOptionPane.showMessageDialog(frame, "Password copied successfully", "Copy password", JOptionPane.INFORMATION_MESSAGE);
    	}
    	 
    }*/
    
    private void updatePasswordAction(int index){
    	if(index == -1){
        	JOptionPane.showMessageDialog(frame, "select a record from the table", "Select record", JOptionPane.WARNING_MESSAGE);
    	}else{
    		 String newPassword = JOptionPane.showInputDialog(frame, "New password", "Update password", JOptionPane.QUESTION_MESSAGE);

    	        if (newPassword != null && !newPassword.trim().isEmpty()) {
    	        	StoredPassword storedPasswordOld = servicesAndPasswords.getPasswords().get(index);    	        	
    	        	if(storedPasswordOld.getPassword().equals(newPassword)){
    	        		JOptionPane.showMessageDialog(frame, "the new password is the same as the previous one", "Error", JOptionPane.ERROR_MESSAGE);
    	        		return;
    	        	}
    	        	
    	        	StoredPassword storedPassword = new StoredPassword(storedPasswordOld.getTitle(), storedPasswordOld.getUsername(),
    	        			newPassword, Instant.now());
    	        	
    	        	servicesAndPasswords.getPasswords().remove(index);
    	        	servicesAndPasswords.getPasswords().add(index, storedPassword);
    	        	savePasswords();
    	        	reloadDataTable();
    	        }
    		
    	}
    }
    
    private void deletePasswordAction(int index){
    	if(index == -1){
        	JOptionPane.showMessageDialog(frame, "select a record from the table", "Select record", JOptionPane.WARNING_MESSAGE);
    	}else{
    		
			int response = JOptionPane.showConfirmDialog(frame,
					"¿Are you sure you want to delete this item from this record?", "Confirm deletion",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

			if (response == JOptionPane.YES_OPTION) {
				servicesAndPasswords.getPasswords().remove(index);
				savePasswords();
				reloadDataTable();
				JOptionPane.showMessageDialog(frame, "Record deleted successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
        		
			}		
    	}    	 
    }

    private void addEntry(int id, StoredPassword storedPassword) {
        tableModel.addRow(mapperToTableRow(id, storedPassword));
    }
        
    private Vector<String> mapperToTableRow(int id, StoredPassword storedPassword){
    	Vector<String> row = new Vector<>();
        row.add(String.valueOf(id));
        row.add(storedPassword.getTitle());
        row.add(storedPassword.getUsername());
        row.add("******");
        row.add(storedPassword.createdAtAsString());
        
        return row;
    }
    
    private void savePasswords() {
		try {
			byte[] encryptedPasswords = servicesAndPasswords.getPasswordManagerService().encodePasswords(servicesAndPasswords.getPasswords());
			servicesAndPasswords.getFileManagerService().write(encryptedPasswords);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, "Error saving new record." + e.getMessage(), "Error Message",
					JOptionPane.ERROR_MESSAGE);
		}
	}

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(MainUI::new);
    }
    
    
    public class AddCredentialsDialog extends JDialog {

        private static final long serialVersionUID = 1L;
        private JTextField titleField;
        private JTextField usernameField;
        private JPasswordField passwordField;
        private boolean succeeded;

        public AddCredentialsDialog(Frame parent) {
            super(parent, "Add Credentials", true);
            setLayout(new BorderLayout(10, 10));

            JPanel fieldsPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Campo: Title
            gbc.gridx = 0;
            gbc.gridy = 0;
            fieldsPanel.add(new JLabel("Title:"), gbc);

            gbc.gridx = 1;
            titleField = new JTextField(15);
            fieldsPanel.add(titleField, gbc);

            // Campo: Username
            gbc.gridx = 0;
            gbc.gridy = 1;
            fieldsPanel.add(new JLabel("Username:"), gbc);

            gbc.gridx = 1;
            usernameField = new JTextField(15);
            fieldsPanel.add(usernameField, gbc);

            // Campo: Password
            gbc.gridx = 0;
            gbc.gridy = 2;
            fieldsPanel.add(new JLabel("Password:"), gbc);

            gbc.gridx = 1;
            passwordField = new JPasswordField(15);
            fieldsPanel.add(passwordField, gbc);

            // Checkbox: Mostrar contraseña (alineado a la izquierda)
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.anchor = GridBagConstraints.WEST;
            JCheckBox showPasswordCheckBox = new JCheckBox("show password");
            showPasswordCheckBox.addActionListener(e -> {
                passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '*');
            });
            fieldsPanel.add(showPasswordCheckBox, gbc);

            // Botones
            JButton okButton = new JButton("Add");
            JButton cancelButton = new JButton("Cancel");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            // Agregar paneles
            add(fieldsPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            // Listeners
            okButton.addActionListener(e -> {
                succeeded = true;
                dispose();
            });

            cancelButton.addActionListener(e -> {
                succeeded = false;
                dispose();
            });

            // Ajustes finales de la ventana
            pack();
            setPreferredSize(new Dimension(320, getPreferredSize().height));
            setMinimumSize(new Dimension(320, getPreferredSize().height));
            setLocationRelativeTo(parent);
            setResizable(false);
        }

        public String getTitleInput() {
            return titleField.getText();
        }

        public String getUsername() {
            return usernameField.getText();
        }

        public String getPassword() {
            return new String(passwordField.getPassword());
        }

        public boolean isSucceeded() {
            return succeeded;
        }
    }
	
	public class PasswordFieldWithToggle extends JPanel {
		private static final long serialVersionUID = 1L;
		private final JPasswordField passwordField;
	    private final JCheckBox toggleVisibilityCheckBox;

	    public PasswordFieldWithToggle() {
	        setLayout(new BorderLayout(5, 5));

	        // Campo de contraseña
	        passwordField = new JPasswordField(15);
	        passwordField.setEchoChar('*');
	        add(passwordField, BorderLayout.CENTER);

	        // Checkbox para mostrar/ocultar contraseña
	        toggleVisibilityCheckBox = new JCheckBox("show password");
	        toggleVisibilityCheckBox.addActionListener(e -> {
	            if (toggleVisibilityCheckBox.isSelected()) {
	                passwordField.setEchoChar((char) 0);
	            } else {
	                passwordField.setEchoChar('*');
	            }
	        });

	        add(toggleVisibilityCheckBox, BorderLayout.SOUTH);
	    }

	    public String getPassword() {
	        return new String(passwordField.getPassword());
	    }
	    
	    public void setPassword(String password) {
	        passwordField.setText(password);
	    }

	    public JPasswordField getPasswordField() {
	        return passwordField;
	    }
	}
}

