package alison.fivethingskotlin.util

fun parseHour(value: String): Int {
    val time = value.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    return Integer.parseInt(time[0])
}

fun parseMinute(value: String): Int {
    val time = value.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    return Integer.parseInt(time[1])
}

fun timeToString(h: Int, m: Int): String {
    return String.format("%02d", h) + ":" + String.format("%02d", m)
}