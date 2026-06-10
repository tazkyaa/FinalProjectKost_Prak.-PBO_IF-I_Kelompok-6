package view;

import dao.AdminDAO;
import model.Admin;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() { build(); }

    private void build() {
        setTitle("KostManager");
        setSize(900, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(AppTheme.BG_BASE);
        setLayout(new BorderLayout());

        // ── Split: left brand panel + right form ──────────────────────────────
        add(buildBrandPanel(), BorderLayout.WEST);
        add(buildFormPanel(),  BorderLayout.CENTER);
    }

    // ── LEFT BRAND ────────────────────────────────────────────────────────────
    private JPanel buildBrandPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // gradient
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0x1A3A55),
                    getWidth(), getHeight(), new Color(0x0A1520));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());

                // decorative circles
                g2.setColor(new Color(AppTheme.PRIMARY.getRed(), AppTheme.PRIMARY.getGreen(),
                    AppTheme.PRIMARY.getBlue(), 18));
                g2.fillOval(-60,-60,280,280);
                g2.setColor(new Color(AppTheme.SECONDARY.getRed(), AppTheme.SECONDARY.getGreen(),
                    AppTheme.SECONDARY.getBlue(), 12));
                g2.fillOval(60, getHeight()-220, 300, 300);

                // right border
                g2.setColor(AppTheme.BORDER_DIM);
                g2.fillRect(getWidth()-1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(340, 0));
        p.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Icon
        JLabel ico = new JLabel("\uD83C\uDFE0", SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        ico.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(ico);
        inner.add(Box.createVerticalStrut(20));

        JLabel t1 = new JLabel("KostManager");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 28));
        t1.setForeground(AppTheme.TEXT_PRIMARY);
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(t1);
        inner.add(Box.createVerticalStrut(8));

        JLabel t2 = new JLabel("<html>Sistem Manajemen Kost<br>Semua Urusan Kost dalam Satu Aplikasi</html>");
        t2.setFont(AppTheme.F_BODY);
        t2.setForeground(AppTheme.TEXT_SUB);
        t2.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(t2);
        inner.add(Box.createVerticalStrut(40));

        // Feature chips
        String[] features = {"Manajemen Penghuni", "Data Kamar", "Riwayat Pembayaran", "Kondisi Fasilitas"};
        for (String f : features) {
            inner.add(featureChip(f));
            inner.add(Box.createVerticalStrut(8));
        }

        p.add(inner);
        return p;
    }

    private JLabel featureChip(String text) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(AppTheme.SECONDARY.getRed(),
                    AppTheme.SECONDARY.getGreen(), AppTheme.SECONDARY.getBlue(), 20));
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setOpaque(false);
        l.setFont(AppTheme.F_LABEL);
        l.setForeground(AppTheme.SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 14));
        return l;
    }

    // ── RIGHT FORM ────────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(AppTheme.BG_BASE); g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        p.setOpaque(false);
        p.setLayout(new GridBagLayout());

        // Card
        JPanel card = AppTheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(320, 400));

        JLabel lHead = new JLabel("Selamat Datang");
        lHead.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lHead.setForeground(AppTheme.TEXT_PRIMARY);
        lHead.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lSub = new JLabel("Masuk dengan akun admin Anda");
        lSub.setFont(AppTheme.F_LABEL);
        lSub.setForeground(AppTheme.TEXT_MUTED);
        lSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = AppTheme.field(20);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = AppTheme.passField(20);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = AppTheme.btnPrimary("Masuk");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));

        card.add(lHead);
        card.add(Box.createVerticalStrut(6));
        card.add(lSub);
        card.add(Box.createVerticalStrut(32));
        card.add(AppTheme.fieldLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));
        card.add(AppTheme.fieldLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(28));
        card.add(btnLogin);

        p.add(card);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());
        return p;
    }

    private void doLogin() {
        String u = txtUsername.getText().trim();
        String pw = new String(txtPassword.getPassword());
        if (u.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        btnLogin.setText("Memverifikasi...");
        btnLogin.setEnabled(false);
        new SwingWorker<Admin,Void>() {
            @Override protected Admin doInBackground() { return new AdminDAO().findByUsernamePassword(u,pw); }
            @Override protected void done() {
                try {
                    Admin a = get();
                    if (a != null) { new MainFrame().setVisible(true); dispose(); }
                    else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                        btnLogin.setText("Masuk"); btnLogin.setEnabled(true);
                        txtPassword.setText(""); txtUsername.requestFocus();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Koneksi gagal: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    btnLogin.setText("Masuk"); btnLogin.setEnabled(true);
                }
            }
        }.execute();
    }
}
