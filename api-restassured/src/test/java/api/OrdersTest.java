package api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class OrdersTest extends BaseTest {

    @Test
    @Tag("smoke")
    void placedOrderShowsUpInTheHistory() {
        LoginResponse.Authentication session = loggedInUser();
        shop.addToBasket(session.token(), new BasketItem(APPLE_JUICE_ID, session.bid(), 1))
                .then()
                .statusCode(200);

        String orderId =
                shop.checkout(session.token(), session.bid())
                        .then()
                        .statusCode(200)
                        .body("orderConfirmation", not(emptyString()))
                        .extract()
                        .path("orderConfirmation");

        shop.orderHistory(session.token())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/order-history.json"))
                .body("data.orderId", hasItem(orderId))
                .body("data[0].products[0].name", equalTo(APPLE_JUICE))
                .body("data[0].products[0].quantity", equalTo(1));
    }

    @Test
    void checkoutEmptiesTheBasket() {
        LoginResponse.Authentication session = loggedInUser();
        shop.addToBasket(session.token(), new BasketItem(APPLE_JUICE_ID, session.bid(), 1))
                .then()
                .statusCode(200);

        shop.checkout(session.token(), session.bid()).then().statusCode(200);

        shop.basket(session.token(), session.bid())
                .then()
                .statusCode(200)
                .body("data.Products", empty());
    }

    @Test
    void checkoutRequiresLogin() {
        shop.checkout(1).then().statusCode(401);
    }
}
