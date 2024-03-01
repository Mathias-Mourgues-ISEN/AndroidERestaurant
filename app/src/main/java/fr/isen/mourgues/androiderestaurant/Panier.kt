package fr.isen.mourgues.androiderestaurant

import android.content.Context
import com.google.gson.Gson

data class Plat(
    val nom: String,
    val prix: Double,
    val quantite: Int,
    val image : String
)
class PanierManager (val context: Context) {
    private val gson = Gson()
    private val fileName = "panier.json"

    companion object {
        private var panier = mutableListOf<Plat>()
        private var number = 0
    }

    fun ajouterAuPanier(plat: Plat) {
        panier.add(plat)
        sauvegarderPanier()
    }

    fun retirerDuPanier(plat: Plat) {
        panier.remove(plat)
        sauvegarderPanier()
    }

    fun getPanier(): List<Plat> {
        panier = try {
            val json = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val list = gson.fromJson(json, Array<Plat>::class.java).toMutableList()
            list
        } catch (e: Exception) {
            mutableListOf()
        }
        return panier
    }

    fun getNumber(): Int {
        try {
            val json = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            val list = gson.fromJson(json, Array<Plat>::class.java).toMutableList()
            panier = list
            number = panier.size
        } catch (e: Exception) {
            number = 0
        }
        return number
    }

    fun videPanier() {
        panier.clear()
        sauvegarderPanier()
    }

    private fun sauvegarderPanier() {
        val json = gson.toJson(panier)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }
}
