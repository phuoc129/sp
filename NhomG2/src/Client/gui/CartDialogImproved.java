package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.print.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * DIALOG GIỎ HÀNG CẢI TIẾN - CÓ IN HÓA ĐƠN
 */
public class CartDialogImproved extends JDialog {
    private ClientConnection connection;
    private Map<String, Integer> cart;
    private List<Product> allProducts;
    private NumberFormat currencyFormat;
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JLabel lblItemCount;
    private double totalAmount;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(243, 156, 18);

    public CartDialogImproved(Frame parent, ClientConnection connection, Map<String, Integer> cart, 
                      List<Product> allProducts, NumberFormat currencyFormat) {
        super(parent, "🛒 Giỏ hàng của bạn", true);
        
        this.connection = connection;
        this.cart = cart;
        this.allProducts = allProducts;
        this.currencyFormat = currencyFormat;
        
        initComponents();
        loadCart();
    }

    private void initComponents() {
        setSize(900, 600);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== TOP PANEL - TITLE =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTitle = new JLabel("🛒 GIỎ HÀNG CỦA BẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);
        
        lblItemCount = new JLabel("0 sản phẩm");
        lblItemCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblItemCount.setForeground(new Color(127, 140, 141));
        
        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(lblItemCount, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL - TABLE =====
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(236, 240, 241));
        
        String[] columns = {"Sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        table.setGridColor(new Color(189, 195, 199));
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== BOTTOM PANEL =====
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 15));
        bottomPanel.setBackground(new Color(236, 240, 241));
        
        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        totalPanel.setBackground(Color.BLACK);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTotalLabel = new JLabel("TỔNG TIỀN:");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalLabel.setForeground(new Color(52, 73, 94));
        
        lblTotal = new JLabel("0 ₫");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(SUCCESS_COLOR);
        
        totalPanel.add(lblTotalLabel);
        totalPanel.add(lblTotal);
        
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        JButton btnRemove = createStyledButton("🗑️ Xóa sản phẩm", WARNING_COLOR, 150);
        btnRemove.addActionListener(e -> removeItem());
        
        JButton btnClear = createStyledButton("🧹 Xóa tất cả", DANGER_COLOR, 150);
        btnClear.addActionListener(e -> clearCart());
        
        JButton btnOrder = createStyledButton("✅ Đặt hàng", SUCCESS_COLOR, 150);
        btnOrder.addActionListener(e -> placeOrder());
        
        JButton btnClose = createStyledButton("❌ Đóng", new Color(149, 165, 166), 150);
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnOrder);
        buttonPanel.add(btnClose);
        
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color color, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, 40));
        
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

    private void loadCart() {
        tableModel.setRowCount(0);
        totalAmount = 0;
        int totalItems = 0;
        
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            
            Product product = findProduct(productId);
            
            if (product != null) {
                double subtotal = product.getPrice() * quantity;
                totalAmount += subtotal;
                totalItems += quantity;
                
                tableModel.addRow(new Object[] {
                    product.getName(),
                    currencyFormat.format(product.getPrice()),
                    quantity,
                    currencyFormat.format(subtotal)
                });
            }
        }
        
        lblTotal.setText(currencyFormat.format(totalAmount));
        lblItemCount.setText(totalItems + " sản phẩm");
    }

    private Product findProduct(String id) {
        for (Product p : allProducts) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private void removeItem() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String productName = (String) tableModel.getValueAt(selectedRow, 0);
        
        String productId = null;
        for (Product p : allProducts) {
            if (p.getName().equals(productName)) {
                productId = p.getId();
                break;
            }
        }
        
        if (productId != null) {
            cart.remove(productId);
            loadCart();
            
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng đã trống!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        }
    }

    private void clearCart() {
        if (cart.isEmpty()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa toàn bộ giỏ hàng?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            cart.clear();
            loadCart();
            JOptionPane.showMessageDialog(this, "Đã xóa toàn bộ giỏ hàng!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận đặt hàng với tổng tiền:\n" + currencyFormat.format(totalAmount) + "?", 
            "Xác nhận đặt hàng", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Order order = new Order();
            order.setTotalAmount(totalAmount);
            
            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                Product product = findProduct(entry.getKey());
                
                if (product != null) {
                    OrderItem item = new OrderItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        entry.getValue()
                    );
                    order.addItem(item);
                }
            }
            
            Response response = connection.createOrder(order);
            
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Đặt hàng thành công!\n\nBạn có muốn in hóa đơn không?", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Hỏi in hóa đơn
                int printConfirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có muốn in hóa đơn không?", 
                    "In hóa đơn", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (printConfirm == JOptionPane.YES_OPTION) {
                    printInvoice(order);
                }
                
                cart.clear();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * IN HÓA ĐƠN
     */
    private void printInvoice(Order order) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new InvoicePrintable(order));
        
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, 
                    "In hóa đơn thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi in hóa đơn: " + e.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * CLASS IN HÓA ĐƠN
     */
    private class InvoicePrintable implements Printable {
        private Order order;
        private DateTimeFormatter dateFormat;
        
        public InvoicePrintable(Order order) {
            this.order = order;
            this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        }
        
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }
            
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            
            int y = 50;
            int lineHeight = 20;
            
            // Header
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("HÓA ĐƠN BÁN HÀNG", 200, y);
            y += lineHeight * 2;
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Ngày: " + LocalDateTime.now().format(dateFormat), 50, y);
            y += lineHeight;
            g2d.drawString("Khách hàng: " + connection.getCurrentUser().getFullName(), 50, y);
            y += lineHeight;
            g2d.drawString("─".repeat(80), 50, y);
            y += lineHeight;
            
            // Table header
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Sản phẩm", 50, y);
            g2d.drawString("SL", 300, y);
            g2d.drawString("Đơn giá", 350, y);
            g2d.drawString("Thành tiền", 450, y);
            y += lineHeight;
            g2d.drawString("─".repeat(80), 50, y);
            y += lineHeight;
            
            // Items
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            for (OrderItem item : order.getItems()) {
                g2d.drawString(item.getProductName(), 50, y);
                g2d.drawString(String.valueOf(item.getQuantity()), 300, y);
                g2d.drawString(currencyFormat.format(item.getPrice()), 350, y);
                g2d.drawString(currencyFormat.format(item.getSubtotal()), 450, y);
                y += lineHeight;
            }
            
            y += lineHeight;
            g2d.drawString("─".repeat(80), 50, y);
            y += lineHeight;
            
            // Total
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("TỔNG TIỀN:", 350, y);
            g2d.drawString(currencyFormat.format(order.getTotalAmount()), 450, y);
            y += lineHeight * 2;
            
            // Footer
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString("Cảm ơn quý khách đã mua hàng!", 200, y);
            
            return PAGE_EXISTS;
        }
    }
}