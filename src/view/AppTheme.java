package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class AppTheme {

    // ── PALETTE ──────────────────────────────────────────────────────────────
    public static final Color BG_BASE      = new Color(0x0F1923);
    public static final Color BG_SURFACE   = new Color(0x172332);
    public static final Color BG_ELEVATED  = new Color(0x1F3044);
    public static final Color BG_CARD      = new Color(0x243850);
    public static final Color BG_INPUT     = new Color(0x172332);
    public static final Color SIDEBAR_BG   = new Color(0x0C1520);

    public static final Color PRIMARY      = new Color(0xFF6B35);
    public static final Color PRIMARY_DIM  = new Color(0xFF6B35, false);
    public static final Color SECONDARY    = new Color(0x00D4AA);
    public static final Color DANGER       = new Color(0xFF4757);
    public static final Color WARN         = new Color(0xFFB800);
    public static final Color SUCCESS      = new Color(0x2ED573);

    public static final Color TEXT_PRIMARY = new Color(0xF1F5F9);
    public static final Color TEXT_SUB     = new Color(0x94A3B8);
    public static final Color TEXT_MUTED   = new Color(0x4A6080);
    public static final Color BORDER_DIM   = new Color(0x1E3448);
    public static final Color BORDER       = new Color(0x2A4560);

    // ── FONTS ────────────────────────────────────────────────────────────────
    public static final Font F_DISPLAY = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font F_HEADING = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font F_BOLD_SM = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font F_MONO    = new Font("Consolas",  Font.PLAIN, 12);

    // ── BUTTON ───────────────────────────────────────────────────────────────
    // FIX: hov/press sebagai boolean[] agar bisa diakses dari anonymous inner class
    public static JButton btn(String text, Color bg, Color bgHover, Color fg) {
        final boolean[] state = {false, false}; // [0]=hovered, [1]=pressed

        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = state[1] ? bg.darker() : state[0] ? bgHover : bg;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(F_BOLD_SM);
        btn.setForeground(fg);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e)  { state[0]=true;  btn.repaint(); }
            public void mouseExited(java.awt.event.MouseEvent e)   { state[0]=false; state[1]=false; btn.repaint(); }
            public void mousePressed(java.awt.event.MouseEvent e)  { state[1]=true;  btn.repaint(); }
            public void mouseReleased(java.awt.event.MouseEvent e) { state[1]=false; btn.repaint(); }
        });
        return btn;
    }

    public static JButton btnPrimary(String t)   { return btn(t, PRIMARY,              PRIMARY.brighter(), Color.WHITE); }
    public static JButton btnSuccess(String t)   { return btn(t, new Color(0x0E6B55),  SECONDARY,          Color.WHITE); }
    public static JButton btnDanger(String t)    { return btn(t, new Color(0x5C1C1C),  DANGER,             new Color(0xFFAAAA)); }
    public static JButton btnGhost(String t)     { return btn(t, BG_ELEVATED,          BORDER,             TEXT_SUB); }

    public static void sizeBtn(JButton b, int w, int h) { b.setPreferredSize(new Dimension(w, h)); }

    // ── TEXT FIELD ───────────────────────────────────────────────────────────
    public static JTextField field(int cols) {
        JTextField f = new JTextField(cols);
        styleText(f);
        return f;
    }

    public static JPasswordField passField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        styleText(f);
        return f;
    }

    private static void styleText(javax.swing.text.JTextComponent f) {
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(PRIMARY);
        f.setFont(F_BODY);
        f.setSelectionColor(new Color(255, 107, 53, 80));
        // FIX: fully qualified AppTheme.LineBorderRound agar FocusListener bisa resolve
        f.setBorder(BorderFactory.createCompoundBorder(
            new AppTheme.LineBorderRound(BORDER_DIM, 1, 8),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 38));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new AppTheme.LineBorderRound(PRIMARY, 1, 8),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new AppTheme.LineBorderRound(BORDER_DIM, 1, 8),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    // ── COMBO ────────────────────────────────────────────────────────────────
    public static <E> void styleCombo(JComboBox<E> cb) {
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(F_BODY);
        cb.setBorder(new AppTheme.LineBorderRound(BORDER_DIM, 1, 8));
        cb.setPreferredSize(new Dimension(cb.getPreferredSize().width, 38));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                setBackground(sel ? PRIMARY : BG_INPUT);
                setForeground(sel ? Color.WHITE : TEXT_PRIMARY);
                setFont(F_BODY);
                setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                return this;
            }
        });
    }

    // ── TABLE ────────────────────────────────────────────────────────────────
    // FIX: tambah import JTableHeader secara eksplisit di atas, dan cast agar resolve
    public static JScrollPane styledTable(JTable t) {
        t.setBackground(BG_ELEVATED);
        t.setForeground(TEXT_PRIMARY);
        t.setFont(F_BODY);
        t.setRowHeight(36);
        t.setGridColor(BORDER_DIM);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setSelectionBackground(new Color(255, 107, 53, 64));
        t.setSelectionForeground(TEXT_PRIMARY);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);

        JTableHeader h = t.getTableHeader();
        h.setBackground(BG_CARD);
        h.setForeground(PRIMARY);
        h.setFont(F_BOLD_SM);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
        h.setPreferredSize(new Dimension(0, 40));
        h.setReorderingAllowed(false);

        t.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (sel) {
                    setBackground(new Color(255, 107, 53, 64));
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? BG_ELEVATED : BG_CARD);
                    setForeground(TEXT_SUB);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                setFont(F_BODY);
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(new AppTheme.LineBorderRound(BORDER_DIM, 1, 12));
        sp.getViewport().setBackground(BG_ELEVATED);
        sp.setBackground(BG_ELEVATED);
        JScrollBar vsb = sp.getVerticalScrollBar();
        vsb.setBackground(BG_ELEVATED);
        vsb.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = BORDER;
                trackColor = BG_ELEVATED;
            }
            @Override protected JButton createDecreaseButton(int o) { return zero(); }
            @Override protected JButton createIncreaseButton(int o) { return zero(); }
            private JButton zero() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });
        return sp;
    }

    // ── LABELS ───────────────────────────────────────────────────────────────
    public static JLabel sectionLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_TITLE);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static JLabel fieldLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_LABEL);
        l.setForeground(TEXT_SUB);
        return l;
    }

    public static JLabel statusBar() {
        JLabel l = new JLabel("  Siap");
        l.setFont(F_SMALL);
        l.setForeground(TEXT_MUTED);
        l.setBackground(BG_BASE);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_DIM));
        l.setPreferredSize(new Dimension(0, 26));
        return l;
    }

    // ── CARD ─────────────────────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    // ── SEPARATOR ────────────────────────────────────────────────────────────
    public static JSeparator sep() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER_DIM);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    // ── ROUND BORDER ─────────────────────────────────────────────────────────
    public static class LineBorderRound extends AbstractBorder {
        private final Color c;
        private final int thickness;
        private final int radius;

        public LineBorderRound(Color c, int thickness, int radius) {
            this.c = c;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(
                x + thickness / 2f, y + thickness / 2f,
                w - thickness, h - thickness,
                radius, radius));
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c) {
            return new Insets(radius / 3, radius / 3, radius / 3, radius / 3);
        }

        @Override public Insets getBorderInsets(Component c, Insets i) {
            i.set(radius / 3, radius / 3, radius / 3, radius / 3);
            return i;
        }
    }
}
