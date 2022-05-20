package com.example.dolarblue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dolarblue.databinding.ActivityMainBinding
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.calculateButton.setOnClickListener {calculateDollarExchange()}
        binding.updateDollar.setOnClickListener {getDollarToArg()}
        binding.calculateGwei.setOnClickListener {getGweiToUsd()}

        }

    private fun calculateDollarExchange() {
        val amountToExchange = binding.costOfServiceEditText.text.toString()
        val dolarPrice = binding.dolarPrice.text.toString()

        val a = amountToExchange.toDoubleOrNull()
        val d = dolarPrice.toDoubleOrNull()

        val formatCurrency = NumberFormat.getCurrencyInstance()

        when{
            a == null && d == null -> {
                binding.totalAmount.text = getString(R.string.error_total)
            }
            a == null && d != null -> {
                binding.totalAmount.text = getString(R.string.error_monto)
                binding.amountToPay.hint = getString(R.string.error_monto)
            }
            a != null && d == null -> {
                binding.totalAmount.text = getString(R.string.error_dolar)
                binding.dolarPrice.hint = getString(R.string.error_dolar)
            }
            else -> {
                val formattedTotal = formatCurrency.format(amountToExchange.toDouble() * dolarPrice.toDouble())
                binding.totalAmount.text = getString(R.string.total_amount, formattedTotal)
                binding.amountToPay.hint = getString(R.string.cantidad_a_cambiar)
            }
        }
    }

    private fun getDollarToArg() {
        val textView = findViewById<TextView>(R.id.dolarPrice)
        val queue = Volley.newRequestQueue(this)
        val url = "https://app.ripio.com/api/v3/public/rates/"

        val stringRequest = StringRequest(url,
            { response -> textView.text = usdcPriceArg(response)
            },  { error -> Toast.makeText(this, "No hay internet!", Toast.LENGTH_SHORT).show() }
        )
        queue.add(stringRequest)
    }

    private fun getGweiToUsd() {
        val textView2 = findViewById<TextView>(R.id.gwei_text)
        val queue2 = Volley.newRequestQueue(this)
        val url = "https://etherscan.io/gastracker"

        val stringRequest = StringRequest(url,
            { response -> textView2.text = gasEstimate(response)
            },  { error -> Toast.makeText(this, "No hay internet!", Toast.LENGTH_SHORT).show() }
        )
        queue2.add(stringRequest)
    }

    private fun usdcPriceArg(string: String): String {
        val array = string.split("{\"ticker\"")
        return array[2].split("\",\"")[2].replace("sell_rate\":\"", "")
    }

    private fun gasEstimate(string: String): String {
        val array = string.split("spanHighPriorityAndBase")
        val gweiAmount = array[0].split("spanHighPrice\">")[1].split("</span>")[0]
        val gweiToUsd = array[1].split("text-secondary\">")[1].split("|")[0].replace("\n", "")

        return "$gweiAmount Gwei = $gweiToUsd USD"
    }
}
