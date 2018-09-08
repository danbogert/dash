package com.yogurtpowered.dash;

import java.util.Objects;

public class Dash {
    private final String name;
    private final String address;

    Dash(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Dash{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dash dash = (Dash) o;
        return Objects.equals(name, dash.name) &&
                Objects.equals(address, dash.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }
}
