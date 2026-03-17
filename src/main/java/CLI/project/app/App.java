package CLI.project.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import CLI.project.domain.Comic;
import CLI.project.domain.Member;
import CLI.project.domain.Rental;
import CLI.project.repository.ComicRepository;
import CLI.project.repository.MemberRepository;
import CLI.project.repository.RentalRepository;
import CLI.project.util.DBUtil;
import CLI.project.util.Rq;

public class App {
    private final Scanner scanner = new Scanner(System.in);
    private final ComicRepository comicRepo = new ComicRepository();
    private final MemberRepository memberRepo = new MemberRepository();
    private final RentalRepository rentalRepo = new RentalRepository();

    public void run() {
        System.out.println("=================================================");
        System.out.println("            📚 만화 대여 관리 시스템 📚            ");
        System.out.println("=================================================");
        System.out.println("도움말이 필요하시면 'help'를 입력하세요.");
        System.out.println();
        
        while (true) {
            System.out.print("명령어 입력 ❯ ");
            String command = scanner.nextLine().trim();
            if (command.equals("exit")) {
                System.out.println("\n프로그램을 종료합니다. 이용해 주셔서 감사합니다! 👋");
                break;
            }
            if (command.isEmpty()) continue;

            System.out.println(); // 명령어 실행 전 공백
            handleCommand(command);
            System.out.println(); // 명령어 실행 후 공백
        }
    }

    private void handleCommand(String commandInput) {
        Rq rq = new Rq(commandInput);
        switch (rq.getCommand()) {
            case "help":
                printHelp();
                break;
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
            case "member-add":
                addMember();
                break;
            case "member-list":
                memberRepo.listMembers();
                break;
            default:
                System.out.println("❌ 존재하지 않는 명령어입니다. ('help'를 입력하여 명령어를 확인하세요)");
                break;
        }
    }

    private void printHelp() {
        System.out.println("┌──────────────────── 사용 가능한 명령어 ────────────────────┐");
        System.out.println("│  [도서 관리]                                              │");
        System.out.println("│  · comic-add       : 새 만화책 등록                       │");
        System.out.println("│  · comic-list      : 전체 만화책 목록 조회                │");
        System.out.println("│  · comic-detail    : 만화책 상세 정보 (사용법: comic-detail [번호])│");
        System.out.println("│  · comic-update    : 만화책 정보 수정 (사용법: comic-update [번호])│");
        System.out.println("│  · comic-delete    : 만화책 삭제 (사용법: comic-delete [번호])     │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│  [회원 관리]                                              │");
        System.out.println("│  · member-add      : 신규 회원 등록                       │");
        System.out.println("│  · member-list     : 전체 회원 목록 조회                  │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│  [대여/반납]                                              │");
        System.out.println("│  · rent            : 도서 대여 (사용법: rent [만화번호] [회원번호])│");
        System.out.println("│  · return          : 도서 반납 (사용법: return [대여번호])         │");
        System.out.println("│  · list-rentals    : 전체 대여 내역 조회                  │");
        System.out.println("│  · list-rentals open : 미반납 대여 내역만 조회            │");
        System.out.println("├──────────────────────────────────────────────────────────┤");
        System.out.println("│  [기타]                                                   │");
        System.out.println("│  · exit            : 프로그램 종료                        │");
        System.out.println("└──────────────────────────────────────────────────────────┘");
    }

    private void rentComic(Rq rq) {
        String[] args = rq.getArguments();
        if (args.length < 2) {
            System.out.println("💡 사용법: rent <만화번호> <회원번호>");
            return;
        }
        int comicId = Integer.parseInt(args[0]);
        int memberId = Integer.parseInt(args[1]);

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            if (rentalRepo.isRented(comicId)) {
                System.out.println("❌ 이미 대여 중인 만화입니다.");
                return;
            }

            rentalRepo.rentComic(conn, comicId, memberId);
            comicRepo.updateRentalStatus(conn, comicId, true);

            conn.commit();
            System.out.println("✅ 대여가 완료되었습니다! (도서 번호: " + comicId + ", 회원 번호: " + memberId + ")");

        } catch (SQLException e) {
            DBUtil.rollback(conn);
            System.out.println("❌ 대여 처리 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    private void returnComic(Rq rq) {
        String[] args = rq.getArguments();
        if (args.length < 1) {
            System.out.println("💡 사용법: return <대여번호>");
            return;
        }
        int rentalId = Integer.parseInt(args[0]);

        Connection conn = null;
        try {
            Rental rental = rentalRepo.getById(rentalId);

            if (rental == null) {
                System.out.println("❌ [" + rentalId + "번] 대여 내역을 찾을 수 없습니다.");
                return;
            }

            if (rental.getReturnDate() != null) {
                System.out.println("ℹ️ 이미 반납 처리된 건입니다. (반납일: " + rental.getReturnDate() + ")");
                return;
            }

            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            rentalRepo.returnComic(conn, rentalId);
            comicRepo.updateRentalStatus(conn, rental.getComicId(), false);

            conn.commit();
            System.out.println("✅ 반납이 성공적으로 완료되었습니다!");

        } catch (SQLException e) {
            DBUtil.rollback(conn);
            System.out.println("❌ 반납 처리 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    private void listRentals(Rq rq) {
        String[] args = rq.getArguments();
        boolean onlyOpen = args.length > 0 && "open".equals(args[0]);
        
        if (onlyOpen) {
            System.out.println("▶ 미반납 대여 목록");
        } else {
            System.out.println("▶ 전체 대여 목록");
        }
        
        List<Rental> rentals = rentalRepo.listRentals(onlyOpen, null);
        if (rentals == null || rentals.isEmpty()) { //null 체크 추가
             System.out.println("조회된 내역이 없습니다.");
             return;
        }
        
        System.out.println("─────────────────────────────────────────────────────────────────────────");
        rentals.forEach(System.out::println);
        System.out.println("─────────────────────────────────────────────────────────────────────────");
    }

    // #5 만화책 관련 기능

    private void addComic() {
        System.out.println("▶ 만화책 등록 (취소: 'cancel' 입력)");
        System.out.println("───────────────────────────");

        System.out.print("  · 제목: ");
        String title = scanner.nextLine().trim();
        if (title.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        if (title.isEmpty()) {
            System.out.println("❌ 제목을 입력해주세요.");
            return;
        }

        System.out.print("  · 권수: ");
        String volumeStr = scanner.nextLine().trim();
        if (volumeStr.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        int volume;
        try {
            volume = Integer.parseInt(volumeStr);
        } catch (NumberFormatException e) {
            System.out.println("❌ 권수는 숫자로 입력해주세요.");
            return;
        }

        System.out.print("  · 작가: ");
        String author = scanner.nextLine().trim();
        if (author.equals("cancel")) {
            System.out.println("취소되었습니다.");
            return;
        }
        if (author.isEmpty()) {
            System.out.println("❌ 작가를 입력해주세요.");
            return;
        }

        comicRepo.addComic(title, volume, author);
        System.out.println("✅ 만화책이 성공적으로 등록되었습니다!");
    }

    private void listComics() {
        List<Comic> comics = comicRepo.listComics();

        if (comics.isEmpty()) {
            System.out.println("ℹ️ 등록된 만화책이 없습니다.");
            return;
        }

        System.out.println("▶ 만화책 목록 (총 " + comics.size() + "권)");
        System.out.println("┌──────┬──────────────────────────────┬──────┬──────────────┬──────────┐");
        System.out.println("│ 번호 │             제목             │ 권수 │     작가     │ 상태     │");
        System.out.println("├──────┼──────────────────────────────┼──────┼──────────────┼──────────┤");

        for (Comic comic : comics) {
            String rented = comic.isRented() ? "🔴 대여중" : "🟢 가능  ";
            // 한글 포함 문자열 정렬을 위해 printf 대신 수동 포맷팅을 고려할 수 있지만, 일단 printf 유지
            System.out.printf("│ %-4d │ %-28s │ %-4d │ %-12s │ %s │%n",
                    comic.getId(),
                    truncateString(comic.getTitle(), 14), // 영문 기준 28칸
                    comic.getVolume(),
                    truncateString(comic.getAuthor(), 6),
                    rented);
        }
        System.out.println("└──────┴──────────────────────────────┴──────┴──────────────┴──────────┘");
    }

    // 긴 문자열 자르기 유틸리티
    private String truncateString(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 1) + "…";
    }

    private void detailComic(Rq rq) {
        if (rq.getArguments().length == 0) {
            System.out.println("💡 사용법: comic-detail [번호]");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(rq.getArguments()[0]);
        } catch (NumberFormatException e) {
            System.out.println("❌ 번호는 숫자로 입력해주세요.");
            return;
        }

        Comic comic = comicRepo.getComicById(id);

        if (comic == null) {
            System.out.println("❌ [" + id + "번] 만화책을 찾을 수 없습니다.");
            return;
        }

        System.out.println("▶ 만화책 상세 정보");
        System.out.println("───────────────────────────");
        System.out.println("  · 번호  : " + comic.getId());
        System.out.println("  · 제목  : " + comic.getTitle());
        System.out.println("  · 권수  : " + comic.getVolume() + "권");
        System.out.println("  · 작가  : " + comic.getAuthor());
        System.out.println("  · 상태  : " + (comic.isRented() ? "🔴 대여중" : "🟢 대여 가능"));
        System.out.println("  · 등록일: " + comic.getRegDate());
        System.out.println("───────────────────────────");
    }

    private void updateComic(Rq rq) {
        if (rq.getArguments().length == 0) {
            System.out.println("💡 사용법: comic-update [번호]");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(rq.getArguments()[0]);
        } catch (NumberFormatException e) {
            System.out.println("❌ 번호는 숫자로 입력해주세요.");
            return;
        }

        Comic comic = comicRepo.getComicById(id);

        if (comic == null) {
            System.out.println("❌ [" + id + "번] 만화책을 찾을 수 없습니다.");
            return;
        }

        System.out.println("▶ 만화책 정보 수정 (변경하지 않으려면 엔터, 취소: 'cancel')");
        System.out.println("───────────────────────────");

        System.out.print("  · 새 제목 (현재: " + comic.getTitle() + "): ");
        String title = scanner.nextLine().trim();
        if (title.equals("cancel")) { System.out.println("취소되었습니다."); return; }
        if (title.isEmpty()) title = comic.getTitle();

        System.out.print("  · 새 권수 (현재: " + comic.getVolume() + "): ");
        String volumeStr = scanner.nextLine().trim();
        if (volumeStr.equals("cancel")) { System.out.println("취소되었습니다."); return; }
        int volume;
        if (volumeStr.isEmpty()) {
            volume = comic.getVolume();
        } else {
            try {
                volume = Integer.parseInt(volumeStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ 권수는 숫자로 입력해야 합니다. (수정 취소)");
                return;
            }
        }

        System.out.print("  · 새 작가 (현재: " + comic.getAuthor() + "): ");
        String author = scanner.nextLine().trim();
        if (author.equals("cancel")) { System.out.println("취소되었습니다."); return; }
        if (author.isEmpty()) author = comic.getAuthor();

        comicRepo.updateComic(id, title, volume, author);
        System.out.println("✅ [" + id + "번] 만화책 정보가 수정되었습니다.");
    }

    private void deleteComic(Rq rq) {
        if (rq.getArguments().length == 0) {
            System.out.println("💡 사용법: comic-delete [번호]");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(rq.getArguments()[0]);
        } catch (NumberFormatException e) {
            System.out.println("❌ 번호는 숫자로 입력해주세요.");
            return;
        }

        Comic comic = comicRepo.getComicById(id);

        if (comic == null) {
            System.out.println("❌ [" + id + "번] 만화책을 찾을 수 없습니다.");
            return;
        }
        
        if (comic.isRented()) {
             System.out.println("❌ 현재 대여 중인 만화책은 삭제할 수 없습니다.");
             return;
        }

        comicRepo.deleteComic(id);
        System.out.println("✅ [" + id + "번] 만화책이 삭제되었습니다.");
     }
     
      private void addMember() {
        System.out.println("▶ 회원 등록 (취소: 'cancel' 입력)");
        System.out.println("───────────────────────────");
        
        System.out.print("  · 이름: ");
        String name = scanner.nextLine().trim();
        if (name.equals("cancel")) return;

        System.out.print("  · 전화번호: ");
        String phone = scanner.nextLine().trim();
        if (phone.equals("cancel")) return;

        // 입력값으로 Member 객체 생성
        Member member = new Member(name, phone);

        // Repository에 전달해서 DB 저장
        memberRepo.addMember(member);
        System.out.println("✅ 회원이 성공적으로 등록되었습니다!");
    }
}