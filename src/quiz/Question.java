package quiz;

import java.util.List;

public class Question {
    private String question;
    private List<String> choices;
    private String answer;

    public Question(String question, List<String> choices, String answer) {
        this.question = question;
        this.choices = choices;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }

    public String getAnswer() {
        return answer;
    }
}