package com.github.Sergo_o.Caesar_code;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    static String ACTION_SELECTION = "Приветствую!\nВыберите действие:\n" +
                                        "1 - Зашифровать текст;\n" +
                                        "2 - Расшифровать текст.";
    static String DATA_REQUEST = "Введите данные:\n" +
                                "путь к файлу, где находится исходной текст;\n" +
                                "путь к файлу, где нужно сохранить результат работы программы;\n" +
                                "число - шаг сдвига букв.";
    static char[] CYRILLIC_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя".toCharArray();

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println(ACTION_SELECTION);
        int operation = scan.nextInt();
        while (operation != 1 && operation != 2){
            System.out.println("Неверное число! Выберите число от 1 до 2.");
        }

        System.out.println(DATA_REQUEST);
        Path inputPath = Paths.get(scan.nextLine());
        Path outputPath = Paths.get(scan.nextLine());
        int stepCoder = scan.nextInt();

        pathCheck(inputPath);
        pathCheck(outputPath);

        if(operation == 1){
            encryption(inputPath,outputPath,stepCoder);
        }else transcript(inputPath,outputPath,stepCoder);

    }
    public static void encryption(Path inputPath, Path outputPath, int stepCoder){

    }

    public static void transcript(Path inputPath, Path outputPath, int stepCoder) {

    }

    public static void pathCheck (Path path) {

    }
}
