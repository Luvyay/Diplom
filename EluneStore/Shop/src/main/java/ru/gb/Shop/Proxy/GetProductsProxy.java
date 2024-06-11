package ru.gb.Shop.Proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.gb.Shop.model.web.InfoPageProducts;
import ru.gb.Shop.model.web.Product;

@FeignClient(name = "getProduct", url = "http://localhost:8765")
public interface GetProductsProxy {
    @GetMapping("/products-with-quantity")
    InfoPageProducts getProductsWithQuantityMoreZero();

    @GetMapping("/products-with-quantity/{id}")
    InfoPageProducts getProductsWithQuantityMoreZeroByIdPage(@PathVariable(name = "id") int id);

    @GetMapping("/rest-product/{id}")
    Product getProductById(@PathVariable(name = "id") Long id);

    @GetMapping("/decrease-quantity-by-id/{id}")
    void decreaseQuantityById(@PathVariable(name = "id") Long id);

    @GetMapping("/return-decrease-quantity-by-id/{id}")
    void returnDecreaseById(@PathVariable(name = "id") Long id);
}
