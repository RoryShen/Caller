package com.ck.testassistant.been;

/**
 * Created by Rory on 2/8/2018.
 */

public class ContactsInfo {
    private String name;
    private String phone;
    private String email;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String toString() {
        return "Contact [name=" + name + ", phone=" + phone + ", email="
                + email + "]";
    }
}
