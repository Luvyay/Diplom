package ru.gb.Shop.model.web;

import lombok.Data;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private String urlPhoto;
    private int price;
    private int quantity;
}
