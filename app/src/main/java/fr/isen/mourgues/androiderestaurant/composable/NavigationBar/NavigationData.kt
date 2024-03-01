package fr.isen.mourgues.androiderestaurant.composable.NavigationBar

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import fr.isen.mourgues.androiderestaurant.PanierManager

object navigationData {
    var selectedItem = mutableIntStateOf(0)
    var itemNumber = mutableIntStateOf(0)

    fun updateSelectedItem(newValue: Int) {
        selectedItem.intValue = newValue
    }

    fun updateItemNumber(context: Context) {
        val panierManager = PanierManager(context)
        itemNumber.intValue = panierManager.getNumber()
    }
}