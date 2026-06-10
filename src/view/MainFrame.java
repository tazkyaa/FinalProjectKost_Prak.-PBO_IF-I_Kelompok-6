package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MainFrame extends JFrame {

    private JPanel contentArea;
    private CardLayout cardLayout;
    private final String[] PAGES = {"Penghuni", "Kamar", "Pembayaran", "Fasilitas"};
    private final String[] ICONS = {"\uD83D\uDC64", "\uD83D\uDEAA", "\uD83D\uDCB3", "\uD83D\uDD27"};
    private int activeIndex = 0;
    private final SidebarBtn[] navBtns = new SidebarBtn[4];
    private KamarPanel kamarPanel;
    // FIX: simpan referensi label title secara langsung, bukan lewat putClientProperty
    private JLabel pageTitleLabel;

    public MainFrame() { build(); }

    private void build() {
        setTitle("KostManager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(AppTheme.BG_BASE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(AppTheme.BG_BASE);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(AppTheme.BORDER_DIM);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BorderLayout());

        // ── Logo ──
        JPanel logoPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(AppTheme.SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(220, 90));
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 16, 20));

        JLabel logoIcon = new JLabel("\uD83C\uDFE0");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel logoText = new JLabel("KostManager");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logoText.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel logoSub = new JLabel("Kost Tertata Bisnis Lancar");
        logoSub.setFont(AppTheme.F_SMALL);
        logoSub.setForeground(AppTheme.TEXT_MUTED);

        JPanel logoTextPanel = new JPanel(new BorderLayout(0, 2));
        logoTextPanel.setOpaque(false);
        logoTextPanel.add(logoText, BorderLayout.NORTH);
        logoTextPanel.add(logoSub,  BorderLayout.SOUTH);

        JPanel logoRow = new JPanel(new BorderLayout(10, 0));
        logoRow.setOpaque(false);
        logoRow.add(logoIcon,      BorderLayout.WEST);
        logoRow.add(logoTextPanel, BorderLayout.CENTER);
        logoPanel.add(logoRow, BorderLayout.CENTER);

        // ── Nav ──
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int i = 0; i < PAGES.length; i++) {
            final int idx = i;
            navBtns[i] = new SidebarBtn(ICONS[i], PAGES[i], i == activeIndex);
            navBtns[i].addActionListener(e -> switchPage(idx));
            navPanel.add(navBtns[i]);
            navPanel.add(Box.createVerticalStrut(4));
        }

        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER_DIM);
        sep.setBackground(AppTheme.SIDEBAR_BG);

        JLabel ver = new JLabel("  v1.0  •  PBO 2024");
        ver.setFont(AppTheme.F_SMALL);
        ver.setForeground(AppTheme.TEXT_MUTED);
        ver.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 0));

        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.add(sep,      BorderLayout.NORTH);
        mid.add(navPanel, BorderLayout.CENTER);

        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(mid,       BorderLayout.CENTER);
        sidebar.add(ver,       BorderLayout.SOUTH);
        return sidebar;
    }

    // ── MAIN ─────────────────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppTheme.BG_BASE);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(AppTheme.BG_SURFACE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setOpaque(false);
        topBar.setPreferredSize(new Dimension(0, 56));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        // FIX: simpan sebagai field instance, bukan putClientProperty
        pageTitleLabel = new JLabel(PAGES[0]);
        pageTitleLabel.setFont(AppTheme.F_TITLE);
        pageTitleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        topBar.add(pageTitleLabel, BorderLayout.CENTER);

        // Content
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(AppTheme.BG_BASE);

        kamarPanel = new KamarPanel();
        contentArea.add(new PenghuniPanel(),   "Penghuni");
        contentArea.add(kamarPanel,            "Kamar");
        contentArea.add(new PembayaranPanel(), "Pembayaran");
        contentArea.add(new FasilitasPanel(),  "Fasilitas");

        wrapper.add(topBar,      BorderLayout.NORTH);
        wrapper.add(contentArea, BorderLayout.CENTER);
        return wrapper;
    }

    private void switchPage(int idx) {
        activeIndex = idx;
        for (int i = 0; i < navBtns.length; i++) navBtns[i].setActive(i == idx);
        cardLayout.show(contentArea, PAGES[idx]);
        // FIX: update label langsung lewat field, bukan putClientProperty
        pageTitleLabel.setText(PAGES[idx]);
        if (idx == 1) kamarPanel.refresh();
    }

    // ── SIDEBAR BUTTON ───────────────────────────────────────────────────────
    static class SidebarBtn extends JButton {
        private boolean active;
        // FIX: hovered sebagai field instance SidebarBtn, diakses lewat SidebarBtn.this di MouseAdapter
        private boolean hovered = false;
        private final String icon, label;

        SidebarBtn(String icon, String label, boolean active) {
            super();
            this.icon = icon;
            this.label = label;
            this.active = active;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            setPreferredSize(new Dimension(196, 48));

            addMouseListener(new MouseAdapter() {
                // FIX: akses field hovered via SidebarBtn.this
                public void mouseEntered(MouseEvent e) { SidebarBtn.this.hovered = true;  repaint(); }
                public void mouseExited(MouseEvent e)  { SidebarBtn.this.hovered = false; repaint(); }
            });
        }

        void setActive(boolean a) { active = a; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            if (active) {
                g2.setColor(new Color(255, 107, 53, 28));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));
                g2.setColor(AppTheme.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, h / 4f, 3, h / 2f, 3, 3));
            } else if (hovered) {
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));
            }

            // Icon
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            g2.setColor(active ? AppTheme.PRIMARY : AppTheme.TEXT_MUTED);
            g2.drawString(icon, 14, h / 2 + 6);

            // Label
            g2.setFont(active
                ? new Font("Segoe UI", Font.BOLD,  13)
                : new Font("Segoe UI", Font.PLAIN, 13));
            g2.setColor(active ? AppTheme.TEXT_PRIMARY : AppTheme.TEXT_SUB);
            g2.drawString(label, 44, h / 2 + 5);

            g2.dispose();
        }
    }
}
