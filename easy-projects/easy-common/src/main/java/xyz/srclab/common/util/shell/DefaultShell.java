package xyz.srclab.common.util.shell;

import java.util.Scanner;

public class DefaultShell implements Shell {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void print(Object any) {
        System.out.print(any);
    }

    @Override
    public String read() {
        return scanner.nextLine();
    }
}
