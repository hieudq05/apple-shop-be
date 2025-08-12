# Apple Shop BE - Test Strategy and Plan (Draft)

## Scope

-   Backend Spring Boot services under `com.web.appleshop` covering public and admin modules: Auth, User, Product, Category, Color, Cart, Order, Payment (VnPay/PayPal), Promotion, Review, SavedProduct, ShippingInfo, Blog, Otp, Webhook.

## Test Types & Priority

-   Unit (high): services, utilities, specifications.
-   Component (high): controller-service-repo slices.
-   Integration/API E2E (highest): happy paths for login, catalogue browse, cart, order, payment callbacks.
-   Contract (high): JSON schema validation for success/error/validation/user-info.
-   Manual exploratory (medium): edge cases, permission, pagination, concurrency basics.
-   Non-functional (medium): smoke performance for hot endpoints; basic security checks.

## Assumptions

-   DB: MSSQL in app; tests run with Testcontainers MSSQL. No prod data.
-   External: VnPay/PayPal/GHN mocked in tests; no real calls.
-   JWT secret provided via test profile; context path `/api/v1`.

## Risks

-   Payment side effects, idempotency of callbacks.
-   Concurrency around stock/cart.
-   File uploads in CI environment.

## Metrics

-   Build/lint pass; Unit+IT pass.
-   Coverage ≥ 80% for core service logic (Jacoco report).
-   Smoke API suite ≤ 3 min.

## Schedule (phased)

1. Discovery and inventory (done draft).
2. Test infra & seed data.
3. Core automation (auth, product, cart, order, payment callbacks).
4. Manual exploratory per module.
5. Non-functional smoke & security checks.
6. CI wiring and reporting gates.

## Deliverables

-   Test Plan (this file), manual cases CSV/MD, RestAssured tests, WireMock stubs, Jacoco/Allure reports, CI pipeline.

## Smoke Flow

-   login → browse products → add to cart → create order/payment URL → simulate callback → verify order status.
