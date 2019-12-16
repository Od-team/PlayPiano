package od.team.playpiano.RecyclerItemData;

public class FeedbackContentData {

    String teacher_name;
    String content;

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public FeedbackContentData(String teacher_name, String content) {
        this.teacher_name = teacher_name;
        this.content = content;
    }
}
