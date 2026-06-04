package util;

import dao.KondisiFasilitasDao;
import model.KondisiFasilitas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// MULTITHREADING - ExecutorService
// Simulasi beberapa proses jalan bersamaan saat update kondisi fasilitas
// seperti coffee shop: kasir, barista, packaging jalan paralel
public class KondisiFasilitasService {

    private final KondisiFasilitasDao dao = new KondisiFasilitasDao();

    // 3 thread jalan bersamaan
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public void updateKondisiParalel(KondisiFasilitas kf, Runnable onSelesai) {

        // Thread 1 - simpan kondisi ke DB
        executor.submit(() -> {
            System.out.println("[Thread-1: " + Thread.currentThread().getName() + "] Menyimpan kondisi fasilitas kamar " + kf.getIdKamar() + "...");
            dao.update(kf);
            System.out.println("[Thread-1: " + Thread.currentThread().getName() + "] Kondisi berhasil disimpan!");
        });

        // Thread 2 - catat log perubahan
        executor.submit(() -> {
            System.out.println("[Thread-2: " + Thread.currentThread().getName() + "] Mencatat log perubahan...");
            try { Thread.sleep(100); } catch (InterruptedException ignored) {} // simulasi proses
            System.out.println("[Thread-2: " + Thread.currentThread().getName() + "] Log tercatat: kamar=" + kf.getIdKamar() + ", kondisi=" + kf.getKondisi());
        });

        // Thread 3 - notifikasi / refresh UI
        executor.submit(() -> {
            System.out.println("[Thread-3: " + Thread.currentThread().getName() + "] Mengirim notifikasi update...");
            try { Thread.sleep(50); } catch (InterruptedException ignored) {} // simulasi proses
            System.out.println("[Thread-3: " + Thread.currentThread().getName() + "] Notifikasi terkirim!");
            if (onSelesai != null) {
                javax.swing.SwingUtilities.invokeLater(onSelesai); // balik ke EDT untuk update UI
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}