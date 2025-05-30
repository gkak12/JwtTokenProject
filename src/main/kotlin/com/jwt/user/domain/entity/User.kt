package com.jwt.user.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "USER")
open class User() {

    @Id
    @Column(name = "ID")
    open var id: String = ""

    @Column(name = "PASSWORD")
    open var password: String = ""

    @Column(name = "NAME")
    open var name: String = ""

    @Column(name = "AUTH")
    open var auth: String = ""
}
