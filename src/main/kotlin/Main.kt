// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.*

@Composable
@Preview
fun App() {
    val file = File("src//main//resources//input.csv")


    val keys :Array<String>
    val values:Array<String>
    val builder: TypeSpec.Builder = TypeSpec.classBuilder("CSVModel")
        .addModifiers(javax.lang.model.element.Modifier.PUBLIC)

    try {
        val bufferedReader = BufferedReader(FileReader(file))
        val firstLine = bufferedReader.readLine()
        keys = firstLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        values = bufferedReader.readLine().split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (i in keys.indices) {
            var value = ""
            if (i < values.size) {
                value = values[i]
            }
            val field: FieldSpec = FieldSpec.builder(String::class.java, keys[i])
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .initializer("\$S", value)
                .build()
            builder.addField(field)
        }
        val typeSpec: TypeSpec = builder.build()
        val javaFile: JavaFile = JavaFile.builder("", typeSpec).build()
        javaFile.writeTo(File("src//main//kotlin/"))
//        generate()
    } catch (e: IOException) {
        throw RuntimeException(e)
    }


    val map = LinkedHashMap<String,String>()


    MaterialTheme {
        Column {
            for(i in keys.indices){
                Text(keys[i])
                val textValue = remember {
                    map[keys[i]] = values[i]

                    mutableStateOf(values[i])

                }
                TextField(modifier = Modifier, value = textValue.value, onValueChange = {input ->
                    textValue.value = input
                    map[keys[i]] = input
                })
            }
            Button(onClick = {
                generateFile(map)
            }){
                Text("Generate")
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

private fun generateFile(map: LinkedHashMap<String, String>) {
    val file = File("src//main//resources//output.csv")
    var fileWriter: FileWriter? = null
    fileWriter = try {
        FileWriter(file)
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
    val header = StringBuilder()

    map.keys.forEach {
        header.append(it)
        header.append(",")
    }
    header.deleteCharAt(header.length-1)
    header.append("\n");
    fileWriter?.write(header.toString())

    val line = StringBuilder()

    map.values.forEach {
        line.append(it)
        line.append(",")
    }
    line.deleteCharAt(line.length-1)
    fileWriter?.write(line.toString())
    fileWriter?.close()

}


private fun generate() {
    val file = File("src//main//resources//output.csv")
    var fileWriter: FileWriter? = null
    fileWriter = try {
        FileWriter(file)
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
    val header = StringBuilder()
    val aClass: Class<CSVModel> = CSVModel::class.java
    for (field in aClass.getFields()) {
        header.append(field.name)
        header.append(",")
    }
    header.deleteCharAt(header.length - 1)
    try {
        fileWriter?.write(header.toString())
        fileWriter?.write("\n")
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
    val csvModel = CSVModel()
    val line = StringBuilder()
    for (field in aClass.getFields()) {
        try {
//            if (field.name == paramter.key()) {
//                line.append(paramter.value())
//            } else {
                line.append(field[csvModel])
//            }
            line.append(",")
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }
    line.deleteCharAt(line.length - 1)
    try {
        fileWriter?.write(line.toString())
        fileWriter?.write("\n")
        fileWriter?.close()
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}

