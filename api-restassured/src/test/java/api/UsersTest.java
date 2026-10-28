package api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class UsersTest extends BaseTest {

    @Test
    void createsAUser() {
        Credentials user = randomCredentials();

        shop.createUser(user).then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/user.json"))
                .body("data.email", equalTo(user.email()));
    }

    @Test
    void duplicateEmailReturns400() {
        Credentials user = newUser();

        shop.createUser(user).then().statusCode(400);
    }
}
