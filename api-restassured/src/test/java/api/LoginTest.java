package api;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class LoginTest extends BaseTest {

    @Test
    @Tag("smoke")
    void validCredentialsReturnAToken() {
        Credentials user = newUser();

        shop.login(user)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/login-response.json"))
                .body("authentication.token", not(emptyString()))
                .body("authentication.umail", equalTo(user.email()));
    }

    @Test
    void tokenIdentifiesTheUser() {
        Credentials user = newUser();
        LoginResponse login =
                shop.login(user).then().statusCode(200).extract().as(LoginResponse.class);

        shop.whoAmI(login.authentication().token())
                .then()
                .statusCode(200)
                .body("user.email", equalTo(user.email()));
    }

    @Test
    void wrongPasswordReturns401() {
        Credentials user = newUser();

        shop.login(new Credentials(user.email(), "wrongpassword"))
                .then()
                .statusCode(401)
                .body(equalTo("Invalid email or password."));
    }

    @Test
    void unknownEmailReturns401() {
        shop.login(new Credentials("wrong@example.com", "wrongpassword"))
                .then()
                .statusCode(401)
                .body(equalTo("Invalid email or password."));
    }

    @Test
    void emptyBodyReturns401() {
        shop.login("{}").then().statusCode(401);
    }
}
