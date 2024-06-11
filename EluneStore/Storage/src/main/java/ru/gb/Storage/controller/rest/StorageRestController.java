package ru.gb.Storage.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.Storage.model.web.InfoPageProducts;
import ru.gb.Storage.model.web.Product;
import ru.gb.Storage.service.web.ProductService;

@RestController
@AllArgsConstructor
public class StorageRestController {
    private ProductService productService;

    /**
     * Метод по возвращению страницы товаров с остатком больше 0 по рест запросу
     * @return
     */
    @GetMapping("/products-with-quantity")
    public ResponseEntity<InfoPageProducts> getProductsWithQuantityMoreZero() {
        // isQuantityMoreZer - true, т.к. отдаем те товары у которых есть остаток
        InfoPageProducts infoPageProducts = productService.getPage(true);

        return new ResponseEntity<>(infoPageProducts, HttpStatus.OK);
    }

    /**
     * Метод по возвращению конкретной страницы (по id) с остатком больше 0 по рест запросу
     * @param id
     * @return
     */
    @GetMapping("/products-with-quantity/{id}")
    public ResponseEntity<InfoPageProducts> getProductsWithQuantityMoreZeroByIdPage(@PathVariable(name = "id") int id) {
        // isQuantityMoreZer - true, т.к. отдаем те товары у которых есть остаток
        InfoPageProducts infoPageProducts = productService.getPageById(id, true);

        return new ResponseEntity<>(infoPageProducts, HttpStatus.OK);
    }

    /**
     * Метод по возвращению конкртеного товара (по id) по рест запросу
     * @param id
     * @return
     */
    @GetMapping("/rest-product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "id") Long id) {
        Product productFromDb = productService.getProductById(id);

        if (productFromDb == null) return new ResponseEntity<>(null, HttpStatus.OK);

        return ResponseEntity.status(HttpStatus.OK).body(productFromDb);
    }

    /**
     * Метод по уменьшению количества остатков (по id) на складе на 1 по рест запросу
     * @param id
     * @return
     */
    @GetMapping("/decrease-quantity-by-id/{id}")
    public ResponseEntity<Product> decreaseQuantityById(@PathVariable(name = "id") Long id) {
        productService.decreaseQuantityById(id);

        return ResponseEntity.ok().build();
    }

    /**
     * Метод по возврату уменьшеной удиницы остатка на складе по рест запросу (т.е. +1 к остатку конкретному товару)
     * @param id
     * @return
     */
    @GetMapping("/return-decrease-quantity-by-id/{id}")
    public ResponseEntity<Product> returnDecreaseById(@PathVariable(name = "id") Long id) {
        productService.returnDecreaseById(id);

        return ResponseEntity.ok().build();
    }
}
