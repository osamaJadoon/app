package com.example.adminapp;

public class DriverData {
    private String driverName;
    private String driverEmail;
    private Integer driverBusNumber;
    private String driverPassword;
    private String status;

    public DriverData() {
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public Integer getDriverBusNumber() {
        return driverBusNumber;
    }

    public void setDriverBusNumber(Integer driverBusNumber) {
        this.driverBusNumber = driverBusNumber;
    }

    public String getDriverPassword() {
        return driverPassword;
    }

    public void setDriverPassword(String driverPassword) {
        this.driverPassword = driverPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
