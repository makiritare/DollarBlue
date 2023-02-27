package com.example.dolarblue

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


class Calculator : AppCompatActivity() {
    private lateinit var expression: TextView
    private lateinit var result: TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        expression= findViewById(R.id.solutionTv)
        result= findViewById(R.id.resultTv)
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

        expression.movementMethod = ScrollingMovementMethod()
        expression.isActivated = true
        expression.isPressed = true

        var str:String

        val switchButtonBackToMain = findViewById<Button>(R.id.switch_button_to_main)
        switchButtonBackToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        clear.setOnClickListener{
            expressionText("0")
            expression.textSize = 26F
            result.textSize = 16F
            resultText()
        }
        openbracket.setOnClickListener{
            if (expression.text.toString().isNotEmpty()){
                val lastIndex = expression.text.toString().lastIndex
                str = expression.text.toString().substring(0,lastIndex)
                expressionText(str)
                resultText()
            }

        }
        closebracket.setOnClickListener{
            if(expression.text.toString().endsWith("%")||expression.text.toString().endsWith("/")||expression.text.toString().endsWith("*")||expression.text.toString().endsWith("+")||expression.text.toString().endsWith("-")||expression.text.toString().endsWith(".")){
                str = expression.text.toString()
                expressionText(str)
            }else{
                str = expression.text.toString() + "%"
                expressionText(str)
            }
        }
        divide.setOnClickListener{
            if(expression.text.toString().endsWith("%")||expression.text.toString().endsWith("/")||expression.text.toString().endsWith("*")||expression.text.toString().endsWith("+")||expression.text.toString().endsWith("-")||expression.text.toString().endsWith(".")){
                str = expression.text.toString()
                expressionText(str)
            }else{
                str = expression.text.toString() + "/"
                expressionText(str)
            }
        }
        dot.setOnClickListener{
            if(expression.text.toString().endsWith("%")||expression.text.toString().endsWith("/")||expression.text.toString().endsWith("*")||expression.text.toString().endsWith("+")||expression.text.toString().endsWith("-")||expression.text.toString().endsWith(".")){
                str = expression.text.toString()
                expressionText(str)
            }else{
                str = expression.text.toString() + "."
                expressionText(str)
            }
        }
        multiply.setOnClickListener{
            if(expression.text.toString().endsWith("%")||expression.text.toString().endsWith("/")||expression.text.toString().endsWith("*")||expression.text.toString().endsWith("+")||expression.text.toString().endsWith("-")||expression.text.toString().endsWith(".")){
                str = expression.text.toString()
                expressionText(str)
            }else{
                str = expression.text.toString() + "*"
                expressionText(str)
            }
        }
        add.setOnClickListener{
            if(expression.text.toString().endsWith("%")||expression.text.toString().endsWith("/")||expression.text.toString().endsWith("*")||expression.text.toString().endsWith("+")||expression.text.toString().endsWith("-")||expression.text.toString().endsWith(".")){
                str = expression.text.toString()
                expressionText(str)
            }else{
                str = expression.text.toString() + "+"
                expressionText(str)
            }
        }
        substract.setOnClickListener{
            if(expression.text.toString().endsWith("%")||expression.text.toString().endsWith("/")||expression.text.toString().endsWith("*")||expression.text.toString().endsWith("+")||expression.text.toString().endsWith("-")||expression.text.toString().endsWith(".")){
                str = expression.text.toString()
                expressionText(str)
            }else{
                str = expression.text.toString() + "-"
                expressionText(str)
            }
        }
        equal.setOnClickListener{
            expression.textSize = 16F
            result.textSize = 26F
        }
        zero.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"0"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "0"
                expressionText(str)
                resultText()
            }
        }
        one.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"1"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "1"
                expressionText(str)
                resultText()
            }
        }
        two.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"2"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "2"
                expressionText(str)
                resultText()
            }
        }
        three.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"3"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "3"
                expressionText(str)
                resultText()
            }
        }
        four.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"4"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "4"
                expressionText(str)
                resultText()
            }
        }
        five.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"5"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "5"
                expressionText(str)
                resultText()
            }
        }
        six.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"6"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "6"
                expressionText(str)
                resultText()
            }
        }
        seven.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"7"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "7"
                expressionText(str)
                resultText()
            }
        }
        eight.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"8"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "8"
                expressionText(str)
                resultText()
            }
        }
        nine.setOnClickListener{
            if(expression.text.toString().startsWith("0")){
                str = expression.text.toString().replace("0","") +"9"
                expressionText(str)
                resultText()
            }else{
                str = expression.text.toString() + "9"
                expressionText(str)
                resultText()
            }
        }

    }

    private fun expressionText(str:String){
        expression.text = str
    }

    private fun resultText(){
        val exp = expression.text.toString()
        val engine:ScriptEngine = ScriptEngineManager().getEngineByName("rhino")
        try {
            val res = engine.eval(exp)
            if(res.toString().endsWith(".0")){
                result.text = "= " + res.toString().replace(".0","")
            }else{
                result.text = "=$res"
            }
        }catch (e:Exception){
            expression.text = expression.text.toString()
            result.text = expression.text.toString()
        }
    }
}