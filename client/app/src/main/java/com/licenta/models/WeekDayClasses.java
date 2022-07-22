package com.licenta.models;

import com.licenta.models.FitnessClass;

import java.util.List;

public class WeekDayClasses {
    private String displayDate;
    private String queryDate;
    private List<FitnessClass> fitnessClassList;

    public WeekDayClasses(String displayDate, String queryDate, List<FitnessClass> fitnessClassList) {
        this.displayDate = displayDate;
        this.queryDate = queryDate;
        this.fitnessClassList = fitnessClassList;
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

    public List<FitnessClass> getFitnessClassList() {
        return fitnessClassList;
    }

    public void setFitnessClassList(List<FitnessClass> fitnessClassList) {
        this.fitnessClassList = fitnessClassList;
    }
}
