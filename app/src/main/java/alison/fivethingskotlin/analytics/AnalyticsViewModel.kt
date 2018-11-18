package alison.fivethingskotlin.analytics

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.util.SingleLiveEvent
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import io.reactivex.disposables.CompositeDisposable
import lecho.lib.hellocharts.model.PointValue
import java.util.*


class AnalyticsViewModel(private val fiveThingsService: FiveThingsService = FiveThingsService.create()) : ViewModel() {

    private val disposables = CompositeDisposable()


    private val sentimentDataPoints = MutableLiveData<ArrayList<PointValue>>()

    val errorLiveEvent = SingleLiveEvent<String>()

    fun getSentimentOverTime(token: String, startDate: Date, endDate: Date) {

        val calendar = Calendar.getInstance()
        val d1 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d2 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d3 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d4 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d5 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d6 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d7 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d8 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d9 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d10 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d11 = calendar.time

        val values = ArrayList<PointValue>()
        values.add(PointValue(d1.time.toFloat(), -.5f))
        values.add(PointValue(d2.time.toFloat(), -.4f))
        values.add(PointValue(d3.time.toFloat(), .2f))
        values.add(PointValue(d4.time.toFloat(), .11f))
//        values.add(PointValue(d5.time.toFloat(), -.1f))
        values.add(PointValue(d6.time.toFloat(),  .8f))
        values.add(PointValue(d7.time.toFloat(), 0f))
        values.add(PointValue(d8.time.toFloat(), -.2f))
//        values.add(PointValue(d9.time.toFloat(), -.2f))
//        values.add(PointValue(d10.time.toFloat(), .2f))

        sentimentDataPoints.postValue(values)



//        disposables.add(fiveThingsService.getSentimentOverTime(token, startDateString, endDateString)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        { sentimentPoints ->
//                            //post to the live data here!
//                        },
//                        { error ->
//                            errorLiveEvent.postValue(error.localizedMessage)
//                        }
//                ))
    }

    fun getSentimentDataPoints(): LiveData<ArrayList<PointValue>> {
        return sentimentDataPoints
    }
}