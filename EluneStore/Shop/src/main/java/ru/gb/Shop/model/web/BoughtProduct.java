package ru.gb.Shop.model.web;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bought_products")
public class BoughtProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "id_user")
    private Long idUser;
    @Column(name = "product_id")
    private Long idProduct;

    public BoughtProduct() {
    }

    public BoughtProduct(Long idUser, Long idProduct) {
        this.idUser = idUser;
        this.idProduct = idProduct;
    }
}
