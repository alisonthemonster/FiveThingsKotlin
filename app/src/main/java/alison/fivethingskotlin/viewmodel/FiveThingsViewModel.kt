package alison.fivethingskotlin.viewmodel

import alison.fivethingskotlin.api.repository.FiveThingsRepository
import alison.fivethingskotlin.model.FiveThings
import alison.fivethingskotlin.model.Resource
import alison.fivethingskotlin.model.Status
import alison.fivethingskotlin.util.getNextDate
import alison.fivethingskotlin.util.getPreviousDate
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import java.util.*


class FiveThingsViewModel(private val fiveThingsRepository: FiveThingsRepository, private val authState: AuthState?, private val authorizationService: AuthorizationService) : ViewModel() {

    private val fiveThingsData = MutableLiveData<Resource<FiveThings>>()
    private val datesLiveData = MutableLiveData<Resource<List<Date>>>()


    fun getFiveThings(date: Date): LiveData<Resource<FiveThings>> {

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                fiveThingsData.postValue(Resource(Status.ERROR, "Log in failed: ${ex.errorDescription}", null))
            } else {
                idToken?.let {
                    val things = fiveThingsRepository.getFiveThings("Bearer $idToken", date, fiveThingsData)
                    fiveThingsData.postValue(things.value)
                }
            }
        }
        return fiveThingsData
    }

    fun saveFiveThings(fiveThings: FiveThings): LiveData<Resource<List<Date>>> {

        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                datesLiveData.postValue(Resource(Status.ERROR, "Log in failed: ${ex.errorDescription}", null))
            } else {
                idToken?.let {
                    val dates = fiveThingsRepository.saveFiveThings("Bearer $idToken", fiveThings, fiveThingsData, datesLiveData)
                    datesLiveData.postValue(dates.value)
                }
            }
        }
        return datesLiveData
    }

    fun getWrittenDays(): LiveData<Resource<List<Date>>> {
        authState?.performActionWithFreshTokens(authorizationService) { accessToken, idToken, ex ->
            if (ex != null) {
                datesLiveData.postValue(Resource(Status.ERROR, "Log in failed: ${ex.errorDescription}", null))
            } else {
                idToken?.let {
                    val dates = fiveThingsRepository.getWrittenDates("Bearer $idToken", datesLiveData)
                    datesLiveData.postValue(dates.value)
                }
            }
        }

        return datesLiveData
    }

    private var editCount = 0

    fun onEditText() {
        if (editCount >= 5) {
            //FIX: ignores the first five edit texts that occur thanks to data binding
            val fiveThings = fiveThingsData.value
            fiveThings?.data?.edited = true
            fiveThingsData.value = fiveThings
        }
        editCount++
    }

}