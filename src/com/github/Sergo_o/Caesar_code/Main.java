package com.github.Sergo_o.Caesar_code;

import java.io.IOException;
import java.nio.file.Files;
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
            "число - шаг сдвига букв (положительное число).";
    static char[] CYRILLIC_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя".toCharArray();
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println(ACTION_SELECTION);
        int operation = scan.nextInt();
        while (operation != 1 && operation != 2) {
            System.out.println("Неверное число! Выберите число от 1 до 2.");
        }

        System.out.println(DATA_REQUEST);
        Path inputPath = pathCheck();
        try{
            while (Files.size(inputPath) == 0){
                System.out.println(inputPath + " - Файл пустой! Введи путь к файлу с данными.");
                inputPath = pathCheck();
            }
        } catch (IOException ioException){
            System.err.println("Ошибка работы с файлом." + ioException.getMessage());
        }

        Path outputPath = pathCheck();

        int stepCoder = scan.nextInt();
        while(stepCoder<0){
            System.out.println("Введите положительное число.");
            stepCoder = scan.nextInt();
        }

        if (operation == 1) {
            encryption(inputPath, outputPath, stepCoder);
        } else transcript(inputPath, outputPath, stepCoder);

    }

    public static void encryption(Path inputPath, Path outputPath, int stepCoder) {

    }

    public static void transcript(Path inputPath, Path outputPath, int stepCoder) {

    }

    public static Path pathCheck() {
        Path absolutPath = Paths.get(scan.nextLine()).toAbsolutePath();
        if (Files.notExists(absolutPath)) {
            System.out.println(absolutPath + " - Такого файла не существует! Укажите путь к существующему файлу!");
            absolutPath = Paths.get(scan.nextLine()).toAbsolutePath();
        } else if (Files.isDirectory(absolutPath)) {
            System.out.println(absolutPath + " - Вы указали путь к директории! Укажите путь к файлу!");
            absolutPath = Paths.get(scan.nextLine()).toAbsolutePath();
        }
        return absolutPath;
    }
}
