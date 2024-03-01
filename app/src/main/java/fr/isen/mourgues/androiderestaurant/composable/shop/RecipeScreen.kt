package fr.isen.mourgues.androiderestaurant.composable.shop

import MenuSubItem
import PreferencesManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.mourgues.androiderestaurant.PanierManager
import fr.isen.mourgues.androiderestaurant.Plat
import fr.isen.mourgues.androiderestaurant.composable.NavigationBar.navigationData
import fr.isen.mourgues.androiderestaurant.composable.dialog.AlertDialog
import fr.isen.mourgues.androiderestaurant.composable.image.ImageHandler

@Composable
fun RecipeDetailScreen(recipe: MenuSubItem, navController: NavController) {
    var quantity by remember { mutableIntStateOf(1) }
    val openAlertDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val panierManager = PanierManager(context)

    val price = try {
        recipe.prices[0].price.toDouble()
    } catch (e: Exception) {
        0.0
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ImageHandler(images = recipe.images, imageSize = 400)
            Text(
                text = recipe.name_fr,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
            Text(
                text = "Ingrédients :",
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = recipe.ingredients.joinToString(", ") { it.name_fr },
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center

                ) {
                    ElevatedButton(onClick = {
                        if (quantity > 1) {
                            quantity--
                        }
                    }) {
                        Text("-")
                    }
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                    ElevatedButton(onClick = { quantity++ }) {
                        Text("+")
                    }
                }
                ElevatedButton(
                    onClick = {
                        openAlertDialog.value = true
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Ajouter au panier - " + (quantity * price) + "€",
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
                quantity = 1
            },
            onConfirmation = {
                val plat = Plat(
                    recipe.name_fr,
                    price,
                    quantity,
                    recipe.images[0]
                )
                panierManager.ajouterAuPanier(plat)
                openAlertDialog.value = false
                preferencesManager.saveData("panierQuantity", (preferencesManager.getData("panierQuantity", "0").toInt() + quantity).toString())
                navigationData.updateItemNumber(context)
                quantity = 1
                navController.navigate("shopCart")

            },
            dialogTitle = "Ajout au panier",
            dialogText = "Voulez-vous ajouter ${recipe.name_fr} x $quantity au panier ?",
            icon = Icons.Filled.ShoppingCart,
        )
    }
}