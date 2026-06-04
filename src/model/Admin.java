package model;

// PILAR - Inheritance
// Admin mewarisi field nama & noTelp dari Person
// ga perlu deklarasi lagi di sini

public class Admin extends Person {

    // PILAR - Encapsulation
    private int    idAdmin;
    private String username;
    private String password;

    public Admin() {}

    public Admin(int idAdmin, String nama, String noTelp, String username, String password) {
        super(nama, noTelp); // memanggil constructor Person
        this.idAdmin  = idAdmin;
        this.username = username;
        this.password = password;
    }

    // getter
    public int    getIdAdmin()  { return idAdmin; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // setter
    public void setIdAdmin(int idAdmin)       { this.idAdmin  = idAdmin; }
    public void setUsername(String username)  { this.username = username; }
    public void setPassword(String password)  { this.password = password; }

    // PILAR - Polymorphism
    // override method abstract dari Person
    @Override
    public String getRole() { return "Admin"; }
}