package id.cikup.covid19cart

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.activity_chart_country.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ChartCountryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COUNTRY = "extra_country"
        const val EXTRA_LATESTUPDATE = "latest_update"
        const val EXTRA_NEWDEATH = "extra_newdeath"
        const val EXTRA_NEWCONFIRMED = "extra_newconfirmed"
        const val EXTRA_NEWRECOVERED = "extra_newrecovered"
        const val EXTRA_TOTALDEATH = "extra_totaldeath"
        const val EXTRA_TOTALCONFIRMED = "extra_totalconfirmed"
        const val EXTRA_TOTALRECOVERED = "extra_totalrecovered"
        const val EXTRA_COUNTRYID = "extra_countryid"
        lateinit var simpanDataNegara: String
        lateinit var simpanDataFlag: String
    }
    private val sharedPrefFile = "kotlinsharedpreference"
    private lateinit var sharedPreferences: SharedPreferences
    private var dayCases = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_country)

        sharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)

        val formatter: NumberFormat = DecimalFormat("#,###")
        val intentNamaNegara: String? = intent.getStringExtra(EXTRA_COUNTRY)
        val lastUdate: String? = intent.getStringExtra(EXTRA_LATESTUPDATE)
        val newDeath: String? = intent.getStringExtra(EXTRA_NEWDEATH)
        val totalDeath: String? = intent.getStringExtra(EXTRA_TOTALDEATH)
        val newConfirmed: String? = intent.getStringExtra(EXTRA_NEWCONFIRMED)
        val totalConfirmed: String? = intent.getStringExtra(EXTRA_TOTALCONFIRMED)
        val newRecovery: String? = intent.getStringExtra(EXTRA_NEWRECOVERED)
        val totalRecovery: String? = intent.getStringExtra(EXTRA_TOTALRECOVERED)
        val idCountry: String? = intent.getStringExtra(EXTRA_COUNTRYID)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        txt_name_country.text = intentNamaNegara.toString()
        latest_update.text = lastUdate.toString()
        hasil_total_death_currently.text = formatter.format(totalDeath.toString().toDouble())
        hasil_new_death_currently.text = formatter.format(newDeath.toString().toDouble())
        hasil_total_confirmed_currently.text = formatter.format(totalConfirmed.toString().toDouble())
        hasil_new_confirmed_currently.text = formatter.format(newConfirmed.toString().toDouble())
        hasil_total_recovery_currently.text = formatter.format(totalRecovery.toString().toDouble())
        hasil_new_recovery_currently.text = formatter.format(newRecovery.toString().toDouble())
        editor.putString(intentNamaNegara, intentNamaNegara)
        editor.apply()
        editor.commit()

        val simpanNegara = sharedPreferences.getString(intentNamaNegara, intentNamaNegara)
        val simpanFlag = sharedPreferences.getString(idCountry, idCountry)
        simpanDataNegara = simpanNegara.toString()
        simpanDataFlag = simpanFlag.toString() + "/flat/64.png"

        if (simpanFlag != null){
            Glide.with(this).load("https://www.countryflags.io/$simpanDataFlag").into(img_flag_country)
        }else{
            Toast.makeText(this, "Image Not Found", Toast.LENGTH_SHORT).show()
        }

        getCountry()

    }

    private fun getCountry() {
        val okkhtp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/dayone/country/")
            .client(okkhtp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(InfoService::class.java)
        api.getInfoService(simpanDataNegara).enqueue(object : Callback<List<InfoNegara>> {
            @SuppressLint("SimpleDateFormat")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<InfoNegara>>,
                response: Response<List<InfoNegara>>
            ) {
                val getListDataCorona:List<InfoNegara> = response.body()!!
                if (response.isSuccessful) {
                    val barEntries: ArrayList<BarEntry> = ArrayList()
                    val barEntries2: ArrayList<BarEntry> = ArrayList()
                    val barEntries3: ArrayList<BarEntry> = ArrayList()
                    val barEntries4: ArrayList<BarEntry> = ArrayList()
                    var i = 0

                    while (i < getListDataCorona.size) {
                        for (s in getListDataCorona) {
                            val barEntry = BarEntry(i.toFloat(), s.Confirmed.toFloat())
                            val barEntry2 = BarEntry(i.toFloat(), s.Deaths.toFloat())
                            val barEntry3 = BarEntry(i.toFloat(), s.Recovered.toFloat())
                            val barEntry4 = BarEntry(i.toFloat(), s.Active.toFloat())

                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'")
                            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
                            val date: Date? = inputFormat.parse(s.Date)
                            val formattedDate: String = outputFormat.format(date!!)
                            dayCases.add(formattedDate)

                            barEntries.add(barEntry)
                            barEntries2.add(barEntry2)
                            barEntries3.add(barEntry3)
                            barEntries4.add(barEntry4)

                            i++
                        }
                    }

                    val xAxis: XAxis = barChartView.xAxis
                    xAxis.valueFormatter = IndexAxisValueFormatter(dayCases)
                    barChartView.axisLeft.axisMinimum = 0f
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    xAxis.setCenterAxisLabels(true)
                    xAxis.isGranularityEnabled = true

                    val barDataSet = BarDataSet(barEntries, "Confirmed")
                    val barDataSet2 = BarDataSet(barEntries2, "Deaths")
                    val barDataSet3 = BarDataSet(barEntries3, "Recovered")
                    val barDataSet4 = BarDataSet(barEntries4, "Active")
                    barDataSet.setColors(Color.parseColor("#F44336"))
                    barDataSet2.setColors(Color.parseColor("#FFEB3B"))
                    barDataSet3.setColors(Color.parseColor("#03DAC5"))
                    barDataSet4.setColors(Color.parseColor("#2196F3"))

                    val data = BarData(barDataSet, barDataSet2, barDataSet3, barDataSet4)
                    barChartView.data = data

                    val barSpace = 0.02f
                    val groupSpace = 0.3f
                    val groupCount = 4f

                    data.barWidth = 0.15f
                    barChartView.invalidate()
                    barChartView.setNoDataTextColor(R.color.black)
                    barChartView.setTouchEnabled(true)
                    barChartView.description.isEnabled = false
                    barChartView.xAxis.axisMinimum = 0f
                    barChartView.setVisibleXRangeMaximum(0f + barChartView.barData.getGroupWidth(groupSpace, barSpace) * groupCount)
                    barChartView.groupBars(0f, groupSpace, barSpace)
                }
            }

            override fun onFailure(call: Call<List<InfoNegara>>, t: Throwable) {
                Toast.makeText(this@ChartCountryActivity, "error, please re-enter to this Country", Toast.LENGTH_SHORT).show()
            }
        })
    }

}