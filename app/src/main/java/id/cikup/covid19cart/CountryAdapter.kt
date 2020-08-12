package id.cikup.covid19cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_country.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CountryAdapter(private var negara: ArrayList<Negara>, private val clickListener: (Negara) -> Unit)
    : RecyclerView.Adapter<CountryAdapter.ViewHolder>(), Filterable {

    var countryFilterList = ArrayList<Negara>()

    init {
        countryFilterList = negara
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_country, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return countryFilterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(countryFilterList[position], clickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val country: Button = itemView.countryName
        fun bind(negara: Negara, clickListener: (Negara) -> Unit) {
            val country: TextView = itemView.countryName
            val cTotalCase: TextView = itemView.country_total_case
            val cTotalRecovered: TextView = itemView.country_total_recovered
            val cTotalDeaths: TextView = itemView.country_total_deaths
            val flag: ImageView = itemView.img_flag_circle
            val formatter: NumberFormat = DecimalFormat("#,###")
            country.countryName.text = negara.Country
            cTotalCase.country_total_case.text = formatter.format(negara.TotalConfirmed.toDouble())
            cTotalRecovered.country_total_recovered.text = formatter.format(negara.TotalRecovered.toDouble())
            cTotalDeaths.country_total_deaths.text = formatter.format(negara.TotalDeaths.toDouble())
            Glide.with(itemView).load("https://www.countryflags.io/" + negara.CountryCode + "/flat/16.png").into(flag)
            country.setOnClickListener { clickListener(negara)}
            cTotalCase.setOnClickListener { clickListener(negara)}
            cTotalRecovered.setOnClickListener { clickListener(negara)}
            cTotalDeaths.setOnClickListener { clickListener(negara)}
            flag.setOnClickListener { clickListener(negara) }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                countryFilterList = if (charSearch.isEmpty()) {
                    negara
                } else {
                    val resultList = ArrayList<Negara>()
                    for (row in negara) {
                        if (row.Country.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<Negara>
                notifyDataSetChanged()
            }

        }
    }

}
