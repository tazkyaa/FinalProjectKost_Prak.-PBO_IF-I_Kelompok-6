package controller;

import dao.FasilitasDao;
import dao.KondisiFasilitasDao;
import model.Fasilitas;
import model.KondisiFasilitas;
import java.util.List;

public class FasilitasController {

    private final FasilitasDao fasilitasDao = new FasilitasDao();
    private final KondisiFasilitasDao kondisiDao = new KondisiFasilitasDao();

    // ── Fasilitas ─────────────────────────────────────────────────────────────
    public List<Fasilitas> getAll()      { return fasilitasDao.findAll(); }
    public void tambah(Fasilitas f)      { fasilitasDao.insert(f); }
    public void ubah(Fasilitas f)        { fasilitasDao.update(f); }
    public void hapus(int id)            { fasilitasDao.delete(id); }

    // ── Kondisi Fasilitas ─────────────────────────────────────────────────────
    public List<KondisiFasilitas> getAllKondisi()    { return kondisiDao.findAll(); }
    public void tambahKondisi(KondisiFasilitas kf)  { kondisiDao.insert(kf); }
    public void ubahKondisi(KondisiFasilitas kf)    { kondisiDao.update(kf); }
    public void hapusKondisi(int idKamar, int idFasilitas) {
        kondisiDao.deleteByKamarAndFasilitas(idKamar, idFasilitas);
    }
}
