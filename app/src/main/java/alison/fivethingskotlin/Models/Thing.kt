package alison.fivethingskotlin.Models

data class Thing(val date: String,
                 var content: String,
                 val order: Int) {

    val isEmpty: Boolean
        get() {
            return content.isEmpty()
        }


}