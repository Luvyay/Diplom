package ru.gb.Storage.repository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.gb.Storage.model.web.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value="SELECT * FROM products WHERE quantity > 0", nativeQuery = true)
    List<Product> findAllWhereQuantityMoreZero();
}
