package praktikum.models;


public class Order {
    private String _id;
    private String[] ingredients;
    private String status;
    private String name;
    private String createdAt;
    private String updatedAt;
    private int number;

    public String getId() {
        return _id;
    }

    public int getNumber() {
        return number;
    }
}
