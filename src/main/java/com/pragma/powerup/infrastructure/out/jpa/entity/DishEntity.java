package com.pragma.powerup.infrastructure.out.jpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer price;
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    private String category;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    private Boolean active;
}
