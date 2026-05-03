# B2B Shop Demo

Demonstracja integracji z AuthenID (OAuth 2.1 + OpenID Connect).
Platforma e-commerce B2B — widoki zmieniają się w zależności od roli użytkownika.

---

## Konta demo

| Username   | Hasło        | Rola       | Dostęp                              |
|------------|--------------|------------|-------------------------------------|
| `buyer1`   | `Buyer1234!` | B2B_BUYER  | Catalog, Orders                     |
| `buyer2`   | `Buyer1234!` | B2B_BUYER  | Catalog, Orders                     |
| `sales1`   | `Sales1234!` | B2B_SALES  | Catalog, Pricing                    |
| `b2badmin` | `Admin1234!` | B2B_ADMIN  | Catalog, Orders, Pricing, Account   |

---

## Uruchomienie lokalne (development)

### Wymagania
- Java 21
- Maven lub `./mvnw`
- AuthenID backend uruchomiony na `localhost:9000`

### Kroki

```bash
# 1. Uruchom MySQL i AuthenID backend (z katalogu AuthenID/docker/)
docker compose up -d mysql
cd ../backend && ./mvnw spring-boot:run

# 2. Uruchom demo (z katalogu AuthenID/demo/)
./mvnw spring-boot:run

# 3. Otwórz http://localhost:8080
```

---

## Uruchomienie w Dockerze (cały stack)

```bash
# Z katalogu AuthenID/docker/

# Cały stack: MySQL + AuthenID backend + frontend + demo
docker compose up -d

# Tylko demo (AuthenID backend musi działać lokalnie na porcie 9000)
docker compose -f docker-compose.demo.yml up -d

# Zatrzymaj wszystko
docker compose down

# Zatrzymaj i usuń dane (reset bazy)
docker compose down -v
```

### Adresy po uruchomieniu

| Serwis           | URL                        |
|------------------|----------------------------|
| B2B Shop Demo    | http://localhost:8080      |
| AuthenID Backend | http://localhost:9000      |
| Admin Panel      | http://localhost:3000      |

---

## Rebuild po zmianach w kodzie

```bash
# Zatrzymaj kontener
docker compose -f docker-compose.demo.yml down

# Jeśli sieć jest zajęta przez inne kontenery (np. MySQL)
docker compose -f docker-compose.demo.yml down --remove-orphans

# Przebuduj i uruchom ponownie
docker compose -f docker-compose.demo.yml up -d --build

# Przebuduj cały stack od zera
docker compose build --no-cache
docker compose up -d
```

---

## Struktura projektu

```
demo/
├── src/main/java/com/fijalkowskim/b2bshop/
│   ├── B2bShopApplication.java
│   ├── config/
│   │   ├── DemoData.java        # dane statyczne (produkty, zamówienia, cennik)
│   │   └── UserInfoHelper.java  # odczyt claims z OIDC tokenu
│   ├── controller/
│   │   └── ShopController.java  # wszystkie endpointy + kontrola dostępu
│   ├── model/
│   │   ├── UserInfo.java        # wrapper na dane użytkownika z tokenu
│   │   ├── Product.java
│   │   ├── Order.java
│   │   └── PricingRule.java
│   └── security/
│       └── SecurityConfig.java  # OAuth2 login + zabezpieczenie tras
└── src/main/resources/
    ├── application.properties   # konfiguracja OAuth2 client
    ├── templates/
    │   ├── home.html            # strona logowania
    │   ├── fragments/layout.html # nawigacja (Thymeleaf fragment)
    │   └── shop/
    │       ├── dashboard.html   # widok powitalny (zależny od roli)
    │       ├── catalog.html     # dostępny dla wszystkich
    │       ├── orders.html      # B2B_BUYER + B2B_ADMIN
    │       ├── pricing.html     # B2B_SALES + B2B_ADMIN
    │       └── account.html     # B2B_ADMIN only
    └── static/css/app.css
```
