package fr.isen.mourgues.androiderestaurant.composable.panier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fr.isen.mourgues.androiderestaurant.PanierManager
import fr.isen.mourgues.androiderestaurant.Plat
import fr.isen.mourgues.androiderestaurant.composable.NavigationBar.navigationData
import fr.isen.mourgues.androiderestaurant.composable.dialog.AlertDialog

@Composable
fun CartItemCard(plat: Plat, navController: NavController) {

    val openAlertDialog = remember { mutableStateOf(false) }
    val panierManager = PanierManager(LocalContext.current)

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${plat.quantite}x ${plat.nom}",
                modifier = Modifier
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
        Text(
            text = "${plat.prix * plat.quantite}€",
            modifier = Modifier
                .padding(10.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = {
                openAlertDialog.value = true
            },
            modifier = Modifier
                .padding(10.dp)
        ) {
            Text("Supprimer")
        }
    }

    if (openAlertDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openAlertDialog.value = false
            },
            onConfirmation = {
                panierManager.retirerDuPanier(plat)
                openAlertDialog.value = false
                navigationData.itemNumber.intValue = panierManager.getPanier().size
                navController.navigate("shopCart")
            },
            dialogTitle = "Confirmation",
            dialogText = "Voulez-vous vraiment supprimer ${plat.nom} du panier ?",
            icon = Icons.Filled.ShoppingCart,
            navController = rememberNavController()
        )
    }
}

@Composable
fun ShoppingCartScreen(navController: NavController) {
    val openAlertDialog = remember { mutableStateOf(false) }
    val panierManager = PanierManager(LocalContext.current)
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        val panier = panierManager.getPanier()
        if (panier.isEmpty()) {
            item {
                Text(
                    text = "Le panier est vide",
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                )
            }
        } else {
            panier.forEach { plat ->
                item {
                    CartItemCard(plat, navController)
                }
            }
            item {
                Button(
                    onClick = {
                        openAlertDialog.value = true
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Valider le panier" + " : " + panier.sumOf { it.prix * it.quantite } + "€",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }

    if (openAlertDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openAlertDialog.value = false
            },
            onConfirmation = {
                panierManager.videPanier()
                openAlertDialog.value = false
                navigationData.itemNumber.intValue = panierManager.getPanier().size
                navController.navigate("main")
            },
            dialogTitle = "Confirmation",
            dialogText = "Voulez-vous confirmer votre commande",
            icon = Icons.Filled.ShoppingCart,
            navController = rememberNavController()
        )
    }
}
