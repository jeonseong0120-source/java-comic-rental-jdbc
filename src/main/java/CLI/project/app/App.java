package CLI.project.app;

import CLI.project.repository.ComicRepository;
import CLI.project.repository.MemberRepository;
import CLI.project.repository.RentalRepository;
import CLI.project.util.Rq;

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
    }

    // TODO: 각 기능별 메서드 구현 (addComic, addMember, rentComic 등)
}