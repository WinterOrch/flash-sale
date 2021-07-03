package com.winter.flashsale.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleUser {

    private Long id;
    private String nickname;

    private String password;
    private String salt;

    private String avatar;

    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;

    @Override
    public String toString() {
        return "FlashSaleUser{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", registerDate=" + registerDate +
                ", lastLoginDate=" + lastLoginDate +
                ", loginCount=" + loginCount +
                '}';
    }
}
