package model;

import java.util.List;

public class Question {
    public int id;
    public String text;
    public List<Option> options;
    public Question(int id, String text, List<Option> options) {
        this.id = id; this.text = text; this.options = options;
    }
    public static class Option {
        public int id;
        public String text;
        public boolean correct;
        public Option(int id, String text, boolean correct) {
            this.id = id; this.text = text; this.correct = correct;
        }
    }
}
