import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.net.URLEncoder;

class MyProgram {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Введите поисковой запрос — ");
        String request = sc.nextLine();


        //передаем запрос
        List<String> searchResults = searchWikipedia(request);

        if(searchResults.isEmpty()){System.out.print("Вы ничего не ввели или запрос введен некорректно! Пожалуйста, перезапустите программу и введите запрос."); return;}

        //выводим найденные статьи
        printSearchResults(searchResults);

        //принимаем ответ от пользователя
        int option = getChoiceFromUser(searchResults.size());

        //вывод выбранной строки
        String selectedUrl = searchResults.get(option - 1);
        openUrlInBrowser(selectedUrl);

        sc.close();
    }

    private static List<String> searchWikipedia(String request) throws IOException {
        String encodedRequest = URLEncoder.encode(request, "UTF-8");
        String apiUrl = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + encodedRequest + "&format=json";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();

        //парсинг ответа
        List<String> searchResults = new ArrayList<>();
        String[] parts = response.toString().split("\"title\":\"");
        for (int i = 1; i < parts.length; i++) {
            String title = parts[i].substring(0, parts[i].indexOf("\""));
            String urly = "https://en.wikipedia.org/wiki/" + title.replace(" ", "_");
            searchResults.add(urly);
        }
        return searchResults;
    }

    private static void printSearchResults(List<String> searchResults) {
        for (int i = 0; i < searchResults.size(); i++) {
            String title = searchResults.get(i).substring(searchResults.get(i).lastIndexOf("/") + 1).replace("_", " ");
            System.out.println((i + 1) + ". " + title);
        }
    }

    private static int getChoiceFromUser(int maxChoice) {
        Scanner sc = new Scanner(System.in);
        int option = -1;

        System.out.print("Выберите номер статьи из предложенных вариантов — ");

        while (option <= 0 || option >= maxChoice + 1) {

            if (sc.hasNextInt()) {
                option = sc.nextInt();
                sc.nextLine();
                if (option <= 0 || option >= maxChoice + 1) {
                    System.out.print("Выбран недопустимый номер статьи! Пожалуйста, выберите номер статьи из предложенных вариантов — ");
                }
            }
            else {
                System.out.print("Неверный ввод. Введите число — номер статьи из предложенных вариантов — ");
                sc.nextLine();
            }
        }
        sc.close();
        return option;
    }

    private static void openUrlInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception e) {
            System.err.println("Не удалось открыть URL в браузере: " + e.getMessage());
        }
    }
}
