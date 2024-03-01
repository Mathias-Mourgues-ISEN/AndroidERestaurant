package fr.isen.mourgues.androiderestaurant.composable.shop

import MenuItem
import MenuSubItem
import PreferencesManager
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.GsonBuilder
import fr.isen.mourgues.androiderestaurant.composable.image.ImageHandler

@Composable
fun ShopScreen(navController: NavController, shopNumber: Int) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
    ) {
        for (i in 1..shopNumber) {
            item {
                Button(
                    onClick = {
                        navController.navigate("menu/$i")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Shop $i")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDisplayCard(item: MenuSubItem, navController: NavController) {
    val preferencesManager = PreferencesManager(LocalContext.current)
    OutlinedCard(
        onClick = {
            //use gson to convert object to string
            val gson = GsonBuilder().create()
            val itemString = gson.toJson(item)
            preferencesManager.saveData("item", itemString)
            //log
            Log.d("item", itemString)
            navController.navigate("recipe/${item.id}")
        },
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.fillMaxWidth(0.95F),
    ) {
        Row {
            ImageHandler(images = item.images, imageSize = 150)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 1.dp)
            ) {
                Text(
                    text = item.name_fr,
                    modifier = Modifier
                        .padding(1.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                Text(
                    text = item.prices[0].price + "€",
                    modifier = Modifier
                        .padding(16.dp),
                    textAlign = TextAlign.Right,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun CategoryScreen(categoryName: String?, navController: NavController) {
    val preferencesManager = PreferencesManager(LocalContext.current)
    val gson = GsonBuilder().create()
    val menuItemString = preferencesManager.getData(categoryName ?: "", "")
    val menuItem = gson.fromJson(menuItemString, MenuItem::class.java)
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
    ) {
        menuItem.items.forEach { item ->
            item {
                ItemDisplayCard(item, navController)
            }
        }
    }
}



