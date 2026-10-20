# Delivery Fee Application
This repository contains a Spring Boot REST API, which calculates delivery fees based on city, vehicle type, and weather conditions. The project is created for a trial task for Fujitsu.

The application imports weather observations from the Estonian Environment Agency and stores them along with regional base fees and configurable weather rules in an H2 database.

## Technologies

- Java 25
- Spring Boot
- Spring Data JPA
- H2 Database
- JUnit 5 and Mockito

## Running the application
Clone the repository and navigate to the project directory:

```bash
git clone https://github.com/HannaVallner/delivery-fee
cd delivery-fee
```

Run the application with:
```powershell
.\mvnw.cmd spring-boot:run
```

The application runs on `http://localhost:8080`. The exact API endpoint URLs and their descriptions are listed in a later section.

Upon startup, the application initializes any missing default regional base fees and weather rules.

## Weather data import

Weather observations are imported automatically from the  [Estonian Environment Agency](https://www.ilmateenistus.ee/teenused/ilmainfo/eesti-vaatlusandmed-xml/)'s XML file hourly, at 15 minutes past the hour. The schedule can be reconfigured through the `weather.import.cron` property.

Observations are stored with their station name, WMO code, observation timestamp, air temperature, wind speed, and weather phenomenon. Historical observations are retained, so that delivery fees can be calculated for historical times.

## Default regional base fees

| City | Car | Scooter | Bike |
|---|---:|---:|---:|
| Tallinn | €4.00 | €3.50 | €3.00 |
| Tartu | €3.50 | €3.00 | €2.50 |
| Pärnu | €3.00 | €2.50 | €2.00 |

## Weather-fee rules

Weather rules are configurable through the weather-rule API.

| Condition | Vehicle | Fee or restriction |
|---|---|---|
| Temperature below −10°C | Scooter, Bike | +€1.00 |
| Temperature from −10°C to 0°C, inclusive | Scooter, Bike | +€0.50 |
| Wind speed from 10 to 20, inclusive | Bike | +€0.50 |
| Wind speed above 20 | Bike | Delivery forbidden |
| Snow or sleet | Scooter, Bike | +€1.00 |
| Rain | Scooter, Bike | +€0.50 |
| Glaze, hail, or thunder | Scooter, Bike | Delivery forbidden |

The final delivery fee is the regional base fee plus any applicable weather surcharges. Multiple surcharges can apply to the same delivery and are added altogether.

## Database

The application uses the H2 database. The H2 console is available at `http://localhost:8080/h2-console`.

- **JDBC URL:** `jdbc:h2:file:./data/delivery-fee-db`
- **Username:** `sa`
- **Password:** blank

## API endpoints
The delivery-fee endpoint supports cities `Tallinn`, `Tartu`, and `Pärnu`, and vehicle types `CAR`, `SCOOTER`, and `BIKE`. An optional `datetime` parameter allows for fee calculations using historical weather observations.

Example request:

`GET http://localhost:8080/api/delivery-fee?city=Tallinn&vehicleType=BIKE`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/delivery-fee` | Calculate delivery fee; accepts `city`, `vehicleType`, and optional `datetime` query parameters |
| GET | `/api/base-fees` | Retrieve regional base fees; supports optional filters |
| POST | `/api/base-fees` | Create a base fee |
| PUT | `/api/base-fees/{id}` | Update a base fee |
| DELETE | `/api/base-fees/{id}` | Delete a base fee |
| GET | `/api/weather-rules` | Retrieve weather rules; supports an optional vehicle-type filter |
| POST | `/api/weather-rules` | Create a weather rule |
| PUT | `/api/weather-rules/{id}` | Update a weather rule |
| DELETE | `/api/weather-rules/{id}` | Delete a weather rule |


## Tests

The project contains 15 tests to ensure that delivery fee calculations, weather-based surcharges, vehicle restrictions, and the handling of missing data function as per the given business rules.

Run the tests with:
```powershell
.\mvnw.cmd test
```

