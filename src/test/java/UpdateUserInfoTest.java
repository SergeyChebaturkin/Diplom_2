import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import praktikum.models.User;
import praktikum.user.UserClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static praktikum.datafaker.UserDataFaker.*;
import static praktikum.user.UserGenerator.*;
import static org.hamcrest.Matchers.equalTo;


public class UpdateUserInfoTest {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private UserClient userClient;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        userClient = new UserClient();
    }

    @Test
    @DisplayName("changing information of authorized user")
    public void changeInfoAuthorizedUser() {
        User user = randomValidUser();
        userClient.register(user);
        Response loginResponse = userClient.login(user);
        String token = loginResponse.path("accessToken");
        String newEmail = randomValidEmail();
        String newPassword = randomValidPassword();
        String newName = randomValidName();
        User updatedUser = updateUser(user, newEmail, newPassword, newName);
        Response updateResponse = userClient.updateInfo(updatedUser, token);
        updateResponse.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
        Response loginWithUpdatedUserResponse = userClient.login(updatedUser);
        loginWithUpdatedUserResponse.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
        assertEquals(newEmail, loginWithUpdatedUserResponse.path("user.email"));
        assertEquals(newName, loginWithUpdatedUserResponse.path("user.name"));
        userClient.delete(token);
    }

    @Test
    @DisplayName("changing information without access token")
    public void changeInfoWithoutAccessToken() {
        User user = randomValidUser();
        Response updateResponse = userClient.updateInfo(user, "");
        updateResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
        Response loginResponse = userClient.login(user);
        loginResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
}
