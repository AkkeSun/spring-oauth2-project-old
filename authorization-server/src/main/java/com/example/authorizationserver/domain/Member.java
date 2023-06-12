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
create table local_test.MEMBER
(
    TABLE_INDEX int auto_increment primary key,
    USERNAME          varchar(50)  not null,
    PASSWORD          varchar(150) not null,
    ROLE              varchar(50)  not null,
    SNS_SYNC          varchar(50)  null,
    SNS_SECRET_KEY    varchar(150) null
 );

create index MEMBER_USERNAME_SNS_SYNC_index
    on local_test.MEMBER (USERNAME, SNS_SYNC);

create index MEMBER_USERNAME_index
    on local_test.MEMBER (USERNAME);
 */
@Entity
@Data
@Table(name = "MEMBER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TABLE_INDEX")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SNS_SYNC")
    private String snsSync;

    @Column(name = "SNS_SECRET_KEY")
    private String snsSecretKey;

    @Enumerated(EnumType.STRING)
    private Role role;
}