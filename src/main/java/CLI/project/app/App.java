package CLI.project.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

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
            case "rent":
                rentComic(rq);
                break;
            case "return":
                returnComic(rq);
                break;
            case "list-rentals":
                listRentals(rq);
                break;
            // TODO: 다른 담당자 명령어 추가
            // case "add-comic":
            // case "list-comics":
            // case "add-member":
            // case "list-members":
            default:
                System.out.println("알 수 없는 명령어입니다.");
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
            conn.setAutoCommit(false);

            if (rentalRepo.isRented(comicId)) {
                System.out.println("이미 대여 중인 만화입니다.");
                return;
            }

            rentalRepo.rentComic(conn, comicId, memberId);
            // TODO: comicRepo.updateRentalStatus(conn, comicId, true); 담당자 구현 후 활성화

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
            // TODO: comicRepo.updateRentalStatus(conn, comicId, false); 담당자 구현 후 활성화

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

    // TODO: 각 기능별 메서드 구현 (addComic, addMember 등)
}