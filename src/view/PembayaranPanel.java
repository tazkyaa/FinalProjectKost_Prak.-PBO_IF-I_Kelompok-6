package view;

import controller.PembayaranController;
import controller.PenghuniController;
import model.Pembayaran;
import model.Penghuni;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PembayaranPanel extends JPanel {

    private final PembayaranController controller = new PembayaranController();
    private final PenghuniController penghuniController = new PenghuniController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Penghuni> cmbPenghuni;
    private JTextField txtBulan, txtTanggal, txtJumlah, txtMetode, txtKeterangan;
    private JComboBox<String> cmbStatus;
    private JButton btnTambah, btnUbah, btnHapus, btnBersih;
    private JLabel lblStatus;
    private int selectedId = -1;

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

        JLabel title = new JLabel("Data Pembayaran");
        title.setFont(AppTheme.F_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY); title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Catat transaksi pembayaran sewa");
        sub.setFont(AppTheme.F_SMALL); sub.setForeground(AppTheme.TEXT_MUTED); sub.setAlignmentX(LEFT_ALIGNMENT);

        cmbPenghuni = new JComboBox<>(); AppTheme.styleCombo(cmbPenghuni);
        cmbPenghuni.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); cmbPenghuni.setAlignmentX(LEFT_ALIGNMENT);

        txtBulan      = mkf(); txtTanggal   = mkf(); txtJumlah  = mkf();
        txtMetode     = mkf(); txtKeterangan = mkf();
        cmbStatus = new JComboBox<>(new String[]{"lunas","tunggak"}); AppTheme.styleCombo(cmbStatus);
        cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE,38)); cmbStatus.setAlignmentX(LEFT_ALIGNMENT);

        btnTambah = AppTheme.btnPrimary("+ Tambah");    sz(btnTambah);
        btnUbah   = AppTheme.btnSuccess("✎  Simpan");   sz(btnUbah);
        btnHapus  = AppTheme.btnDanger("✕  Hapus");     sz(btnHapus);
        btnBersih = AppTheme.btnGhost("↺  Reset");      sz(btnBersih);

        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub);   card.add(Box.createVerticalStrut(20));
        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(16));

        af(card,"Penghuni",       cmbPenghuni);
        af(card,"Bulan Bayar",    txtBulan);
        af(card,"Tanggal (YYYY-MM-DD)", txtTanggal);
        af(card,"Jumlah (Rp)",    txtJumlah);
        af(card,"Metode Bayar",   txtMetode);
        af(card,"Status",         cmbStatus);
        af(card,"Keterangan",     txtKeterangan);

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

    private JPanel buildTable() {
        JPanel p = new JPanel(new BorderLayout(0,12)); p.setOpaque(false);

        JLabel title = new JLabel("Riwayat Pembayaran");
        title.setFont(AppTheme.F_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY);

        String[] cols = {"ID","Penghuni","Kamar","Bulan","Tanggal","Jumlah","Metode","Status"};
        tableModel = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // Status badge renderer
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

        p.add(title, BorderLayout.NORTH);
        p.add(AppTheme.styledTable(table), BorderLayout.CENTER);
        return p;
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
                    List<Pembayaran> list=get(); tableModel.setRowCount(0);
                    for (Pembayaran p:list)
                        tableModel.addRow(new Object[]{p.getIdPembayaran(),p.getNamaPenghuni(),
                            p.getIdKamar(),p.getBulan(),p.getTanggal(),
                            String.format("Rp %,.0f",p.getJumlah()),p.getMetodeBayar(),p.getStatus()});
                    lblStatus.setText("  "+list.size()+" transaksi tercatat");
                } catch (Exception ex) { lblStatus.setText("  ✕ "+ex.getMessage()); }
            }
        }.execute();
    }

    private void doAdd() {
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
        int row=table.getSelectedRow(); if (row==-1) return;
        selectedId=(int)tableModel.getValueAt(row,0);
        txtBulan.setText((String)tableModel.getValueAt(row,3));
        Object tgl=tableModel.getValueAt(row,4); txtTanggal.setText(tgl!=null?tgl.toString():"");
        txtJumlah.setText(tableModel.getValueAt(row,5).toString().replaceAll("[^0-9]",""));
        Object m=tableModel.getValueAt(row,6); txtMetode.setText(m!=null?m.toString():"");
        cmbStatus.setSelectedItem(tableModel.getValueAt(row,7));
    }
    private Pembayaran makeObj() {
        Penghuni pen=(Penghuni)cmbPenghuni.getSelectedItem();
        int idP=pen!=null?pen.getIdPenghuni():0;
        String nama=pen!=null?pen.getNama():"";
        int idK=pen!=null?pen.getIdKamar():0;
        double jml=0; try { jml=Double.parseDouble(txtJumlah.getText().trim().replaceAll("[^0-9]","")); } catch (Exception ignored){}
        String tgl=txtTanggal.getText().trim().isEmpty()?null:txtTanggal.getText().trim();
        String met=txtMetode.getText().trim().isEmpty()?null:txtMetode.getText().trim();
        return new Pembayaran(0,idP,nama,idK,txtBulan.getText().trim(),tgl,jml,
            (String)cmbStatus.getSelectedItem(),met,txtKeterangan.getText().trim());
    }
    private void clearForm() {
        selectedId=-1; txtBulan.setText(""); txtTanggal.setText(""); txtJumlah.setText("");
        txtMetode.setText(""); txtKeterangan.setText(""); cmbStatus.setSelectedIndex(0); table.clearSelection();
    }
}
