package CLI.project.app;

import CLI.project.repository.ComicRepository;
import CLI.project.repository.MemberRepository;
import CLI.project.repository.RentalRepository;
import CLI.project.util.DBUtil;
import CLI.project.util.Rq;

import java.sql.Connection;
import java.sql.SQLException;
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
        // TODO: 명령어 처리 로직 구현 (switch-case 등)
        // 예:
        // switch (rq.getCommand()) {
        //     case "rent":
        //         rentComic(rq);
        //         break;
        //     ...
        // }
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

    // TODO: 각 기능별 메서드 구현 (addComic, addMember 등)
}