package fr.isen.mourgues.androiderestaurant

import APIData
import MenuSubItem
import PreferencesManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.volley.Cache
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.GsonBuilder
import fr.isen.mourgues.androiderestaurant.composable.NavigationBar.AppTopBar
import fr.isen.mourgues.androiderestaurant.composable.NavigationBar.CustomNavigationBar
import fr.isen.mourgues.androiderestaurant.composable.NavigationBar.navigationData
import fr.isen.mourgues.androiderestaurant.composable.panier.ShoppingCartScreen
import fr.isen.mourgues.androiderestaurant.composable.shop.CategoryScreen
import fr.isen.mourgues.androiderestaurant.composable.shop.RecipeDetailScreen
import fr.isen.mourgues.androiderestaurant.composable.shop.ShopScreen
import fr.isen.mourgues.androiderestaurant.ui.theme.AndroidERestaurantTheme
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class MainActivity : ComponentActivity() {
    private val shopNumber = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            val preferencesManager = PreferencesManager(LocalContext.current)
            preferencesManager.saveData("panierQuantity", "0")
            AndroidERestaurantTheme {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppTopBar()
                        val navController = rememberNavController()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "main"
                            ) {
                                composable("main") {
                                    navigationData.updateSelectedItem(0)
                                    ShopScreen(navController, shopNumber = shopNumber)
                                }
                                composable("menu/{shopNumber}") { backStackEntry ->
                                    val shopNumber =
                                        backStackEntry.arguments?.getString("shopNumber")
                                    navigationData.updateSelectedItem(0)
                                    MenuScreen(navController, shopNumber = shopNumber?.toInt() ?: 1)
                                }
                                composable("category/{categoryName}") { backStackEntry ->
                                    val categoryName =
                                        backStackEntry.arguments?.getString("categoryName")
                                    navigationData.updateSelectedItem(0)
                                    CategoryScreen(categoryName, navController)
                                }
                                composable("recipe/{recipeId}") { backStackEntry ->
                                    backStackEntry.arguments?.getString("recipeId")
                                    val preferencesManager = PreferencesManager(LocalContext.current)
                                    val recipe = preferencesManager.getData("item", "")
                                    val gson = GsonBuilder().create()
                                    val recipeItem = gson.fromJson(recipe, MenuSubItem::class.java)
                                    navigationData.updateSelectedItem(0)
                                    RecipeDetailScreen(recipeItem, navController)
                                }
                                composable("shopCart") {
                                    navigationData.updateSelectedItem(1)
                                    ShoppingCartScreen(navController)
                                }
                            }
                        }
                        CustomNavigationBar(navController)
                    }
                }
            }
        }
    }

    @Composable
    fun MenuScreen(navController: NavController, shopNumber: Int = 1) {
        var apiData by remember { mutableStateOf<APIData?>(null) }
        val preferencesManager = PreferencesManager(LocalContext.current)

        LaunchedEffect(key1 = true) {
            apiData = getData(shopNumber)
            Log.d("API", apiData.toString())
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)

        ) {
            apiData?.data?.forEach { menuItem ->
                menuItem.name_fr.let { categoryName ->
                    item {
                        Button(
                            onClick = {
                                navController.navigate("category/$categoryName")
                                val gson = GsonBuilder().create()
                                val menuItemString = gson.toJson(menuItem)
                                preferencesManager.saveData(categoryName, menuItemString)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(categoryName)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getData(shopNumber: Int): APIData {
        val deferred = CompletableDeferred<APIData>()

        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cache
        val network = BasicNetwork(HurlStack())
        val cacheQueue = RequestQueue(cache, network).apply {
            start()
        }

        val jsonBody = JSONObject().apply {
            put("id_shop", "$shopNumber")
        }
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            getString(R.string.api_url),
            jsonBody,
            { response ->
                Log.d("dataog", response.toString(4))
                val data = responseParser(response.toString())
                deferred.complete(data)
            },
            { error: VolleyError ->
                error.printStackTrace()
                deferred.completeExceptionally(error)
            }
        ) {
            override fun getCacheEntry(): Cache.Entry {
                return Cache.Entry()
            }

            override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
                return try {
                    val jsonString = String(response.data, Charset.defaultCharset())
                    val jsonObject = JSONObject(jsonString)
                    Response.success(
                        jsonObject,
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                } catch (e: UnsupportedEncodingException) {
                    Response.error(ParseError(e))
                } catch (je: JSONException) {
                    Response.error(ParseError(je))
                }
            }
        }


        cacheQueue.add(jsonObjectRequest)

        return deferred.await()
    }

    private fun responseParser(data: String): APIData {
        val gson = GsonBuilder().create()
        return gson.fromJson(data, APIData::class.java)
    }
}

