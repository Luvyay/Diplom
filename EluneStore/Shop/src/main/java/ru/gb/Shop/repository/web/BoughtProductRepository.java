package ru.gb.Shop.repository.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.Shop.model.web.BoughtProduct;

@Repository
public interface BoughtProductRepository extends JpaRepository<BoughtProduct, Long> {
}
