package api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import org.junit.jupiter.api.Test;

class BasketTest extends BaseTest {

    @Test
    void newUserStartsWithAnEmptyBasket() {
        LoginResponse.Authentication session = loggedInUser();

        shop.basket(session.token(), session.bid()).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/basket.json"))
                .body("data.Products", empty());
    }

    @Test
    void addedProductShowsUpInTheBasket() {
        LoginResponse.Authentication session = loggedInUser();

        shop.addToBasket(session.token(), new BasketItem(1, session.bid(), 2)).then().statusCode(200);

        shop.basket(session.token(), session.bid()).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/basket.json"))
                .body("data.Products.name", hasItem("Apple Juice (1000ml)"))
                .body("data.Products[0].BasketItem.quantity", equalTo(2));
    }

    @Test
    void basketRequiresLogin() {
        shop.basket(1).then().statusCode(401);
    }
}
