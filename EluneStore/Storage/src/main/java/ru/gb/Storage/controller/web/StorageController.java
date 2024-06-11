package ru.gb.Storage.controller.web;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.gb.Storage.model.web.InfoPageProducts;
import ru.gb.Storage.model.web.Product;
import ru.gb.Storage.service.security.UserService;
import ru.gb.Storage.service.web.ProductService;

@Controller
@AllArgsConstructor
public class StorageController {
    private UserService userService;
    private ProductService productService;

    /**
     * Метод по выводу всех товаров
     * @param model
     * @return
     */
    @GetMapping("/")
    public String getHomePage(Model model) {

        // isQuantityMoreZero - false, т.к. нам нужны все товары (даже те у которых остаток 0)
        InfoPageProducts infoPageProducts = productService.getPage(false);

        model.addAttribute("products", infoPageProducts.getProductsInPage());
        model.addAttribute("prev", infoPageProducts.getPrevPage());
        model.addAttribute("next", infoPageProducts.getNextPage());

        return "index";
    }

    /**
     * Метод по отображению конкретной страницы с товарами по id страницы
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/page/{id}")
    public String getPageWithProducts(Model model, @PathVariable(name = "id") int id) {
        // isQuantityMoreZero - false, т.к. нам нужны все товары (даже те у которых остаток 0)
        InfoPageProducts infoPageProducts = productService.getPageById(id, false);

        model.addAttribute("products", infoPageProducts.getProductsInPage());
        model.addAttribute("prev", infoPageProducts.getPrevPage());
        model.addAttribute("next", infoPageProducts.getNextPage());

        return "home";
    }

    /**
     * Метод по отображению страницы с конкретным товаром
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/product/{id}")
    public String getProductPageById(Model model, @PathVariable(name = "id") Long id) {

        model.addAttribute("product", productService.getProductById(id));

        return "product";
    }

    /**
     * Метод по удалению товара по id
     * @param id
     * @return
     */
    @GetMapping("/delete-product/{id}")
    public String deleteProductById(@PathVariable(name = "id") Long id) {
        productService.deleteProduct(id);

        return "redirect:/";
    }

    /**
     * Метод по отображению страницы для изменения товара по id
     * @param id
     * @param model
     * @param product
     * @return
     */
    @GetMapping("/change-product/{id}")
    public String getPageForChangeProductById(@PathVariable(name = "id") Long id, Model model, Product product) {

        Product productFromDb = productService.getProductById(id);

        model.addAttribute("product", productFromDb);

        return "change-product";
    }

    /**
     * Метод по изменению информации о товаре по id
     * @param id
     * @param model
     * @param product
     * @return
     */
    @PostMapping("/change-product/{id}")
    public String changeProductById(@PathVariable(name = "id") Long id, Model model, Product product) {
        productService.updateProduct(product);

        return "redirect:/product/" + id;
    }

    /**
     * Метод для отображения административной панели
     * @return
     */
    @GetMapping("/admin-panel")
    public String getAdminPage() {

        return "admin-panel";
    }

    /**
     * Метод по отображению страницы для создания нового твоара
     * @param product
     * @return
     */
    @GetMapping("/admin-panel/create-product")
    public String getPageForCreateProduct(Product product) {
        return "create-product";
    }

    /**
     * Метод по созданию нового товара
     * @param product
     * @return
     */
    @PostMapping("/admin-panel/create-product")
    public String createProduct(Product product) {
        productService.createProduct(product);

        return "redirect:/admin-panel/create-product";
    }
}
