package api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ProductsTest extends BaseTest {

    @Test
    @Tag("smoke")
    void listsProducts() {
        shop.products()
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/product-list.json"))
                .body("data.name", hasItem(APPLE_JUICE));
    }

    @Test
    void returnsOneProductById() {
        shop.product(APPLE_JUICE_ID)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/product.json"))
                .body("data.name", equalTo(APPLE_JUICE));
    }

    @Test
    void unknownProductReturns404() {
        shop.product(99999).then().statusCode(404);
    }

    @Test
    void searchReturnsOnlyMatchingProducts() {
        shop.search("apple")
                .then()
                .statusCode(200)
                .body("data.name", hasItem(APPLE_JUICE))
                .body("data.name", not(hasItem(BANANA_JUICE)));
    }

    @Test
    void searchWithNoMatchReturnsEmptyList() {
        shop.search("zzzz").then().statusCode(200).body("data", empty());
    }
}
