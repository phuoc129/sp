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
 * GIAO DIỆN NGƯỜI DÙNG
 */
public class UserMainFrame extends JFrame {
    private ClientConnection connection;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    private JTable tableProducts;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnViewAll;
    private JButton btnAddToCart;
    private JButton btnViewCart;
    private JButton btnMyOrders;
    private JButton btnLogout;
    
    private List<Product> currentProducts;
    private Map<String, Integer> cart; // productId -> quantity

    public UserMainFrame(ClientConnection connection) {
        this.connection = connection;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.currentProducts = new ArrayList<>();
        this.cart = new HashMap<>();
        
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setTitle("Hệ thống bán hàng - " + connection.getCurrentUser().getFullName());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        
        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnSearch = new JButton("Tìm");
        btnSearch.setFont(new Font("Arial", Font.BOLD, 12));
        btnSearch.addActionListener(e -> searchProducts());
        
        btnViewAll = new JButton("Xem tất cả");
        btnViewAll.setFont(new Font("Arial", Font.BOLD, 12));
        btnViewAll.addActionListener(e -> loadProducts());
        
        topPanel.add(lblSearch);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnViewAll);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center - Table
        String[] columns = {"Mã SP", "Tên sản phẩm", "Danh mục", "Giá", "Số lượng"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProducts = new JTable(tableModel);
        tableProducts.setFont(new Font("Arial", Font.PLAIN, 13));
        tableProducts.setRowHeight(25);
        tableProducts.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tableProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tableProducts);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Right Panel - Actions
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.setPreferredSize(new Dimension(200, 0));
        
        btnAddToCart = createButton("Thêm vào giỏ", new Color(33, 150, 243));
        btnAddToCart.addActionListener(e -> addToCart());
        
        btnViewCart = createButton("Xem giỏ hàng", new Color(76, 175, 80));
        btnViewCart.addActionListener(e -> viewCart());
        
        btnMyOrders = createButton("Đơn hàng của tôi", new Color(255, 152, 0));
        btnMyOrders.addActionListener(e -> viewMyOrders());
        
        btnLogout = createButton("Đăng xuất", new Color(244, 67, 54));
        btnLogout.addActionListener(e -> logout());
        
        rightPanel.add(btnAddToCart);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(btnViewCart);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(btnMyOrders);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(btnLogout);
        
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
        
        // Enter to search
        txtSearch.addActionListener(e -> searchProducts());
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    @SuppressWarnings("unchecked")
    private void loadProducts() {
        Response response = connection.getAllProducts();
        
        if (response.isSuccess()) {
            currentProducts = (List<Product>) response.getData().get("products");
            updateTable(currentProducts);
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
            updateTable(currentProducts);
            
            if (currentProducts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Product> products) {
        tableModel.setRowCount(0);
        
        for (Product p : products) {
            tableModel.addRow(new Object[] {
                p.getId(),
                p.getName(),
                p.getCategory(),
                currencyFormat.format(p.getPrice()),
                p.getQuantity()
            });
        }
    }

    private void addToCart() {
        int selectedRow = tableProducts.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Product product = currentProducts.get(selectedRow);
        
        if (product.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm đã hết hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String input = JOptionPane.showInputDialog(this, "Nhập số lượng:", "1");
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            int quantity = Integer.parseInt(input.trim());
            
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải > 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int currentInCart = cart.getOrDefault(product.getId(), 0);
            
            if (currentInCart + quantity > product.getQuantity()) {
                JOptionPane.showMessageDialog(this, "Không đủ hàng! Chỉ còn " + product.getQuantity(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            cart.put(product.getId(), currentInCart + quantity);
            
            JOptionPane.showMessageDialog(this, "Đã thêm vào giỏ hàng!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        new CartDialog(this, connection, cart, currentProducts, currencyFormat).setVisible(true);
        
        // Clear cart after order
        if (cart.isEmpty()) {
            loadProducts();
        }
    }

    @SuppressWarnings("unchecked")
    private void viewMyOrders() {
        Response response = connection.getUserOrders();
        
        if (response.isSuccess()) {
            List<Order> orders = (List<Order>) response.getData().get("orders");
            new OrdersDialog(this, orders, currencyFormat, dateFormat).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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