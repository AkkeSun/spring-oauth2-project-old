package com.example.authorizationserver.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/*
    create table MEMBER
(
    TABLE_INDEX  int auto_increment primary key,
    USERNAME varchar(50)  not null,
    PASSWORD varchar(100) not null,
    ROLE varchar(50) not null
);
create index MEMBER_USERNAME_index
    on MEMBER (USERNAME);
 */
@Entity
@Data
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "TABLE_INDEX")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Enumerated(EnumType.STRING)
    private Member_Role role;
}