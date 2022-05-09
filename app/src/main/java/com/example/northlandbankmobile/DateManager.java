package com.example.northlandbankmobile;

import android.os.Build;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateManager {
    private LocalDate currentDate;

    public DateManager(){

    }

    public LocalDate getCurrentDate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        return currentDate;
    }
    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }
}
