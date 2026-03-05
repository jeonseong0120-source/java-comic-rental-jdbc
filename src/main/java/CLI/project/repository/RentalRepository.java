package CLI.project.repository;

import CLI.project.domain.Rental;
import CLI.project.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;

public class RentalRepository {
    public void rentComic(int comicId, int memberId) {
        // TODO: 만화 대여 로직 구현 (INSERT)
    }

    public void returnComic(int rentalId) {
        // TODO: 만화 반납 로직 구현 (UPDATE)
    }

    public void listRentals() {
        // TODO: 대여 목록 조회 로직 구현 (SELECT)
    }
}