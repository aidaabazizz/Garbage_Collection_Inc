## REQ5: Real Weather Anomaly System

### Pitch

This feature connects the game to the OpenWeather API and uses real weather to change the facility environment. The weather data is not just shown to the player. It is used to trigger different anomalies inside the game, such as conductive rain, heat distortion, and storm surge.

The worker triggers the feature through the SuperComputer. Depending on the weather returned by the API, nearby terrain, hazards, and actors may be affected.

---

### Mechanics

* The worker stands near the SuperComputer and selects the weather sync option.
* `WeatherSyncAction` builds a weather request using the current game state, instead of using one fixed URL.
* The current map and actor position help decide which real-world coordinates are used.
* `WeatherApiClient` sends the request to the OpenWeather API.
* The API response is stored as a `WeatherSnapshot`, so the rest of the game does not need to deal with raw JSON.
* `WeatherAnomalyManager` checks the snapshot using separate interpreter classes.
* If the humidity is high or the weather condition contains rain, the conductive rain anomaly can activate.
* If the temperature is high, the heat distortion anomaly can activate.
* If the wind speed is high or the weather condition contains storm/thunderstorm, the storm surge anomaly can activate.
* Depending on the active anomaly, the matching effect changes the nearby game world.
* `ConductiveRainEffect` creates `Puddle` terrain, suppresses `Extinguishable` hazards, and triggers `ChargeReactive` grounds from REQ3.
* `HeatDistortionEffect` strengthens `FireStackable` hazards and creates `BlueFire` near distorted or sanctuary-related areas from REQ4.
* `StormSurgeEffect` applies `GalvanicCharge`, triggers `AtmosphericChargeSource` behaviour, may damage actors, may place a new `AtmosphericChargeSource`, and may spread `RageGround`.
* The effects avoid overwriting important terrain such as the SuperComputer.
* Capability checks such as `FacilityCapability`, `MaterialCapability`, and `DistortionCapability` are used to decide whether a tile is safe to modify.
* The design avoids `switch` statements and `instanceof` checks by relying on polymorphism and capability-based checks.

---

### Architecture

#### Abstraction 1: WeatherAnomalyInterpreter

Package: `game.weather.interpreters`

`WeatherAnomalyInterpreter` represents the interpretation part of the weather system. Its job is to look at the `WeatherSnapshot` and decide whether a certain type of weather anomaly is active. These classes do not directly change the map. They only decide what the weather data means.

| Class                           | Type | Purpose                                                                                     |
| ------------------------------- | ---- | ------------------------------------------------------------------------------------------- |
| `HumidityAnomalyInterpreter`    | NEW  | Checks whether the weather is humid or rainy enough to trigger conductive rain.             |
| `TemperatureAnomalyInterpreter` | NEW  | Checks whether the temperature is high enough to trigger heat distortion.                   |
| `StormAnomalyInterpreter`       | NEW  | Checks whether the wind speed or weather condition is strong enough to trigger storm surge. |

#### Abstraction 2: AnomalyWorldEffect

Package: `game.weather.effects`

`AnomalyWorldEffect` represents the part of the system that actually changes the game world. These classes use the interpreted weather data to create effects such as terrain changes, hazard interaction, actor damage, and links to existing REQ3 and REQ4 systems.

| Class                  | Type | Purpose                                                                                                                                                  |
| ---------------------- | ---- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `ConductiveRainEffect` | NEW  | Creates `Puddle` terrain, suppresses `Extinguishable` hazards, and triggers `ChargeReactive` grounds.                                                    |
| `HeatDistortionEffect` | NEW  | Strengthens `FireStackable` hazards and creates `BlueFire` around distorted or sanctuary-related terrain.                                                |
| `StormSurgeEffect`     | NEW  | Applies `GalvanicCharge`, triggers `AtmosphericChargeSource`, may damage actors, may place a new `AtmosphericChargeSource`, and may spread `RageGround`. |

#### Higher-level classes using abstractions

| Class                   | Package         | Role                                                                                                                                                       |
| ----------------------- | --------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `WeatherSyncAction`     | `game.actions`  | The action offered through the SuperComputer. It builds the weather query, calls the API client, receives the snapshot, and passes it to the manager.      |
| `WeatherAnomalyManager` | `game.managers` | Coordinates all interpreters and effects using lists of the two abstractions. This keeps the manager dependent on interfaces rather than concrete classes. |

#### API support classes

| Class                  | Package        | Role                                                                                                         |
| ---------------------- | -------------- | ------------------------------------------------------------------------------------------------------------ |
| `WeatherApiClient`     | `game.weather` | Sends the HTTP request to OpenWeather and converts the response into a `WeatherSnapshot`.                    |
| `WeatherQuery`         | `game.weather` | Stores query values such as latitude, longitude, and units, then builds the final API URL.                   |
| `WeatherSnapshot`      | `game.weather` | Stores the parsed weather values: temperature, humidity, wind speed, condition, city name, and country code. |
| `WeatherConfig`        | `game.weather` | Reads the API key from the `OPENWEATHER_API_KEY` environment variable.                                       |
| `WeatherApiException`  | `game.weather` | Represents weather API errors, such as a missing API key, failed request, or parsing issue.                  |
| `WeatherSystemFactory` | `game.weather` | Builds and connects the weather action, API client, manager, interpreters, and effects.                      |

#### Existing systems/classes integrated

| Existing class/interface           | Package             | How REQ5 uses it                                                                               |
| ---------------------------------- | ------------------- | ---------------------------------------------------------------------------------------------- |
| `SuperComputer`                    | `game.grounds`      | Offers the weather sync action to the worker.                                                  |
| `Puddle`                           | `game.grounds`      | Created during conductive rain.                                                                |
| `ChargeReactive`                   | `game.highvoltage`  | Triggered by conductive rain.                                                                  |
| `ChargeContext` / `GalvanicCharge` | `game.highvoltage`  | Used to apply weather-related charge behaviour.                                                |
| `AtmosphericChargeSource`          | `game.grounds`      | Triggered or created during storm surge.                                                       |
| `BlueFire`                         | `game.grounds`      | Created during heat distortion.                                                                |
| `RageGround`                       | `game.grounds`      | May spread during storm surge.                                                                 |
| `Extinguishable`                   | `game.sanctuary`    | Suppressed by conductive rain.                                                                 |
| `FireStackable`                    | `game.capabilities` | Strengthened by heat distortion.                                                               |
| `FacilityCapability`               | `game.enums`        | Protects important facility tiles such as the SuperComputer.                                   |
| `MaterialCapability`               | `game.enums`        | Helps prevent unsuitable or energised tiles from being replaced.                               |
| `DistortionCapability`             | `game.enums`        | Helps protect distorted, sanctuary, or active hazard tiles from being overwritten incorrectly. |

---

### Request Example

The OpenWeather request is built dynamically by `WeatherQuery`. This means the request changes based on the game state instead of always using the same fixed URL.

Example request:

```text
https://api.openweathermap.org/data/2.5/weather?lat=-27.4705&lon=153.0260&appid=${OPENWEATHER_API_KEY}&units=metric
```

| Game-state value      | How it affects the request                                                        |
| --------------------- | --------------------------------------------------------------------------------- |
| Current map           | The map is linked to a `WeatherMapAnchor`, which provides real-world coordinates. |
| Actor location        | The actor’s current position can influence the selected query location.           |
| `WeatherMapAnchor`    | Converts the in-game map context into latitude and longitude.                     |
| Units                 | The request uses metric units so temperature is returned in Celsius.              |
| `OPENWEATHER_API_KEY` | The API key is inserted at runtime through `WeatherConfig`.                       |

For example, when the worker triggers the weather sync on the `99-Deprecated` map, the system selects the real-world coordinates linked to that map. `WeatherQuery` then combines those coordinates with the metric unit setting and the API key from `WeatherConfig` to build the final request.

---

### JSON Schema

The game expects the OpenWeather response to contain weather condition, temperature, humidity, wind speed, city name, and country code.

Example response structure:

```json
{
  "weather": [
    {
      "main": "Rain",
      "description": "light rain"
    }
  ],
  "main": {
    "temp": 28.5,
    "humidity": 82
  },
  "wind": {
    "speed": 9.1
  },
  "name": "Brisbane",
  "sys": {
    "country": "AU"
  }
}
```

| JSON field        | Stored in `WeatherSnapshot` as | Used for                                            |
| ----------------- | ------------------------------ | --------------------------------------------------- |
| `main.temp`       | `temperature`                  | Checks whether heat distortion should activate.     |
| `main.humidity`   | `humidity`                     | Checks whether conductive rain should activate.     |
| `wind.speed`      | `windSpeed`                    | Checks whether storm surge should activate.         |
| `weather[0].main` | `condition`                    | Checks for rain, storm, or thunderstorm conditions. |
| `name`            | `cityName`                     | Shows where the weather data came from.             |
| `sys.country`     | `countryCode`                  | Shows the country code with the city name.          |

The parsed `WeatherSnapshot` stores:

```text
temperature: double
humidity: int
windSpeed: double
condition: String
cityName: String
countryCode: String
```

---

### API Key Security Note

The OpenWeather API key is not hardcoded anywhere in the source code. It should also not be committed to the GitLab repository.

The key must be stored in this environment variable:

```text
OPENWEATHER_API_KEY
```

`WeatherConfig` reads the key at runtime using:

```java
System.getenv("OPENWEATHER_API_KEY")
```

If the environment variable is missing or blank, `WeatherConfig` throws `WeatherApiException`, and the weather sync does not continue.

Example setup:

Windows PowerShell:

```powershell
$env:OPENWEATHER_API_KEY="your_api_key_here"
```

Mac/Linux terminal:

```bash
export OPENWEATHER_API_KEY="your_api_key_here"
```

Security rules:

* Do not commit the actual API key to GitLab.
* Do not hardcode the key in `WeatherQuery`, `WeatherApiClient`, `WeatherConfig`, `Application`, or `README`.
* Do not push a `.env` file if it contains the real API key.
* The README can explain how to set the key, but it should only use a placeholder such as `your_api_key_here`.
* The API key should only be accessed through `WeatherConfig`.
