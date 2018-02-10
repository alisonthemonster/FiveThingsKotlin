package alison.fivethingskotlin.API

import alison.fivethingskotlin.Models.FiveThings
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import java.util.*



class FiveThingsRepositoryImpl: FiveThingsRepository {

    init {
        RetrofitHelper.build().create(FiveThingsService::class.java)
    }

    override fun getFiveThings(date: Date, fiveThingsData: MutableLiveData<FiveThings>): LiveData<FiveThings> {
        return MutableLiveData<FiveThings>()
        //if server says token is bad call invalidateAuthToken
    }

    override fun saveFiveThings(fiveThings: FiveThings, fiveThingsData: MutableLiveData<FiveThings>) {
    }

    override fun getWrittenDates(): MutableLiveData<List<Date>> {
        return MutableLiveData()

    }

}
