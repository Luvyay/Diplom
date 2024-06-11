package ru.gb.Shop.model.web.enums;

public enum StatusPay {
    // все окей
    OK,
    // недостаточно денег
    NOT_ENOUGH_MONEY,
    // не найден номер карты или указан не верно номер карты продавца в микросервисе Payment,
    USER_NOT_FOUND,
    //товар не найден или его остаток равен 0
    NO_QUANTITY,
    ERROR_PAYMENT
}
