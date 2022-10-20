package com.qsilver.sarea.model;

import java.io.Serializable;
import java.util.List;

public class Travel implements Serializable {
    private User driver;
    private String time;
    private UserLocation travelStartLocation;
    private UserLocation travelEndLocation;
    private String schoolId;
    private String travelDays;
    private String travelId;
    private String type;
    private UserLocation busLocation;


    public Travel() {
    }


    public Travel(User driver, String time, UserLocation travelStartLocation, UserLocation travelEndLocation, String schoolId, String travelDays, String travelId, String type, UserLocation busLocation) {
        this.driver = driver;
        this.time = time;
        this.travelStartLocation = travelStartLocation;
        this.travelEndLocation = travelEndLocation;
        this.schoolId = schoolId;
        this.travelDays = travelDays;
        this.travelId = travelId;
        this.type = type;
        this.busLocation = busLocation;
    }

    public UserLocation getBusLocation() {
        return busLocation;
    }

    public void setBusLocation(UserLocation busLocation) {
        this.busLocation = busLocation;
    }

    public String getTravelDays() {
        return travelDays;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTravelDays(String travelDays) {
        this.travelDays = travelDays;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public UserLocation getTravelStartLocation() {
        return travelStartLocation;
    }

    public void setTravelStartLocation(UserLocation travelStartLocation) {
        this.travelStartLocation = travelStartLocation;
    }

    public UserLocation getTravelEndLocation() {
        return travelEndLocation;
    }

    public void setTravelEndLocation(UserLocation travelEndLocation) {
        this.travelEndLocation = travelEndLocation;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getTravelId() {
        return travelId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public Travel( User driver, String time, UserLocation travelStartLocation, UserLocation travelEndLocation, String schoolId, String travelId) {

        this.driver = driver;
        this.time = time;
        this.travelStartLocation = travelStartLocation;
        this.travelEndLocation = travelEndLocation;
        this.schoolId = schoolId;
        this.travelId = travelId;
    }
}
