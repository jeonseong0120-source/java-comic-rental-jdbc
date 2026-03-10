package CLI.project.app;

import CLI.project.domain.Comic;
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
            default:
                System.out.println("존재하지 않는 명령어입니다.");
                break;
        }
    }

    // 트랜잭션 사용 예시 (팀원 참고용)
    private void rentComic(Rq rq) {
        // 1. 입력값 파싱 (comicId, memberId)
        // int comicId = ...
        // int memberId = ...

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 2. 대여 처리 (Rental 테이블 INSERT)
            // rentalRepo.rentComic(conn, comicId, memberId);

            // 3. 만화책 상태 변경 (Comic 테이블 UPDATE)
            // comicRepo.updateRentalStatus(conn, comicId, true);

            conn.commit(); // 성공 시 커밋
            System.out.println("대여가 완료되었습니다.");

        } catch (SQLException e) {
            DBUtil.rollback(conn); // 실패 시 롤백
            System.out.println("대여 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(conn); // 자원 해제
        }
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
}