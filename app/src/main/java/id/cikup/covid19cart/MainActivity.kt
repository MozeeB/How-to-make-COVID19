package id.cikup.covid19cart

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import kotlin.sequences.sequence

class MainActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null
    private var ascending = true

    companion object{
        lateinit var adapters:CountryAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progress_Bar)

        search_view.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapters.filter.filter(newText)
                return false
            }
        })

        swipe_refresh.setOnRefreshListener {
            getCountry()
            swipe_refresh.isRefreshing = false
        }

        getCountry()
        initializeViews()

    }

    private fun initializeViews() {
        sequence.setOnClickListener {
            sequenceWithoutInternet(ascending)
            ascending = !ascending
        }
    }

    private fun sequenceWithoutInternet(asc: Boolean){
        recyclerViewCountry.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            if (asc){
                (layoutManager as LinearLayoutManager).reverseLayout = true
                (layoutManager as LinearLayoutManager).stackFromEnd = true
                Toast.makeText(this@MainActivity, "Z - A", Toast.LENGTH_SHORT).show()
            }else{
                (layoutManager as LinearLayoutManager).reverseLayout = false
                (layoutManager as LinearLayoutManager).stackFromEnd = false
                Toast.makeText(this@MainActivity, "A - Z", Toast.LENGTH_SHORT).show()
            }
            adapter = adapters
        }
    }

    private fun getCountry(){
        val okkhtp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/")
            .client(okkhtp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)
        api.getAllNegara().enqueue(object : Callback<AllNegara> {
            override fun onResponse(call: Call<AllNegara>, response: Response<AllNegara>) {
                if (response.isSuccessful) {
                    val getListDataCorona = response.body()!!.Global
                    val formatter: NumberFormat = DecimalFormat("#,###")
                    confirmed_globe.text = formatter.format(getListDataCorona.TotalConfirmed.toDouble())
                    recovered_globe.text = formatter.format(getListDataCorona.TotalRecovered.toDouble())
                    deaths_globe.text = formatter.format(getListDataCorona.TotalDeaths.toDouble())
                    recyclerViewCountry.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        progressBar?.visibility = View.GONE
                        adapters = CountryAdapter(
                            response.body()!!.Countries as ArrayList<Negara>
                        ) { negara -> itemClicked(negara)
                        }
                        adapter = adapters
                    }
                }else{
                    progressBar?.visibility = View.GONE
                    errorCuy(this@MainActivity)
                }
            }
            override fun onFailure(call: Call<AllNegara>, t: Throwable) {
                progressBar?.visibility = View.GONE
                errorCuy(this@MainActivity)
            }
        })
    }

    private fun itemClicked(negara: Negara) {
        val moveWithData = Intent(this@MainActivity, ChartCountryActivity::class.java)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_COUNTRY, negara.Country)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_LATESTUPDATE, negara.Date)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_NEWDEATH, negara.NewDeaths)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_NEWCONFIRMED, negara.NewConfirmed)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_NEWRECOVERED, negara.NewRecovered)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_TOTALDEATH, negara.TotalDeaths)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_TOTALCONFIRMED, negara.TotalConfirmed)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_TOTALRECOVERED, negara.TotalRecovered)
        moveWithData.putExtra(ChartCountryActivity.EXTRA_COUNTRYID, negara.CountryCode)
        startActivity(moveWithData)
    }

    private fun  errorCuy(context: Context){
        val builder = AlertDialog.Builder(context)
        with(builder){
            setTitle("Network Error!")
            setCancelable(false)
            setPositiveButton("REFRESH") { _, _ ->
                super.onRestart()
                val ripres = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(ripres)
                finish()
            }
            setNegativeButton("EXIT") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

}