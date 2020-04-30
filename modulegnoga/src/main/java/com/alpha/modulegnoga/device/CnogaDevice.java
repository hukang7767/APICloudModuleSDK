package com.alpha.modulegnoga.device;

public class CnogaDevice {
    private String address;

    private String name;

    private boolean available;

    private boolean paired;

    public CnogaDevice(String address, String name, boolean available, boolean paired) {
        this.address = address;
        this.name = name;
        this.available = available;
        this.paired = paired;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }
}
