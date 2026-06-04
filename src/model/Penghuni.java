package model;

// PILAR - Inheritance
// Penghuni mewarisi field nama & noTelp dari Person
public class Penghuni extends Person {

    // PILAR - Encapsulation
    private int    idPenghuni;
    private int    idKamar;
    private String namaKamar;
    private String asal;
    private String tglMasuk;
    private String statusPenghuni;
    private String namaOrtu;
    private String telpOrtu;

    public Penghuni() {}

    public Penghuni(int idPenghuni, String nama, String noTelp, int idKamar, String asal,
                    String tglMasuk, String statusPenghuni, String namaOrtu, String telpOrtu) {
        super(nama, noTelp); // memanggil constructor Person
        this.idPenghuni     = idPenghuni;
        this.idKamar        = idKamar;
        this.asal           = asal;
        this.tglMasuk       = tglMasuk;
        this.statusPenghuni = statusPenghuni;
        this.namaOrtu       = namaOrtu;
        this.telpOrtu       = telpOrtu;
    }

    // getter
    public int    getIdPenghuni()      { return idPenghuni; }
    public int    getIdKamar()         { return idKamar; }
    public String getNamaKamar() { return namaKamar; }
    public String getAsal()            { return asal; }
    public String getTglMasuk()        { return tglMasuk; }
    public String getStatusPenghuni()  { return statusPenghuni; }
    public String getNamaOrtu()        { return namaOrtu; }
    public String getTelpOrtu()        { return telpOrtu; }

    // setter
    public void setIdPenghuni(int idPenghuni)           { this.idPenghuni     = idPenghuni; }
    public void setIdKamar(int idKamar)                 { this.idKamar        = idKamar; }
    public void setNamaKamar(String namaKamar) { this.namaKamar = namaKamar; }
    public void setAsal(String asal)                    { this.asal           = asal; }
    public void setTglMasuk(String tglMasuk)            { this.tglMasuk       = tglMasuk; }
    public void setStatusPenghuni(String statusPenghuni){ this.statusPenghuni = statusPenghuni; }
    public void setNamaOrtu(String namaOrtu)            { this.namaOrtu       = namaOrtu; }
    public void setTelpOrtu(String telpOrtu)            { this.telpOrtu       = telpOrtu; }

    // PILAR - Polymorphism
    // override method abstract dari Person
    @Override
    public String getRole() { return "Penghuni"; }
}