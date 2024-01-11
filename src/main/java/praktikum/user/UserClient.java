package praktikum.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.models.User;
import praktikum.models.UserCreds;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String USER_REGISTER_URL = "api/auth/register";
    private static final String USER_GET_AND_CHANGE_INFO_URL = "api/auth/user";
    private static final String USER_LOGIN_URL = "api/auth/login";

    @Step("Creating user")
    public Response register(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(USER_REGISTER_URL);
    }

    @Step("Logging in user by creds")
    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(UserCreds.fromUser(user))
                .when()
                .post(USER_LOGIN_URL);
    }

    @Step("Updating user info")
    public Response updateInfo(User userWithNewInfo, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .and()
                .body(userWithNewInfo)
                .when()
                .patch(USER_GET_AND_CHANGE_INFO_URL);
    }

    @Step("Deleting user by token")
    public Response delete(String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .when()
                .delete(USER_GET_AND_CHANGE_INFO_URL);
    }
}
