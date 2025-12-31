package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import java.awt.*;

/**
 * DIALOG THÊM/SỬA SẢN PHẨM
 */
public class ProductDialog extends JDialog {
    private ClientConnection connection;
    private Product product;
    private boolean isUpdate;
    
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtCategory;
    private JTextField txtPrice;
    private JTextField txtQuantity;
    private JTextArea txtDescription;
    private JButton btnSave;
    private JButton btnCancel;

    public ProductDialog(Frame parent, ClientConnection connection, Product product) {
        super(parent, product == null ? "Thêm sản phẩm" : "Cập nhật sản phẩm", true);
        
        this.connection = connection;
        this.product = product;
        this.isUpdate = (product != null);
        
        initComponents();
        
        if (isUpdate) {
            fillData();
            txtId.setEditable(false);
        }
    }

    private void initComponents() {
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Mã sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Mã sản phẩm:"), gbc);
        
        gbc.gridx = 1;
        txtId = new JTextField(20);
        formPanel.add(txtId, gbc);

        // Tên sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        
        gbc.gridx = 1;
        txtName = new JTextField(20);
        formPanel.add(txtName, gbc);

        // Danh mục
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Danh mục:"), gbc);
        
        gbc.gridx = 1;
        txtCategory = new JTextField(20);
        formPanel.add(txtCategory, gbc);

        // Giá
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Giá:"), gbc);
        
        gbc.gridx = 1;
        txtPrice = new JTextField(20);
        formPanel.add(txtPrice, gbc);

        // Số lượng
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Số lượng:"), gbc);
        
        gbc.gridx = 1;
        txtQuantity = new JTextField(20);
        formPanel.add(txtQuantity, gbc);

        // Mô tả
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescription = new JTextArea(5, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtDescription), gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnSave = new JButton(isUpdate ? "Cập nhật" : "Thêm");
        btnSave.setFont(new Font("Arial", Font.BOLD, 13));
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(120, 35));
        btnSave.addActionListener(e -> save());
        
        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.setBackground(new Color(158, 158, 158));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(120, 35));
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void fillData() {
        txtId.setText(product.getId());
        txtName.setText(product.getName());
        txtCategory.setText(product.getCategory());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtQuantity.setText(String.valueOf(product.getQuantity()));
        txtDescription.setText(product.getDescription());
    }

    private void save() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String quantityStr = txtQuantity.getText().trim();
            String description = txtDescription.getText().trim();
            
            if (id.isEmpty() || name.isEmpty() || category.isEmpty() || 
                priceStr.isEmpty() || quantityStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);
            
            if (price <= 0 || quantity < 0) {
                JOptionPane.showMessageDialog(this, "Giá và số lượng phải >= 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Product p = new Product(id, name, category, price, quantity);
            p.setDescription(description);
            
            Response response;
            
            if (isUpdate) {
                response = connection.updateProduct(p);
            } else {
                response = connection.addProduct(p);
            }
            
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá hoặc số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}