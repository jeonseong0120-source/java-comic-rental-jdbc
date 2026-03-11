package CLI.project.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import CLI.project.domain.Member;
import CLI.project.util.DBUtil;

public class MemberRepository {

    // 일반 회원 등록 메서드
    // 내부에서 Connection을 열고, 아래 addMember(conn, member)를 호출
    public void addMember(Member member) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            addMember(conn, member);
            System.out.println("=> 회원이 등록되었습니다.");
        } catch (SQLException e) {
            System.out.println("=> 회원 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
    }

    // 트랜잭션 참여용 메서드
    // 외부에서 전달받은 Connection을 사용해서 INSERT 수행
    public void addMember(Connection conn, Member member) throws SQLException {
        String sql = "INSERT INTO member(name, phone) VALUES (?, ?)";

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            // 첫 번째 ? 에 이름 저장
            pstmt.setString(1, member.getName());

            // 두 번째 ? 에 전화번호 저장
            pstmt.setString(2, member.getPhone());

            // INSERT 실행
            pstmt.executeUpdate();

        } finally {
            DBUtil.close(pstmt);
        }
    }

    // 회원 목록 조회
    public void listMembers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT id, name, phone, reg_date FROM member ORDER BY id ASC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            System.out.println("번호 | 이름 | 전화번호 | 등록일");
            System.out.println("------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String regDate = rs.getString("reg_date");

                System.out.println(id + " | " + name + " | " + phone + " | " + regDate);
            }

        } catch (SQLException e) {
            System.out.println("=> 회원 목록 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
            DBUtil.close(conn);
        }
    }
}

