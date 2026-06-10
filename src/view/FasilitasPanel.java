package view;

import controller.FasilitasController;
import model.KondisiFasilitas;
import util.KondisiFasilitasService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.time.LocalDate;
import java.util.List;

public class FasilitasPanel extends JPanel {

    private final FasilitasController controller = new FasilitasController();
    private final KondisiFasilitasService kondisiService = new KondisiFasilitasService();

    // ── Tab Kondisi ───────────────────────────────────────────────────────────
    private JTable tblKondisi;
    private DefaultTableModel mdlKondisi;
    private JTextField txtIdKamar, txtIdFasilitas, txtKet, txtDiperbarui;
    private JComboBox<String> cmbKondisi;
    private JButton btnTambahK, btnUbahK, btnHapusK, btnBersihK;
    // composite key untuk baris yang sedang dipilih
    private int selIdKamar = -1, selIdFasilitas = -1;

    private JLabel lblStatus;

    public FasilitasPanel() { build(); loadKondisi(); }

    // ── ROOT BUILD ────────────────────────────────────────────────────────────
    private void build() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        lblStatus = AppTheme.statusBar();
        add(buildKondisiPage(), BorderLayout.CENTER);
        add(lblStatus,          BorderLayout.SOUTH);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB — KONDISI FASILITAS  (CRUD lengkap)
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildKondisiPage() {
        JPanel p = new JPanel(new BorderLayout(16, 0));
        p.setOpaque(false);

        // Form card
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(270, 0));

        JPanel card = AppTheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        JLabel title = new JLabel("Form Kondisi Fasilitas");
        title.setFont(AppTheme.F_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY); title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Tambah, ubah, atau hapus kondisi");
        sub.setFont(AppTheme.F_SMALL); sub.setForeground(AppTheme.TEXT_MUTED); sub.setAlignmentX(LEFT_ALIGNMENT);

        txtIdKamar     = mkf(); txtIdFasilitas = mkf();
        txtKet         = mkf(); txtDiperbarui  = mkf();
        txtDiperbarui.setText(LocalDate.now().toString());

        cmbKondisi = new JComboBox<>(new String[]{"berfungsi", "rusak"});
        AppTheme.styleCombo(cmbKondisi);
        cmbKondisi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbKondisi.setAlignmentX(LEFT_ALIGNMENT);

        btnTambahK = AppTheme.btnPrimary("Tambah Kondisi"); szb(btnTambahK);
        btnUbahK   = AppTheme.btnSuccess("Simpan Ubahan"); szb(btnUbahK);
        btnHapusK  = AppTheme.btnDanger("Hapus Kondisi");  szb(btnHapusK);
        btnBersihK = AppTheme.btnGhost("Reset Form");      szb(btnBersihK);

        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub);   card.add(Box.createVerticalStrut(20));
        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(16));

        af(card, "ID Kamar",    txtIdKamar);
        af(card, "ID Fasilitas", txtIdFasilitas);
        af(card, "Kondisi",     cmbKondisi);
        af(card, "Keterangan Kerusakan", txtKet);
        af(card, "Terakhir Diperbarui (YYYY-MM-DD)", txtDiperbarui);

        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(14));
        card.add(btnTambahK); card.add(Box.createVerticalStrut(7));
        card.add(btnUbahK);   card.add(Box.createVerticalStrut(7));
        card.add(btnHapusK);  card.add(Box.createVerticalStrut(10));
        card.add(btnBersihK);

        JScrollPane formScroll = new JScrollPane(card);
        formScroll.setBorder(null); formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        wrap.add(formScroll, BorderLayout.CENTER);

        // Table
        JPanel right = new JPanel(new BorderLayout(0, 12)); right.setOpaque(false);

        JPanel topRow = new JPanel(new BorderLayout()); topRow.setOpaque(false);
        JLabel tTitle = new JLabel("Data Kondisi Fasilitas");
        tTitle.setFont(AppTheme.F_TITLE); tTitle.setForeground(AppTheme.TEXT_PRIMARY);
        JButton btnRefresh = AppTheme.btnGhost("Refresh");
        btnRefresh.setPreferredSize(new Dimension(110, 34));
        btnRefresh.addActionListener(e -> loadKondisi());
        topRow.add(tTitle,     BorderLayout.WEST);
        topRow.add(btnRefresh, BorderLayout.EAST);

        String[] cols = {"Kamar", "Fasilitas", "Kondisi", "Keterangan", "Diperbarui", "_idK", "_idF"};
        mdlKondisi = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKondisi = new JTable(mdlKondisi);
        tblKondisi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Badge renderer kolom Kondisi
        tblKondisi.getColumnModel().getColumn(2).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object v,
                        boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                    String s = v != null ? v.toString() : "";
                    if (!sel) switch (s.toLowerCase()) {
                        case "berfungsi": setBackground(new Color(0x0B2A1E)); setForeground(AppTheme.SUCCESS); break;
                        case "rusak":     setBackground(new Color(0x2A0B0B)); setForeground(AppTheme.DANGER); break;
                        default:          setBackground(row%2==0 ? AppTheme.BG_ELEVATED : AppTheme.BG_CARD);
                                          setForeground(AppTheme.TEXT_SUB);
                    }
                    setFont(AppTheme.F_BOLD_SM);
                    setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                    return this;
                }
            }
        );

        right.add(topRow, BorderLayout.NORTH);
        right.add(AppTheme.styledTable(tblKondisi), BorderLayout.CENTER);

        p.add(wrap,  BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);

        // Events
        tblKondisi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillKondisiFromTable();
        });
        btnTambahK.addActionListener(e -> doAddKondisi());
        btnUbahK.addActionListener(e   -> doEditKondisi());
        btnHapusK.addActionListener(e  -> doDeleteKondisi());
        btnBersihK.addActionListener(e -> clearKondisi());

        return p;
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private JTextField mkf() {
        JTextField f = AppTheme.field(16);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }
    private void szb(JButton b) { b.setAlignmentX(LEFT_ALIGNMENT); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); }
    private void af(JPanel p, String label, Component comp) {
        JLabel l = AppTheme.fieldLabel(label); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5)); p.add(comp); p.add(Box.createVerticalStrut(12));
    }

    // ── KONDISI CRUD ──────────────────────────────────────────────────────────
    private void loadKondisi() {
        new SwingWorker<List<KondisiFasilitas>, Void>() {
            @Override protected List<KondisiFasilitas> doInBackground() { return controller.getAllKondisi(); }
            @Override protected void done() {
                try {
                    List<KondisiFasilitas> list = get(); mdlKondisi.setRowCount(0);
                    for (KondisiFasilitas kf : list) {
                        mdlKondisi.addRow(new Object[]{
                            "Kamar " + kf.getIdKamar(),
                            "Fasilitas " + kf.getIdFasilitas(),
                            kf.getKondisi(),
                            kf.getKeteranganRusak(),
                            kf.getTerakhirDiperbarui(),
                            kf.getIdKamar(),       // hidden col 5
                            kf.getIdFasilitas()    // hidden col 6
                        });
                    }
                    // sembunyikan kolom ID internal
                    hideCol(5); hideCol(6);
                } catch (Exception ex) { lblStatus.setText("Gagal load kondisi: " + ex.getMessage()); }
            }
        }.execute();
    }

    private void hideCol(int col) {
        tblKondisi.getColumnModel().getColumn(col).setMinWidth(0);
        tblKondisi.getColumnModel().getColumn(col).setMaxWidth(0);
        tblKondisi.getColumnModel().getColumn(col).setPreferredWidth(0);
    }

    private void fillKondisiFromTable() {
        int row = tblKondisi.getSelectedRow(); if (row == -1) return;
        selIdKamar     = (int) mdlKondisi.getValueAt(row, 5);
        selIdFasilitas = (int) mdlKondisi.getValueAt(row, 6);
        txtIdKamar.setText(String.valueOf(selIdKamar));
        txtIdFasilitas.setText(String.valueOf(selIdFasilitas));
        cmbKondisi.setSelectedItem(mdlKondisi.getValueAt(row, 2));
        Object ket = mdlKondisi.getValueAt(row, 3);
        txtKet.setText(ket != null ? ket.toString() : "");
        Object upd = mdlKondisi.getValueAt(row, 4);
        txtDiperbarui.setText(upd != null ? upd.toString() : LocalDate.now().toString());
    }

    private KondisiFasilitas buildKondisiObj() {
        int idK = 0, idF = 0;
        try { idK = Integer.parseInt(txtIdKamar.getText().trim()); }    catch (Exception ignored) {}
        try { idF = Integer.parseInt(txtIdFasilitas.getText().trim()); } catch (Exception ignored) {}
        String diperbarui = txtDiperbarui.getText().trim().isEmpty() ? LocalDate.now().toString() : txtDiperbarui.getText().trim();
        return new KondisiFasilitas(idK, idF, (String) cmbKondisi.getSelectedItem(), txtKet.getText().trim(), diperbarui);
    }

    private boolean validateKondisiForm() {
        if (txtIdKamar.getText().trim().isEmpty())     { JOptionPane.showMessageDialog(null, "ID Kamar tidak boleh kosong!");     return false; }
        if (txtIdFasilitas.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "ID Fasilitas tidak boleh kosong!"); return false; }
        try { Integer.parseInt(txtIdKamar.getText().trim()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(null, "ID Kamar harus berupa angka!"); return false; }
        try { Integer.parseInt(txtIdFasilitas.getText().trim()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(null, "ID Fasilitas harus berupa angka!"); return false; }
        return true;
    }

    private void doAddKondisi() {
        if (!validateKondisiForm()) return;
        KondisiFasilitas kf = buildKondisiObj();
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { controller.tambahKondisi(kf); return null; }
            @Override protected void done() {
                try { get(); clearKondisi(); loadKondisi(); JOptionPane.showMessageDialog(null, "Kondisi fasilitas berhasil ditambahkan!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private void doEditKondisi() {
        if (selIdKamar == -1) {
            JOptionPane.showMessageDialog(null, "Pilih baris kondisi yang ingin diubah!");
            return;
        }
        if (!validateKondisiForm()) return;

        KondisiFasilitas kf = buildKondisiObj();

        // gunakan 3 thread paralel (multithreading PBO) untuk update
        lblStatus.setText("Mengupdate kondisi...");
        kondisiService.updateKondisiParalel(kf, () -> {
            loadKondisi();
            lblStatus.setText("Kondisi diperbarui!");
            JOptionPane.showMessageDialog(null, "Kondisi berhasil diubah!");
            clearKondisi();
        });
    }

    private void doDeleteKondisi() {
        if (selIdKamar == -1) { JOptionPane.showMessageDialog(null, "Pilih baris kondisi yang ingin dihapus!"); return; }
        if (JOptionPane.showConfirmDialog(null,
                "Yakin hapus kondisi kamar " + selIdKamar + " fasilitas " + selIdFasilitas + "?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        int idK = selIdKamar, idF = selIdFasilitas;
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { controller.hapusKondisi(idK, idF); return null; }
            @Override protected void done() {
                try { get(); clearKondisi(); loadKondisi(); JOptionPane.showMessageDialog(null, "Kondisi berhasil dihapus!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private void clearKondisi() {
        selIdKamar = -1; selIdFasilitas = -1;
        txtIdKamar.setText(""); txtIdFasilitas.setText("");
        txtKet.setText(""); txtDiperbarui.setText(LocalDate.now().toString());
        cmbKondisi.setSelectedIndex(0); tblKondisi.clearSelection();
    }
}
