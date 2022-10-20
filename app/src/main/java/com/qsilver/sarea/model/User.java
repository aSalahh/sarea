package com.qsilver.sarea.model;

import java.io.Serializable;

public class User implements Serializable {
    private String email, civilNumber, fatherPhone, matherPhone, level, name, address, id, type, schoolId, ImageId, travelId,travelType;
    private UserLocation userLocation;
    private boolean inBus;

    public User() {
    }

    public boolean isInBus() {
        return inBus;
    }

    public void setInBus(boolean inBus) {
        this.inBus = inBus;
    }

    public User(String email, String civilNumber, String fatherPhone, String matherPhone,
                String level, String name, String address, String id, String type,
                String schoolId, String imageId, String travelId, UserLocation userLocation, boolean inBus ,String travelType) {
        this.email = email;
        this.civilNumber = civilNumber;
        this.fatherPhone = fatherPhone;
        this.matherPhone = matherPhone;
        this.level = level;
        this.name = name;
        this.address = address;
        this.id = id;
        this.type = type;
        this.schoolId = schoolId;
        ImageId = imageId;
        this.travelId = travelId;
        this.userLocation = userLocation;
        this.inBus = inBus;
        this.travelType=travelType;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public User(String email, String civilNumber, String name,
                String address, String id, String type, String schoolId,
                String imageId, UserLocation userLocation, boolean inBus) {
        this.email = email;
        this.civilNumber = civilNumber;
        this.name = name;
        this.address = address;
        this.id = id;
        this.type = type;
        this.schoolId = schoolId;
        this.ImageId = imageId;
        this.userLocation = userLocation;
        this.inBus=inBus;
    }

    public User(String email, String civilNumber, String fatherPhone,
                String matherPhone, String level, String name, String address, String id, String type
            , String schoolId, String imageId, String travelId, UserLocation userLocation) {
        this.email = email;
        this.civilNumber = civilNumber;
        this.fatherPhone = fatherPhone;
        this.matherPhone = matherPhone;
        this.level = level;
        this.name = name;
        this.address = address;
        this.id = id;
        this.type = type;
        this.schoolId = schoolId;
        ImageId = imageId;
        this.travelId = travelId;
        this.userLocation = userLocation;
    }

    public User(String email, String civilNumber,
                String fatherPhone, String matherPhone, String level, String name, String address,
                String id, String type, String schoolId, String imageId, UserLocation userLocation,boolean inBus) {
        this.email = email;
        this.civilNumber = civilNumber;
        this.fatherPhone = fatherPhone;
        this.matherPhone = matherPhone;
        this.level = level;
        this.name = name;
        this.address = address;
        this.id = id;
        this.type = type;
        this.schoolId = schoolId;
        ImageId = imageId;
        this.userLocation = userLocation;
        this.inBus=inBus;
    }

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCivilNumber() {
        return civilNumber;
    }

    public void setCivilNumber(String civilNumber) {
        this.civilNumber = civilNumber;
    }

    public String getFatherPhone() {
        return fatherPhone;
    }

    public void setFatherPhone(String fatherPhone) {
        this.fatherPhone = fatherPhone;
    }

    public String getMatherPhone() {
        return matherPhone;
    }

    public void setMatherPhone(String matherPhone) {
        this.matherPhone = matherPhone;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getImageId() {
        return ImageId;
    }

    public void setImageId(String imageId) {
        ImageId = imageId;
    }

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }
}
