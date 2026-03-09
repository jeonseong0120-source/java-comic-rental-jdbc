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
        String sql = "INSERT INTO comic (title, volume, author) VALUES (?, ?, ?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, comic.getTitle());
            pstmt.setInt(2, comic.getVolume());
            pstmt.setString(3, comic.getAuthor());
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt);
        }
    }

    // 만화책 등록 (제목, 권수, 작가를 직접 받는 버전)
    public void addComic(String title, int volume, String author) {
        String sql = "INSERT INTO comic (title, volume, author) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setInt(2, volume);
            pstmt.setString(3, author);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("만화책 등록 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    // 만화책 전체 목록 조회
    public List<Comic> listComics() {
        List<Comic> comics = new ArrayList<>();
        String sql = "SELECT * FROM comic ORDER BY id DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Comic comic = new Comic(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("volume"),
                        rs.getString("author"),
                        rs.getBoolean("is_rented"),
                        rs.getString("reg_date"));
                comics.add(comic);
            }
        } catch (SQLException e) {
            System.out.println("만화책 목록 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return comics;
    }

    // 만화책 상세 조회 (ID로 1건 조회)
    public Comic getComicById(int id) {
        String sql = "SELECT * FROM comic WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Comic(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("volume"),
                        rs.getString("author"),
                        rs.getBoolean("is_rented"),
                        rs.getString("reg_date"));
            }
        } catch (SQLException e) {
            System.out.println("만화책 상세 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    // 만화책 정보 수정
    public void updateComic(int id, String title, int volume, String author) {
        String sql = "UPDATE comic SET title = ?, volume = ?, author = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setInt(2, volume);
            pstmt.setString(3, author);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("만화책 수정 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    // 만화책 삭제
    public void deleteComic(int id) {
        String sql = "DELETE FROM comic WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("만화책 삭제 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    // 대여 시 상태 변경을 위한 메서드 (트랜잭션 필수)
    public void updateRentalStatus(Connection conn, int comicId, boolean isRented) throws SQLException {
        String sql = "UPDATE comic SET is_rented = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, isRented);
            pstmt.setInt(2, comicId);
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt);
        }
    }
}