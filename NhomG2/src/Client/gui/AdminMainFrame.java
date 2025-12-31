package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * GIAO DIỆN ADMIN
 */
public class AdminMainFrame extends JFrame {
    private ClientConnection connection;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    private JTabbedPane tabbedPane;
    private JTable tableProducts;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private List<Product> currentProducts;

    public AdminMainFrame(ClientConnection connection) {
        this.connection = connection;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.currentProducts = new ArrayList<>();
        
        initComponents();
    }

    private void initComponents() {
        setTitle("Quản trị hệ thống - " + connection.getCurrentUser().getFullName());
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        
        tabbedPane.addTab("Quản lý sản phẩm", createProductPanel());
        tabbedPane.addTab("Quản lý đơn hàng", createOrderPanel());
        tabbedPane.addTab("Thống kê", createStatisticsPanel());
        
        add(tabbedPane);
    }

    // ============================================================
    // TAB QUẢN LÝ SẢN PHẨM
    // ============================================================
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top - Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        txtSearch = new JTextField(30);
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> searchProducts());
        
        JButton btnViewAll = new JButton("Xem tất cả");
        btnViewAll.addActionListener(e -> loadProducts());
        
        topPanel.add(lblSearch);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnViewAll);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Center - Table
        String[] columns = {"Mã SP", "Tên", "Danh mục", "Giá", "Số lượng", "Mô tả"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProducts = new JTable(tableModel);
        tableProducts.setFont(new Font("Arial", Font.PLAIN, 12));
        tableProducts.setRowHeight(25);
        tableProducts.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tableProducts);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Right - Actions
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.setPreferredSize(new Dimension(180, 0));
        
        JButton btnAdd = createButton("Thêm SP", new Color(76, 175, 80));
        btnAdd.addActionListener(e -> addProduct());
        
        JButton btnUpdate = createButton("Cập nhật SP", new Color(33, 150, 243));
        btnUpdate.addActionListener(e -> updateProduct());
        
        JButton btnDelete = createButton("Xóa SP", new Color(244, 67, 54));
        btnDelete.addActionListener(e -> deleteProduct());
        
        JButton btnRefresh = createButton("Làm mới", new Color(158, 158, 158));
        btnRefresh.addActionListener(e -> loadProducts());
        
        JButton btnLogout = createButton("Đăng xuất", new Color(96, 125, 139));
        btnLogout.addActionListener(e -> logout());
        
        rightPanel.add(btnAdd);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(btnUpdate);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(btnDelete);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(btnRefresh);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(btnLogout);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        loadProducts();
        
        return panel;
    }

    @SuppressWarnings("unchecked")
    private void loadProducts() {
        Response response = connection.getAllProducts();
        
        if (response.isSuccess()) {
            currentProducts = (List<Product>) response.getData().get("products");
            updateProductTable(currentProducts);
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void searchProducts() {
        String keyword = txtSearch.getText().trim();
        
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }
        
        Response response = connection.searchProducts(keyword);
        
        if (response.isSuccess()) {
            currentProducts = (List<Product>) response.getData().get("products");
            updateProductTable(currentProducts);
        }
    }

    private void updateProductTable(List<Product> products) {
        tableModel.setRowCount(0);
        
        for (Product p : products) {
            tableModel.addRow(new Object[] {
                p.getId(),
                p.getName(),
                p.getCategory(),
                currencyFormat.format(p.getPrice()),
                p.getQuantity(),
                p.getDescription()
            });
        }
    }

    private void addProduct() {
        ProductDialog dialog = new ProductDialog(this, connection, null);
        dialog.setVisible(true);
        loadProducts();
    }

    private void updateProduct() {
        int selectedRow = tableProducts.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Product product = currentProducts.get(selectedRow);
        ProductDialog dialog = new ProductDialog(this, connection, product);
        dialog.setVisible(true);
        loadProducts();
    }

    private void deleteProduct() {
        int selectedRow = tableProducts.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Product product = currentProducts.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa sản phẩm: " + product.getName() + "?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Response response = connection.deleteProduct(product.getId());
            
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============================================================
    // TAB QUẢN LÝ ĐỢN HÀNG
    // ============================================================
    @SuppressWarnings("unchecked")
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnLoad = new JButton("Tải đơn hàng");
        btnLoad.setFont(new Font("Arial", Font.BOLD, 13));
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        btnLoad.addActionListener(e -> {
            Response response = connection.getAllOrders();
            
            if (response.isSuccess()) {
                List<Order> orders = (List<Order>) response.getData().get("orders");
                
                StringBuilder sb = new StringBuilder();
                sb.append("=== DANH SÁCH ĐƠN HÀNG ===\n\n");
                
                if (orders.isEmpty()) {
                    sb.append("Chưa có đơn hàng nào.\n");
                } else {
                    for (Order order : orders) {
                        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                        sb.append("Đơn hàng #").append(order.getId()).append("\n");
                        sb.append("Khách hàng: ").append(order.getUsername()).append("\n");
                        sb.append("Ngày: ").append(order.getOrderDate().format(dateFormat)).append("\n");
                        sb.append("Trạng thái: ").append(order.getStatus()).append("\n");
                        sb.append("\nChi tiết:\n");
                        
                        for (OrderItem item : order.getItems()) {
                            sb.append("  • ").append(item.getProductName())
                              .append(" x").append(item.getQuantity())
                              .append(" = ").append(currencyFormat.format(item.getSubtotal()))
                              .append("\n");
                        }
                        
                        sb.append("\nTổng tiền: ").append(currencyFormat.format(order.getTotalAmount())).append("\n\n");
                    }
                    
                    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    sb.append("Tổng số đơn hàng: ").append(orders.size()).append("\n");
                }
                
                textArea.setText(sb.toString());
                textArea.setCaretPosition(0);
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(btnLoad, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        return panel;
    }

    // ============================================================
    // TAB THỐNG KÊ
    // ============================================================
    @SuppressWarnings("unchecked")
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnLoad = new JButton("Tải thống kê");
        btnLoad.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        
        btnLoad.addActionListener(e -> {
            Response response = connection.getStatistics();
            
            if (response.isSuccess()) {
                Map<String, Object> stats = (Map<String, Object>) response.getData().get("statistics");
                
                statsPanel.removeAll();
                
                statsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                JLabel lblTitle = new JLabel("THỐNG KÊ HỆ THỐNG");
                lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
                lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                statsPanel.add(lblTitle);
                
                statsPanel.add(Box.createRigidArea(new Dimension(0, 40)));
                
                addStatItem(statsPanel, "Tổng số sản phẩm (MySQL):", stats.get("totalProducts").toString());
                addStatItem(statsPanel, "Tổng số đơn hàng (JSON):", stats.get("totalOrders").toString());
                addStatItem(statsPanel, "Tổng doanh thu:", currencyFormat.format(stats.get("totalRevenue")));
                
                statsPanel.revalidate();
                statsPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(btnLoad, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void addStatItem(JPanel panel, String label, String value) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        lblValue.setForeground(new Color(33, 150, 243));
        
        itemPanel.add(lblLabel);
        itemPanel.add(lblValue);
        
        panel.add(itemPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    // ============================================================
    // HELPER
    // ============================================================
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(160, 35));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            connection.logout();
            connection.disconnect();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}