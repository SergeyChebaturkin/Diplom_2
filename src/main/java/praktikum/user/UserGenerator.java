package praktikum.user;

import praktikum.models.User;

import static praktikum.datafaker.UserDataFaker.*;

public class UserGenerator {

    public static User randomValidUser() {
        return new User()
                .withEmail(randomValidEmail())
                .withPassword(randomValidPassword())
                .withName(randomValidName());
    }
    public static User userWithoutPassword() {
        return new User()
                .withEmail(randomValidEmail())
                .withName(randomValidName());
    }

    public static User updateUser(User user, String email, String password, String name) {
        return user
                .withEmail(email)
                .withPassword(password)
                .withName(name);
    }
}
