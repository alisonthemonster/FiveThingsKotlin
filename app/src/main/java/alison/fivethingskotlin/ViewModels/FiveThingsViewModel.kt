package alison.fivethingskotlin.ViewModels

import alison.fivethingskotlin.API.repository.FiveThingsRepository
import alison.fivethingskotlin.API.repository.FiveThingsRepositoryImpl
import alison.fivethingskotlin.Models.FiveThings
import alison.fivethingskotlin.Util.Resource
import alison.fivethingskotlin.Util.getNextDate
import alison.fivethingskotlin.Util.getPreviousDate
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import java.util.*

class FiveThingsViewModel(val token: String, val fiveThingsRepository: FiveThingsRepository) : ViewModel() {

    private val fiveThingsData = MutableLiveData<Resource<FiveThings>>()

    fun getFiveThings(date: Date): LiveData<Resource<FiveThings>> {
        return fiveThingsRepository.getFiveThings(token, date, fiveThingsData)
    }

    fun onEditText() {
        //TODO: BUG when bindings get executed and text placed in edittexts for the first time
        //it counts as an edit, which overwrites the edited field to cause the save button to always
        //say save instead of saved
        val fiveThings = fiveThingsData.value
        fiveThings?.data?.edited = true
        fiveThingsData.value = fiveThings
    }

    fun writeFiveThings(fiveThings: FiveThings): LiveData<Resource<List<Date>>> {
        return fiveThingsRepository.saveFiveThings(token, fiveThings, fiveThingsData)
    }

    fun getToday(): LiveData<Resource<FiveThings>> {
        return getFiveThings(Date())
    }

    fun getPreviousDay(date: Date): LiveData<Resource<FiveThings>> {
        val prevDate = getPreviousDate(date)
        return getFiveThings(prevDate)
    }

    fun getNextDay(date: Date): LiveData<Resource<FiveThings>>  {
        val nextDate = getNextDate(date)
        return getFiveThings(nextDate)
    }

    fun changeDate(date: Date): LiveData<Resource<FiveThings>> {
        return getFiveThings(date)
    }

    fun getWrittenDays(): LiveData<Resource<List<Date>>> {
        return fiveThingsRepository.getWrittenDates(token)
    }
}