package com.licenta;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class GlobalData {
    public static final String RETROFIT_BASE_URL = "http://10.0.2.2:6601/api/";

    public static String getTodaysDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String todayDate = df.format(today);

        return todayDate;
    }

    public static String getDisplayDateRomanian(String databaseDate) {
        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Date startDate = initialFormat.parse(databaseDate);
            String startDateString = outputFormat.format(startDate);

            return startDateString;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    public static String getDisplayDateTimeRomanian(String databaseDate) {
        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        try {
            Date startDate = initialFormat.parse(databaseDate);
            String startDateString = outputFormat.format(startDate);

            return startDateString;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    public static String getDisplayDate(String databaseDate) {
        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date startDate = initialFormat.parse(databaseDate);
            String startDateString = outputFormat.format(startDate);

            return startDateString;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    public static String getDisplayTime(String databaseDate) {
        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

        try {
            Date startDate = initialFormat.parse(databaseDate);
            String startDateString = outputFormat.format(startDate);

            return startDateString;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    public static String getDisplayDateTime(String databaseDate) {
        SimpleDateFormat initialFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");

        try {
            Date startDate = initialFormat.parse(databaseDate);
            String startDateString = outputFormat.format(startDate);

            return startDateString;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    public static String parseDate(String displayDate) {
        SimpleDateFormat initialFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date startDate = initialFormat.parse(displayDate);
            String startDateString = outputFormat.format(startDate);

            return startDateString;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    public static String parseDateSecondary(String displayDate) {
        SimpleDateFormat initialFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date startDate = initialFormat.parse(displayDate);
            String startDateString = outputFormat.format(startDate);

            return startDateString;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calculateAge(String birthday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dob = LocalDate.parse(birthday.split(" ")[0], formatter);

        LocalDate curDate = LocalDate.now();
        if ((dob != null) && (curDate != null)) {
            return Period.between(dob, curDate).getYears();
        } else {
            return 0;
        }
    }
}
