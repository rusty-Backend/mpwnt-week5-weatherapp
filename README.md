# mpwnt-week5-weatherapp

# Viikkotehtävä 5: Sääsovellus
Yksinkertainen Android-sääsovellus, joka hakee säätietoja API-rajapinnan avulla. HUOM: Tehtävän ohjeistuksesta poiketen käyntin WeatherAPI.com rajapintaa enkä OpenWeatherMap-rajapintaa, koska en löytänyt (tämä oli nyt puhtaasti skill issue) OpenWeatherin sivuilta sellaista API-avainta joka ei olisi vaatinut maksutietoja, vaikka olisinkin ollut ilmainen. WeatherAPI ei pyytänyt maksutietoja joten päädyin käyttämään sitä. Toimintaperiaate on kuitenkin molemmissa sama!

# Video

https://www.youtube.com/shorts/MyJ_LzqdlQ4?feature=shared

# Retrofit
Retrofit hoitaa HTTP-pyyntöjen hallinnan. Eli se muodostaa URL:n automaattisesti jolla haetaan sitten HTTP GET-pyyntöä ja palauttaa vastauksen Kotlin-oliona
-> baseurl + "current.json?=q=Helsinki&key=xxx"

````kotlin
object RetrofitInstance {
    private const val BASE_URL = "https://api.weatherapi.com/v1/"
    
    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}

````

# JSON/GSON
Gson muuttaa JSON-datan automaattisesti Kotlin dataluokaksi

```json
{
  "location": {
    "name": "Helsinki",
    "country": "Finland"
  },
  "current": {
    "temp_c": 15.5,
    "condition": {
      "text": "Partly cloudy"
    }
  }
}
```

````kotlin
data class WeatherResponse(
    @SerializedName("location")
    val location: Location,
    
    @SerializedName("current")
    val current: Current
)

data class Location(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("country")
    val country: String
)
````

@SerializedName kertoo Gsonille, että hae JSON kenttä nimeltä country ja muuta se Kotlin muuttajaksi country

# Coroutines
Coroutines Coroutines mahdollistaa verkkopyyntöjen tekemisen ilman UI:n jäätymistä. viewModelScope hallinnoi coroutineja automaattisesti.
````kotlin
fun fetchWeather() {
    viewModelScope.launch {
        _uiState.value = UiState(isLoading = true)
        
        val response = api.getCurrentWeather(...)
        
        _uiState.value = UiState(weatherData = response)
    }
}
````
1. viewModelScope.launch käynnistää coroutinen taustalle
2. API-kutsu tehdään taustasäikeessä (suspend-funktio)
3. UI-thread pysyy vapaana → sovellus ei jäädy
4. Kun data saapuu, päivitetään UI-tila
5. Compose näkee muutoksen ja päivittää näkymän automaattisesti

# StateFlow

Vanha kunnon ja tutuksi tullut stateFlow
WeatherViewModel.kt
````kotlin
data class UiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherResponse? = null,
    val errorMessage: String? = null
)

// Yksityinen (vain ViewModel voi muuttaa)
private val _uiState = MutableStateFlow(UiState())

// Julkinen (UI voi lukea)
val uiState: StateFlow<UiState> = _uiState.asStateFlow()
````
WeatherScreen.kt
````kotlin
val uiState by viewModel.uiState.collectAsState()

when {
    uiState.isLoading -> CircularProgressIndicator()
    uiState.errorMessage != null -> ErrorCard()
    uiState.weatherData != null -> WeatherDisplay()
}
````
```
Käyttäjä → Button click → ViewModel.fetchWeather()
    ↓
ViewModel päivittää: _uiState.value = UiState(isLoading = true)
    ↓
collectAsState() havaitsee muutoksen
    ↓
Compose suorittaa UI:n uudelleen
    ↓
UI näyttää: CircularProgressIndicator
    ↓
API vastaa → ViewModel päivittää: _uiState.value = UiState(weatherData = ...)
    ↓
collectAsState() havaitsee muutoksen
    ↓
Compose suorittaa UI:n uudelleen
    ↓
UI näyttää: Säätiedot
```

collectAsState kuuntelee StateFlown muutoksia. Kun Viewmodel mututaa _uiState.value, niin Compose havaitsee sen. Sitten Composable suoritetaan uudelleen automaattisesti ja UI päivittyy vastaamaan uutta tilaa

# API-avaimen tallennus
Avain laitettu local.properties tiedostoon

WEATHER_API_KEY=MY_API_KEY

jota kutsutaan sitten build.gradle.kts.

Vastaavasti sitten käytetään koodissa esim:

````kotlin
val response = RetrofitInstance.api.getCurrentWeather(
    city = city,
    apiKey = BuildConfig.API_KEY
)
````
# Rakenne
```
app/src/main/java/com/example/week5/
├── data/
│   ├── model/
│   │   └── WeatherResponse.kt    # JSON dataluokat
│   └── remote/
│       ├── WeatherApi.kt          # Retrofit interface
│       └── RetrofitInstance.kt    # Retrofit konfiguraatio
├── ViewModel/
│   └── WeatherViewModel.kt        # UI-logiikka & tilan hallinta
├── ui/
│   └── WeatherScreen.kt           # Compose UI
└── MainActivity.kt                # Entry point
```
