package ru.gb.Bank.model.web.enums;

public enum StatusPay {
    // все окей
    OK,
    // недостаточно денег
    NOT_ENOUGH_MONEY,
    // не найден номер карты или указан не верно номер карты продавца в микросервисе Payment,
    USER_NOT_FOUND
}
