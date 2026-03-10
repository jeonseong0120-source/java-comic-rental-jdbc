package CLI.project.domain;

public class Rental {
    private int id;
    private int comicId;
    private int memberId;
    private String rentalDate;
    private String returnDate;
    private String comicTitle;
    private String memberName;

    public Rental(int id, int comicId, int memberId, String rentalDate, String returnDate) {
        this.id = id;
        this.comicId = comicId;
        this.memberId = memberId;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
    }

    public Rental(int id, int comicId, int memberId, String rentalDate, String returnDate,
                  String comicTitle, String memberName) {
        this(id, comicId, memberId, rentalDate, returnDate);
        this.comicTitle = comicTitle;
        this.memberName = memberName;
    }

    public int getId() { return id; }
    public int getComicId() { return comicId; }
    public int getMemberId() { return memberId; }
    public String getRentalDate() { return rentalDate; }
    public String getReturnDate() { return returnDate; }
    public String getComicTitle() { return comicTitle; }
    public String getMemberName() { return memberName; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s | 대여일: %s | 반납일: %s",
            id,
            comicTitle != null ? comicTitle : "comicId:" + comicId,
            memberName != null ? memberName : "memberId:" + memberId,
            rentalDate,
            returnDate != null ? returnDate : "대여 중"
        );
    }
}