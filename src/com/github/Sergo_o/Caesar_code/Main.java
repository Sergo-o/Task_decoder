package com.github.Sergo_o.Caesar_code;

import javax.print.DocFlavor;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    static String ACTION_SELECTION = "Приветствую!\nВыберите действие:\n" +
            "1 - Зашифровать текст;\n" +
            "2 - Расшифровать текст;\n" +
            "3 - Подбор ключа для расшифровки.";
    static String DATA_REQUEST = "Введите данные:\n" +
            "путь к файлу, где находится исходной текст;\n" +
            "путь к файлу, где нужно сохранить результат работы программы;\n";
    static String NUMBER_STEP_CODER = "число - шаг сдвига букв.";
    static String CYRILLIC_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"; // 33 буквы.
    static String LATIN_ALPHABET = "abcdefghijklmnopqrstuvwxyz"; // 26 букв.
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println(ACTION_SELECTION);
        int operation = scan.nextInt();
        while (operation != 1 && operation != 2 && operation != 3) {
            System.out.println("Не верно выбрана операция, введите число от 1 до 3!");
            operation = scan.nextInt();
        }

        if (operation == 3){
        System.out.println(DATA_REQUEST);
        }else System.out.println(DATA_REQUEST + NUMBER_STEP_CODER);

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

        int stepCoder = 0;
        if(operation != 3){
            stepCoder = scan.nextInt();}

        switch (operation){
            case 1 -> encryption(inputPath,outputPath,stepCoder);
            case 2 -> transcript(inputPath,outputPath,stepCoder);
            case 3 -> decryptionBySelection(inputPath,outputPath);
        }

    }

    public static void decryptionBySelection(String inputPath, String outputPath){
        String TOP_RUSSIAN_WORDS = "и,в,не,на,я,быть,он,с,что,а,по,это,она,этот,к,но,они,мы,как,из,у,который,то,за," +
                "свой,что,весь,год,от,так,о,для,ты,же,все,тот,мочь,вы,человек,такой,его,сказать,только,или,ещё,бы," +
                "себя,один,как,уже,до,время,если,сам,когда,другой,вот,говорить,наш,мой,знать,стать,при,чтобы,дело," +
                "жизнь,кто,первый,очень,два,день,её,новый,рука,даже,во,со,раз,где,там,под,можно,ну,какой,после,их," +
                "работа,без,самый,потом,надо,хотеть,ли,слово,идти,большой,должен,место,иметь,ничто";
        String[] words = TOP_RUSSIAN_WORDS.split(",");
        int maxLengthWord = 0;
        Map<String,Integer> TOP_RUSSIAN_WORDS_MAP = new HashMap<>();

        for (String word : words) {
            TOP_RUSSIAN_WORDS_MAP.put(word,0);
            if(maxLengthWord<word.length()){
                maxLengthWord = word.length();
            }
        }
        StringBuffer stringBufferInputData = new StringBuffer();

        try (RandomAccessFile inputFile = new RandomAccessFile(inputPath,"rw");
                FileChannel inputChannel = inputFile.getChannel()){
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            CharBuffer charBuffer;

            while (inputChannel.read(byteBuffer) != -1){
                byteBuffer.flip();
                charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
                stringBufferInputData.append(charBuffer);

                byteBuffer.clear();
            }

        }catch (IOException e){
            System.err.println("Ошибка работы с файлами данных!" + e.getMessage());
        }

            String stringInputData = stringBufferInputData.toString();
            String[] arrayInputData = stringInputData.replaceAll("\\p{Punct}","").split(" ");
            ArrayList<String> listInputData = new ArrayList<>();

            for (int i = 0; i < arrayInputData.length; i++) {
                arrayInputData[i] = arrayInputData[i].trim();
                if(arrayInputData[i].length() <= maxLengthWord){
                    listInputData.add(arrayInputData[i]);
                }
            }

            boolean bool = true;
            int[] numberOfMatches = new int[CYRILLIC_ALPHABET.length()];
        while (bool) {
            ArrayList<String> listOutputData = new ArrayList<>();
            for (int i = 1; i <= CYRILLIC_ALPHABET.length(); i++) {
                for (String wordInput : listInputData) {
                    char[] charsWord = wordInput.toCharArray();
                    for (int j = 0; j < charsWord.length; j++) {
                        if(Character.isLetter(charsWord[j])){
                        charsWord[j] = characterOffsetForward(charsWord[j],i);
                        }
                    }
                    listOutputData.add(Arrays.toString(charsWord));
                }
                for (Map.Entry<String,Integer> topRussianWord : TOP_RUSSIAN_WORDS_MAP.entrySet()) {
                    for (int j = 0; j < listOutputData.size(); j++) {
                        if(topRussianWord.getKey().equalsIgnoreCase(listOutputData.get(j))){
                            topRussianWord.setValue(topRussianWord.getValue()+1);
                        }
                    }
                }
                int sumMatches = 0;
                for (Map.Entry<String,Integer> topRussianWord : TOP_RUSSIAN_WORDS_MAP.entrySet()) {
                    sumMatches += topRussianWord.getValue();
                }
                numberOfMatches[i-1] = sumMatches;
                if(i==CYRILLIC_ALPHABET.length()){
                    bool = false;
                }
            }
        }
        System.out.println(Arrays.toString(numberOfMatches));

        int maxMatches = 0;
        int stepCoder = 0;

        for (int i = 0; i < numberOfMatches.length; i++) {
            if(maxMatches<numberOfMatches[i]){
                maxMatches = numberOfMatches [i];
                stepCoder = i;
            }
        }
        System.out.println(stepCoder);
        transcript(inputPath,outputPath,stepCoder);
    }

    public static String alphabetDefinition (char inputChar){
        int presenceSymbolInAlphabet = CYRILLIC_ALPHABET.indexOf(Character.toLowerCase(inputChar));
         return presenceSymbolInAlphabet != -1 ? CYRILLIC_ALPHABET : LATIN_ALPHABET;
    }
    public static char characterOffsetForward (char inputChar, int shift){
        int indexCharInAlphabet;
        boolean charIsUpperCase = Character.isUpperCase(inputChar);
        String alphabet = alphabetDefinition(inputChar);

        if(charIsUpperCase){
            indexCharInAlphabet = alphabet.indexOf(Character.toLowerCase(inputChar));
        }
        else indexCharInAlphabet = alphabet.indexOf(inputChar);

        if(indexCharInAlphabet == -1){
            System.out.printf("Символ - %s, не входит в английский алфавит!\n", inputChar );
            return inputChar;
        }

        shift += indexCharInAlphabet;
        while (shift>=alphabet.length()){
            shift -= alphabet.length();
        }

        if(shift>=0){
            if(charIsUpperCase){
                return Character.toUpperCase(alphabet.charAt(shift));
            }
            else return alphabet.charAt(shift);
        }
        return '?';
    }

    public static char characterOffsetBack (char inputChar, int shift){
        int indexCharInAlphabet;
        boolean charIsUpperCase = Character.isUpperCase(inputChar);
        String alphabet = alphabetDefinition(inputChar);

        if(charIsUpperCase){
            indexCharInAlphabet = alphabet.indexOf(Character.toLowerCase(inputChar));
        }
        else indexCharInAlphabet = alphabet.indexOf(inputChar);

        if(indexCharInAlphabet == -1){
            System.out.printf("Символ - %s, не входит в русский алфавит!\n" , inputChar);
            return inputChar;
        }

        shift = indexCharInAlphabet + shift;
        while (shift<0){
            shift = alphabet.length() + shift;
        }

        if(shift<alphabet.length()){
            if(charIsUpperCase){
                return Character.toUpperCase(alphabet.charAt(shift));
            }
            else return alphabet.charAt(shift);
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
                        char charOffset = (stepCoder>-1) ? characterOffsetForward(charFromBuffer,stepCoder)
                                : characterOffsetBack(charFromBuffer,stepCoder);
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
        encryption(inputPath,outputPath,stepCoder);
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
