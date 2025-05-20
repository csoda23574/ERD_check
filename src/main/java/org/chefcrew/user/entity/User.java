package org.chefcrew.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "pw")
    private String password;

    @Column(name = "kakao_id")
    private Long kakaoId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "profile")
    private String profile;

    @Column(name = "refreshToken")
    private String refreshToken;

    @Column(name = "socialId", nullable = false)
    private String socialId;

    public User(String email, String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }

    public User(String email, String password, String userName, Long kakaoId) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.kakaoId = kakaoId;
    }

    public User(String email, String password, String userName, Long kakaoId, String nickname, String profile, String refreshToken, String socialId) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.profile = profile;
        this.refreshToken = refreshToken;
        this.socialId = socialId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", kakaoId=" + kakaoId +
                ", nickname='" + nickname + '\'' +
                ", profile='" + profile + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", socialId='" + socialId + '\'' +
                '}';
    }
}
