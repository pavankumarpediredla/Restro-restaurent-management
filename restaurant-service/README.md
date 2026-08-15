# Restaurant Service

Spring Boot microservice for restaurant orders and menu items.

## Roles

- `ADMIN` and `MANAGER`: create and update menu items, create orders.
- `KITCHEN`: view orders and accept orders.

## Local login

With the `local` profile, the service starts with an H2 database and seeds one
development account: `admin / admin123`. Configure a different bootstrap
account with these environment variables (recommended outside local use):

- `RESTRO_BOOTSTRAP_ADMIN_USERNAME`
- `RESTRO_BOOTSTRAP_ADMIN_PASSWORD`
- `RESTRO_BOOTSTRAP_ADMIN_NAME`

## MySQL config

Set these before starting the service:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

If you do not set them yet, the app uses the connection defaults in
`application.properties`. The `local` profile uses an in-memory H2 database.

The `local` profile is the default for a plain IDE launch. To select it
explicitly before MySQL is ready, run with `SPRING_PROFILES_ACTIVE=local`.

- `SPRING_PROFILES_ACTIVE=local`

## Endpoints

- `GET /api/v1/items`
- `GET /api/v1/items/{id}`
- `POST /api/v1/items`
- `PUT /api/v1/items/{id}`
- `GET /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `POST /api/v1/orders`
- `POST /api/v1/orders/{id}/accept`

## Example payloads

Create item:

```json
{
  "name": "Paneer Tikka",
  "description": "Grilled paneer with spices",
  "category": "Starters",
  "active": true,
  "prices": [
    { "cycle": "DAILY", "amount": 180.00 },
    { "cycle": "WEEKLY", "amount": 1000.00 },
    { "cycle": "MONTHLY", "amount": 3500.00 }
  ]
}
```

Create order:

```json
{
  "tableNumber": "12",
  "customerName": "Walk-in",
  "notes": "No onion",
  "items": [
    { "menuItemId": 1, "priceCycle": "DAILY", "quantity": 2 },
    { "menuItemId": 3, "priceCycle": "MONTHLY", "quantity": 1 }
  ]
}
```
