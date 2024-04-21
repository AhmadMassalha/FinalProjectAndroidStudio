package com.example.androidfinalproject.Utilities;

import java.io.Serializable;

public class Appointment implements Serializable {
    private String workerName;
    private String clientName;
    private String time;
    private String date;

    // No-argument constructor required for Firebase deserialization
    public Appointment() {
    }

    public Appointment(String workerName, String clientName, String time, String date) {
        this.workerName = workerName;
        this.clientName = clientName;
        this.time = time;
        this.date = date;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "workerName='" + workerName + '\'' +
                ", clientName='" + clientName + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
