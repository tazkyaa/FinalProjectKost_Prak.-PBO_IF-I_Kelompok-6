package model;

// PILAR - Abstraction
// Person adalah abstract class, tidak bisa dibuat objek langsung
// Hanya bisa dipake buat "blueprint"  class turunannya

public abstract class Person {

    // PILAR - Encapsulation
    // field yang sama-sama dimiliki Admin & Penghuni
    private String nama;
    private String noTelp;

    public Person() {}

    public Person(String nama, String noTelp) {
        this.nama   = nama;
        this.noTelp = noTelp;
    }

    // getter
    public String getNama()    { return nama; }
    public String getNoTelp()  { return noTelp; }

    // setter
    public String getNoTelepon() { return noTelp; }
    public void setNoTelepon(String noTelp) { this.noTelp = noTelp; }

    // PILAR - Abstraction + Polymorphism
    // method abstract wajib di-override oleh setiap subclass
    public abstract String getRole();

    @Override
    public String toString() { return nama; }
}