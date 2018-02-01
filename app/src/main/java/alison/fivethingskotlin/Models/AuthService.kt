package alison.fivethingskotlin.Models


interface AuthService {

    companion object {
        fun create(): AuthService = RetrofitHelper.build().create(AuthService::class.java)
    }

}