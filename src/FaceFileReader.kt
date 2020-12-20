package processedbeats

import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.FileReader
import kotlinx.coroutines.*


interface FaceInterface{
    fun getFace(): FaceData
}

class FaceFileReader(val filePath: String): FaceInterface{
    val gson = GsonBuilder().create()
    val bufferedReader = BufferedReader(FileReader(filePath))
    var lastLine = bufferedReader.readLine()
    init {
        GlobalScope.launch { // launch a new coroutine in background and continue
            while (true) {
                delay(1L)
                val line = bufferedReader.readLine()
                if (line != null){
                    lastLine = line
                }
            }
        }
    }

    override fun getFace(): FaceData {
        val faceData = gson.fromJson(lastLine, FaceData::class.java)
        return faceData
    }
}

fun main(args: Array<String>){
    val reader = FaceFileReader("facedata.txt")
    while (true){
        Thread.sleep(1_00)
        println(reader.getFace())
    }
}


