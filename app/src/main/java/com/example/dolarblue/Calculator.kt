package com.example.dolarblue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat
import kotlin.math.abs

class Calculator : AppCompatActivity() {
    private lateinit var expression1: TextView
    private lateinit var result1: TextView
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        expression1 = findViewById(R.id.solutionTv)
        result1 = findViewById(R.id.resultTv)

        gestureDetector = GestureDetector(this, GestureListener())

        expression1.movementMethod = ScrollingMovementMethod()
        expression1.isActivated = true
        expression1.isPressed = true

        setupButtons()
    }

    private fun setupButtons() {
        val buttonIds = arrayOf(
            R.id.btnClear,
            R.id.btnOpenBracket,
            R.id.btnCloseBracket,
            R.id.btnDividir,
            R.id.btnPoint,
            R.id.btnMultiplicar,
            R.id.btnSuma,
            R.id.btnRestar,
            R.id.btnIgual,
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9,
            R.id.btnAc,
            R.id.btnSend
        )

        val buttonFunctions = arrayOf<(Button) -> Unit>(
            { clearExpression() },
            { appendToExpression("(") },
            { appendToExpression(")") },
            { appendToExpression("/") },
            { appendToExpression(".") },
            { appendToExpression("*") },
            { appendToExpression("+") },
            { appendToExpression("-") },
            { calculateResult() },
            { appendToExpression("0") },
            { appendToExpression("1") },
            { appendToExpression("2") },
            { appendToExpression("3") },
            { appendToExpression("4") },
            { appendToExpression("5") },
            { appendToExpression("6") },
            { appendToExpression("7") },
            { appendToExpression("8") },
            { appendToExpression("9") },
            { removeLastCharacter() },
            { sendResult() }
        )

        for (i in buttonIds.indices) {
            findViewById<Button>(buttonIds[i]).setOnClickListener {
                buttonFunctions[i](it as Button)
            }
        }
    }

    private fun clearExpression() {
        expression1.text = ""
        expression1.textSize = 32F
        result1.textSize = 12F
    }

    private fun appendToExpression(value: String) {
        expression1.text = expression1.text.toString() + value
    }

    private fun removeLastCharacter() {
        val removedLast = expression1.text.toString().dropLast(1)
        expression1.text = removedLast
    }

    private fun calculateResult() {
        try {
            val expression = getInputExpression()
            val result = Expression(expression).calculate()
            if (result.isNaN()) {
                result1.text = ""
                result1.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            } else {
                result1.text = DecimalFormat("0.######").format(result).toString()
                result1.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
                result1.textSize = 32F
                expression1.textSize = 12F

            }

        } catch (e: Exception) {
            result1.text = ""
            result1.setTextColor(ContextCompat.getColor(this, R.color.primaryLightColor))
        }
    }

    private fun sendResult() {
        val expression = getInputExpression()
        val result = Expression(expression).calculate()
        val resultString = DecimalFormat("0.######").format(result).toString()

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Number", resultString)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
        Log.d("MainActivity", "Result sent: $resultString")
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
                    val intent = Intent(this@Calculator, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                } else {
                    // Swipe left, launch another activity
                    val intent = Intent(this@Calculator, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                }
            }

            return true
        }
    }

    private fun getInputExpression(): String {
        var expression = expression1.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("×"), "*")
        return expression
    }
}