package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * GIAO DIỆN ADMIN - CẢI TIẾN
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
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color INFO_COLOR = new Color(52, 152, 219);

    public AdminMainFrame(ClientConnection connection) {
        this.connection = connection;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.currentProducts = new ArrayList<>();
        
        initComponents();
    }

    private void initComponents() {
        setTitle("Quản trị hệ thống - " + connection.getCurrentUser().getFullName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        
        tabbedPane.addTab("📦 Quản lý sản phẩm", createProductPanel());
        tabbedPane.addTab("🛍️ Quản lý đơn hàng", createOrderPanel());
        tabbedPane.addTab("📊 Thống kê", createStatisticsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    // ============================================================
    // TAB QUẢN LÝ SẢN PHẨM
    // ============================================================
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top - Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblSearch = new JLabel("🔍 Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton btnSearch = createButton("Tìm", INFO_COLOR, 100, 35);
        btnSearch.addActionListener(e -> searchProducts());
        
        JButton btnViewAll = createButton("Xem tất cả", new Color(149, 165, 166), 120, 35);
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
        tableProducts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableProducts.setRowHeight(30);
        tableProducts.setSelectionBackground(new Color(52, 152, 219, 50));
        tableProducts.setGridColor(new Color(189, 195, 199));
        
        tableProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableProducts.getTableHeader().setBackground(PRIMARY_COLOR);
        tableProducts.getTableHeader().setForeground(Color.WHITE);
        tableProducts.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableProducts.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableProducts.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(tableProducts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Right - Actions
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 12, 15, 12)
        ));
        rightPanel.setPreferredSize(new Dimension(190, 0));
        
        JButton btnAdd = createActionButton("➕ Thêm SP", SUCCESS_COLOR);
        btnAdd.addActionListener(e -> addProduct());
        
        JButton btnUpdate = createActionButton("✏️ Cập nhật", INFO_COLOR);
        btnUpdate.addActionListener(e -> updateProduct());
        
        JButton btnDelete = createActionButton("🗑️ Xóa SP", DANGER_COLOR);
        btnDelete.addActionListener(e -> deleteProduct());
        
        JButton btnRefresh = createActionButton("🔄 Làm mới", new Color(149, 165, 166));
        btnRefresh.addActionListener(e -> loadProducts());
        
        JButton btnLogout = createActionButton("🚪 Đăng xuất", new Color(231, 76, 60));
        btnLogout.addActionListener(e -> logout());
        
        rightPanel.add(btnAdd);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnUpdate);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnDelete);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
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
            showError(response.getMessage());
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
                p.getDescription() != null ? p.getDescription() : ""
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
            showWarning("Vui lòng chọn sản phẩm!");
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
            showWarning("Vui lòng chọn sản phẩm!");
            return;
        }
        
        Product product = currentProducts.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa sản phẩm:\n" + product.getName() + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Response response = connection.deleteProduct(product.getId());
            
            if (response.isSuccess()) {
                showSuccess("Xóa thành công!");
                loadProducts();
            } else {
                showError(response.getMessage());
            }
        }
    }

    // ============================================================
    // TAB QUẢN LÝ ĐƠN HÀNG
    // ============================================================
    @SuppressWarnings("unchecked")
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JButton btnLoad = createButton("📥 Tải đơn hàng", PRIMARY_COLOR, 150, 40);
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnLoad.addActionListener(e -> {
            Response response = connection.getAllOrders();
            
            if (response.isSuccess()) {
                List<Order> orders = (List<Order>) response.getData().get("orders");
                
                StringBuilder sb = new StringBuilder();
                sb.append("╔══════════════════════════════════════════════════════════════╗\n");
                sb.append("║              DANH SÁCH TẤT CẢ ĐƠN HÀNG                      ║\n");
                sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");
                
                if (orders.isEmpty()) {
                    sb.append("          Chưa có đơn hàng nào.\n");
                } else {
                    for (Order order : orders) {
                        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                        sb.append(String.format("  📦 Đơn hàng #%-5d | 👤 %-15s\n", 
                            order.getId(), order.getUsername()));
                        sb.append(String.format("  📅 %s | 📊 %s\n",
                            order.getOrderDate().format(dateFormat), order.getStatus()));
                        sb.append("  ─────────────────────────────────────────────────────\n");
                        
                        for (OrderItem item : order.getItems()) {
                            sb.append(String.format("    • %-30s x%-3d = %12s\n",
                                item.getProductName(),
                                item.getQuantity(),
                                currencyFormat.format(item.getSubtotal())));
                        }
                        
                        sb.append("  ─────────────────────────────────────────────────────\n");
                        sb.append(String.format("  💰 TỔNG TIỀN: %36s\n", 
                            currencyFormat.format(order.getTotalAmount())));
                        sb.append("\n");
                    }
                    
                    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    sb.append(String.format("  📊 Tổng số đơn hàng: %d\n", orders.size()));
                }
                
                textArea.setText(sb.toString());
                textArea.setCaretPosition(0);
            } else {
                showError(response.getMessage());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        panel.add(btnLoad, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    // ============================================================
    // TAB THỐNG KÊ
    // ============================================================
    @SuppressWarnings("unchecked")
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JButton btnLoad = createButton("📊 Tải thống kê", PRIMARY_COLOR, 150, 40);
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        
        btnLoad.addActionListener(e -> {
            Response response = connection.getStatistics();
            
            if (response.isSuccess()) {
                Map<String, Object> stats = (Map<String, Object>) response.getData().get("statistics");
                
                statsPanel.removeAll();
                statsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
                
                JLabel lblTitle = new JLabel("📊 THỐNG KÊ HỆ THỐNG");
                lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
                lblTitle.setForeground(PRIMARY_COLOR);
                lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                statsPanel.add(lblTitle);
                
                statsPanel.add(Box.createRigidArea(new Dimension(0, 50)));
                
                addStatCard(statsPanel, "📦 Tổng số sản phẩm (MySQL)", 
                    stats.get("totalProducts").toString(), INFO_COLOR);
                addStatCard(statsPanel, "🛍️ Tổng số đơn hàng (JSON)", 
                    stats.get("totalOrders").toString(), WARNING_COLOR);
                addStatCard(statsPanel, "💰 Tổng doanh thu", 
                    currencyFormat.format(stats.get("totalRevenue")), SUCCESS_COLOR);
                
                statsPanel.revalidate();
                statsPanel.repaint();
            } else {
                showError(response.getMessage());
            }
        });
        
        panel.add(btnLoad, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void addStatCard(JPanel panel, String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        card.setMaximumSize(new Dimension(600, 100));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(color);
        
        card.add(lblLabel, BorderLayout.WEST);
        card.add(lblValue, BorderLayout.EAST);
        
        panel.add(card);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    // ============================================================
    // HELPER
    // ============================================================
    private JButton createButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(170, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn đăng xuất?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            connection.logout();
            connection.disconnect();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }
}