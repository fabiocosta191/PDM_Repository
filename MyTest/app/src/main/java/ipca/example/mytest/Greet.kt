package ipca.example.mytest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import ipca.example.mytest.ui.theme.MyTestTheme

@Composable
fun Greet(
    modifier: Modifier = Modifier
){
    var displayText1 by remember { mutableStateOf("") }
    var displayText by remember { mutableStateOf("0") }
    var num1 by remember { mutableStateOf(0f) }
    var num2: Float? by remember { mutableStateOf(null) }
    var simbl by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = displayText1,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = displayText,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.displayLarge
        )
        Row {
            Button(
                modifier= Modifier.padding(8.dp),
                onClick = {
                    when (displayText) {
                        "0" -> displayText = "7"
                        "Erro" ->{ displayText = "7"
                            displayText = ""
                        }
                        else -> displayText += "7"
                    }
                }
            ) {
                Text(
                    "7",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                modifier= Modifier.padding(8.dp),
                onClick = {
                    when (displayText) {
                        "0" -> displayText = "8"
                        "Erro" ->{ displayText = "8"
                            displayText = ""
                        }
                        else -> displayText += "8"
                    }

                }
            ) {
                Text(
                    "8",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                modifier= Modifier.padding(8.dp),
                onClick = {
                    when (displayText) {
                        "0" -> displayText = "9"
                        "Erro" ->{ displayText = "9"
                            displayText = ""
                        }
                        else -> displayText += "9"
                    }
                }
            ) {
                Text(
                    "9",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                modifier= Modifier.padding(8.dp),
                onClick = {
                    num1 = displayText.toFloat()
                    simbl = "+"
                    displayText1=displayText +"+"
                    displayText = ""
                }

            ) {
                Text(
                    "+",
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
        Row {
            Button(
                modifier= Modifier.padding(8.dp),
                onClick = {
                    when (displayText) {
                        "0",
                        "Erro" ->{ displayText = "4"
                            displayText = ""
                        }
                        else -> displayText += "4"
                    }
                }
            ) {
                Text(
                    "4",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {
                    when (displayText) {
                        "0" -> displayText = "5"
                        "Erro" ->{ displayText = "5"
                        }
                        else -> displayText += "5"
                    }
                }
            ) {
                Text(
                    "5",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {
                    when (displayText) {
                        "0" -> displayText = "6"
                        "Erro" ->{ displayText = "6"
                            displayText = ""
                        }
                        else -> displayText += "6"
                    }
                }
            ) {
                Text(
                    "6",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {
                    when (displayText) {
                        "0",
                        "Erro" -> displayText = "-"
                        else -> {
                                num1 = displayText.toFloat()
                                simbl = "-"
                            displayText1=displayText +"-"
                            displayText = ""
                        }
                    }


                }
            ) {
                Text(
                    "-",
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
        Row {
            Button(
                onClick = {
                    when (displayText) {
                        "0" -> displayText = "1"
                        "Erro" ->{ displayText = "1"
                                displayText = ""
                                }
                        else -> displayText += "1"
                    }
                }
            ) {
                Text(
                    "1",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {
                    when (displayText) {
                        "0" -> displayText = "2"
                        "Erro" ->{ displayText = "2"
                            displayText = ""
                        }
                        else -> displayText += "2"
                    }
                }
            ) {
                Text(
                    "2",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {
                    when (displayText) {
                        "0" -> displayText = "3"
                        "Erro" ->{ displayText = "3"
                            displayText = ""
                        }
                        else -> displayText += "3"
                    }
                }
            ) {
                Text(
                    "3",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {

                    num1 = displayText.toFloat()
                    simbl = "*"
                    displayText1=displayText +"*"
                    displayText = ""
                }
            ) {
                Text(
                    "*",
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
        Row {

            Button(
                onClick = {
                    displayText += "0"
                }
            ) {
                Text(
                    "0",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {
                    displayText+='.'
                }
            ) {
                Text(
                    ".",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {
                    num2 = displayText.toFloat()
                    displayText1+=displayText +"="
                    displayText = when (simbl) {
                        "+" -> (num1 + (num2 ?: 0f)).toString()
                        "-" -> (num1 - (num2 ?: 0f)).toString()
                        "*" -> (num1 * (num2 ?: 0f)).toString()
                        "/" -> if (num2 != null && num2 != 0f) {
                            (num1 / num2!!).toString()
                            } else {
                                "Erro"
                            }
                        else -> displayText
                    }
                    if (displayText == "Erro") {
                        num1 = 0f
                    } else {
                        num1 = displayText.toFloatOrNull() ?: 0f
                    }
                    num2= null
                    simbl = "f"
                }
            ) {
                Text(
                    "=",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            Button(
                onClick = {
                    num1 = displayText.toFloat()
                    simbl = "/"
                    displayText1=displayText +"/"

                    displayText = ""
                }
            ) {
                Text(
                    "/",
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }
        Button(
            onClick = {
                displayText = "0"
                displayText1 = ""
            }
        ) {
            Text(
                "C",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorViewPreview(){
    MyTestTheme {
        Greet()
    }
}