package view;

import controller.PembayaranController;
import controller.PenghuniController;
import controller.KamarController;
import model.Pembayaran;
import model.Penghuni;
import model.Kamar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class PembayaranPanel extends JPanel {

    private final PembayaranController controller = new PembayaranController();
    private final PenghuniController penghuniController = new PenghuniController();
    private final KamarController kamarController = new KamarController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Penghuni> cmbPenghuni;
    private JComboBox<String> cmbBulan, cmbTahun;
    private JTextField txtTanggal, txtJumlah, txtMetode, txtKeterangan;
    private JComboBox<String> cmbStatus;
    private JButton btnTambah, btnUbah, btnHapus, btnBersih;
    private JLabel lblStatus;
    private int selectedId = -1;

    // === TAMBAHAN: filter & summary ===
    private JComboBox<String> cmbFilterBulan, cmbFilterTahun;
    private JButton btnFilter, btnResetFilter;
    private JLabel lblSummaryTotal, lblSummaryLunas, lblSummaryTunggak;
    private List<Pembayaran> allData = new ArrayList<>();

    public PembayaranPanel() { build(); loadData(); }

    private void build() {
        setLayout(new BorderLayout(0,0));
        setBackground(AppTheme.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(20,20,0,20));

        add(buildForm(),  BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);
        lblStatus = AppTheme.statusBar();
        add(lblStatus, BorderLayout.SOUTH);

        loadPenghuniCombo();
        btnTambah.addActionListener(e -> doAdd());
        btnUbah.addActionListener(e   -> doEdit());
        btnHapus.addActionListener(e  -> doDelete());
        btnBersih.addActionListener(e -> clearForm());

        // === TAMBAHAN: listener filter ===
        btnFilter.addActionListener(e -> applyFilter());
        btnResetFilter.addActionListener(e -> {
            cmbFilterBulan.setSelectedIndex(0);
            cmbFilterTahun.setSelectedIndex(0);
            applyFilter();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFromTable();
        });
        cmbPenghuni.addActionListener(e -> {
            Penghuni pen = (Penghuni) cmbPenghuni.getSelectedItem();
            if (pen == null) return;
            new SwingWorker<Kamar, Void>() {
                @Override protected Kamar doInBackground() {
                    return kamarController.getById(pen.getIdKamar());
                }
                @Override protected void done() {
                    try {
                        Kamar k = get();
                        if (k != null) txtJumlah.setText(String.valueOf((long) k.getHarga()));
                    } catch (Exception ex) {}
                }
            }.execute();
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

        JLabel title = new JLabel("Data Pembayaran");
        title.setFont(AppTheme.F_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY); title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Catat transaksi pembayaran sewa");
        sub.setFont(AppTheme.F_SMALL); sub.setForeground(AppTheme.TEXT_MUTED); sub.setAlignmentX(LEFT_ALIGNMENT);

        cmbPenghuni = new JComboBox<>(); AppTheme.styleCombo(cmbPenghuni);
        cmbPenghuni.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); cmbPenghuni.setAlignmentX(LEFT_ALIGNMENT);

        String[] bulanList = {"Januari","Februari","Maret","April","Mei","Juni",
                              "Juli","Agustus","September","Oktober","November","Desember"};
        cmbBulan = new JComboBox<>(bulanList); AppTheme.styleCombo(cmbBulan);
        cmbBulan.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); cmbBulan.setAlignmentX(LEFT_ALIGNMENT);

        String[] tahunList = {"2023","2024","2025","2026","2027","2028"};
        cmbTahun = new JComboBox<>(tahunList); AppTheme.styleCombo(cmbTahun);
        cmbTahun.setSelectedItem("2026");
        cmbTahun.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); cmbTahun.setAlignmentX(LEFT_ALIGNMENT);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cmbBulan.setSelectedIndex(cal.get(java.util.Calendar.MONTH));
        cmbTahun.setSelectedItem(String.valueOf(cal.get(java.util.Calendar.YEAR)));

        txtTanggal    = mkf(); txtJumlah  = mkf();
        txtMetode     = mkf(); txtKeterangan = mkf();
        txtTanggal.setEditable(false);
        txtTanggal.setForeground(AppTheme.TEXT_MUTED);
        txtTanggal.setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new java.util.Date()));

        cmbStatus = new JComboBox<>(new String[]{"lunas","tunggak"}); AppTheme.styleCombo(cmbStatus);
        cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); cmbStatus.setAlignmentX(LEFT_ALIGNMENT);

        btnTambah = AppTheme.btnPrimary("+ Tambah");    sz(btnTambah);
        btnUbah   = AppTheme.btnSuccess("✎  Simpan");   sz(btnUbah);
        btnHapus  = AppTheme.btnDanger("✕  Hapus");     sz(btnHapus);
        btnBersih = AppTheme.btnGhost("↺  Reset");      sz(btnBersih);

        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub);   card.add(Box.createVerticalStrut(20));
        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(16));

        af(card,"Penghuni",             cmbPenghuni);
        af(card,"Bulan Bayar",          cmbBulan);
        af(card,"Tahun",                cmbTahun);
        af(card,"Tanggal & Jam",        txtTanggal);
        af(card,"Jumlah (Rp)",          txtJumlah);
        af(card,"Metode Bayar",         txtMetode);
        af(card,"Status",               cmbStatus);
        af(card,"Keterangan",           txtKeterangan);

        card.add(Box.createVerticalStrut(8));
        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(14));
        card.add(btnTambah); card.add(Box.createVerticalStrut(7));
        card.add(btnUbah);   card.add(Box.createVerticalStrut(7));
        card.add(btnHapus);  card.add(Box.createVerticalStrut(10));
        card.add(btnBersih);

        JScrollPane sp = new JScrollPane(card);
        sp.setBorder(null); sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildTable() {
        JPanel p = new JPanel(new BorderLayout(0,8)); p.setOpaque(false);

        // --- judul (sama persis seperti asli) ---
        JLabel title = new JLabel("Riwayat Pembayaran");
        title.setFont(AppTheme.F_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY);

        // === TAMBAHAN: filter bar tipis di bawah judul ===
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filterRow.setOpaque(false);

        String[] fBulan = {"Semua Bulan","Januari","Februari","Maret","April","Mei","Juni",
                           "Juli","Agustus","September","Oktober","November","Desember"};
        String[] fTahun = {"Semua Tahun","2023","2024","2025","2026","2027","2028"};

        cmbFilterBulan = new JComboBox<>(fBulan); AppTheme.styleCombo(cmbFilterBulan);
        cmbFilterTahun = new JComboBox<>(fTahun); AppTheme.styleCombo(cmbFilterTahun);
        cmbFilterBulan.setPreferredSize(new Dimension(140, 30));
        cmbFilterTahun.setPreferredSize(new Dimension(110, 30));

        // default ke bulan & tahun sekarang
        java.util.Calendar now = java.util.Calendar.getInstance();
        cmbFilterBulan.setSelectedIndex(now.get(java.util.Calendar.MONTH) + 1);
        cmbFilterTahun.setSelectedItem(String.valueOf(now.get(java.util.Calendar.YEAR)));

        btnFilter      = AppTheme.btnPrimary("Filter");
        btnResetFilter = AppTheme.btnGhost("Semua");
        btnFilter.setPreferredSize(new Dimension(80, 30));
        btnResetFilter.setPreferredSize(new Dimension(72, 30));

        filterRow.add(cmbFilterBulan);
        filterRow.add(cmbFilterTahun);
        filterRow.add(btnFilter);
        filterRow.add(btnResetFilter);

        // === TAMBAHAN: summary label (3 chip kecil setelah filter) ===
        JPanel summaryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        summaryRow.setOpaque(false);

        lblSummaryTotal   = makeSummaryChip("Total: 0",   AppTheme.TEXT_SUB,    AppTheme.BG_ELEVATED);
        lblSummaryLunas   = makeSummaryChip("Lunas: 0",   AppTheme.SUCCESS,     new Color(0x0B2A1E));
        lblSummaryTunggak = makeSummaryChip("Tunggak: 0", AppTheme.DANGER,      new Color(0x2A0B0B));

        summaryRow.add(lblSummaryTotal);
        summaryRow.add(lblSummaryLunas);
        summaryRow.add(lblSummaryTunggak);

        // panel atas: judul + filter + summary (stack vertikal tipis)
        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(title);
        topSection.add(Box.createVerticalStrut(8));
        topSection.add(filterRow);
        topSection.add(Box.createVerticalStrut(6));
        topSection.add(summaryRow);

        // --- tabel (sama persis seperti asli) ---
        String[] cols = {"ID","Penghuni","Kamar","Bulan","Tanggal","Jumlah","Metode","Status"};
        tableModel = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        table.getColumnModel().getColumn(7).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object v,
                        boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                    String s = v!=null?v.toString():"";
                    if (!sel) {
                        if ("lunas".equalsIgnoreCase(s)) {
                            setBackground(new Color(0x0B2A1E));
                            setForeground(AppTheme.SUCCESS);
                        } else if ("tunggak".equalsIgnoreCase(s)) {
                            setBackground(new Color(0x2A0B0B));
                            setForeground(AppTheme.DANGER);
                        } else {
                            setBackground(row%2==0?AppTheme.BG_ELEVATED:AppTheme.BG_CARD);
                            setForeground(AppTheme.TEXT_SUB);
                        }
                    }
                    setFont(AppTheme.F_BOLD_SM);
                    setBorder(BorderFactory.createEmptyBorder(0,14,0,14));
                    return this;
                }
            }
        );

        p.add(topSection,              BorderLayout.NORTH);
        p.add(AppTheme.styledTable(table), BorderLayout.CENTER);
        return p;
    }

    // chip kecil untuk summary (tidak mencolok)
    private JLabel makeSummaryChip(String text, Color fg, Color bg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.F_BOLD_SM);
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        // rounded via compound border trick
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fg.darker(), 1, true),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        return lbl;
    }

    // === TAMBAHAN: filter sisi client, tidak query ulang ke DB ===
    private void applyFilter() {
        String selBulan = (String) cmbFilterBulan.getSelectedItem();
        String selTahun = (String) cmbFilterTahun.getSelectedItem();
        boolean filterBulan = selBulan != null && !selBulan.startsWith("Semua");
        boolean filterTahun = selTahun != null && !selTahun.startsWith("Semua");

        List<Pembayaran> filtered = new ArrayList<>();
        for (Pembayaran pm : allData) {
            String b = pm.getBulan(); // "Januari 2026"
            boolean okBulan = !filterBulan || (b != null && b.startsWith(selBulan));
            boolean okTahun = !filterTahun || (b != null && b.endsWith(selTahun));
            if (okBulan && okTahun) filtered.add(pm);
        }

        tableModel.setRowCount(0);
        for (Pembayaran pm : filtered)
            tableModel.addRow(new Object[]{pm.getIdPembayaran(), pm.getNamaPenghuni(),
                pm.getIdKamar(), pm.getBulan(), pm.getTanggal(),
                String.format("Rp %,.0f", pm.getJumlah()), pm.getMetodeBayar(), pm.getStatus()});

        long lunas   = filtered.stream().filter(pm -> "lunas".equalsIgnoreCase(pm.getStatus())).count();
        long tunggak = filtered.stream().filter(pm -> "tunggak".equalsIgnoreCase(pm.getStatus())).count();

        lblSummaryTotal.setText("Total: " + filtered.size());
        lblSummaryLunas.setText("Lunas: " + lunas);
        lblSummaryTunggak.setText("Tunggak: " + tunggak);

        lblStatus.setText("  " + filtered.size() + " transaksi"
            + (filterBulan || filterTahun ? " — " + (filterBulan ? selBulan+" " : "") + (filterTahun ? selTahun : "") : " (semua)"));
    }

    private void loadPenghuniCombo() {
        new SwingWorker<List<Penghuni>,Void>() {
            @Override protected List<Penghuni> doInBackground() { return penghuniController.getAll(); }
            @Override protected void done() {
                try { cmbPenghuni.removeAllItems(); for (Penghuni p:get()) cmbPenghuni.addItem(p); }
                catch (Exception ex) { lblStatus.setText("  ✕ Gagal load penghuni"); }
            }
        }.execute();
    }

    private void loadData() {
        lblStatus.setText("  ⟳ Memuat...");
        new SwingWorker<List<Pembayaran>,Void>() {
            @Override protected List<Pembayaran> doInBackground() { return controller.getAll(); }
            @Override protected void done() {
                try {
                    allData = get();
                    applyFilter(); // render lewat filter supaya summary ikut terupdate
                } catch (Exception ex) { lblStatus.setText("  ✕ "+ex.getMessage()); }
            }
        }.execute();
    }

    private void doAdd() {
        String sekarang = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new java.util.Date());
        txtTanggal.setText(sekarang);
        Pembayaran pm = makeObj();
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.tambah(pm); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); JOptionPane.showMessageDialog(null,"Pembayaran ditambahkan!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private void doEdit() {
        if (selectedId==-1) { JOptionPane.showMessageDialog(null,"Pilih data!"); return; }
        Pembayaran pm = makeObj(); pm.setIdPembayaran(selectedId);
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.ubah(pm); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); JOptionPane.showMessageDialog(null,"Data diubah!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private void doDelete() {
        if (selectedId==-1) { JOptionPane.showMessageDialog(null,"Pilih data!"); return; }
        if (JOptionPane.showConfirmDialog(null,"Yakin hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        int id=selectedId;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.hapus(id); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); JOptionPane.showMessageDialog(null,"Data dihapus!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }

    private void fillFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        selectedId = (int) tableModel.getValueAt(row, 0);

        // Set cmbPenghuni — cari item yang namanya cocok dengan kolom "Penghuni"
        String namaDiTabel = tableModel.getValueAt(row, 1) != null
            ? tableModel.getValueAt(row, 1).toString() : "";
        for (int i = 0; i < cmbPenghuni.getItemCount(); i++) {
            Penghuni p = cmbPenghuni.getItemAt(i);
            if (p != null && p.getNama().equalsIgnoreCase(namaDiTabel)) {
                cmbPenghuni.setSelectedIndex(i);
                break;
            }
        }

        // Set bulan & tahun
        String bulanTahun = tableModel.getValueAt(row, 3) != null
            ? tableModel.getValueAt(row, 3).toString() : "";
        if (bulanTahun.contains(" ")) {
            String[] parts = bulanTahun.split(" ", 2);
            cmbBulan.setSelectedItem(parts[0]);
            if (parts.length > 1) cmbTahun.setSelectedItem(parts[1]);
        } else if (!bulanTahun.isEmpty()) {
            cmbBulan.setSelectedItem(bulanTahun);
        }

        // Set field lainnya
        Object tgl = tableModel.getValueAt(row, 4);
        txtTanggal.setText(tgl != null ? tgl.toString() : "");

        Object jml = tableModel.getValueAt(row, 5);
        txtJumlah.setText(jml != null ? jml.toString().replaceAll("[^0-9]", "") : "");

        Object met = tableModel.getValueAt(row, 6);
        txtMetode.setText(met != null ? met.toString() : "");

        Object sts = tableModel.getValueAt(row, 7);
        if (sts != null) cmbStatus.setSelectedItem(sts.toString());
    }

    private Pembayaran makeObj() {
        Penghuni pen=(Penghuni)cmbPenghuni.getSelectedItem();
        int idP=pen!=null?pen.getIdPenghuni():0;
        String nama=pen!=null?pen.getNama():"";
        int idK=pen!=null?pen.getIdKamar():0;
        double jml=0; try { jml=Double.parseDouble(txtJumlah.getText().trim().replaceAll("[^0-9]","")); } catch (Exception ignored){}
        String tgl=txtTanggal.getText().trim().isEmpty()?null:txtTanggal.getText().trim();
        String met=txtMetode.getText().trim().isEmpty()?null:txtMetode.getText().trim();
        String bulan = cmbBulan.getSelectedItem() + " " + cmbTahun.getSelectedItem();
        return new Pembayaran(0,idP,nama,idK,bulan,tgl,jml,
            met,(String)cmbStatus.getSelectedItem(),txtKeterangan.getText().trim());
    }

    private void clearForm() {
        selectedId=-1;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cmbBulan.setSelectedIndex(cal.get(java.util.Calendar.MONTH));
        cmbTahun.setSelectedItem(String.valueOf(cal.get(java.util.Calendar.YEAR)));
        txtTanggal.setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        txtJumlah.setText(""); txtMetode.setText(""); txtKeterangan.setText("");
        cmbStatus.setSelectedIndex(0); table.clearSelection();
    }

    private JTextField mkf() {
        JTextField f = AppTheme.field(18);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }
    private void sz(JButton b) { b.setAlignmentX(LEFT_ALIGNMENT); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); }
    private void af(JPanel p, String label, Component comp) {
        JLabel l = AppTheme.fieldLabel(label); l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5)); p.add(comp); p.add(Box.createVerticalStrut(12));
    }
}