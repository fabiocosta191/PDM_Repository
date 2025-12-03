package ipca.example.userlistapp.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ipca.example.userlistapp.models.User
import ipca.example.userlistapp.ui.theme.UserListAppTheme

@Composable
fun UserViewCell(
    modifier: Modifier = Modifier,
    user: User
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem do Utilizador
            AsyncImage(
                model = user.image,
                contentDescription = "Avatar de ${user.firstName}",
                modifier = Modifier
                    .size(50.dp) // Tamanho da imagem
                    .clip(CircleShape), // Formato redondo
                contentScale = ContentScale.Crop
            )

            // Espaço entre a imagem e o nome
            Spacer(modifier = Modifier.width(16.dp))

            // Nome do Utilizador
            val name = "${user.firstName ?: ""} ${user.lastName ?: ""}"
            Text(text = name.ifBlank { "Sem nome" })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserViewCellPreview() {
    val mockUser = User(
        id = 1,
        firstName = "João",
        lastName = "Silva",
        email = "joao@email.com",
        phone = null,
        image = "https://dummyjson.com/icon/emilys/128", // Exemplo de imagem
        address = null,
        maidenName = null,
        age = null,
        gender = null,
        username = null,
        birthDate = null,
        bloodGroup = null,
        height = null,
        weight = null,
        eyeColor = null,
        hair = null,
        ip = null,
        macAddress = null,
        university = null,
        bank = null,
        company = null,
        ein = null,
        ssn = null,
        userAgent = null,
        crypto = null,
        role = null
    )

    UserListAppTheme {
        UserViewCell(user = mockUser)
    }
}