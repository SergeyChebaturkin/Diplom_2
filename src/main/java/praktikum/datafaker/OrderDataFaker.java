package praktikum.datafaker;

public class OrderDataFaker extends DataFaker{
    public static String randomHash() {
        return fakeValuesService.regexify("[a-z]{12}[1-9]{11}");
    }
}
