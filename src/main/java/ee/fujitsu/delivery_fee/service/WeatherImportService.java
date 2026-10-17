package ee.fujitsu.delivery_fee.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.time.Instant;
import java.util.Set;

@Service
public class WeatherImportService {

    private static final String WEATHER_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private final RestClient restClient;

    public WeatherImportService() {
        this.restClient = RestClient.create();
    }

    private String downloadWeatherXml() {
        return restClient.get()
                .uri(WEATHER_URL)
                .retrieve()
                .body(String.class);
    }

    public String getTagValue(Element station, String tagName) {
        String value = station.getElementsByTagName(tagName)
                .item(0)
                .getTextContent()
                .trim();

        return value.isEmpty() ? null : value;
    }

    public void findRequiredStations() throws Exception {
        String xml = downloadWeatherXml();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(
                new InputSource(new StringReader(xml))
        );

        //time at the root of the document
        Element root = document.getDocumentElement();
        long timestamp = Long.parseLong(root.getAttribute("timestamp"));
        Instant observedAt = Instant.ofEpochSecond(timestamp);

        //stations from tag name
        NodeList stations = document.getElementsByTagName("station");
        Set<String> requiredStations = Set.of(
                "Tallinn-Harku",
                "Tartu-Tõravere",
                "Pärnu"
        );

        for (int i = 0; i < stations.getLength(); i++) {
            Element station = (Element) stations.item(i);

            String name = station.getElementsByTagName("name")
                    .item(0).getTextContent().trim();

            if (requiredStations.contains(name)) {
                String wmoCode = getTagValue(station, "wmocode");
                String temperature = getTagValue(station, "airtemperature");
                String windSpeed = getTagValue(station, "windspeed");
                String phenomenon = getTagValue(station, "phenomenon");

                System.out.println("Station: " + name);
                System.out.println("WMO code: " + wmoCode);
                System.out.println("Temperature: " + temperature);
                System.out.println("Wind speed: " + windSpeed);
                System.out.println("Phenomenon: " + phenomenon);
                System.out.println("Observed at: " + observedAt);
                System.out.println("--------------------");
            }
        }

    }


}
