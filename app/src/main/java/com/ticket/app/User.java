package com.ticket.app;

import java.util.List;
import java.util.Map;

public class User {

    //private Map<String,String> Preferiti;
    private String email, name, surname, cell;

    public User(){
    }


    public User (String email, String name, String surname, String cell)
    {
        this.surname=surname;
        this.name=name;
        this.email=email;
        this.cell=cell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }
}