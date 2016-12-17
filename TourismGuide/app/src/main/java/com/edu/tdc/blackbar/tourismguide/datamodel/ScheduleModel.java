package com.edu.tdc.blackbar.tourismguide.datamodel;

/**
 * Created by ASUS on 12/14/2016.
 */

public class ScheduleModel {
    String location;
    String datefrom;
    String dateto;
    String time;
    String note;
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDatefrom() {
        return datefrom;
    }

    public void setDatefrom(String datefrom) {
        this.datefrom = datefrom;
    }

    public String getDateto() {
        return dateto;
    }

    public void setDateto(String dateto) {
        this.dateto = dateto;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ScheduleModel(String location, String datefrom, String dateto, String time, String note) {
        this.location = location;
        this.datefrom = datefrom;
        this.dateto = dateto;
        this.time = time;
        this.note = note;
    }

    public ScheduleModel(){

    }


}
