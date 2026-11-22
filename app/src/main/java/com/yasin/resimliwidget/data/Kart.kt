package com.yasin.resimliwidget.data

enum class KartTipi {
    ISIM,
    BASLIK
}

data class Kart(
    val id: String,
    val tip: KartTipi,
    val baslik: String,
    val altyazi: String?,
    val gorsel: String
) {
    val gorselYolu: String
        get() = "$gorsel.jpg"
}
