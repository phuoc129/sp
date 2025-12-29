package view;

import model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtCategory, txtPrice, txtQuantity, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;
    
    public ProductPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 240, 241));

        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(380, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel lblFormTitle = new JLabel("📝 THÔNG TIN SẢN PHẨM");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(44, 62, 80));
        titlePanel.add(lblFormTitle);
        leftPanel.add(titlePanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        // Mã sản phẩm
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createLabel("Mã sản phẩm:", "🔖"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtId = createStyledTextField();
        formPanel.add(txtId, gbc);

        // Tên sản phẩm
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(createLabel("Tên sản phẩm:", "📦"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtName = createStyledTextField();
        formPanel.add(txtName, gbc);

        // Danh mục
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(createLabel("Danh mục:", "📁"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtCategory = createStyledTextField();
        formPanel.add(txtCategory, gbc);

        // Giá bán
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(createLabel("Giá bán (VNĐ):", "💰"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtPrice = createStyledTextField();
        formPanel.add(txtPrice, gbc);

        // Số lượng
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formPanel.add(createLabel("Số lượng:", "📊"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtQuantity = createStyledTextField();
        formPanel.add(txtQuantity, gbc);

        leftPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnAdd = createStyledButton("➕ Thêm", new Color(46, 204, 113), new Color(39, 174, 96));
        btnUpdate = createStyledButton("✏️ Sửa", new Color(52, 152, 219), new Color(41, 128, 185));
        btnDelete = createStyledButton("🗑️ Xóa", new Color(231, 76, 60), new Color(192, 57, 43));
        btnClear = createStyledButton("🔄 Làm mới", new Color(149, 165, 166), new Color(127, 140, 141));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        return leftPanel;
    }

    private JPanel createTablePanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel lblSearchIcon = new JLabel("🔍");
        lblSearchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchPanel.add(lblSearchIcon);
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchPanel.add(lblSearch);
        
        txtSearch = createStyledTextField();
        txtSearch.setPreferredSize(new Dimension(250, 32));
        searchPanel.add(txtSearch);
        
        btnSearch = createSmallButton("Tìm", new Color(241, 196, 15), new Color(243, 156, 18));
        searchPanel.add(btnSearch);
        
        btnRefresh = createSmallButton("Làm mới", new Color(52, 152, 219), new Color(41, 128, 185));
        searchPanel.add(btnRefresh);

        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã SP", "Tên sản phẩm", "Danh mục", "Giá bán (VNĐ)", "Số lượng"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        productTable = new JTable(tableModel);
        productTable.setRowHeight(35);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        productTable.setSelectionBackground(new Color(52, 152, 219));
        productTable.setSelectionForeground(Color.WHITE);
        productTable.setGridColor(new Color(189, 195, 199));
        productTable.setShowGrid(true);
        productTable.setIntercellSpacing(new Dimension(1, 1));

        // Header styling
        JTableHeader header = productTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        productTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        productTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        productTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Format price column
        DefaultTableCellRenderer priceRenderer = new DefaultTableCellRenderer() {
            private NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double || value instanceof Float) {
                    value = formatter.format(value);
                }
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        };
        productTable.getColumnModel().getColumn(3).setCellRenderer(priceRenderer);

        // Set column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setBackground(Color.WHITE);
        JLabel lblStats = new JLabel("📈 Tổng số sản phẩm: 0");
        lblStats.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStats.setForeground(new Color(52, 73, 94));
        statsPanel.add(lblStats);
        rightPanel.add(statsPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JLabel createLabel(String text, String icon) {
        JLabel label = new JLabel(icon + " " + text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 45));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private JButton createSmallButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 32));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    // Getter methods
    public JTable getProductTable() { return productTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtName() { return txtName; }
    public JTextField getTxtCategory() { return txtCategory; }
    public JTextField getTxtPrice() { return txtPrice; }
    public JTextField getTxtQuantity() { return txtQuantity; }
    public JTextField getTxtSearch() { return txtSearch; }
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear() { return btnClear; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnRefresh() { return btnRefresh; }

    public void displayProducts(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(p.toTableRow());
        }
        updateStats(products.size());
    }

    public void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtCategory.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtId.setEditable(true);
        productTable.clearSelection();
    }

    public void fillForm(Product product) {
        txtId.setText(product.getId());
        txtName.setText(product.getName());
        txtCategory.setText(product.getCategory());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtQuantity.setText(String.valueOf(product.getQuantity()));
        txtId.setEditable(false);
    }

    private void updateStats(int count) {
        Component[] components = ((JPanel)getComponent(1)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getLayout() instanceof FlowLayout) {
                    Component[] labels = panel.getComponents();
                    for (Component label : labels) {
                        if (label instanceof JLabel) {
                            ((JLabel)label).setText("📈 Tổng số sản phẩm: " + count);
                        }
                    }
                }
            }
        }
    }
}