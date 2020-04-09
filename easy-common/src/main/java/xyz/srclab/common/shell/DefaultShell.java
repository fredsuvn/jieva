package xyz.srclab.common.shell;

import java.util.Scanner;

public class DefaultShell implements Shell {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void print(Object any) {
        System.out.println(any);
    }

    @Override
    public String read() {
        return scanner.nextLine();
    }
}
