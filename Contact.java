package com.example.contact.models;

public class Contact {
    private String id;
    private String Name;
    private String Fname;
    private String Phone;
    private String Email;
    private String Address;

    public Contact(String id, String Name, String Fname, String Phone
            , String Email, String Address) {
        this.id = id;
        this.Name = Name;
        this.Fname = Fname;
        this.Email = Email;
        this.Phone = Phone;
        this.Address = Address;
    }

    //Getter
    public String getId() { return id; }

    public String getName() {
        return Name;
    }

    public String getFname() {
        return Fname;
    }

    public String getPhone() {
        return Phone;
    }

    public String getEmail() {
        return Email;
    }

    public String getAddress() {
        return Address;
    }

    //Setter
    public void setName(String name) {
        this.Name = name;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPhone(String phone){ Phone = phone; }

    public void setAddress(String address) {
        Address = address;
    }

}
