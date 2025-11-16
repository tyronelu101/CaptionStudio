package com.example.captionstudio.domain.transcriber

import org.json.JSONObject

class VoskResultParser {
    fun parse(jsonString: String): List<VoskWord> {
        val words = mutableListOf<VoskWord>()
        val jsonObj = JSONObject(jsonString)
        val results = jsonObj.getJSONArray("result")
        for (i in 0 until results.length()) {
            val wordObj = results.getJSONObject(i)
            val conf = wordObj.getDouble("conf")
            val start = wordObj.getDouble("start")
            val end = wordObj.getDouble("end")
            val word = wordObj.getString("word")

            words.add(VoskWord(conf = conf, start=start, end=end, word=word))
        }
        return words
    }
}