package view;

import controller.PenghuniController;
import controller.KamarController;
import model.Penghuni;
import model.Kamar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PenghuniPanel extends JPanel {

    private final PenghuniController controller = new PenghuniController();
    private final KamarController kamarController = new KamarController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtNoHp, txtAsal, txtTglMasuk, txtNamaOrtu, txtNoHpOrtu, txtCari;
    private JComboBox<Kamar> cmbKamar;
    private JComboBox<String> cmbStatus;
    private JButton btnTambah, btnUbah, btnHapus, btnBersih, btnCari;
    private JLabel lblStatus;
    private int selectedId = -1;

    public PenghuniPanel() { build(); loadData(); }

    private void build() {
        setLayout(new BorderLayout(0,0));
        setBackground(AppTheme.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(20,20,0,20));

        add(buildForm(),  BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);

        lblStatus = AppTheme.statusBar();
        add(lblStatus, BorderLayout.SOUTH);

        loadKamarCombo();
        btnTambah.addActionListener(e -> doAdd());
        btnUbah.addActionListener(e   -> doEdit());
        btnHapus.addActionListener(e  -> doDelete());
        btnBersih.addActionListener(e -> clearForm());
        btnCari.addActionListener(e   -> doSearch());
        txtCari.addActionListener(e   -> doSearch());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFromTable();
        });
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(280, 0));
        wrap.setBorder(BorderFactory.createEmptyBorder(0,0,0,16));

        JPanel card = AppTheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24,20,24,20));

        JLabel title = new JLabel("Data Penghuni");
        title.setFont(AppTheme.F_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY); title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Kelola informasi penghuni kos");
        sub.setFont(AppTheme.F_SMALL); sub.setForeground(AppTheme.TEXT_MUTED); sub.setAlignmentX(LEFT_ALIGNMENT);

        txtNama     = mkField(); txtNoHp     = mkField(); txtAsal     = mkField();
        txtTglMasuk = mkField(); txtNamaOrtu = mkField(); txtNoHpOrtu = mkField();
        cmbKamar = new JComboBox<>(); AppTheme.styleCombo(cmbKamar);
        cmbKamar.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); cmbKamar.setAlignmentX(LEFT_ALIGNMENT);
        cmbStatus = new JComboBox<>(new String[]{"aktif","keluar"}); AppTheme.styleCombo(cmbStatus);
        cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); cmbStatus.setAlignmentX(LEFT_ALIGNMENT);

        btnTambah = AppTheme.btnPrimary("+ Tambah");    sizeBtn(btnTambah);
        btnUbah   = AppTheme.btnSuccess("✎  Simpan");   sizeBtn(btnUbah);
        btnHapus  = AppTheme.btnDanger("✕  Hapus");     sizeBtn(btnHapus);
        btnBersih = AppTheme.btnGhost("↺  Reset");      sizeBtn(btnBersih);

        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub);   card.add(Box.createVerticalStrut(20));
        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(16));

        addField(card,"Nama Lengkap",txtNama);
        addField(card,"No. HP",txtNoHp);
        addField(card,"Kamar",cmbKamar);
        addField(card,"Asal Daerah",txtAsal);
        addField(card,"Tgl Masuk (YYYY-MM-DD)",txtTglMasuk);
        addField(card,"Status",cmbStatus);
        addField(card,"Nama Orang Tua",txtNamaOrtu);
        addField(card,"No. HP Orang Tua",txtNoHpOrtu);

        card.add(Box.createVerticalStrut(8));
        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(14));
        card.add(btnTambah); card.add(Box.createVerticalStrut(7));
        card.add(btnUbah);   card.add(Box.createVerticalStrut(7));
        card.add(btnHapus);  card.add(Box.createVerticalStrut(10));
        card.add(btnBersih);

        // Scrollable form
        JScrollPane sp = new JScrollPane(card);
        sp.setBorder(null); sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));

        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    private JTextField mkField() {
        JTextField f = AppTheme.field(18);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }
    private void sizeBtn(JButton b) { b.setAlignmentX(LEFT_ALIGNMENT); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); }
    private void addField(JPanel p, String label, Component comp) {
        JLabel l = AppTheme.fieldLabel(label); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5)); p.add(comp); p.add(Box.createVerticalStrut(12));
    }

    private JPanel buildTable() {
        JPanel p = new JPanel(new BorderLayout(0,12)); p.setOpaque(false);

        // Top bar: title + search
        JPanel top = new JPanel(new BorderLayout(12,0)); top.setOpaque(false);
        JLabel title = new JLabel("Daftar Penghuni");
        title.setFont(AppTheme.F_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY);

        // Search row
        JPanel searchRow = new JPanel(new BorderLayout(8,0)); searchRow.setOpaque(false);
        txtCari = AppTheme.field(18); txtCari.setPreferredSize(new Dimension(200,36));
        btnCari = AppTheme.btnGhost("🔍");  AppTheme.sizeBtn(btnCari,40,36);
        searchRow.add(txtCari, BorderLayout.CENTER);
        searchRow.add(btnCari, BorderLayout.EAST);

        top.add(title,     BorderLayout.WEST);
        top.add(searchRow, BorderLayout.EAST);

        String[] cols = {"ID","Nama","No. HP","Kamar","Asal","Tgl Masuk","Status"};
        tableModel = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(6).setCellRenderer(new KamarPanel.BadgeRenderer(
            new String[]{"aktif","keluar"},
            new Color[]{AppTheme.SUCCESS, AppTheme.TEXT_MUTED}
        ));

        p.add(top, BorderLayout.NORTH);
        p.add(AppTheme.styledTable(table), BorderLayout.CENTER);
        return p;
    }

    private void loadData() {
        lblStatus.setText("  ⟳ Memuat...");
        btnTambah.setEnabled(false); btnUbah.setEnabled(false); btnHapus.setEnabled(false);
        new SwingWorker<List<Penghuni>,Void>() {
            @Override protected List<Penghuni> doInBackground() { return controller.getAll(); }
            @Override protected void done() {
                try {
                    List<Penghuni> list=get(); tableModel.setRowCount(0);
                    for (Penghuni p : list)
                        tableModel.addRow(new Object[]{p.getIdPenghuni(),p.getNama(),p.getNoTelepon(),
                            p.getNamaKamar(),p.getAsal(),p.getTglMasuk(),p.getStatusPenghuni()});
                    lblStatus.setText("  "+list.size()+" penghuni terdaftar");
                } catch (Exception ex) { lblStatus.setText("  ✕ "+ex.getMessage()); }
                finally { btnTambah.setEnabled(true); btnUbah.setEnabled(true); btnHapus.setEnabled(true); }
            }
        }.execute();
    }

    private void loadKamarCombo() {
        new SwingWorker<List<Kamar>,Void>() {
            @Override protected List<Kamar> doInBackground() {
                return kamarController.getAll().stream().filter(Kamar::isTersedia).collect(java.util.stream.Collectors.toList());
            }
            @Override protected void done() {
                try { cmbKamar.removeAllItems(); for (Kamar k:get()) cmbKamar.addItem(k); }
                catch (Exception ex) { lblStatus.setText("  ✕ Gagal load kamar"); }
            }
        }.execute();
    }

    private void doAdd() {
        if (!validate2()) return;
        Penghuni pen = makeObj();
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.tambah(pen); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); loadKamarCombo(); JOptionPane.showMessageDialog(null,"Penghuni ditambahkan!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }
    private void doEdit() {
        if (selectedId==-1) { JOptionPane.showMessageDialog(null,"Pilih penghuni!"); return; }
        if (!validate2()) return;
        Penghuni pen = makeObj(); pen.setIdPenghuni(selectedId);
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.ubah(pen); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); JOptionPane.showMessageDialog(null,"Data diubah!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }
    private void doDelete() {
        if (selectedId==-1) { JOptionPane.showMessageDialog(null,"Pilih penghuni!"); return; }
        if (JOptionPane.showConfirmDialog(null,"Yakin hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        int id = selectedId;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.hapus(id); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); loadKamarCombo(); JOptionPane.showMessageDialog(null,"Penghuni dihapus!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }
    private void doSearch() {
        String kw = txtCari.getText().trim();
        if (kw.isEmpty()) { loadData(); return; }
        new SwingWorker<List<Penghuni>,Void>() {
            @Override protected List<Penghuni> doInBackground() { return controller.cari(kw); }
            @Override protected void done() {
                try {
                    List<Penghuni> list=get(); tableModel.setRowCount(0);
                    for (Penghuni p:list)
                        tableModel.addRow(new Object[]{p.getIdPenghuni(),p.getNama(),p.getNoTelepon(),
                            p.getNamaKamar(),p.getAsal(),p.getTglMasuk(),p.getStatusPenghuni()});
                    lblStatus.setText("  Ditemukan: "+list.size());
                } catch (Exception ex) { lblStatus.setText("  ✕ "+ex.getMessage()); }
            }
        }.execute();
    }
    private void fillFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        selectedId = (int) tableModel.getValueAt(row, 0);
        txtNama.setText((String) tableModel.getValueAt(row, 1));
        txtNoHp.setText((String) tableModel.getValueAt(row, 2));
        txtAsal.setText((String) tableModel.getValueAt(row, 4));
        Object tgl = tableModel.getValueAt(row, 5);
        txtTglMasuk.setText(tgl != null ? tgl.toString() : "");
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6));

        // Load data penghuni lengkap (termasuk id_kamar)
        new SwingWorker<Penghuni, Void>() {
            @Override protected Penghuni doInBackground() {
                return controller.getById(selectedId);
            }
            @Override protected void done() {
                try {
                    Penghuni p = get();
                    if (p != null) {
                        txtNamaOrtu.setText(p.getNamaOrtu());
                        txtNoHpOrtu.setText(p.getTelpOrtu());

                        // Load semua kamar (termasuk kamar yang sudah terisi)
                        List<Kamar> semuaKamar = kamarController.getAll();
                        cmbKamar.removeAllItems();
                        for (Kamar k : semuaKamar) cmbKamar.addItem(k);

                        // Set dropdown ke kamar penghuni ini
                        for (int i = 0; i < cmbKamar.getItemCount(); i++) {
                            if (cmbKamar.getItemAt(i).getIdKamar() == p.getIdKamar()) {
                                cmbKamar.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {}
            }
        }.execute();
    }
    private Penghuni makeObj() {
        Kamar k=(Kamar)cmbKamar.getSelectedItem(); int idK=k!=null?k.getIdKamar():0;
        return new Penghuni(0,txtNama.getText().trim(),txtNoHp.getText().trim(),idK,
            txtAsal.getText().trim(),txtTglMasuk.getText().trim(),
            (String)cmbStatus.getSelectedItem(),txtNamaOrtu.getText().trim(),txtNoHpOrtu.getText().trim());
    }
    private boolean validate2() {
        if (txtNama.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null,"Nama tidak boleh kosong!"); return false; }
        if (txtNoHp.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null,"No. HP tidak boleh kosong!"); return false; }
        return true;
    }
    private void clearForm() {
        selectedId = -1;
        txtNama.setText(""); txtNoHp.setText(""); txtAsal.setText("");
        txtTglMasuk.setText(""); txtNamaOrtu.setText(""); txtNoHpOrtu.setText("");
        cmbStatus.setSelectedIndex(0);
        table.clearSelection();
        loadKamarCombo(); // balik ke hanya kamar tersedia
    }
}
