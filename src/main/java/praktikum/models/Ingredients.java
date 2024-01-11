package praktikum.models;

import java.util.List;

public class Ingredients {
    private List<String> ingredients;

    public Ingredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "ingredients=" + ingredients +
                '}';
    }
}
