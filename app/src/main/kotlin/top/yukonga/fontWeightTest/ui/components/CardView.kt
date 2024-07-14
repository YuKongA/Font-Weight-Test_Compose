package top.yukonga.fontWeightTest.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardView(view: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            view()
        }
    }
}