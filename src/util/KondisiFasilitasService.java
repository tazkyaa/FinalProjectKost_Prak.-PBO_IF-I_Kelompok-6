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

        // Pakai CountDownLatch agar Thread-3 (refresh UI) hanya jalan
        // SETELAH Thread-1 (simpan DB) benar-benar selesai
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        // Thread 1 - simpan kondisi ke DB, lalu release latch
        executor.submit(() -> {
            System.out.println("[Thread-1: " + Thread.currentThread().getName() + "] Menyimpan kondisi fasilitas kamar " + kf.getIdKamar() + "...");
            try {
                dao.update(kf);
                System.out.println("[Thread-1: " + Thread.currentThread().getName() + "] Kondisi berhasil disimpan!");
            } finally {
                latch.countDown(); // sinyal ke Thread-3 bahwa DB sudah selesai
            }
        });

        // Thread 2 - catat log perubahan (tetap paralel, tidak blokir UI)
        executor.submit(() -> {
            System.out.println("[Thread-2: " + Thread.currentThread().getName() + "] Mencatat log perubahan...");
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            System.out.println("[Thread-2: " + Thread.currentThread().getName() + "] Log tercatat: kamar=" + kf.getIdKamar() + ", kondisi=" + kf.getKondisi());
        });

        // Thread 3 - refresh UI, tapi tunggu Thread-1 selesai dulu via latch
        executor.submit(() -> {
            System.out.println("[Thread-3: " + Thread.currentThread().getName() + "] Menunggu DB selesai...");
            try {
                latch.await(); // blok sampai Thread-1 countDown()
            } catch (InterruptedException ignored) {}
            System.out.println("[Thread-3: " + Thread.currentThread().getName() + "] DB selesai, refresh UI!");
            if (onSelesai != null) {
                javax.swing.SwingUtilities.invokeLater(onSelesai); // balik ke EDT untuk update UI
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}