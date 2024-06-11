package ru.gb.Storage.service.web;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.Storage.aspect.TrackUserAction;
import ru.gb.Storage.model.web.InfoPageProducts;
import ru.gb.Storage.model.web.Product;
import ru.gb.Storage.repository.web.ProductRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;
    @PersistenceContext
    private EntityManager em;
    // Количество товаров на странице "Главная"
    private static final int countProductInPage = 8;

    /**
     * Метод по получению всех товаров
     * @return
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Метод по получению всех товаров у которых количество (quantity) больше 0
     * @return
     */
    public List<Product> getAllProductsWhereQuantityMoreZero() {
        return productRepository.findAllWhereQuantityMoreZero();
    }

    /**
     * Метод по получению 1 страницы с товарами
     * @param isQuantityMoreZero - логическая переменная для определения формирования объекта с товарами имеющих
     *                          остаток или всех (включая без остатка)
     * @return
     */
    public InfoPageProducts getPage(boolean isQuantityMoreZero) {
        return getPageById(1, isQuantityMoreZero);
    }

    /**
     * Метод по получению конкретной страницы с товарами
     * @param id
     * @param isQuantityMoreZero - логическая переменная для определения формирования объекта с товарами имеющих
     *                           остаток или всех (включая без остатка)
     * @return
     */
    public InfoPageProducts getPageById(int id, boolean isQuantityMoreZero) {
        int countPage;

        if (isQuantityMoreZero) {
            countPage = (int) Math.ceil((double) getAllProductsWhereQuantityMoreZero().size() / countProductInPage);
        } else {
            countPage = (int) Math.ceil((double) getAllProducts().size() / countProductInPage);
        }

        if (countPage == 0) countPage = 1;

        if (id > countPage) id = countPage;
        if (id < 0) id = 1;

        Integer nextPage = id + 1;

        if (nextPage > countPage) nextPage = null;

        Integer prevPage = id - 1;

        if (prevPage <= 0) prevPage = null;

        int limitValue = countProductInPage;
        int offsetValue = (id - 1) * limitValue;

        List<Product> products;

        if (isQuantityMoreZero) {
            products = em.createQuery("SELECT u FROM Product u WHERE u.quantity > 0 ORDER BY u.id ASC LIMIT :paramLimit OFFSET :paramOffset",
                            Product.class)
                    .setParameter("paramLimit", limitValue)
                    .setParameter("paramOffset", offsetValue)
                    .getResultList();
        } else {
            products = em.createQuery("SELECT u FROM Product u ORDER BY u.id ASC LIMIT :paramLimit OFFSET :paramOffset",
                            Product.class)
                    .setParameter("paramLimit", limitValue)
                    .setParameter("paramOffset", offsetValue)
                    .getResultList();
        }

        return new InfoPageProducts(countPage, prevPage, nextPage, products);
    }

    /**
     * Метод по получению конкретного товара по id
     * @param id
     * @return
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    /**
     * Метод по созданию нового товара
     * @param product
     * @return
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Метод по обновлению информации о товаре
     * @param product
     * @return
     */
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Метод по удалению товара по id
     * @param id
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Метод по уменьшению количества (quantity) у конкретного товара по id
     * @param id
     */
    @TrackUserAction
    public void decreaseQuantityById(Long id) {
        Product findProduct = productRepository.findById(id).orElse(null);

        if (findProduct != null) {
            findProduct.setQuantity(findProduct.getQuantity() - 1);
            updateProduct(findProduct);
        }
    }

    /**
     * Метод по увелечению количества (quantity) у конкретного твоара по id
     * @param id
     */
    @TrackUserAction
    public void returnDecreaseById(Long id) {
        Product findProduct = productRepository.findById(id).orElse(null);

        if (findProduct != null) {
            findProduct.setQuantity(findProduct.getQuantity() + 1);
            updateProduct(findProduct);
        }
    }
}
