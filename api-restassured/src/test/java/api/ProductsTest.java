package api;

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
                .body("status", equalTo("success"))
                .body("data", not(empty()))
                .body("data.name", hasItem("Apple Juice (1000ml)"));
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
