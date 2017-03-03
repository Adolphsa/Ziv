package com.zividig.ziv.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 *
 * Created by adolph
 * on 2017-03-03.
 */
@Entity
public class Users {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "username")
    private String username;

    @Property(nameInDb = "password")
    private String password;

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 114396017)
    public Users(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Generated(hash = 2146996206)
    public Users() {
    }
}
