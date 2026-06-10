package dao;

import model.Penghuni;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenghuniDAO implements GenerialDAO<Penghuni> {

    private Connection conn() {
        return DatabaseConnection.getConnection();
    }

    // INSERT
    @Override
    public void insert(Penghuni p) {
        // Cek berdasarkan nama + no_hp (identitas unik penghuni)
        String cekSql = "SELECT id_penghuni, id_kamar FROM penghuni "
                + "WHERE nama = ? AND no_hp = ?";

        try (PreparedStatement cekPs = conn().prepareStatement(cekSql)) {
            cekPs.setString(1, p.getNama());
            cekPs.setString(2, p.getNoTelepon());

            ResultSet rs = cekPs.executeQuery();

            if (rs.next()) {
                // Penghuni sudah ada → UPDATE
                int existingId = rs.getInt("id_penghuni");
                int oldKamarId = rs.getInt("id_kamar");

                String updateSql = "UPDATE penghuni SET "
                        + "no_hp=?, id_kamar=?, asal_daerah=?, tanggal_masuk=?, "
                        + "status_penghuni=?, nama_ortu=?, no_hp_ortu=? "
                        + "WHERE id_penghuni=?";

                try (PreparedStatement updatePs = conn().prepareStatement(updateSql)) {
                    updatePs.setString(1, p.getNoTelepon());
                    updatePs.setInt(2, p.getIdKamar());
                    updatePs.setString(3, p.getAsal());
                    updatePs.setString(4, p.getTglMasuk());
                    updatePs.setString(5, p.getStatusPenghuni());
                    updatePs.setString(6, p.getNamaOrtu());
                    updatePs.setString(7, p.getTelpOrtu());
                    updatePs.setInt(8, existingId);
                    updatePs.executeUpdate();
                }

                // Kalau kamar berubah, update status kamar lama jadi kosong
                if (oldKamarId != p.getIdKamar()) {
                    updateStatusKamar(oldKamarId, "kosong");
                    updateStatusKamar(p.getIdKamar(), "terisi");
                }

            } else {
                // Penghuni baru → INSERT
                String insertSql = "INSERT INTO penghuni "
                        + "(nama, no_hp, id_kamar, asal_daerah, tanggal_masuk, "
                        + "status_penghuni, nama_ortu, no_hp_ortu) "
                        + "VALUES (?,?,?,?,?,?,?,?)";

                try (PreparedStatement insertPs = conn().prepareStatement(insertSql)) {
                    insertPs.setString(1, p.getNama());
                    insertPs.setString(2, p.getNoTelepon());
                    insertPs.setInt(3, p.getIdKamar());
                    insertPs.setString(4, p.getAsal());
                    insertPs.setString(5, p.getTglMasuk());
                    insertPs.setString(6, p.getStatusPenghuni());
                    insertPs.setString(7, p.getNamaOrtu());
                    insertPs.setString(8, p.getTelpOrtu());
                    insertPs.executeUpdate();
                }

                updateStatusKamar(p.getIdKamar(), "terisi");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // UPDATE
    @Override
    public void update(Penghuni p) {
        String sql = "UPDATE penghuni SET "
                + "nama=?, no_hp=?, id_kamar=?, asal_daerah=?, "
                + "tanggal_masuk=?, status_penghuni=?, nama_ortu=?, no_hp_ortu=? "
                + "WHERE id_penghuni=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoTelepon());
            ps.setInt(3, p.getIdKamar());
            ps.setString(4, p.getAsal());
            ps.setString(5, p.getTglMasuk());
            ps.setString(6, p.getStatusPenghuni());
            ps.setString(7, p.getNamaOrtu());
            ps.setString(8, p.getTelpOrtu());
            ps.setInt(9, p.getIdPenghuni());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE
    @Override
    public void delete(int id) {
        Penghuni p = findById(id);

        // 1. Hapus pembayaran dulu (karena ada foreign key ke penghuni)
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM pembayaran WHERE id_penghuni = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 2. Baru hapus penghuni
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM penghuni WHERE id_penghuni = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            if (p != null) updateStatusKamar(p.getIdKamar(), "kosong"); // kamar jadi kosong
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // READ
    @Override
    public Penghuni findById(int id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM penghuni WHERE id_penghuni=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

   @Override
    public List<Penghuni> findAll() {
        List<Penghuni> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT p.*, k.nama_kamar FROM penghuni p " +
                 "LEFT JOIN kamar k ON p.id_kamar = k.id_kamar " +
                 "ORDER BY p.nama")) {
            while (rs.next()) list.add(mapWithKamar(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public List<Penghuni> search(String keyword) {
        List<Penghuni> list = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kamar FROM penghuni p " +
                     "LEFT JOIN kamar k ON p.id_kamar = k.id_kamar " +
                     "WHERE p.nama LIKE ? OR p.no_hp LIKE ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapWithKamar(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    private Penghuni mapWithKamar(ResultSet rs) throws SQLException {
        Penghuni p = map(rs);
        p.setNamaKamar(rs.getString("nama_kamar"));
        return p;
    }

    // update status kamar otomatis
    private void updateStatusKamar(int idKamar, String status) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE kamar SET status_kamar=? WHERE id_kamar=?")) {
            ps.setString(1, status);
            ps.setInt(2, idKamar);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Penghuni map(ResultSet rs) throws SQLException {
        return new Penghuni(
            rs.getInt("id_penghuni"),
            rs.getString("nama"),
            rs.getString("no_hp"),
            rs.getInt("id_kamar"),
            rs.getString("asal_daerah"),
            rs.getString("tanggal_masuk"),
            rs.getString("status_penghuni"),
            rs.getString("nama_ortu"),
            rs.getString("no_hp_ortu")
        );
    }
}