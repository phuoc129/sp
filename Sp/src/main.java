import controller.ProductController;
import dao.ProductDAO;
import database.DatabaseConnection;
import view.ProductPanel;
import javax.swing.*;
import java.awt.*;

public class main extends JFrame {
    
    public main() {
        DatabaseConnection.initDatabase();
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ thống quản lý cửa hàng tạp hóa");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header với gradient
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(new Color(236, 240, 241));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Initialize MVC
        ProductDAO productDAO = new ProductDAO();
        ProductPanel productPanel = new ProductPanel();
        new ProductController(productDAO, productPanel);

        contentPanel.add(productPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), 
                                                     getWidth(), 0, new Color(44, 62, 80));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG TẠP HÓA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        JLabel lblSubtitle = new JLabel("Quản lý sản phẩm hiệu quả", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(new Color(236, 240, 241));
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        footerPanel.setPreferredSize(new Dimension(0, 35));
        
        JLabel lblFooter = new JLabel("© 2024 Grocery Store Management System | Phiên bản 1.0");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(189, 195, 199));
        footerPanel.add(lblFooter);
        
        return footerPanel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new main().setVisible(true);
        });
    }
}