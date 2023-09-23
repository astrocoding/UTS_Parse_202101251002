package com.belajar.uts_parse_202101251002

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.belajar.uts_parse_202101251002.databinding.ActivityQuranListBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.util.ArrayList

class QuranList : AppCompatActivity() {
    companion object {
        private val TAG = QuranList::class.java.simpleName
    }

    private lateinit var binding: ActivityQuranListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuranListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Surah Al-Qur'an"

        getListQuran()
    }

    private fun getListQuran() {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "http://api.alquran.cloud/v1/surah"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE

                val listQuran = ArrayList<String>()

                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray = jsonObject.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val no = jsonObject.getString("number")
                        val nama = jsonObject.getString("name")
                        val namaEng = jsonObject.getString("englishName")
                        val namaTL = jsonObject.getString("englishNameTranslation")
                        val ayat = jsonObject.getString("numberOfAyahs")
                        val diturunkan = jsonObject.getString("revelationType")
                        listQuran.add("\n$no - $namaEng\n $diturunkan | $ayat \n$nama\n\n")
                    }

                    val adapter = ArrayAdapter(this@QuranList, R.layout.simple_list_item_1, listQuran)
                    binding.listQuran.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this@QuranList, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                // Jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@QuranList, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}