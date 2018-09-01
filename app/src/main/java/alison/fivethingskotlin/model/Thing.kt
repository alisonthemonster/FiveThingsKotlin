package alison.fivethingskotlin.model

data class Thing(val date: String,
                 var content: String,
                 val order: Int) {

    val isEmpty: Boolean
        get() {
            return content.isEmpty()
        }
}