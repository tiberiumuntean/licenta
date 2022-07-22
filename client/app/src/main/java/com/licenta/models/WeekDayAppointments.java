package com.licenta.models;

import java.util.List;

public class WeekDayAppointments {
    private String displayDate;
    private String queryDate;
    private List<Appointment> appointmentsList;

    public WeekDayAppointments(String displayDate, String queryDate, List<Appointment> appointmentsList) {
        this.displayDate = displayDate;
        this.queryDate = queryDate;
        this.appointmentsList = appointmentsList;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    public List<Appointment> getAppointmentsList() {
        return appointmentsList;
    }

    public void setAppointmentsList(List<Appointment> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }
}
