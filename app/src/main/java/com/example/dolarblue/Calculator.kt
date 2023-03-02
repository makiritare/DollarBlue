package com.example.dolarblue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
    private lateinit var clear: Button
    private lateinit var openbracket: Button
    private lateinit var closebracket: Button
    private lateinit var divide: Button
    private lateinit var dot: Button
    private lateinit var multiply: Button
    private lateinit var add: Button
    private lateinit var substract: Button
    private lateinit var equal: Button
    private lateinit var zero: Button
    private lateinit var one: Button
    private lateinit var two: Button
    private lateinit var three: Button
    private lateinit var four: Button
    private lateinit var five: Button
    private lateinit var six: Button
    private lateinit var seven: Button
    private lateinit var eight: Button
    private lateinit var nine: Button
    private lateinit var clear1: Button
    private lateinit var gestureDetector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        expression1= findViewById(R.id.solutionTv)
        result1= findViewById(R.id.resultTv)
        clear= findViewById(R.id.btnClear)
        openbracket= findViewById(R.id.btnOpenBracket)
        closebracket= findViewById(R.id.btnCloseBracket)
        divide= findViewById(R.id.btnDividir)
        dot= findViewById(R.id.btnPoint)
        multiply= findViewById(R.id.btnMultiplicar)
        add= findViewById(R.id.btnSuma)
        substract= findViewById(R.id.btnRestar)
        equal= findViewById(R.id.btnIgual)
        zero= findViewById(R.id.btn0)
        one= findViewById(R.id.btn1)
        two= findViewById(R.id.btn2)
        three= findViewById(R.id.btn3)
        four= findViewById(R.id.btn4)
        five= findViewById(R.id.btn5)
        six= findViewById(R.id.btn6)
        seven= findViewById(R.id.btn7)
        eight= findViewById(R.id.btn8)
        nine= findViewById(R.id.btn9)
        clear1 = findViewById(R.id.btnAc)

        gestureDetector = GestureDetector(this, GestureListener())

        expression1.movementMethod = ScrollingMovementMethod()
        expression1.isActivated = true
        expression1.isPressed = true


     /*   val switchButtonBackToMain = findViewById<Button>(R.id.switch_button_to_main)
        switchButtonBackToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }*/
        clear1.setOnClickListener{
            val removedLast = expression1.text.toString().dropLast(1)
            expression1.text = removedLast
        }

        clear.setOnClickListener{
            expression1.text= ""
            expression1.textSize = 32F
            result1.textSize = 12F
        }
        openbracket.setOnClickListener{
            expression1.text = addToInputText("(")
        }
        closebracket.setOnClickListener{
            expression1.text = addToInputText(")")
        }
        divide.setOnClickListener{
            expression1.text = addToInputText("/")
        }

        dot.setOnClickListener{
            expression1.text = addToInputText(".")
        }
        multiply.setOnClickListener{
            expression1.text = addToInputText("*")
        }
        add.setOnClickListener{
            expression1.text = addToInputText("+")
        }
        substract.setOnClickListener{
            expression1.text = addToInputText("-")
        }
        equal.setOnClickListener{
            resultText()
            expression1.textSize = 12F
            result1.textSize = 32F
        }
        zero.setOnClickListener{
            expression1.text = addToInputText("0")

        }

        one.setOnClickListener{
            expression1.text = addToInputText("1")
        }

        two.setOnClickListener{
            expression1.text = addToInputText("2")
        }
        three.setOnClickListener{
            expression1.text = addToInputText("3")
        }
        four.setOnClickListener{
            expression1.text = addToInputText("4")
        }
        five.setOnClickListener{
            expression1.text = addToInputText("5")
        }
        six.setOnClickListener{
            expression1.text = addToInputText("6")
        }
        seven.setOnClickListener{
            expression1.text = addToInputText("7")
        }
        eight.setOnClickListener{
            expression1.text = addToInputText("8")
        }
        nine.setOnClickListener{
            expression1.text = addToInputText("9")
        }

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

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
                abs(diffX) > SWIPE_THRESHOLD &&
                abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
            ) {
                if (diffX < 0) {
                    // Swipe right, launch target activity
                    val intent = Intent(this@Calculator, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Swipe left, launch another activity
                    val intent = Intent(this@Calculator, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            return true
        }
    }

    private fun addToInputText(buttonValue: String): String {
        return expression1.text.toString() + "" + buttonValue
    }

    private fun getInputExpression(): String {
        var expression = expression1.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("×"), "*")
        return expression
    }

    private fun resultText(){

        try {
            val expression = getInputExpression()
            val result = Expression(expression).calculate()
            if (result.isNaN()) {
                // Show Error Message
                result1.text = ""
                result1.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            } else {
                // Show Result
                result1.text = DecimalFormat("0.######").format(result).toString()
                result1.setTextColor(ContextCompat.getColor(this, R.color.purple_500))
            }

        } catch (e:Exception){
            result1.text = ""
            result1.setTextColor(ContextCompat.getColor(this, R.color.primaryLightColor))
        }
    }
}