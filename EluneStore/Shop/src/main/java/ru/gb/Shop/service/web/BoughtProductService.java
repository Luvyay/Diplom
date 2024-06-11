package ru.gb.Shop.service.web;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.Shop.model.web.BoughtProduct;
import ru.gb.Shop.model.web.InfoPageBoughtProducts;
import ru.gb.Shop.repository.web.BoughtProductRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class BoughtProductService {
    private BoughtProductRepository boughtProductRepository;
    @PersistenceContext
    private EntityManager em;
    // Количество товаров на странице "Профиль"
    private static final int countBoughtProductInPage = 5;

    /**
     * Метод по добавлению нового проданного товара
     * @param boughtProduct
     * @return
     */
    public BoughtProduct addBoughtProducts(BoughtProduct boughtProduct) {
        return boughtProductRepository.save(boughtProduct);
    }

    /**
     * Метод по обновлению проданного товара
     * @param boughtProduct
     * @return
     */
    public BoughtProduct updateBoughtProducts(BoughtProduct boughtProduct) {
        return boughtProductRepository.save(boughtProduct);
    }

    /**
     * Метод по получению всех проданных товаров по id пользователя
     * @param idUser
     * @return
     */
    public List<BoughtProduct> getAllBoughtProductByUserId(Long idUser) {
        return em.createQuery("SELECT u FROM BoughtProduct u WHERE u.idUser = :paramId",
                        BoughtProduct.class)
                .setParameter("paramId", idUser).getResultList();
    }

    /**
     * Получение 1 страницы с купленными товарами
     * @param idUser
     * @return
     */
    public InfoPageBoughtProducts getPage(Long idUser) {
        return getPageById(1, idUser);
    }

    /**
     * Метод по получению конкретной страницы с купленными товарами
     * @param id - id страницы
     * @param idUser - id пользователя
     * @return
     */
    public InfoPageBoughtProducts getPageById(int id, Long idUser) {
        // Определяем количество страниц (если дробное число, то округляем вверх)
        int countPage = (int) Math.ceil((double) getAllBoughtProductByUserId(idUser).size() / countBoughtProductInPage);

        // Если количество страниц 0, то устанавливаем как 1,
        // т.к. при расчете OFFSET может получится отрицательное число при 0
        if (countPage == 0) countPage = 1;

        // если id страницы > количества страниц, то получаем последнюю страницу
        if (id > countPage) id = countPage;
        // если id страницы < 0, то получаем первую страницу
        if (id < 0) id = 1;

        Integer nextPage = id + 1;

        if (nextPage > countPage) nextPage = null;

        Integer prevPage = id - 1;

        if (prevPage <= 0) prevPage = null;

        int limitValue = countBoughtProductInPage;
        int offsetValue = (id - 1) * limitValue;

        List<Long> idBoughtProducts = em.createQuery("SELECT u.idProduct FROM BoughtProduct u WHERE u.idUser = :paramId ORDER BY u.id ASC LIMIT :paramLimit OFFSET :paramOffset",
                        Long.class)
                .setParameter("paramId", idUser)
                .setParameter("paramLimit", limitValue)
                .setParameter("paramOffset", offsetValue)
                .getResultList();

        return new InfoPageBoughtProducts(countPage, prevPage, nextPage, idBoughtProducts);
    }
}
