package com.ticket.app;



public class Ticket {

    private String Name;
    private String Category ;
    private String Description ;
    private String Region ;
    private String City ;
    private String Price ;
    private String Email;
    private String Tel;
    private String Date;
    private String Eventdate;

    private String Thumbnail ;
    private String utente;
    private String uid;

    //ticket class with getter and setter
    public Ticket() { }

    public Ticket(String name, String category, String eventdate, String description, String region, String city, String price, String email, String tel, String date, String thumbnail) {
        Name = name;
        Category = category;
        Eventdate = eventdate;
        Description = description;
        Region = region;
        City = city;
        Price = price;
        Email = email;
        Tel = tel;
        Date = date;
        Thumbnail=thumbnail;
    }
    public String getName() {
        return Name;
    }

    public String getCategory() {
        return Category;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente ;
    }

    public String getDescription() {
        return Description;
    }
    public String getRegion() {
        return Region;
    }
    public String getCity() {
        return City;
    }
    public String getPrice() {
        return Price;
    }
    public String getTel() {
        return Tel;
    }
    public String getDate() {
        return Date;
    }
    public String getEventdate() {
        return Eventdate;
    }
    public String getEmail() {
        return Email;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setDescription(String description) {
        Description = description;
    }
    public void setRegion(String region) {
        Region = region;
    }
    public void setTel(String tel) {
        Tel = tel;
    }
    public void setDate(String date) {
        Date = date;
    }
    public void setEventdate(String eventdate) {
        Eventdate = eventdate;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setCity(String city) {
        City = city;
    }
    public void setPrice(String price) {
        Price = price;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}