package com.example.real_calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.real_calculator.R
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var expressionTextView: TextView
    private lateinit var resultTextView: TextView

    private var currentInput = ""
    private var isLastButtonClickOperator = false
    private var isInputEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expressionTextView = findViewById(R.id.expressionTextView)
        resultTextView = findViewById(R.id.resultTextView)

        val numberButtons = listOf<Button>(
            findViewById(R.id.button0),
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8),
            findViewById(R.id.button9)
        )

        val operatorButtons = listOf<Button>(
            findViewById(R.id.buttonDivision),
            findViewById(R.id.buttonMultiplication),
            findViewById(R.id.buttonSubtraction),
            findViewById(R.id.buttonAddition),
            findViewById(R.id.buttonSqrt)
        )

        val decimalButton = findViewById<Button>(R.id.buttonDecimal)
        val equalsButton = findViewById<Button>(R.id.buttonEquals)
        val clearButton = findViewById<Button>(R.id.buttonClear)

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                onNumberClick(index.toString())
            }
        }

        operatorButtons.forEach { button ->
            button.setOnClickListener {
                onOperatorClick(button.text.toString())
            }
        }

        decimalButton.setOnClickListener { onDecimalClick() }

        equalsButton.setOnClickListener { onEqualsClick() }

        clearButton.setOnClickListener { onClearClick() }
    }

    private fun onNumberClick(number: String) {
        if(isInputEnabled){
            currentInput += number
            isLastButtonClickOperator = false
            updateExpression()
        }
    }

    private fun onOperatorClick(operator: String) {

        if(isInputEnabled){
            if (!isLastButtonClickOperator || operator == "√") {
                currentInput += operator
                isLastButtonClickOperator = true
                updateExpression()
            } else {
                // Replace the previous operator with the new one
                currentInput = currentInput.dropLast(1) + operator
                updateExpression()
            }
        }
    }

    private fun onDecimalClick() {
        if (!isLastButtonClickOperator&&isInputEnabled) {
            val lastOperand = currentInput.split(Regex("[-+*/ ]")).lastOrNull()
            if (lastOperand == null || !lastOperand.contains(".")) {
                currentInput += "."
                isLastButtonClickOperator = false
                updateExpression()
            }
        }
    }

    private fun onEqualsClick() {
        isInputEnabled = false
        if (currentInput.isNotEmpty()) {
            try {
                if (currentInput.endsWith("√")) {
                    throw Exception("Invalid use of square root!")
                }

                // If the expression ends with an operator, remove it and calculate the result
                if (currentInput.endsWith("+") || currentInput.endsWith("-") ||
                    currentInput.endsWith("*") || currentInput.endsWith("/")) {
                    currentInput = currentInput.dropLast(1)
                }

                val result = evaluateExpression(currentInput)
                currentInput = result.toString()
                resultTextView.text = currentInput
            } catch (e: ArithmeticException) {
                currentInput = "Error: Division by zero"
                resultTextView.text = currentInput
            } catch (e: Exception) {
                currentInput = "Error: ${e.message}"
                resultTextView.text = currentInput
            }
        }
        isLastButtonClickOperator = false
        updateExpression()
    }


    private fun onClearClick() {
        currentInput = ""
        resultTextView.text = ""
        isInputEnabled = true
        updateExpression()
    }

    private fun evaluateExpression(expression: String): Double {
        try {
            val modifiedExpression = expression.replace("√", "sqrt")
            return ExpressionBuilder(modifiedExpression).build().evaluate()
        } catch (e: Exception) {
            throw ArithmeticException("Error evaluating expression: ${e.message}")
        }
    }

    private fun updateExpression() {
        expressionTextView.text = currentInput
    }

}
