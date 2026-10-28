package api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

class ProductsTest extends BaseTest {

    @Test
    void listsProducts() {
        shop.products().then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/product-list.json"))
                .body("data.name", hasItem("Apple Juice (1000ml)"));
    }

    @Test
    void returnsOneProductById() {
        shop.product(1).then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/product.json"))
                .body("data.name", equalTo("Apple Juice (1000ml)"));
    }

    @Test
    void unknownProductReturns404() {
        shop.product(99999).then().statusCode(404);
    }

    @Test
    void searchReturnsOnlyMatchingProducts() {
        shop.search("apple").then()
                .statusCode(200)
                .body("data.name", hasItem("Apple Juice (1000ml)"))
                .body("data.name", not(hasItem("Banana Juice (1000ml)")));
    }

    @Test
    void searchWithNoMatchReturnsEmptyList() {
        shop.search("zzzz").then()
                .statusCode(200)
                .body("data", empty());
    }
}
