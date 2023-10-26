package com.kplritesh.treeleaf.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class NewUserProfileEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String email;
    private String address;
    private String gender;
    private String countryCode;
    private String phone;
    private String photoUri;
    private String description;

    public NewUserProfileEntity() {}

    public NewUserProfileEntity(int id, String name, String email, String address, String gender, String countryCode, String phone, String photoUri, String description) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.countryCode = countryCode;
        this.phone = phone;
        this.photoUri = photoUri;
        this.description = description;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public String getGender() {return gender;}

    public void setGender(String gender) {this.gender = gender;}

    public String getCountryCode() {return countryCode;}

    public void setCountryCode(String countryCode) {this.countryCode = countryCode;}

    public String getPhone() {return phone;}

    public void setPhone(String phone) {this.phone = phone;}

    public String getPhotoUri() {return photoUri;}

    public void setPhotoUri(String photoUri) {this.photoUri = photoUri;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}
}
