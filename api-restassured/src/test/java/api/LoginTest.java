package api;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Test;

class LoginTest extends BaseTest {

    @Test
    void validCredentialsReturnAToken() {
        Credentials user = newUser();

        shop.login(user).then()
                .statusCode(200)
                .body("authentication.token", not(emptyString()))
                .body("authentication.umail", equalTo(user.email()));
    }

    @Test
    void tokenIdentifiesTheUser() {
        Credentials user = newUser();
        LoginResponse login = shop.login(user).as(LoginResponse.class);

        shop.whoAmI(login.authentication().token()).then()
                .statusCode(200)
                .body("user.email", equalTo(user.email()));
    }
}
