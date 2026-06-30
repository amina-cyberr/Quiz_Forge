package model;

public class Subject {
    public int id;
    public String name;
    public Subject(int id, String name) { this.id = id; this.name = name; }
    @Override public String toString() { return name; }
}
