package api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class BasketTest extends BaseTest {

    @Test
    void newUserStartsWithAnEmptyBasket() {
        LoginResponse.Authentication session = loggedInUser();

        shop.basket(session.token(), session.bid())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/basket.json"))
                .body("data.Products", empty());
    }

    @Test
    @Tag("smoke")
    void addedProductShowsUpInTheBasket() {
        LoginResponse.Authentication session = loggedInUser();

        shop.addToBasket(session.token(), new BasketItem(APPLE_JUICE_ID, session.bid(), 2))
                .then()
                .statusCode(200);

        shop.basket(session.token(), session.bid())
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/basket.json"))
                .body("data.Products.name", hasItem(APPLE_JUICE))
                .body("data.Products[0].BasketItem.quantity", equalTo(2));
    }

    @Test
    void quantityCanBeChanged() {
        LoginResponse.Authentication session = loggedInUser();
        int itemId = addAppleJuice(session);

        shop.updateBasketItem(session.token(), itemId, 5).then().statusCode(200);

        shop.basket(session.token(), session.bid())
                .then()
                .statusCode(200)
                .body("data.Products[0].BasketItem.quantity", equalTo(5));
    }

    @Test
    void removedItemLeavesTheBasketEmpty() {
        LoginResponse.Authentication session = loggedInUser();
        int itemId = addAppleJuice(session);

        shop.removeBasketItem(session.token(), itemId).then().statusCode(200);

        shop.basket(session.token(), session.bid())
                .then()
                .statusCode(200)
                .body("data.Products", empty());
    }

    @Test
    void basketRequiresLogin() {
        shop.basket(1).then().statusCode(401);
    }

    @Test
    void addingToTheBasketRequiresLogin() {
        shop.addToBasket(new BasketItem(APPLE_JUICE_ID, 1, 1)).then().statusCode(401);
    }

    @Test
    void invalidTokenReturns401() {
        shop.basket("abc.def.ghi", 1).then().statusCode(401);
    }

    @Test
    void quantityAboveTheLimitReturns400() {
        LoginResponse.Authentication session = loggedInUser();

        shop.addToBasket(session.token(), new BasketItem(APPLE_JUICE_ID, session.bid(), 6))
                .then()
                .statusCode(400)
                .body("error", containsString("up to 5"));
    }

    @Test
    void updatingAboveTheLimitReturns400() {
        LoginResponse.Authentication session = loggedInUser();
        int itemId = addAppleJuice(session);

        shop.updateBasketItem(session.token(), itemId, 6).then().statusCode(400);

        shop.basket(session.token(), session.bid())
                .then()
                .statusCode(200)
                .body("data.Products[0].BasketItem.quantity", equalTo(2));
    }

    @Test
    void cannotAddToTheBasketOfAnotherUser() {
        LoginResponse.Authentication session = loggedInUser();
        LoginResponse.Authentication other = loggedInUser();

        shop.addToBasket(session.token(), new BasketItem(APPLE_JUICE_ID, other.bid(), 1))
                .then()
                .statusCode(401);

        shop.basket(other.token(), other.bid())
                .then()
                .statusCode(200)
                .body("data.Products", empty());
    }

    private int addAppleJuice(LoginResponse.Authentication session) {
        return shop.addToBasket(session.token(), new BasketItem(APPLE_JUICE_ID, session.bid(), 2))
                .then()
                .statusCode(200)
                .extract()
                .path("data.id");
    }
}
