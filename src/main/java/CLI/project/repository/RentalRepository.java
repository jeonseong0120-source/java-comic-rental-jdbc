package CLI.project.repository;

import CLI.project.domain.Rental;
import CLI.project.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;

public class RentalRepository {

    // 트랜잭션 없이 단일 작업으로 대여 (기존 메서드)
    public void rentComic(int comicId, int memberId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            rentComic(conn, comicId, memberId); // 아래의 트랜잭션용 메서드 호출

            conn.commit(); // 성공 시 커밋
        } catch (SQLException e) {
            DBUtil.rollback(conn); // 실패 시 롤백
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    // 트랜잭션 제어를 위해 Connection을 외부에서 주입받는 메서드
    public void rentComic(Connection conn, int comicId, int memberId) throws SQLException {
        // TODO: 만화 대여 로직 구현 (INSERT)
        //  1. rental 테이블에 INSERT
        //  2. comic 테이블에 is_rented = 1로 UPDATE
        //  이 두 작업이 모두 성공해야 함.
    }

    public void returnComic(int rentalId) {
        // TODO: 만화 반납 로직 구현 (UPDATE)
    }

    public void listRentals() {
        // TODO: 대여 목록 조회 로직 구현 (SELECT)
    }
}