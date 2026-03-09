package CLI.project.repository;

import CLI.project.domain.Member;
import CLI.project.util.DBUtil;

import java.sql.*;

public class MemberRepository {

    public void addMember(Member member) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            addMember(conn, member);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    // 트랜잭션 참여용 메서드
    public void addMember(Connection conn, Member member) throws SQLException {
        // TODO: 회원 추가 로직 구현 (INSERT)
    }

    public void listMembers() {
        // TODO: 회원 목록 조회 로직 구현 (SELECT)
    }
}