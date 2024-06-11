package ru.gb.Shop.controller;

import feign.codec.DecodeException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.Shop.Proxy.GetProductsProxy;
import ru.gb.Shop.Proxy.RequestToPaymentProxy;
import ru.gb.Shop.model.web.BoughtProduct;
import ru.gb.Shop.model.web.InfoPageBoughtProducts;
import ru.gb.Shop.model.web.InfoPageProducts;
import ru.gb.Shop.model.web.Product;
import ru.gb.Shop.model.web.enums.StatusPay;
import ru.gb.Shop.service.security.UserService;
import ru.gb.Shop.service.web.BoughtProductService;
import ru.gb.Shop.service.web.FileProductsGetawayInterface;

import java.math.BigDecimal;
import java.util.List;

@Controller
@AllArgsConstructor
public class StoreController {
    private BoughtProductService boughtProductService;
    private GetProductsProxy getProductsProxy;
    private RequestToPaymentProxy requestToPaymentProxy;
    private UserService userService;
    private FileProductsGetawayInterface fileProductsGetawayInterface;
    private static final String NAME_OF_FILE_WITH_PRODUCTS = "products.txt";

    /**
     * Метод по отображанию каталога товаров
     * @param model
     * @return
     */
    @GetMapping("/")
    public String getHomePageWithProducts(Model model) {
        // запрашиваем товары с остатком у микросервиса Storage
        InfoPageProducts infoPageProducts = getProductsProxy.getProductsWithQuantityMoreZero();

        model.addAttribute("products", infoPageProducts.getProductsInPage());
        model.addAttribute("prev", infoPageProducts.getPrevPage());
        model.addAttribute("next", infoPageProducts.getNextPage());

        return "home";
    }

    /**
     * Метод по отображанию страницы каталога по id
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/page/{id}")
    public String getPageWithProducts(Model model, @PathVariable(name = "id") int id) {
        // запрашиваем товары с остатком у микросервиса Storage
        InfoPageProducts infoPageProducts = getProductsProxy.getProductsWithQuantityMoreZeroByIdPage(id);

        model.addAttribute("products", infoPageProducts.getProductsInPage());
        model.addAttribute("prev", infoPageProducts.getPrevPage());
        model.addAttribute("next", infoPageProducts.getNextPage());

        return "home";
    }

    /**
     * Метод по отображению страницы с товаром по id
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/product/{id}")
    public String getProductPageById(Model model, @PathVariable(name = "id") Long id) {
        // запрашиваем конкретный товар у микросервиса Storage
        Product productFromStorage = getProductsProxy.getProductById(id);

        // если товар равен null или у него остаток равен 0, то возвращаем на главную страницу
        if (productFromStorage == null || productFromStorage.getQuantity() == 0) return "redirect:/";

        model.addAttribute("product", productFromStorage);

        return "product";
    }

    /**
     * Метод по отображению страницы для осуществления оплаты
     * На данной странице выводится форма для указания номера карты покупателя
     * @param idProduct
     * @param model
     * @param numberOfCard
     * @return
     */
    @GetMapping("/buy")
    public String getPageForWriteNumberOfCard(@RequestParam() Long idProduct, Model model, String numberOfCard) {

        model.addAttribute("idProduct", idProduct);

        return "buy";
    }

    /**
     * Метод по осуществлению покупки товара
     * @param idProduct
     * @param model
     * @param numberOfCard - номер карты банка покупателя
     * @return
     */
    @PostMapping("/buy")
    public String payProduct(@RequestParam() Long idProduct, Model model, String numberOfCard) {
        // запрашиваем конкретный товар у микросервиса Storage
        Product productFromStorage = getProductsProxy.getProductById(idProduct);

        // если товар равен null или у него остаток равен 0, то перенаправляем на страницу со статусом NO_QUANTITY
        if (productFromStorage == null || productFromStorage.getQuantity() == 0) return "redirect:/status-page/NO_QUANTITY";

        // уменьшаем остаток товара на складе (микросервис Storage)
        getProductsProxy.decreaseQuantityById(idProduct);

        try {
            // осуществляем оплату через микросервис Payment (передаем номер карты покупателя и стоимость товара)
            StatusPay status = requestToPaymentProxy.toDoPay(numberOfCard, new BigDecimal(productFromStorage.getPrice()));

            // если статус покупки не равен StatusPay.OK, то возвращаем остаток у товара
            if (status != StatusPay.OK) {
                getProductsProxy.returnDecreaseById(idProduct);
            }

            // перенаправялем пользователя на страницу со статусом
            return "redirect:/status-page?status=" + status.name() + "&idProduct=" + productFromStorage.getId();

        } catch (DecodeException e) {
            // в случае возникновения ошибки возвращаем остаток и перенаправляем на страницу со статусом

            getProductsProxy.returnDecreaseById(idProduct);

            return "redirect:/status-page?status=" + StatusPay.ERROR_PAYMENT
                    + "&idProduct=" + productFromStorage.getId();
        }
    }

    /**
     * Метод по отображению статуса покупки
     * @param status - статус покупки (OK - успешно, NOT_ENOUGH_MONEY - недостаточно денег,
     *           USER_NOT_FOUND - не найден номер карты или указан не верно номер карты продавца в микросервисе Payment,
     *           NO_QUANTITY - товар не найден или его остаток равен 0)
     * @param idProduct
     * @param model
     * @return
     */
    @GetMapping("/status-page")
    public String getStatusPageOfPay(@RequestParam() String status, @RequestParam() Long idProduct, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        Long idUser = userService.findUserByName(userDetails.getUsername()).getId();

        if (status.equals(StatusPay.OK.name())) {
            boughtProductService.addBoughtProducts(new BoughtProduct(idUser, idProduct));

            // вытягиваем купленный товар и записываем его в файл со всеми проданными товарами
            Product product = getProductsProxy.getProductById(idProduct);
            fileProductsGetawayInterface.writeToFile(NAME_OF_FILE_WITH_PRODUCTS, product);
        }

        model.addAttribute("status", status);
        model.addAttribute("idProduct", idProduct);

        return "status-page";
    }

    /**
     * Метод, который перенаправляет пользователя на страницу /profile/1
     * @return
     */
    @GetMapping("/profile")
    public String getProfileWithBoughtProducts() {
        return "redirect:/profile/1";
    }

    /**
     * Метод по отображению профиля пользователя со списком купленных товаров
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/profile/{idPage}")
    public String getPageWithBoughtProducts(Model model, @PathVariable(name = "idPage") int id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        model.addAttribute("username", userDetails.getUsername());

        Long idUser = userService.findUserByName(userDetails.getUsername()).getId();

        // Получение информации по страницам купленных товаров
        InfoPageBoughtProducts infoPageBoughtProducts = boughtProductService.getPageById(id, idUser);

        // infoPageBoughtProducts.getIdBoughtProductsInPage() предоставляет список с id товаров
        // и необходимо это перевести в список самих товаров
        List<Product> boughtProductsInPage = infoPageBoughtProducts.getIdBoughtProductsInPage().stream()
                .map(idProduct -> getProductsProxy.getProductById(idProduct))
                .toList();

        model.addAttribute("products", boughtProductsInPage);
        model.addAttribute("prev", infoPageBoughtProducts.getPrevPage());
        model.addAttribute("next", infoPageBoughtProducts.getNextPage());

        return "profile";
    }
}
