package CLI.project.repository;

import CLI.project.domain.Comic;
import CLI.project.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComicRepository {

    public void addComic(Comic comic) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            addComic(conn, comic);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    // 트랜잭션 참여용 메서드
    public void addComic(Connection conn, Comic comic) throws SQLException {
        // TODO: 만화책 추가 로직 구현 (INSERT)
    }

    public void listComics() {
        // TODO: 만화책 목록 조회 로직 구현 (SELECT)
    }

    // 대여 시 상태 변경을 위한 메서드 (트랜잭션 필수)
    public void updateRentalStatus(Connection conn, int comicId, boolean isRented) throws SQLException {
        // TODO: comic 테이블의 is_rented 컬럼 업데이트
    }
}