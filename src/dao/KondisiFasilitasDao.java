package dao;

import model.KondisiFasilitas;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KondisiFasilitasDao implements GenerialDAO<KondisiFasilitas> {

    private Connection conn() { return DatabaseConnection.getConnection(); }

    // INSERT
    @Override
    public void insert(KondisiFasilitas kf) {
        String sql = "INSERT INTO fasilitas_kondisi "
                + "(id_kamar, id_fasilitas, kondisi, keterangan_rusak, terakhir_diperbarui) "
                + "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt   (1, kf.getIdKamar());
            ps.setInt   (2, kf.getIdFasilitas());
            ps.setString(3, kf.getKondisi());
            ps.setString(4, kf.getKeteranganRusak());
            ps.setString(5, kf.getTerakhirDiperbarui());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // UPDATE (atau INSERT jika belum ada — upsert)
    @Override
    public void update(KondisiFasilitas kf) {
        // INSERT ... ON DUPLICATE KEY UPDATE memastikan:
        // - kalau baris sudah ada  → update kondisi/keterangan/waktu
        // - kalau baris belum ada  → insert baru (UPDATE biasa diam-diam gagal)
        String sql = "INSERT INTO fasilitas_kondisi "
                + "(id_kamar, id_fasilitas, kondisi, keterangan_rusak, terakhir_diperbarui) "
                + "VALUES (?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE "
                + "kondisi=VALUES(kondisi), "
                + "keterangan_rusak=VALUES(keterangan_rusak), "
                + "terakhir_diperbarui=VALUES(terakhir_diperbarui)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt   (1, kf.getIdKamar());
            ps.setInt   (2, kf.getIdFasilitas());
            ps.setString(3, kf.getKondisi());
            ps.setString(4, kf.getKeteranganRusak());
            ps.setString(5, kf.getTerakhirDiperbarui());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // DELETE by id_kamar only (lama — dipertahankan agar GenerialDAO contract terpenuhi)
    @Override
    public void delete(int idKamar) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM fasilitas_kondisi WHERE id_kamar=?")) {
            ps.setInt(1, idKamar);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // DELETE by composite key (id_kamar + id_fasilitas) — dipakai untuk hapus satu baris spesifik
    public void deleteByKamarAndFasilitas(int idKamar, int idFasilitas) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM fasilitas_kondisi WHERE id_kamar=? AND id_fasilitas=?")) {
            ps.setInt(1, idKamar);
            ps.setInt(2, idFasilitas);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // READ by id_kamar
    @Override
    public KondisiFasilitas findById(int id) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT * FROM fasilitas_kondisi WHERE id_kamar=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    // READ ALL
    @Override
    public List<KondisiFasilitas> findAll() {
        List<KondisiFasilitas> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT * FROM fasilitas_kondisi ORDER BY id_kamar, id_fasilitas")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    private KondisiFasilitas map(ResultSet rs) throws SQLException {
        return new KondisiFasilitas(
            rs.getInt   ("id_kamar"),
            rs.getInt   ("id_fasilitas"),
            rs.getString("kondisi"),
            rs.getString("keterangan_rusak"),
            rs.getString("terakhir_diperbarui")
        );
    }
}