package alison.fivethingskotlin.analytics

import alison.fivethingskotlin.api.FiveThingsService
import alison.fivethingskotlin.util.*
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Color
import io.reactivex.disposables.CompositeDisposable
import lecho.lib.hellocharts.model.*
import java.util.*
import kotlin.collections.ArrayList


class AnalyticsViewModel(private val fiveThingsService: FiveThingsService = FiveThingsService.create()) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val chartData = MutableLiveData<LineChartData>()

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
        calendar.add(Calendar.HOUR, 24)
        val d12 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d13 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d14 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d15 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d16 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d17 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d18 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d19 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d20 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d21 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d22 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d23 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d24 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d25 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d26 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d27 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d28 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d29 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d30 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d31 = calendar.time
        calendar.add(Calendar.HOUR, 24)
        val d32 = calendar.time

        val values = ArrayList<PointValue>()
        values.add(PointValue(d1.time.toFloat(), -.5f))
        values.add(PointValue(d2.time.toFloat(), -1f))
        values.add(PointValue(d3.time.toFloat(), .2f))
        values.add(PointValue(d4.time.toFloat(), 1f))
        values.add(PointValue(d5.time.toFloat(), -.1f))
        values.add(PointValue(d6.time.toFloat(),  .8f))
        values.add(PointValue(d7.time.toFloat(), 0f))
//        values.add(PointValue(d8.time.toFloat(), -.2f))
//        values.add(PointValue(d9.time.toFloat(), -.2f))
//        values.add(PointValue(d10.time.toFloat(), -.1f))
//        values.add(PointValue(d11.time.toFloat(), .2f))
//        values.add(PointValue(d12.time.toFloat(), .3f))
//        values.add(PointValue(d13.time.toFloat(), -.6f))
//        values.add(PointValue(d14.time.toFloat(), -.3f))
//        values.add(PointValue(d15.time.toFloat(), .14f))
//        values.add(PointValue(d16.time.toFloat(), .47f))
//        values.add(PointValue(d17.time.toFloat(), .23f))
//        values.add(PointValue(d18.time.toFloat(), .2f))
//        values.add(PointValue(d19.time.toFloat(), -.42f))
//        values.add(PointValue(d20.time.toFloat(), .82f))
//        values.add(PointValue(d21.time.toFloat(), .22f))
//        values.add(PointValue(d22.time.toFloat(), .12f))
//        values.add(PointValue(d23.time.toFloat(), .32f))
//        values.add(PointValue(d24.time.toFloat(), -.12f))
//        values.add(PointValue(d25.time.toFloat(), .09f))
//        values.add(PointValue(d26.time.toFloat(), .3f))
//        values.add(PointValue(d27.time.toFloat(), -.7f))
//        values.add(PointValue(d28.time.toFloat(), -.1f))
//        values.add(PointValue(d29.time.toFloat(), 0f))
//        values.add(PointValue(d30.time.toFloat(), .34f))
//        values.add(PointValue(d31.time.toFloat(), .22f))
//        values.add(PointValue(d32.time.toFloat(), .56f))

        chartData.postValue(buildChart(values))

//        //TODO find out the format of the strings in request
//        disposables.add(fiveThingsService.getSentimentOverTime(token, startDateString, endDateString)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        { sentimentPoints ->
//                            chartData.postValue(buildChart(sentimentPoints))
//                        },
//                        { error ->
//                            errorLiveEvent.postValue(error.localizedMessage)
//                        }
//                ))
    }

    fun getChartData(): LiveData<LineChartData> {
        return chartData
    }

    private fun buildChart(values: List<PointValue>): LineChartData {
        val line = Line(values)
                .setColor(Color.WHITE)
        val lines = ArrayList<Line>()
        lines.add(line)

        val data = LineChartData()
        data.lines = lines

        val startDate = Date(values!![0].x.toLong())
        val endDate = Date(values[values.size - 1].x.toLong())

        val daysBetween = getDaysBetween(startDate, endDate)

        val axis = if (daysBetween < 7) {
            getAxisForSevenDays(startDate)
        } else if (daysBetween < 30) {
            getAxisForLastThirtyDays(startDate)
        } else if (daysBetween < 90) {
            getAxisForLastNinetyDays(startDate)
        } else {
            getAxisForAllDays(values)
        }

        axis.textColor = Color.WHITE
        data.axisXBottom = axis

        return data
    }


    private fun getAxisForSevenDays(startDay: Date): Axis {
        val axis = Axis()
        val values = mutableListOf<AxisValue>()
        var currentDay = startDay
        for (i in 1..7) {
            val axisValue = AxisValue(currentDay.time.toFloat())
            axisValue.setLabel(getDayOfWeekShort(currentDay))
            currentDay = getNextDate(currentDay)
            values.add(axisValue)
        }
        axis.values = values
        return axis
    }

    private fun getAxisForLastThirtyDays(startDay: Date): Axis {
        val axis = Axis()
        val values = mutableListOf<AxisValue>()
        var currentDay = startDay
        for (i in 1..7) {
            val axisValue = AxisValue(currentDay.time.toFloat())
            axisValue.setLabel("${getMonthNumber(currentDay)}/${getDay(currentDay)}")
            values.add(axisValue)
            currentDay = addXDaysToDate(currentDay, 5)
        }
        axis.values = values
        return axis
    }

    private fun getAxisForLastNinetyDays(startDay: Date): Axis {
        val axis = Axis()
        val values = mutableListOf<AxisValue>()
        var currentDay = startDay
        for (i in 1..7) {
            val axisValue = AxisValue(currentDay.time.toFloat())
            axisValue.setLabel("${getMonthNumber(currentDay)}/${getDay(currentDay)}")
            values.add(axisValue)
            currentDay = addXDaysToDate(currentDay, 15)
        }
        axis.values = values
        return axis
    }

    private fun getAxisForLastYear(startDate: Date): Axis {
        val axis = Axis()
        val values = mutableListOf<AxisValue>()

        var month = getMonthNumber(startDate)
        val year = getYear(startDate)

        for (i in 1..12) {
            val date = getFirstOfMonth(month, year)
            val axisValue = AxisValue(date.time.toFloat())
            axisValue.setLabel(getShortMonth(date))
            values.add(axisValue)
            month = getMonthNumber(getNextMonth(date))
        }
        axis.values = values
        return axis
    }

    private fun getAxisForAllDays(days: List<PointValue>): Axis {
        val axis = Axis()
        val values = mutableListOf<AxisValue>()
        for (day in days) {
            val date = Date(day.x.toLong())
            val axisValue = AxisValue(day.x)
            axisValue.setLabel("${getMonthNumber(date)}/${getDay(date)}")
            values.add(axisValue)
        }
        axis.values = values
        return axis
    }
}