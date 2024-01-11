import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.User;
import praktikum.user.UserClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static praktikum.user.UserGenerator.*;

public class LoginUserTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private UserClient userClient;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userClient = new UserClient();
    }


    @Test
    @DisplayName("log in with existing user + valid parameters")
    public void loginExistingUser() {
        User user = randomValidUser();
        userClient.register(user);
        Response response = userClient.login(user);
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .and()
                .statusCode(SC_OK);
        String token = response.path("accessToken");
        userClient.delete(token);
    }

    @Test
    @DisplayName("log in with invalid email and password")
    public void loginWithInvalidCreds() {
        User user = randomValidUser();
        Response response = userClient.login(user);
        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
}
