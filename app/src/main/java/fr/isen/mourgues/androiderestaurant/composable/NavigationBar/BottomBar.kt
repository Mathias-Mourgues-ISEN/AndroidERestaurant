package fr.isen.mourgues.androiderestaurant.composable.NavigationBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomNavigationBar(navController: NavController) {
    var selectedItem = navigationData.selectedItem.intValue
    val panierQty = navigationData.itemNumber.intValue
    NavigationBar {
        NavigationBarItem(
            icon = {
                if (selectedItem == 0) {
                    Icon(Icons.Filled.Home, contentDescription = "home")
                } else {
                    Icon(Icons.Outlined.Home, contentDescription = "home")
                }
            },
            label = { Text("Home") },
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                navController.navigate("main")
            }
        )
        NavigationBarItem(
            icon = {
                BadgedBox(badge = { Badge {
                   Text(text = panierQty.toString())
                } } ) {
                    if (selectedItem == 1) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "home")
                    } else {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = "home")
                    }
                }
            },
            label = { Text("Cart") },
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                navController.navigate("shopCart")
            }
        )

    }
}