package controller;

import dao.KondisiFasilitasDao;
import model.KondisiFasilitas;
import util.KondisiFasilitasService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KondisiFasilitasController {

    private final KondisiFasilitasDao dao = new KondisiFasilitasDao();
    private final KondisiFasilitasService service = new KondisiFasilitasService();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ---------------------------------------------------------------
    // GET semua kondisi fasilitas (untuk isi tabel di view)
    // ---------------------------------------------------------------
    public List<KondisiFasilitas> getAllKondisi() {
        return dao.findAll();
    }

    // ---------------------------------------------------------------
    // GET kondisi fasilitas berdasarkan id_kamar
    // ---------------------------------------------------------------
    public KondisiFasilitas getKondisiByKamar(int idKamar) {
        return dao.findById(idKamar);
    }

    // ---------------------------------------------------------------
    // UPDATE kondisi fasilitas (pakai Service multithreading)
    // onSelesai → Runnable dari View untuk refresh tabel setelah DB selesai
    // ---------------------------------------------------------------
    public void updateKondisi(int idKamar, int idFasilitas,
                               String kondisi, String keteranganRusak,
                               Runnable onSelesai) {

        String waktu = LocalDateTime.now().format(FORMATTER);

        KondisiFasilitas kf = new KondisiFasilitas(
                idKamar,
                idFasilitas,
                kondisi,
                keteranganRusak,
                waktu
        );

        // Pakai Service agar proses simpan DB & refresh UI jalan via thread
        service.updateKondisiParalel(kf, onSelesai);
    }

    // ---------------------------------------------------------------
    // INSERT kondisi fasilitas baru
    // ---------------------------------------------------------------
    public void tambahKondisi(int idKamar, int idFasilitas,
                               String kondisi, String keteranganRusak) {

        String waktu = LocalDateTime.now().format(FORMATTER);

        KondisiFasilitas kf = new KondisiFasilitas(
                idKamar,
                idFasilitas,
                kondisi,
                keteranganRusak,
                waktu
        );

        dao.insert(kf);
    }

    // ---------------------------------------------------------------
    // DELETE kondisi fasilitas by id_kamar + id_fasilitas
    // ---------------------------------------------------------------
    public void hapusKondisi(int idKamar, int idFasilitas) {
        dao.deleteByKamarAndFasilitas(idKamar, idFasilitas);
    }

    // ---------------------------------------------------------------
    // Panggil saat aplikasi ditutup agar thread pool bersih
    // ---------------------------------------------------------------
    public void shutdown() {
        service.shutdown();
    }
}