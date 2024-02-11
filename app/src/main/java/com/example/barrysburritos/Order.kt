import android.content.Context
import com.example.barrysburritos.CustomCartItem
import com.example.barrysburritos.PremadeOrderItem
import com.google.gson.Gson
import java.io.IOException

class Order(
    val customItem: List<CustomCartItem>,
    val premadeItem: List<PremadeOrderItem>
) {

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
}