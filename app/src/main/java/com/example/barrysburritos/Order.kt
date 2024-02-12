import android.content.Context
import android.widget.Toast
import com.example.barrysburritos.CustomCartItem
import com.example.barrysburritos.PremadeOrderItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class Order(val premadeItems: List<PremadeOrderItem>, val customItems: List<CustomCartItem>){

    companion object {
        const val FILE_NAME = "order.json"
    }


    fun saveOrderToJson(context: Context) {
        val gson = Gson()
        val orderJson = gson.toJson(this)

        try {
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(orderJson.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun returnOrderFromJsonAsString(context: Context): String {
        var orderJson = ""
        try {
            context.openFileInput(FILE_NAME).use { stream ->
                orderJson = stream.bufferedReader().use {
                    it.readText()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return orderJson
    }

    fun readOrderFromJson(context: Context): List<Any>{
        var premadeItemsList: List<PremadeOrderItem> = emptyList()
        var customItemsList: List<CustomCartItem> = emptyList()

        val orderJson = returnOrderFromJsonAsString(context)

        if (orderJson.isNotEmpty()) {
            val gson = Gson()
            val orderType = object : TypeToken<Order>() {}.type
            val order = gson.fromJson<Order>(orderJson, orderType)

            premadeItemsList = order.premadeItems
            customItemsList = order.customItems
        } else {
            // If the JSON is empty, return an empty list
            Toast.makeText(context, "No order found", Toast.LENGTH_SHORT).show()
            return emptyList()
        }
        val allItems = mutableListOf<Any>()
        allItems.addAll(customItemsList)
        allItems.addAll(premadeItemsList)

        return allItems
    }
}