package ipca.example.convert

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ipca.example.convert.ui.theme.ConvertTheme
import ipca.example.convert.ui.theme.Orange
import ipca.example.convert.ui.theme.Purple40


@Composable
fun ConvertButton(
    modifier: Modifier = Modifier,
    label : String,
    isOperation : Boolean = false,
    onNumPressed : (String) -> Unit
){
    Button(
        modifier = modifier

            .size(90.dp)
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor =  Purple40
        ),
        onClick = {
            onNumPressed(label)
        }
    ) {
        Text(
            label,
            style = MaterialTheme.typography.displayMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorButtonPreview(){
    ConvertTheme {
        ConvertButton(
            modifier = Modifier,
            label = "7",
            onNumPressed = {}
        )
    }
}