package com.pragma.powerup.infrastructure.out.jpa.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "nit")
    private String nitId;
    private String address;
    @Column(name = "phone")
    private String phoneNumber;
    @Column(name = "url_logo")
    private String logoUrl;
    @Column(name = "owner_id")
    private Long ownerId;
}
