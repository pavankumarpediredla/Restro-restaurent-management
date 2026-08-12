# Restaurant Service

Spring Boot microservice for restaurant orders and menu items.

## Roles

- `ADMIN` and `MANAGER`: create and update menu items, create orders.
- `KITCHEN`: view orders and accept orders.

## Default users

- `admin / admin123`
- `manager / manager123`
- `kitchen / kitchen123`

You can override the passwords through environment variables:

- `RESTRO_ADMIN_PASSWORD`
- `RESTRO_MANAGER_PASSWORD`
- `RESTRO_KITCHEN_PASSWORD`

## SQL Server config

Set these before starting the service:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

If you do not set them yet, the app still has placeholders in `application.properties` and the test profile uses H2.

For local startup before SQL Server is ready, run with the `local` Spring profile. That uses H2 in SQL Server compatibility mode:

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
