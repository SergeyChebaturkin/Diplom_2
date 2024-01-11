package praktikum.datafaker;

public class UserDataFaker extends DataFaker {
    public static String randomValidEmail() {
        return faker.internet().emailAddress();
    }
    public static String randomValidPassword() {
        return fakeValuesService.regexify("[A-Z]{1}[a-z]{4}[1-9]{5}");
    }
    public static String randomValidName() {
        return faker.name().username();
    }
}
