# Дипломный проект

## Все что нужно для проверки

### Поднятие контейнеров с БД

Физически базы данных находятся в проектах. База данных применялась для 3 приложений:
- Bank:<br>
    Для поднятия контейнера с БД нужно указать следующую комманду:
    docker run --name bankdb -e POSTGRES_DB=bankdb -e POSTGRES_USER=adminbank 
    -e POSTGRES_PASSWORD=mypassword -p 5432:5432 -e PGDATA=/var/lib/postgresql/data/pgdata 
    -d -v D:\GeekBrains\Diplom\ShopWoW\Bank\src\main\resources\db:/var/lib/postgresql/data postgres<br>
    Вместо "D:\GeekBrains\Diplom\ShopWoW\" нужно указать путь до папки с проектом Bank.
- Storage:<br>
    Для поднятия контейнера с БД нужно указать следующую комманду: docker run --name storagedb 
    -e POSTGRES_DB=storagedb -e POSTGRES_USER=adstorage -e POSTGRES_PASSWORD=mypassword2 -p 5430:5430 
    -e PGDATA=/var/lib/postgresql/data/pgdata -d -v D:\GeekBrains\Diplom\ShopWoW\EluneStore\Storage\src\main\resources\db:/var/lib/postgresql/data postgres<br>
    Вместо "D:\GeekBrains\Diplom\ShopWoW\" нужно указать путь до папки с проектом EluneStore.
- Shop:<br>
    Для поднятия контейнера с БД нужно указать следующую комманду: docker run --name shopdb 
    -e POSTGRES_DB=shopdb -e POSTGRES_USER=adshop -e POSTGRES_PASSWORD=mypassword2 -p 5431:5431 
    -e PGDATA=/var/lib/postgresql/data/pgdata -d -v D:\GeekBrains\Diplom\ShopWoW\EluneStore\Shop\src\main\resources\db:/var/lib/postgresql/data postgres<br>
    Вместо "D:\GeekBrains\Diplom\ShopWoW\" нужно указать путь до папки с проектом EluneStore.

### Порты и ссылки
Bank: http://localhost:8081<br>
Payment: port 8078<br>
Shop: http://localhost:8080<br>
Storage: http://localhost:8079<br>
Eureka: http://localhost:8761<br>
Gateway: port 8765<br>

### Логины и пароли

Bank:
- User:<br>
    login: user<br>
    password: password
- Admin:<br>
    login: admin<br>
    password: password

Storage:
- admin:<br>
    login: admin<br>
    password: password

Shop:
- admin<br>
    login: admin<br>
    password: password

### Как проверять

Необходимо запустить в следующем порядке: Eureka, Gateway, Bank, Storage, Shop и Payment. 
Самое главное запустить первым Eureka, потом Gateway, а дальше без разницы.<br>

Можно залогиниться в банке и перейти в админку с отображением всех пользователей 
(для того, чтобы просматривать как изменится баланс пользователей). После можно перейти в 
Shop и по разному посмотреть на работуспособность (как отображается товар для не авторизированных пользователей,
как для авторизированных, залогиниться и осуществить покупку). Для осуществления покупки необходимо 
залогиниться под данными админа (т.к. другого пользователя по моему нет) и при оплате указать номер карты 
администратора (номер карты администратора 1). Осуществлять покупки могут только те пользователи, 
которые зарегестрированы в банке (они соответственно при оплате указывают номер своей карты из банка). 
По дефолту владельцем сайта (тому, кому приходят деньги за покупку товара) является пользователь банка 
с id 2 и номером карты 2.

## Описание приложений

### Bank

Это вспомогательное приложение, которое имитирует банк. Разработано для корректного отображения 
осуществления оплаты товаров в магазине.

### EluneStore

Это полноценное приложение, которое состоит из следующих элементов:
- Storage;
- Shop;
- Payment.

#### Shop

Центральное приложение, которое взаимодействует с микросервисами. Данное приложение является интернет-магазином. 
Оно отображает товары, позволяет пользователям зарегестрироваться, вайти в систему, осуществлять покупку товаров и 
просматривать историю покупок.

#### Storage

Микросервис иметирующий склад. В целом можно это назвать отдельным приложением, т.к. данный микросервис 
имеет визуальное отображение в браузере для удобства. Доступ к данному ресурсу имеет только администратор. 
Данный микросервис отображает все товары (с остаткоми без), позволяет переходить к конкретному товару и 
либо удалить его, либо редактировать, а также имеет административную панель, где можно создать новый товар. 
Имеет рест точки, которые позволяют получать другим ресурсам список товаров у которых есть остатки и 
получать информацию по конкретному товару, а также осуществлять уменьшение остатков или увеличение остатков.

#### Payment

Микросервис для осуществления оплаты в интернет-магазине. Данный микросервис имеет рест точку, по которой 
получает информацию о покупке и на основе этой информации и информации, которая указана у данного микросервиса 
составляет запрос в банк для осуществления перевода с одного счета на другой. После чего возвращает статус перевода.