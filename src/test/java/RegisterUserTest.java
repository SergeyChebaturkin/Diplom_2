import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.User;
import praktikum.user.UserClient;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static praktikum.user.UserGenerator.*;

public class RegisterUserTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private UserClient userClient;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userClient = new UserClient();
    }

    @Test
    @DisplayName("register user with valid parameters")
    public void registerCourier() {
        User user = randomValidUser();
        Response response = userClient.register(user);
        response.then().assertThat()
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .and()
                .statusCode(SC_OK);
        String token = response.path("accessToken");
        userClient.delete(token);
    }

    @Test
    @DisplayName("register already existing user")
    public void registerAlreadyExistingUser() {
        User user = randomValidUser();
        Response successfulRegisterResponse = userClient.register(user);
        Response failedRegisterResponse = userClient.register(user);
        failedRegisterResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .and()
                .statusCode(SC_FORBIDDEN);
        String token = successfulRegisterResponse.path("accessToken");
        userClient.delete(token);
    }

    @Test
    @DisplayName("register without required field - password")
    public void registerWithoutPassword() {
        User user = userWithoutPassword();
        Response response = userClient.register(user);
        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }
}
