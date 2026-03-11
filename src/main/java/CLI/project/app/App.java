package CLI.project.app;

import CLI.project.domain.Comic;
import CLI.project.domain.Member;
import CLI.project.repository.ComicRepository;
import CLI.project.repository.MemberRepository;
import CLI.project.repository.RentalRepository;
import CLI.project.util.DBUtil;
import CLI.project.util.Rq;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {
    private final Scanner scanner = new Scanner(System.in);
    private final ComicRepository comicRepo = new ComicRepository();
    private final MemberRepository memberRepo = new MemberRepository();
    private final RentalRepository rentalRepo = new RentalRepository();

    public void run() {
        System.out.println("== 만화 대여 관리 프로그램 ==");
        while (true) {
            System.out.print("명령어: ");
            String command = scanner.nextLine();
            if (command.equals("exit")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            handleCommand(command);
        }
    }

    private void handleCommand(String commandInput) {
        Rq rq = new Rq(commandInput);
        switch (rq.getCommand()) {
            case "comic-add":
                addComic();
                break;
            case "comic-list":
                listComics();
                break;
            case "comic-detail":
                detailComic(rq);
                break;
            case "comic-update":
                updateComic(rq);
                break;
            case "comic-delete":
                deleteComic(rq);
                break;
            case "rent":
                rentComic(rq);
                break;
            case "return":
                returnComic(rq);
                break;
            case "list-rentals":
                listRentals(rq);
                break;
            case "member-add" -> addMember();

            case "member-list" -> memberRepo.listMembers();

            default:
                System.out.println("존재하지 않는 명령어입니다.");
                break;
        }
    }

    private void rentComic(Rq rq) {
        String[] args = rq.getArguments();
        if (args.length < 2) {
            System.out.println("사용법: rent <comicId> <memberId>");
            return;
        }
        int comicId = Integer.parseInt(args[0]);
        int memberId = Integer.parseInt(args[1]);

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            if (rentalRepo.isRented(comicId)) {
                System.out.println("이미 대여 중인 만화입니다.");
                return;
            }

            rentalRepo.rentComic(conn, comicId, memberId);

            comicRepo.updateRentalStatus(conn, comicId, true);

            conn.commit();
            System.out.println("대여가 완료되었습니다.");

        } catch (SQLException e) {
            DBUtil.rollback(conn);
            System.out.println("대여 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    private void returnComic(Rq rq) {
        String[] args = rq.getArguments();
        if (args.length < 1) {
            System.out.println("사용법: return <rentalId>");
            return;
        }
        int rentalId = Integer.parseInt(args[0]);

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            int comicId = rentalRepo.getById(rentalId).getComicId();
            rentalRepo.returnComic(conn, rentalId);

            comicRepo.updateRentalStatus(conn, comicId, false);

            conn.commit();
            System.out.println("반납이 완료되었습니다.");

        } catch (SQLException e) {
            DBUtil.rollback(conn);
            System.out.println("반납 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    private void listRentals(Rq rq) {
        String[] args = rq.getArguments();
        boolean onlyOpen = args.length > 0 && "open".equals(args[0]);
        rentalRepo.listRentals(onlyOpen, null).forEach(System.out::println);
    }

    // #5 만화책 관련 기능

    private void addComic() {
        System.out.println("== 만화책 등록 ==");

        System.out.print("제목: ");
        String title = scanner.nextLine().trim();
        if (title.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        if (title.isEmpty()) {
            System.out.println("제목을 입력해주세요.");
            return;
        }

        System.out.print("권수: ");
        String volumeStr = scanner.nextLine().trim();
        if (volumeStr.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        int volume;
        try {
            volume = Integer.parseInt(volumeStr);
        } catch (NumberFormatException e) {
            System.out.println("권수는 숫자로 입력해주세요.");
            return;
        }

        System.out.print("작가: ");
        String author = scanner.nextLine().trim();
        if (author.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        if (author.isEmpty()) {
            System.out.println("작가를 입력해주세요.");
            return;
        }

        comicRepo.addComic(title, volume, author);
        System.out.println("만화책이 등록되었습니다.");
    }

    private void listComics() {
        System.out.println("== 만화책 목록 ==");
        System.out.println("번호 / 제목 / 권수 / 작가 / 대여여부 / 등록일");
        System.out.println("--------------------------------------------");

        List<Comic> comics = comicRepo.listComics();

        if (comics.isEmpty()) {
            System.out.println("등록된 만화책이 없습니다.");
            return;
        }

        for (Comic comic : comics) {
            String rented = comic.isRented() ? "대여중" : "대여가능";
            System.out.printf("%d / %s / %d권 / %s / %s / %s%n",
                    comic.getId(),
                    comic.getTitle(),
                    comic.getVolume(),
                    comic.getAuthor(),
                    rented,
                    comic.getRegDate());
        }
    }

    private void detailComic(Rq rq) {
        if (rq.getArguments().length == 0) {
            System.out.println("사용법: comic-detail [번호]");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(rq.getArguments()[0]);
        } catch (NumberFormatException e) {
            System.out.println("번호는 숫자로 입력해주세요.");
            return;
        }

        Comic comic = comicRepo.getComicById(id);

        if (comic == null) {
            System.out.println(id + "번 만화책은 존재하지 않습니다.");
            return;
        }

        System.out.println("== 만화책 상세 ==");
        System.out.println("번호: " + comic.getId());
        System.out.println("제목: " + comic.getTitle());
        System.out.println("권수: " + comic.getVolume() + "권");
        System.out.println("작가: " + comic.getAuthor());
        System.out.println("대여: " + (comic.isRented() ? "대여중" : "대여가능"));
        System.out.println("등록일: " + comic.getRegDate());
    }

    private void updateComic(Rq rq) {
        if (rq.getArguments().length == 0) {
            System.out.println("사용법: comic-update [번호]");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(rq.getArguments()[0]);
        } catch (NumberFormatException e) {
            System.out.println("번호는 숫자로 입력해주세요.");
            return;
        }

        Comic comic = comicRepo.getComicById(id);

        if (comic == null) {
            System.out.println(id + "번 만화책은 존재하지 않습니다.");
            return;
        }

        System.out.println("== 만화책 수정 ==");

        System.out.print("새 제목(현재: " + comic.getTitle() + "): ");
        String title = scanner.nextLine().trim();
        if (title.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        if (title.isEmpty()) {
            title = comic.getTitle();
        }

        System.out.print("새 권수(현재: " + comic.getVolume() + "): ");
        String volumeStr = scanner.nextLine().trim();
        if (volumeStr.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        int volume;
        if (volumeStr.isEmpty()) {
            volume = comic.getVolume();
        } else {
            try {
                volume = Integer.parseInt(volumeStr);
            } catch (NumberFormatException e) {
                System.out.println("권수는 숫자로 입력해주세요.");
                return;
            }
        }

        System.out.print("새 작가(현재: " + comic.getAuthor() + "): ");
        String author = scanner.nextLine().trim();
        if (author.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        if (author.isEmpty()) {
            author = comic.getAuthor();
        }

        comicRepo.updateComic(id, title, volume, author);
        System.out.println(id + "번 만화책이 수정되었습니다.");
    }

    private void deleteComic(Rq rq) {
        if (rq.getArguments().length == 0) {
            System.out.println("사용법: comic-delete [번호]");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(rq.getArguments()[0]);
        } catch (NumberFormatException e) {
            System.out.println("번호는 숫자로 입력해주세요.");
            return;
        }

        Comic comic = comicRepo.getComicById(id);

        if (comic == null) {
            System.out.println(id + "번 만화책은 존재하지 않습니다.");
            return;
        }

        comicRepo.deleteComic(id);
        System.out.println(id + "번 만화책이 삭제되었습니다.");
     }
      private void addMember() {
        System.out.print("이름: ");
        String name = scanner.nextLine().trim();

        System.out.print("전화번호: ");
        String phone = scanner.nextLine().trim();

        // 입력값으로 Member 객체 생성
        Member member = new Member(name, phone);

        // Repository에 전달해서 DB 저장
        memberRepo.addMember(member);
    }
}