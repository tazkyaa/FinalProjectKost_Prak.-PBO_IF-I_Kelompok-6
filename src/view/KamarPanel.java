package view;

import controller.KamarController;
import model.Kamar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KamarPanel extends JPanel {

    private final KamarController controller = new KamarController();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtHarga;
    private JComboBox<String> cmbStatus;
    private JButton btnTambah, btnUbah, btnHapus, btnBersih;
    private JLabel lblStatus;
    private int selectedId = -1;

    public void refresh() { loadData(); }

    public KamarPanel() { build(); loadData(); }

    private void build() {
        setLayout(new BorderLayout(0,0));
        setBackground(AppTheme.BG_BASE);
        setBorder(BorderFactory.createEmptyBorder(20,20,0,20));

        add(buildForm(),  BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);

        lblStatus = AppTheme.statusBar();
        add(lblStatus, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> doAdd());
        btnUbah.addActionListener(e   -> doEdit());
        btnHapus.addActionListener(e  -> doDelete());
        btnBersih.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(270, 0));
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));

        JPanel card = AppTheme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        // Header row
        JLabel title = new JLabel("Detail Kamar");
        title.setFont(AppTheme.F_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Tambah atau ubah data kamar");
        sub.setFont(AppTheme.F_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        txtNama  = AppTheme.field(18); txtNama.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); txtNama.setAlignmentX(LEFT_ALIGNMENT);
        txtHarga = AppTheme.field(18); txtHarga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); txtHarga.setAlignmentX(LEFT_ALIGNMENT);
        cmbStatus = new JComboBox<>(new String[]{"kosong","terisi"}); AppTheme.styleCombo(cmbStatus);
        cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); cmbStatus.setAlignmentX(LEFT_ALIGNMENT);

        btnTambah = AppTheme.btnPrimary("Tambah"); btnTambah.setAlignmentX(LEFT_ALIGNMENT); btnTambah.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnUbah   = AppTheme.btnSuccess("Simpan Ubahan"); btnUbah.setAlignmentX(LEFT_ALIGNMENT); btnUbah.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnHapus  = AppTheme.btnDanger("Hapus"); btnHapus.setAlignmentX(LEFT_ALIGNMENT); btnHapus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnBersih = AppTheme.btnGhost("Reset Form"); btnBersih.setAlignmentX(LEFT_ALIGNMENT); btnBersih.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        card.add(title); card.add(Box.createVerticalStrut(4));
        card.add(sub);   card.add(Box.createVerticalStrut(24));
        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(20));

        addField(card, "Nama Kamar", txtNama);
        addField(card, "Harga (Rp)", txtHarga);
        addField(card, "Status", cmbStatus);

        card.add(Box.createVerticalStrut(24));
        card.add(AppTheme.sep()); card.add(Box.createVerticalStrut(16));
        card.add(btnTambah); card.add(Box.createVerticalStrut(8));
        card.add(btnUbah);   card.add(Box.createVerticalStrut(8));
        card.add(btnHapus);  card.add(Box.createVerticalStrut(12));
        card.add(btnBersih);

        wrap.add(card, BorderLayout.NORTH);
        return wrap;
    }

    private void addField(JPanel card, String labelText, Component field) {
        JLabel lbl = AppTheme.fieldLabel(labelText);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lbl); card.add(Box.createVerticalStrut(6));
        card.add(field); card.add(Box.createVerticalStrut(14));
    }

    private JPanel buildTable() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);

        // Header row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Daftar Kamar");
        title.setFont(AppTheme.F_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        String[] cols = {"ID", "Nama Kamar", "Harga", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(55);

        // Status badge renderer
        table.getColumnModel().getColumn(3).setCellRenderer(new BadgeRenderer(
            new String[]{"kosong","terisi"},
            new Color[]{AppTheme.SUCCESS, AppTheme.WARN}
        ));

        p.add(header, BorderLayout.NORTH);
        p.add(AppTheme.styledTable(table), BorderLayout.CENTER);
        return p;
    }

    private void loadData() {
        lblStatus.setText("  ⟳ Memuat data kamar...");
        new SwingWorker<List<Kamar>,Void>() {
            @Override protected List<Kamar> doInBackground() { return controller.getAll(); }
            @Override protected void done() {
                try {
                    List<Kamar> list = get(); tableModel.setRowCount(0);
                    for (Kamar k : list)
                        tableModel.addRow(new Object[]{k.getIdKamar(), k.getNamaKamar(),
                            String.format("Rp %,.0f", k.getHarga()), k.getStatus()});
                    lblStatus.setText("  " + list.size() + " kamar terdaftar");
                } catch (Exception ex) { lblStatus.setText("  ✕ " + ex.getMessage()); }
            }
        }.execute();
    }

    private void doAdd() {
        if (txtNama.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null,"Nama kamar tidak boleh kosong!"); return; }
        Kamar k = makeObj();
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.tambah(k); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); JOptionPane.showMessageDialog(null,"Kamar berhasil ditambahkan!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }
    private void doEdit() {
        if (selectedId==-1) { JOptionPane.showMessageDialog(null,"Pilih kamar!"); return; }
        Kamar k = makeObj(); k.setIdKamar(selectedId);
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.ubah(k); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); JOptionPane.showMessageDialog(null,"Kamar berhasil diubah!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }
    private void doDelete() {
        if (selectedId==-1) { JOptionPane.showMessageDialog(null,"Pilih kamar!"); return; }
        if (JOptionPane.showConfirmDialog(null,"Yakin hapus kamar ini?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        int id = selectedId;
        new SwingWorker<Void,Void>() {
            @Override protected Void doInBackground() { controller.hapus(id); return null; }
            @Override protected void done() {
                try { get(); clearForm(); loadData(); JOptionPane.showMessageDialog(null,"Kamar berhasil dihapus!"); }
                catch (Exception ex) { JOptionPane.showMessageDialog(null,"Gagal: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }
    private void fillFormFromTable() {
        int row = table.getSelectedRow(); if (row==-1) return;
        selectedId = (int)tableModel.getValueAt(row,0);
        txtNama.setText((String)tableModel.getValueAt(row,1));
        txtHarga.setText(tableModel.getValueAt(row,2).toString().replaceAll("[^0-9]",""));
        cmbStatus.setSelectedItem(tableModel.getValueAt(row,3));
    }
    private Kamar makeObj() {
        double h=0; try { h=Double.parseDouble(txtHarga.getText().trim().replaceAll("[^0-9]","")); } catch (Exception ignored){}
        return new Kamar(0, txtNama.getText().trim(), h, (String)cmbStatus.getSelectedItem());
    }
    private void clearForm() {
        selectedId=-1; txtNama.setText(""); txtHarga.setText(""); cmbStatus.setSelectedIndex(0); table.clearSelection();
    }

    // ── Badge renderer ────────────────────────────────────────────────────────
    static class BadgeRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private final String[] vals; private final Color[] colors;
        BadgeRenderer(String[] vals, Color[] colors) { this.vals=vals; this.colors=colors; }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t,v,sel,foc,row,col);
            String s = v!=null?v.toString():"";
            Color bg = sel ? AppTheme.PRIMARY.darker() : (row%2==0?AppTheme.BG_ELEVATED:AppTheme.BG_CARD);
            Color fg = AppTheme.TEXT_SUB;
            for (int i=0;i<vals.length;i++) {
                if (s.equalsIgnoreCase(vals[i])) { fg=colors[i]; break; }
            }
            setBackground(bg); setForeground(fg);
            setFont(AppTheme.F_BOLD_SM);
            setBorder(BorderFactory.createEmptyBorder(0,14,0,14));
            return this;
        }
    }
}
