package id.cikup.covid19cart

data class AllNegara(
    val Global:Dunia,
    val Countries: List<Negara>
)

data class Dunia(
    val TotalConfirmed: String = "",
    val TotalRecovered: String = "",
    val TotalDeaths: String = ""
)

data class Negara (
    val Country: String = "",
    val Date: String = "",
    val NewConfirmed: String = "",
    val TotalConfirmed: String = "",
    val TotalDeaths: String = "",
    val NewDeaths: String = "",
    val TotalRecovered: String = "",
    val NewRecovered: String = "",
    val CountryCode: String = ""
)

data class InfoNegara(
    val Deaths: String = "",
    val Confirmed: String = "",
    val Recovered: String = "",
    val Active: String = "",
    val Date: String = ""
)
