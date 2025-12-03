package ipca.example.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class CalculatorBrain {

    enum class Operation(op: String) {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("×"),
        DIVIDE("÷"),
        EQUAL("="),
        SQRT("√"),
        PERCENTAGE("%");

        companion object {
            fun parseOperation(op: String): Operation {
                return when (op) {
                    "+" -> ADD
                    "-" -> SUBTRACT
                    "×" -> MULTIPLY
                    "÷" -> DIVIDE
                    "=" -> EQUAL
                    "√" -> SQRT
                    "%" -> PERCENTAGE
                    else -> EQUAL
                }
            }
        }
    }


    var  operand = 0.0
    var  operation : Operation? = null

    fun doOperation(newOperand: Double, newOperation: Operation) {
        if (operation != null) {
            operand = when (operation) {
                Operation.ADD -> operand + newOperand
                Operation.SUBTRACT -> operand - newOperand
                Operation.MULTIPLY -> operand * newOperand
                Operation.DIVIDE ->  operand / newOperand
                Operation.SQRT -> kotlin.math.sqrt(newOperand)
                Operation.PERCENTAGE -> operand * (newOperand / 100)


                else -> operand
            }
        } else {
            operand = newOperand
        }
        operation = if (newOperation == Operation.EQUAL) null else newOperation
    }
}