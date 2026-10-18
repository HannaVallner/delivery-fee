package ee.fujitsu.delivery_fee.service;

import ee.fujitsu.delivery_fee.entity.WeatherObservation;
import ee.fujitsu.delivery_fee.repository.WeatherObservationRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.scheduling.annotation.Scheduled;

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
    private final WeatherObservationRepository weatherObservationRepository;

    public WeatherImportService(WeatherObservationRepository weatherObservationRepository) {
        this.restClient = RestClient.create();
        this.weatherObservationRepository = weatherObservationRepository;

    }

    private String downloadWeatherXml() {
        return restClient.get()
                .uri(WEATHER_URL)
                .retrieve()
                .body(String.class);
    }

    public String getTagValue(Element station, String tagName) {
        Node node = station.getElementsByTagName(tagName).item(0);
        if (node == null) {
            return null;
        }
        String value = node.getTextContent().trim();
        return value.isEmpty() ? null : value;
    }

    public Double getDoubleValue(Element station, String tagName) {
        String tagValue = getTagValue(station, tagName);
        if (tagValue == null) {
            return null;
        }
        return Double.parseDouble(tagValue);
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
        String timestamp = root.getAttribute("timestamp");
        if (timestamp.isBlank()) {
            throw new IllegalArgumentException(
                    "Weather XML is missing the observations' timestamp"
            );
        }
        Instant observedAt = Instant.ofEpochSecond(Long.parseLong(timestamp));

        //stations from tag name
        NodeList stations = document.getElementsByTagName("station");
        Set<String> requiredStations = Set.of(
                "Tallinn-Harku",
                "Tartu-Tõravere",
                "Pärnu"
        );

        for (int i = 0; i < stations.getLength(); i++) {
            Element station = (Element) stations.item(i);

            String name = getTagValue(station, "name");

            // Skip unneeded stations, including stations without a name
            if (requiredStations.contains(name)) {
                String wmoCode = getTagValue(station, "wmocode");
                Double temperature = getDoubleValue(station, "airtemperature");
                Double windSpeed = getDoubleValue(station, "windspeed");
                String phenomenon = getTagValue(station, "phenomenon");

                WeatherObservation observation = new WeatherObservation();
                observation.setObservedAt(observedAt);
                observation.setStationName(name);
                observation.setWmoCode(wmoCode);
                observation.setAirTemperature(temperature);
                observation.setWindSpeed(windSpeed);
                observation.setWeatherPhenomenon(phenomenon);

                weatherObservationRepository.save(observation);
            }
        }

    }

    @Scheduled(cron = "0 15 * * * *")
    public void scheduledWeatherImport() throws Exception {
        findRequiredStations();
        System.out.println("Scheduled weather import started");
    }

}
