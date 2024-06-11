package ru.gb.Shop.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InfoPageBoughtProducts {
    private int countPage;
    private Integer prevPage;
    private Integer nextPage;
    private List<Long> idBoughtProductsInPage;
}
