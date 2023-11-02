package com.github.Sergo_o.Caesar_code;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    static String ACTION_SELECTION = "Приветствую!\nВыберите действие:\n" +
            "1 - Зашифровать текст;\n" +
            "2 - Расшифровать текст.";
    static String DATA_REQUEST = "Введите данные:\n" +
            "путь к файлу, где находится исходной текст;\n" +
            "путь к файлу, где нужно сохранить результат работы программы;\n" +
            "число - шаг сдвига букв (положительное число).";
    static String CYRILLIC_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"; // 33 буквы.
    static String LATIN_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase(); // 26 букв.
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println(ACTION_SELECTION);
        int operation = scan.nextInt();
        while (operation != 1 && operation != 2) {
            System.out.println("Неверное число! Выберите число от 1 до 2.");
            operation = scan.nextInt();
        }

        System.out.println(DATA_REQUEST);
        String inputPath = pathCheck();
        try{
            while (Files.size(Path.of(inputPath)) == 0){
                System.out.println(inputPath + " - Файл пустой! Введи путь к файлу с данными.");
                inputPath = pathCheck();
            }
        } catch (IOException ioException){
            System.err.println("Ошибка работы с файлом." + ioException.getMessage());
        }

        String outputPath = pathCheck();

        int stepCoder = scan.nextInt();
        while(stepCoder<=0){
            System.out.println("Введите число больше 0.");
            stepCoder = scan.nextInt();
        }

        if (operation == 1) {
            encryption(inputPath, outputPath, stepCoder);
        } else transcript(inputPath, outputPath, stepCoder);

    }
    public static char characterOffsetForward (char inputChar, int shift){
        int indexCharInAlphabet;
        boolean charIsUpperCase = Character.isUpperCase(inputChar);

        if(charIsUpperCase){
            indexCharInAlphabet = CYRILLIC_ALPHABET.indexOf(Character.toLowerCase(inputChar));
        }
        else indexCharInAlphabet = CYRILLIC_ALPHABET.indexOf(inputChar);

        if(indexCharInAlphabet == -1){
            System.out.printf("Символ - %s, не входит в английский алфавит!\n", inputChar );
            return inputChar;
        }

        shift += indexCharInAlphabet;
        while (shift>=CYRILLIC_ALPHABET.length()){
            shift -= CYRILLIC_ALPHABET.length();
        }

        if(shift<CYRILLIC_ALPHABET.length() && shift>=0){
            if(charIsUpperCase){
                return Character.toUpperCase(CYRILLIC_ALPHABET.charAt(shift));
            }
            else return CYRILLIC_ALPHABET.charAt(shift);
        }
        return '?';
    }

    public static char characterOffsetBack (char inputChar, int shift){
        int indexCharInAlphabet;
        boolean charIsUpperCase = Character.isUpperCase(inputChar);

        if(charIsUpperCase){
            indexCharInAlphabet = LATIN_ALPHABET.indexOf(Character.toLowerCase(inputChar));
        }
        else indexCharInAlphabet = LATIN_ALPHABET.indexOf(inputChar);

        if(indexCharInAlphabet == -1){
            System.out.printf("Символ - %s, не входит в русский алфавит!", inputChar);
        }

        shift = indexCharInAlphabet - shift;
        while (shift<0){
            shift += LATIN_ALPHABET.length();
        }

        if(shift<LATIN_ALPHABET.length()){
            if(charIsUpperCase){
                return Character.toUpperCase(LATIN_ALPHABET.charAt(shift));
            }
            else return LATIN_ALPHABET.charAt(shift);
        }
        return '?';
    }

    public static void encryption(String inputPath, String outputPath, int stepCoder) {
        try(RandomAccessFile inputFile = new RandomAccessFile(inputPath,"rw");
        RandomAccessFile outputFile = new RandomAccessFile(outputPath,"rw");
        FileChannel channelInputFile = inputFile.getChannel();
        FileChannel channelOutputFile = outputFile.getChannel()){

            ByteBuffer byteBufferInput = ByteBuffer.allocate(1024);
            CharBuffer charBufferInput;



            while (channelInputFile.read(byteBufferInput) != -1){
                byteBufferInput.flip();
                charBufferInput = StandardCharsets.UTF_8.decode(byteBufferInput);
                CharBuffer charBufferOutput = CharBuffer.allocate(charBufferInput.capacity());

                while(charBufferInput.hasRemaining()) {
                    char charFromBuffer =  charBufferInput.get();
                    if (Character.isLetter(charFromBuffer)) {
                        char charOffset = characterOffsetForward(charFromBuffer, stepCoder);
                        charBufferOutput.put(charOffset);
                    } else charBufferOutput.put(charFromBuffer);
                }
                byteBufferInput.clear();
                charBufferOutput.flip();
                ByteBuffer byteBufferOutput = StandardCharsets.UTF_8.encode(charBufferOutput);
                while (byteBufferOutput.hasRemaining()) {
                    channelOutputFile.write(byteBufferOutput);
                }
                byteBufferOutput.clear();
           }
        }catch (IOException e){
            System.err.println("Ошибка работы с файлами данных!" + e.getMessage());
        }
    }

    public static void transcript(String inputPath, String outputPath, int stepCoder) {

    }

    public static String pathCheck() {
        String absolutPath = scan.nextLine();
        if (absolutPath.isEmpty()){
            absolutPath = scan.nextLine();
        }
        while (Files.notExists(Path.of(absolutPath))) {
            System.out.println(absolutPath + " - Такого файла не существует! Укажите путь к существующему файлу!");
            absolutPath = scan.nextLine();
        } while (Files.isDirectory(Path.of(absolutPath))) {
            System.out.println(absolutPath + " - Вы указали путь к директории! Укажите путь к файлу!");
            absolutPath = scan.nextLine();
        }
        return absolutPath;
    }
}
