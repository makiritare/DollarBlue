package com.example.dolarblue

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dolarblue.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import kotlin.math.abs
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//new implementation to calculate usdc transfer fee
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.calculateButton.setOnClickListener { calculateDollarExchange() }
        binding.dolarHoyButton.setOnClickListener { getDolarHoy() }
        gestureDetector = GestureDetector(this, GestureListener())


        binding.someButton.setOnClickListener {
            scope.launch {
                try {
                    val ethPrice = getEthPrice()
                    val gasPrice = getGasPrice()

                    val gasUsageMin = 48481.0
                    val gasUsageMax = 61000.0

                    val minCostUsd = calculateTransactionCost(ethPrice, gasPrice, gasUsageMin)
                    val maxCostUsd = calculateTransactionCost(ethPrice, gasPrice, gasUsageMax)

                    withContext(Dispatchers.Main) {
                        val formattedMinCostUsd = String.format("%.2f", minCostUsd)
                        val formattedMaxCostUsd = String.format("%.2f", maxCostUsd)

                        val transferInfoText = " $formattedMinCostUsd a $formattedMaxCostUsd USD"
                        binding.priceTranfer.setText(transferInfoText)

                        delay(10000)
                        binding.priceTranfer.setText("")
                    }
                } catch (e: Exception) {
                    // Catch any exceptions that occurred during the network request or processing
                    withContext(Dispatchers.Main) {
                        // Show an error message to the user
                        Snackbar.make(binding.root, "Failed to fetch data. Please check your internet connection and try again.", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(this@MainActivity, R.color.purple_500))
                            .show()
                    }
                }
            }
        }


        ///tincho y sus problemas
        val costOfServiceEditText = binding.costOfServiceEditText

        if (intent.hasExtra("Number")) {
            val number = intent.getStringExtra("Number")
            Log.d("MainActivity", "Received number: $number")
            costOfServiceEditText.setText(number)
        }

        //Functions loaded at the start of the app
        setCheckedChangeListener()
        setCheckedDollars()
        waitTwoSecond()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val swipeThreshold = 100
        private val swipeVelocityThreshold = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY) &&
                abs(diffX) > swipeThreshold &&
                abs(velocityX) > swipeVelocityThreshold
            ) {
                if (diffX < 0) {
                    // Swipe right, launch target activity
                    val intent = Intent(this@MainActivity, Calculator::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                } else {
                    // Swipe left, launch another activity
                    val intent = Intent(this@MainActivity, Calculator::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                }
            }
            return true
        }
    }

    private fun calculateDollarExchange() {
        hideSoftKeyboard()
        val amountToExchange = binding.costOfServiceEditText.text.toString()
        val dolarPrice = binding.dolarPrice.text.toString()
        val a = amountToExchange.toDoubleOrNull()
        val d = dolarPrice.toDoubleOrNull()
        val formatCurrency = NumberFormat.getCurrencyInstance()
        when {
            a == null && d == null -> {
                binding.totalAmount.text = getString(R.string.error_total)
                Toast.makeText(
                    binding.root.context,
                    getString(R.string.error_total),
                    Toast.LENGTH_SHORT
                ).show()
            }
            a == null && d != null -> {
                binding.totalAmount.text = getString(R.string.error_monto)
                Toast.makeText(
                    binding.root.context,
                    getString(R.string.error_monto),
                    Toast.LENGTH_SHORT
                ).show()
            }
            a != null && d == null -> {
                binding.totalAmount.text = getString(R.string.error_dolar)
                Toast.makeText(
                    binding.root.context,
                    getString(R.string.error_dolar),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                scope.launch {
                    val result = calculateExchangeAmountAsync(a, d, binding.switchID.isChecked)

                    if (binding.switchID.isChecked) {
                        binding.totalAmount.text = getString(R.string.dolars_amount, formatCurrency.format(result))
                    } else {
                        binding.totalAmount.text = getString(R.string.pesos_amount, formatCurrency.format(result))
                    }
                }
            }
        }
    }

    private suspend fun calculateExchangeAmountAsync(amount: Double?, dolarPrice: Double?, isChecked: Boolean): Double {
        return withContext(Dispatchers.IO) {
            if (isChecked && amount != null && dolarPrice != null) {
                amount / dolarPrice
            } else if (amount != null && dolarPrice != null) {
                amount * dolarPrice
            } else {
                0.0
            }
        }
    }

    private fun getDollarToArg() {
        val textView = findViewById<TextView>(R.id.dolarPrice)
        val queue = Volley.newRequestQueue(this)
        val url = "https://app.ripio.com/api/v3/public/rates/"
        val stringRequest = StringRequest(url,
            { response ->
                textView.text = usdcPriceArg(response)
            }, {
                Snackbar.make(
                    binding.root,
                    getString(R.string.internet_error),
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(
                    ContextCompat.getColor(this, R.color.purple_500)
                ).show()
            })
        queue.add(stringRequest)
    }

    private fun getDolarHoy() {
        val dolarVenta = findViewById<TextView>(R.id.dolar_venta)
        val dolarCompra = findViewById<TextView>(R.id.dolar_compra)
        val queue = Volley.newRequestQueue(this)
        val url = "https://dolarhoy.com/cotizaciondolarblue"
        val stringRequest = StringRequest(url,
            { response ->
                // number of the array can only be 1 or 2, one is for buy price and 2 is for sell price
                dolarCompra.text = dolarBluePrice(response,1)
                dolarVenta.text = dolarBluePrice(response,2)
                Handler(Looper.getMainLooper()).postDelayed({
                    dolarCompra.text = ""
                    dolarVenta.text = ""
                }, 10000) // 10000ms delay to clear the text
            }, {
                Snackbar.make(
                    binding.root,
                    getString(R.string.internet_error),
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(
                    ContextCompat.getColor(this, R.color.purple_500)
                ).show()
            })
        queue.add(stringRequest)

    }


    private fun getDollarBlue() {
        val textView = findViewById<TextView>(R.id.dolarPrice)
        val queue = Volley.newRequestQueue(this)
        val url = "https://dolarhoy.com/cotizaciondolarblue"
        val stringRequest = StringRequest(url,
            { response ->
                // number of the array can only be 1 or 2, one is for buy price and 2 is for sell price
                textView.text = dolarBluePrice(response,1)
            }, {
                Snackbar.make(
                    binding.root,
                    getString(R.string.internet_error),
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(
                    ContextCompat.getColor(this, R.color.purple_500)
                ).show()
            })
        queue.add(stringRequest)
    }

/*    private fun getGweiToUsd() {
        val textView = findViewById<TextView>(R.id.gwei_text)
        val queue = Volley.newRequestQueue(this)
        val url = "https://etherscan.io/gastracker"
        val stringRequest = StringRequest(url,
            { response ->
                textView.text = gasEstimate(response)
            }, {
                Snackbar.make(
                    binding.root,
                    getString(R.string.internet_error),
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(
                    ContextCompat.getColor(this, R.color.purple_700)
                ).show()
            })
        queue.add(stringRequest)
    }*/

    private fun usdcPriceArg(string: String): String {
        val array = string.split("USDC_ARS")
        return array[1].split("\",\"")[2].replace("sell_rate\":\"", "")
    }

    private fun dolarBluePrice(string: String, num: Int): String {
        val array = string.split("<div class=\"value\">\$")
/*        //tag to print the value to the console
        Log.d("TAG", array[1].split("</div>")[0])
        Log.d("TAG", array[2].split("</div>")[0])*/
        return array[num].split("</div>")[0]
    }

/*
    private fun gasEstimate(string: String): String {
        val array = string.split("spanHighPriorityAndBase")
        val gweiAmount = array[0].split("spanHighPrice\">")[1].split("</span>")[0]
        val gweiToUsd =
            array[1].split("spanHighPriorityAndBase")[0].split("<div class=\"text-muted\">")[1].split(
                "|"
            )[0].replace("\nn", "").replace("$", "").toDouble()
        val formatDecimal = String.format("%.2f", gweiToUsd)

        return "${gweiAmount.replace("\n", "")} Gwei = $formatDecimal USD"
    }*/

    private fun setCheckedChangeListener() {
        binding.switchID.setOnCheckedChangeListener { _, isChecked ->
            val msg = getString(if (isChecked) R.string.on else R.string.off)
            val exchangeButton =
                getString(if (isChecked) R.string.calculate_dollars else R.string.calculate_pesos)
            val exchangeText = getString(if (isChecked) R.string.dollar_to_exchange else R.string.pesos_to_exchange)
            //Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            binding.switchID.text = msg
            binding.calculateButton.text = exchangeButton
            binding.amountToPay.hint = exchangeText
        }
    }


    private fun setCheckedDollars() {
        binding.dollarSwitch.setOnCheckedChangeListener { _, isChecked ->
            val dollarKind = getString(if (isChecked) R.string.blue_dolar else R.string.ripio_dolar)
            binding.dollarSwitch.text = dollarKind
            if (isChecked) {
                scope.launch {
                    getDollarBlue()
                    binding.totalAmount.text = ""
                }
            } else {
                scope.launch {
                    getDollarToArg()
                    binding.totalAmount.text = ""
                }
            }
        }
    }

    private fun waitTwoSecond() {
        scope.launch {
            delay(250)
            getDollarToArg()
        }
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager: InputMethodManager = this.getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            this.currentFocus?.windowToken ?: return, 0
        )
    }

    private suspend fun getEthPrice(): Double = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd")
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            val gson = Gson()
            val result = gson.fromJson(body, Map::class.java)
            val ethereum = result["ethereum"]
            if (ethereum is Map<*, *>) {
                val usdPrice = ethereum["usd"]
                if (usdPrice is Double) {
                    return@withContext usdPrice
                }
            }
            error("ETH price not found")
        }
    }



    // Modified to be a suspend function for coroutine use
    private suspend fun getGasPrice(): Double = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val infuraUrl = "https://mainnet.infura.io/v3/84028e506fc8417797e1313d2185c93a"
        val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
        val bodyContent = """
    {
        "jsonrpc":"2.0",
        "method":"eth_gasPrice",
        "params":[],
        "id":1
    }
    """.trimIndent()
        val body = bodyContent.toRequestBody(mediaTypeJson)
        val request = Request.Builder()
            .url(infuraUrl)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: throw IllegalStateException("Response body is null")
            val jsonObject = JsonParser.parseString(responseBody).asJsonObject
            val result = jsonObject.get("result").asString
            // Correctly handle the hexadecimal string
            val gasPriceWei = result.removePrefix("0x").toBigInteger(16).toDouble()
            // Convert Wei to Gwei
            gasPriceWei / 1_000_000_000
        }
    }

    private fun calculateTransactionCost(ethPrice: Double, gasPriceGwei: Double, gasUsed: Double): Double {
        // Convert Gwei to Ether for the gas price
        val gasPriceEther = gasPriceGwei / 1_000_000_000
        // Calculate the cost in Ether
        val costInEther = gasUsed * gasPriceEther
        // Convert the cost to USD
        return costInEther * ethPrice
    }



// Example of using these functions asynchronously in an Activity

}
