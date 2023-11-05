package com.github.Sergo_o.Caesar_code;


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
         String TOP_RUSSIAN_WORDS = "и,в,не,он,на,я,что,тот,быть,с,а,весь,это,как,она,по,но,они,к,у,ты,из,мы,за,вы" +
                 ",так,же,от,сказать,этот,который,мочь,человек,о,один,еще,бы,такой,только,себя,свое,какой,когда,уже" +
                 ",для,вот,кто,да,говорить,год,знать,мой,до,или,если,время,рука,нет,самый,ни,стать,большой,даже" +
                 ",другой,наш,свой,ну,под,где,дело,есть,сам,раз,чтобы,два,там,чем,глаз,жизнь,первый,день,тута,во" +
                 ",ничто,потом,очень,со,хотеть,ли,при,голова,надо,без,видеть,идти,теперь,тоже,стоять,друг,дом" +
                 ",сейчас,можно,после,слово,здесь,думать,место,спросить,через,лицо,что,тогда,ведь,хороший" +
                 ",каждый,новый,жить,должный,смотреть,почему,потому,сторона,просто,нога,сидеть,понять,иметь,конечный" +
                 ",делать,вдруг,над,взять,никто,сделать,дверь,перед,нужный,понимать,казаться,работа,три,ваш,уж" +
                 ",земля,конец,несколько,час,голос,город,последний,пока,хорошо,давать,вода,более,хотя,всегда" +
                 ",второй,куда,пойти,стол,ребенок,увидеть,сила,отец,женщина,машина,случай,ночь,сразу,мир,совсем" +
                 ",остаться,об,вид,выйти,дать,работать,любить,старый,почти,ряд,оказаться,начало,твой,вопрос,много" +
                 ",война,снова,ответить,между,подумать,опять,белый,деньги,значить,про,лишь,минута,жена,посмотреть" +
                 ",правда,главный,страна,свет,ждать,мать,будто,никогда,товарищ,дорога,однако,лежать,именно,окно" +
                 ",никакой,найти,писать,комната,москва,часть,вообще,книга,маленький,улица,решить,далекий,душа,чуть" +
                 ",вернуться,утро,некоторый,считать,сколько,помнить,вечер,пол,таки,получить,народ,плечо,хоть,сегодня" +
                 ",бог,вместе,взгляд,ходить,зачем,советский,русский,бывать,полный,прийти,палец,россия,любой,история" +
                 ",наконец,мысль,узнать,назад,общий,заметить,словно,прошлый,уйти,известный,давно,слышать,слушать" +
                 ",бояться,сын,нельзя,прямо,долго,быстро,лес,похожий,пора,пять,глядеть,оно,сесть,имя,ж,разговор" +
                 ",тело,молодой,стена,красный,читать,право,старик,ранний,хотеться,мама,оставаться,высокий,путь,поэтому" +
                 ",совершенно,кроме,тысяча,месяц,брать,написать,целый,огромный,начинать,спина,настоящий,пусть,язык" +
                 ",точно,среди,чувствовать,сердце,вести,иногда,мальчик,успеть,небо,живой,смерть,продолжать,девушка" +
                 ",образ,ко,забыть,вокруг,письмо,власть,черный,пройти,появиться,воздух,разный,выходить,просить,брат" +
                 ",собственный,отношение,затем,пытаться,показать,вспомнить,система,четыре,квартира,держать,также" +
                 ",любовь,солдат,откуда,чтоб,называть,третий,хозяин,вроде,уходить,подойти,поднять,особенно,спрашивать" +
                 ",начальник,оба,бросить,школа,парень,кровь,двадцать,солнце,неделя,послать,находиться,ребята,поставить" +
                 ",встать,например,шаг,мужчина,равно,нос,мало,внимание,капитан,ухо,туда,сюда,играть,следовать" +
                 ",рассказать,великий,действительно,слишком,тяжелый,спать,оставить,войти,длинный,чувство,молчать" +
                 ",рассказывать,отвечать,становиться,остановиться,берег,семья,искать,генерал,момент,десять,начать" +
                 ",следующий,личный,труд,верить,группа,немного,впрочем,видно,являться,муж,разве,движение,порядок" +
                 ",ответ,тихо,знакомый,газета,помощь,сильный,скорый,собака,дерево,снег,сон,смысл,смочь,против,бежать" +
                 ",двор,форма,простой,приехать,иной,кричать,возможность,общество,зеленый,грудь,угол,открыть,происходить" +
                 ",ладно,черный,век,карман,ехать,немец,наверное,губа,дядя,приходить,часто,домой,огонь,писатель,армия" +
                 ",состояние,зуб,очередь,кой,подняться,камень,гость,показаться,ветер,собираться,попасть,принять,сначала" +
                 ",либо,поехать,услышать,уметь,случиться,странный,единственный,рота,закон,короткий,море,добрый,темный" +
                 ",гора,врач,край,стараться,лучший,река,военный,мера,страшный,вполне,звать,произойти,вперед,медленно" +
                 ",возле,никак,заниматься,действие,довольно,вещь,необходимый,ход,боль,судьба,причина,положить,едва" +
                 ",черта,девочка,легкий,волос,купить,номер,основной,широкий,умереть,далеко,плохо,глава,красивый,серый" +
                 ",пить,командир,обычно,партия,проблема,страх,проходить,ясно,снять,бумага,герой,пара,государство,деревня" +
                 ",речь,начаться,средство,положение,связь,скоро,небольшой,представлять,завтра,объяснить,пустой" +
                 ",произнести,человеческий,нравиться,однажды,мимо,иначе,существовать,класс,удаться,толстый,цель" +
                 ",сквозь,прийтись,чистый,знать,прежний,профессор,господин,счастье,худой,дух,план,чужой,зал,представить" +
                 ",особый,директор,бывший,память,близкий,сей,результат,больной,данный,кстати,назвать,след,улыбаться" +
                 ",бутылка,трудно,условие,прежде,ум,улыбнуться,процесс,картина,вместо,старший,легко,центр,подобный" +
                 ",возможно,около,смеяться,сто,будущее,хватать,число,всякое,рубль,почувствовать,принести,вера,вовсе" +
                 ",удар,телефон,колено,согласиться,мало,коридор,мужик,правый,автор,холодный,хватить,многие,встреча" +
                 ",кабинет,документ,самолет,вниз,принимать,игра,рассказ,хлеб,развитие,убить,родной,открытый,менее" +
                 ",предложить,желтый,приходиться,выпить,крикнуть,трубка,враг,показывать,двое,доктор,ладонь,вызвать" +
                 ",спокойно,попросить,наука,лейтенант,служба,оказываться,привести,сорок,счет,возвращаться,золотой" +
                 ",местный,кухня,крупный,решение,молодая,тридцать,роман,требовать,компания,частый,российский" +
                 ",рабочий,потерять,течение,синий,столько,теплый,метр,достать,железный,институт,сообщить,интерес" +
                 ",обычный,появляться,упасть,остальной,половина,московский,шесть,получиться,качество,бой,шея,вон" +
                 ",идея,видимо,достаточно,провести,важный,трава,дед,сознание,родитель,простить,бить,чай,поздний" +
                 ",кивнуть,род,исчезнуть,тонкий,немецкий,звук,отдать,магазин,президент,поэт,спасибо,болезнь,событие" +
                 ",помочь,кожа,лист,слать,вспоминать,прекрасный,слеза,надежда,молча,сильно,верный,литература,оружие" +
                 ",готовый,запах,неожиданно,вчера,вздохнуть,роль,рост,природа,политический,точка,звезда,петь,садиться" +
                 ",фамилия,характер,пожалуйста,выше,офицер,толпа,перестать,придтись,уровень,неизвестный,кресло,баба,секунда";

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
            String[] arrayInputData = stringInputData
                    .replaceAll("\\p{Punct}","")
                    .replaceAll("\\d","").replaceAll("\\t","")
                    .replaceAll("\\p{Cntrl}"," ").split(" ");

            ArrayList<String> listInputData = new ArrayList<>();

            for (int i = 0; i < arrayInputData.length; i++) {
                arrayInputData[i] = arrayInputData[i].trim();
                if(arrayInputData[i].length() > 1 && arrayInputData[i].length() <= maxLengthWord && !arrayInputData[i].isEmpty()){
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

                    listOutputData.add(Arrays.toString(charsWord).replaceAll("\\p{Punct}","")
                            .replaceAll(" ",""));
                }
                for (int j = 0; j < listOutputData.size(); j++) {
                    for (Map.Entry<String,Integer> topRussianWord : TOP_RUSSIAN_WORDS_MAP.entrySet()) {
                        if(topRussianWord.getKey().equalsIgnoreCase(listOutputData.get(j))){
                            topRussianWord.setValue(topRussianWord.getValue()+1);
                        }
                    }
                }
                int sumMatches = 0;
                for (Map.Entry<String,Integer> topRussianWord : TOP_RUSSIAN_WORDS_MAP.entrySet()) {
                    sumMatches += topRussianWord.getValue();
                    topRussianWord.setValue(0);
                }
                numberOfMatches[i-1] = sumMatches;
                listOutputData.clear();

                if(i==CYRILLIC_ALPHABET.length()){
                    bool = false;
                }
            }
        }
        System.out.println(Arrays.toString(numberOfMatches));

        int maxMatches = 0;
        int stepCoder = 0;

        for (int i = 1; i <= numberOfMatches.length; i++) {
            if(maxMatches<numberOfMatches[i-1]){
                maxMatches = numberOfMatches[i-1];
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
