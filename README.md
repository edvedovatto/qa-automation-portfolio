# qa-automation-portfolio

[![smoke](https://github.com/edvedovatto/qa-automation-portfolio/actions/workflows/smoke.yml/badge.svg)](https://github.com/edvedovatto/qa-automation-portfolio/actions/workflows/smoke.yml)
[![regression](https://github.com/edvedovatto/qa-automation-portfolio/actions/workflows/regression.yml/badge.svg)](https://github.com/edvedovatto/qa-automation-portfolio/actions/workflows/regression.yml)

Automated tests for [OWASP Juice Shop](https://github.com/juice-shop/juice-shop) v20.3.0, an online shop built for security training. I test the same shop two ways: through the UI with Playwright and TypeScript, with the scenarios written in Gherkin, and through the API with REST Assured and Java 17.

What I wanted to show here: how I split what belongs in the UI and what belongs in the API, test data created through the API, contracts checked against JSON schemas, error cases, smoke kept separate from regression, and how I deal with flaky tests.

## Running it

You need Node 22+ (the pipeline uses 24), Java 17 and Maven. The app can run in Docker:

```
docker run --rm -d -p 3000:3000 bkimminich/juice-shop:v20.3.0
```

Without Docker, clone [juice-shop](https://github.com/juice-shop/juice-shop) at v20.3.0, run `npm install` and `npm start` there, and it listens on the same port.

UI (full suite, then smoke only):

```
cd e2e-playwright
npm ci
npx playwright install chromium
npm test
npm run test:smoke
```

API (full suite, then smoke only):

```
cd api-restassured
mvn test
mvn test -Dgroups=smoke
```

If the app runs somewhere else, set `BASE_URL=http://host:port` (Maven also takes `-DbaseUrl=...`). Lint and formatting: `npm run lint`, `npm run typecheck` and `mvn spotless:check`.

The order tests use up real stock. The app goes back to its initial state when it restarts, so if you run the suite many times in a row, restart the app now and then. The pipeline doesn't have this problem because every run starts a fresh container.

## Strategy

There are 25 UI tests and 35 API tests, plus 13 API tests for bugs in the app, which run in a separate pass (more on that below). The UI covers what only the UI can show: error messages, disabled buttons, redirects and the purchase flow end to end. Business rules, permissions and contracts live in the API tests, which are faster and break less.

**UI:** login (success, wrong password, unknown email, session after reload, logout, return to the page that asked for login), registration (success, email already in use, invalid email, short password, passwords that don't match), search, basket (including a visitor being sent to the login page at checkout) and checkout (full order, and being blocked without an address or a card).

**Accessibility:** the shop, login, registration and basket pages are scanned with [axe](https://github.com/dequelabs/axe-core-npm/tree/develop/packages/playwright) against WCAG 2.1 AA. The problems the app already has (listed below) are skipped by rule, so the scenario fails when a new kind of problem shows up. When it fails, the full axe result is attached to the report.

The UI scenarios are in `e2e-playwright/features`, written in Gherkin, and the steps are in `e2e-playwright/steps`. I use [playwright-bdd](https://github.com/vitalets/playwright-bdd), which turns each scenario into a Playwright test, so fixtures, traces and the Playwright report still work. Steps are short and do the work through page objects. Steps like "Given I am a logged in customer" or "Given I have an account" set up the data through the API.

I didn't use Gherkin for the API. REST Assured already reads as `given / when / then`, and the method name says what the scenario is (`wrongPasswordReturns401`, `addressOfAnotherUserCannotBeDeleted`).

**API:** login, products, registration, basket, addresses, cards, checkout and order history. The main responses are validated against JSON schemas. Error cases cover:
- 401 without login and with an invalid token;
- 400 for a duplicate email, an invalid card and more than 5 units of a product;
- 404 for a product that doesn't exist;
- one user trying to read or delete another user's data.

**Test data:** every test creates its own user through the API, with a unique email. The checkout address and card are created through the API too. The UI only does the part being tested.

**Smoke and regression:** smoke is 7 tagged tests (`@smoke` in the UI, `@Tag("smoke")` in the API): login, search, basket, product list and one order. Regression is the whole suite.

**Left out:** browsers other than Chromium, visual screenshot comparison (it breaks easily and the images change from one machine to another), checkout variations (coupons, wallet, other delivery options), load testing and in-depth security testing. At this suite size they weren't worth it.

## Bugs the suite exposes

Juice Shop has bugs on purpose, and several of these are among its own listed challenges. I turned them into tests, in `KnownIssuesTest`. Each test is written with the correct behavior and tagged `known-issue`, with the bug noted above it. `mvn test` leaves them out; `mvn test -Dgroups=known-issue -DexcludedGroups=` runs only them. In the pipeline they run in a separate pass after the regression: it doesn't fail the build, and it raises a warning if one of them starts passing, which means the bug was fixed. The main ones:

- a user can read another user's basket, change an item in it and check it out;
- registration accepts an invalid email, no password, and passwords that don't match;
- the basket accepts negative and zero quantities, and checkout accepts an empty basket;
- an address is saved with only a name;
- broken JSON on login, an unknown product in the basket and order history without login all return 500.

Accessibility problems found by axe, skipped in `accessibility.steps.ts`. They are skipped by rule, so a new problem of one of these same kinds would not be caught:

- the search field in the header has no label (`label`);
- the quantity and remove buttons in the basket have no name (`button-name`);
- the logo, basket and language buttons read differently from what they show, so voice control doesn't find them (`label-content-name-mismatch`);
- the basket table header is missing its cells for screen readers (`aria-required-children`);
- low contrast on "Only 1 left" and on a small text in the basket header (`color-contrast`).

## Pipeline

- `smoke.yml`, on every push to `main` and every pull request: lint (Biome, `tsc`, Spotless) and the UI and API smoke tests, with the app started from the Docker image.
- `regression.yml`, Monday to Friday at night and whenever I trigger it by hand: the whole suite, then the known-issue tests in a separate pass that only warns.

Flaky tests: in the pipeline, a failing UI test gets one retry, only one. The failure trace is kept, and the UI and API reports are uploaded as artifacts even when the run fails. On the API side, the request and response are only logged when a check fails.

## What I'd do next

- Publish the Playwright report to GitHub Pages.
- Add schemas for the error responses, not only the successful ones.
