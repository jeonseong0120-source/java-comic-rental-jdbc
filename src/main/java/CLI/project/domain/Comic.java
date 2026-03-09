package CLI.project.domain;

public class Comic {
    private int id;
    private String title;
    private int volume;
    private String author;
    private boolean isRented;
    private String regDate;

    public Comic() {
    }

    public Comic(int id, String title, int volume, String author, boolean isRented, String regDate) {
        this.id = id;
        this.title = title;
        this.volume = volume;
        this.author = author;
        this.isRented = isRented;
        this.regDate = regDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean isRented) {
        this.isRented = isRented;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }
}