import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filePath;

        while (true) {
            System.out.print("Введите путь до файла-справочника (или 'exit' для завершения) — ");
            filePath = scanner.nextLine();

            if (filePath.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                long startTime = System.currentTimeMillis();
                if (filePath.endsWith(".xml")) {
                    processXmlFile(filePath);
                    long endTime = System.currentTimeMillis();
                    System.out.println("Время обработки файла: " + (endTime - startTime) + " мс\n");
                } else if (filePath.endsWith(".csv")) {
                    processCsvFile(filePath);
                    long endTime = System.currentTimeMillis();
                    System.out.println("Время обработки файла: " + (endTime - startTime) + " мс\n");
                } else {
                    System.out.println("Неверный формат файла.");
                }
            } catch (Exception e) {
                System.err.println("Ошибка при обработке файла: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void processXmlFile(String filePath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filePath);

        Map<String, Integer> cityCounts = new HashMap<>();
        Map<String, Map<Integer, Integer>> cityFloorCounts = new HashMap<>();

        NodeList items = document.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) item;
                String city = element.getAttribute("city");
                int floor = Integer.parseInt(element.getAttribute("floor"));

                cityCounts.put(city, cityCounts.getOrDefault(city, 0) + 1);

                Map<Integer, Integer> floorCount = cityFloorCounts.getOrDefault(city, new HashMap<>());
                floorCount.put(floor, floorCount.getOrDefault(floor, 0) + 1);
                cityFloorCounts.put(city, floorCount);
            }
        }

        printDuplicates(cityCounts);
        printFloorCounts(cityFloorCounts);
    }

    private static void processCsvFile(String filePath) throws IOException {
        Map<String, Integer> cityCounts = new HashMap<>();
        Map<String, Map<Integer, Integer>> cityFloorCounts = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] header = reader.readLine().split(";"); // Assuming the first line is the header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                String city = data[0].replaceAll("\"", ""); // Assuming city is the first column
                int floor = Integer.parseInt(data[3]); // Assuming floor is the fourth column

                cityCounts.put(city, cityCounts.getOrDefault(city, 0) + 1);

                Map<Integer, Integer> floorCount = cityFloorCounts.getOrDefault(city, new HashMap<>());
                floorCount.put(floor, floorCount.getOrDefault(floor, 0) + 1);
                cityFloorCounts.put(city, floorCount);
            }
        }

        printDuplicates(cityCounts);
        printFloorCounts(cityFloorCounts);
    }

    private static void printDuplicates(Map<String, Integer> cityCounts) {
        System.out.println("\nДубликаты:");
        for (Map.Entry<String, Integer> entry : cityCounts.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private static void printFloorCounts(Map<String, Map<Integer, Integer>> cityFloorCounts) {
        System.out.println("\nКоличество этажей в городах:");
        for (Map.Entry<String, Map<Integer, Integer>> entry : cityFloorCounts.entrySet()) {
            String city = entry.getKey();
            Map<Integer, Integer> floorCounts = entry.getValue();
            System.out.println(city + ":");
            for (int i = 1; i <= 5; i++) {
                int count = floorCounts.getOrDefault(i, 0);
                System.out.println("\t" + i + "-этажные здания: " + count);
            }
        }
    }
}
