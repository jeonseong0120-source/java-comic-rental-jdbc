package CLI.project.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import CLI.project.domain.Rental;
import CLI.project.util.DBUtil;

public class RentalRepository {

    // App에서 트랜잭션 관리 - Connection을 파라미터로 받음
    public int rentComic(Connection conn, int comicId, int memberId) {
        String sql = "INSERT INTO rental (comic_id, member_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, comicId);
            pstmt.setInt(2, memberId);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void returnComic(Connection conn, int rentalId) {
        String sql = "UPDATE rental SET return_date = NOW() WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rentalId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Rental getById(int id) {
        String sql = "SELECT r.*, c.title AS comicTitle, m.name AS memberName " +
                    "FROM rental r " +
                    "JOIN comic c ON r.comic_id = c.id " +
                    "JOIN member m ON r.member_id = m.id " +
                    "WHERE r.id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) return mapToRental(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    public boolean isRented(int comicId) {
        String sql = "SELECT is_rented FROM comic WHERE id = ?";
        try (PreparedStatement pstmt = DBUtil.getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, comicId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("is_rented") == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Rental> listRentals(boolean onlyOpen, Integer memberId) {
        StringBuilder sql = new StringBuilder(
            "SELECT r.*, c.title AS comicTitle, m.name AS memberName " +
            "FROM rental r " +
            "JOIN comic c ON r.comic_id = c.id " +
            "JOIN member m ON r.member_id = m.id"
        );

        if (onlyOpen) {
            sql.append(" WHERE r.return_date IS NULL");
        } else if (memberId != null) {
            sql.append(" WHERE r.member_id = ?");
        }

        List<Rental> rentals = new ArrayList<>();
        try (PreparedStatement pstmt = DBUtil.getConnection().prepareStatement(sql.toString())) {

            if (!onlyOpen && memberId != null) pstmt.setInt(1, memberId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) rentals.add(mapToRental(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }

    public void listRentals() {
        listRentals(false, null).forEach(System.out::println);
    }

    private Rental mapToRental(ResultSet rs) throws SQLException {
        return new Rental(
            rs.getInt("id"),
            rs.getInt("comic_id"),
            rs.getInt("member_id"),
            rs.getString("rental_date"),
            rs.getString("return_date"),
            rs.getString("comicTitle"),
            rs.getString("memberName")
        );
    }
}