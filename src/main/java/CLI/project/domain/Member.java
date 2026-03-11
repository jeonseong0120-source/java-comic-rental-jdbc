package CLI.project.domain;

public class Member {
    // TODO: 필드 정의 (id, name, phone, regDate)
    // TODO: 생성자, Getter, Setter 구현

    private final int id;
    private final String name;
    private final String phone;
    private final String regDate;

    public Member(int id, String name, String phone, String regDate) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.regDate = regDate;
    }

    // 회원 추가할 때 사용하는 생성자
    public Member(String name, String phone) {
        this.id = 0;
        this.name = name;
        this.phone = phone;
        this.regDate = null;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getRegDate() { return regDate; }
}
