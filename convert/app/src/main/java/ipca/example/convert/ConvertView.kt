package ipca.example.convert

import android.text.Selection
import ipca.example.convert.ui.theme.ConvertTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.nio.channels.SelectionKey


@Composable
fun ConvertView(
    modifier: Modifier = Modifier
){
    var displayText by remember { mutableStateOf("0") }
    var result by remember { mutableStateOf("0") }


    //val calculatorBrain by remember {  mutableStateOf(CalculatorBrain()) }

    var userIsTypingNumber by remember { mutableStateOf(true) }

    val onDigitPressed : (String) -> Unit = { digit ->
        if (userIsTypingNumber) {
            if (digit == ".") {
                if (!displayText.contains('.')) {
                    displayText += digit
                }
            } else {
                if (displayText == "0") {
                    displayText = digit
                } else {
                    displayText += digit
                }
            }
        }else{
            if (digit == ".") {
                displayText = "0."
            }else {
                displayText = digit
            }
        }
        userIsTypingNumber = true
    }


    val onOperationPressed : (String) -> Unit = { op ->
//        if(op == "AC"){
//            displayText = "0"
//            calculatorBrain.operand = 0.0
//            calculatorBrain.operation = null
//            userIsTypingNumber = false
//        }
//        else if(op == "C"){
//            displayText = "0"
//            userIsTypingNumber = false
//        }
//        else{
//            calculatorBrain.doOperation(
//                displayText.toDouble(),
//                CalculatorBrain.Operation.parseOperation(op)
//            )
//
//            val result = calculatorBrain.operand
//
//            if ((result % 1.0) == 0.0 ) {
//                displayText = result.toInt().toString()
//            }
//            else if(result == Double.POSITIVE_INFINITY || result == Double.NEGATIVE_INFINITY){
//                displayText = "Error"
//            }
//            else{
//                displayText = result.toString()
//            }
//
//            userIsTypingNumber = false}
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row {
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                text = displayText,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.displayLarge
            )

        }
        Text(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            text = result,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.displayLarge
        )
        Row() {
            ConvertButton( label = "7" , onNumPressed =  onDigitPressed )
            ConvertButton( label = "8" , onNumPressed =  onDigitPressed )
            ConvertButton( label = "9" , onNumPressed =  onDigitPressed )

        }
        Row() {
            ConvertButton( label = "6" , onNumPressed =  onDigitPressed )
            ConvertButton( label = "5" , onNumPressed =  onDigitPressed )
            ConvertButton( label = "4" , onNumPressed =  onDigitPressed )

        }
        Row() {
            ConvertButton( label = "1" , onNumPressed =  onDigitPressed )
            ConvertButton( label = "2" , onNumPressed =  onDigitPressed )
            ConvertButton( label = "3" , onNumPressed =  onDigitPressed )

        }
        Row() {
            ConvertButton( label = "0" , onNumPressed =  onDigitPressed )
            ConvertButton( label = "." , onNumPressed =  onDigitPressed )
            ConvertButton( label = "c" , onNumPressed =  onDigitPressed )
        }

        ConvertButton( label = "km -> mi" ,
            onNumPressed =  onOperationPressed,
            isOperation = true)

        ConvertButton( label = "Cº -> Fº" ,
            onNumPressed =  onOperationPressed,
            isOperation = true)

        ConvertButton( label = "L -> Gal" ,
            onNumPressed =  onOperationPressed,
            isOperation = true)



    }


}

@Preview(showBackground = true)
@Composable
fun CalculatorViewPreview(){
    ConvertTheme {
        ConvertView()
    }
}