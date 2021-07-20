package edu.uci.ics.fabflixmobile;

public class Person {
    private String name;
    private String birthYear;

    public Person(String name, String birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public String getBirthYear() {
        return birthYear;
    }
}
