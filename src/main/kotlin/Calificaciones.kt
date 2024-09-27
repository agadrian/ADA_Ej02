package org.example

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path

class Calificaciones(private val path: Path) {

    /**
     * Una función que reciba el fichero de calificaciones y devuelva una lista de diccionarios, donde cada diccionario contiene la información de los exámenes y la asistencia de un alumno. La lista tiene que estar ordenada por apellidos.
     */
    fun createDicc(): List<MutableMap<String, Any>> {
        val fichero = path.resolve("calificaciones.csv")

        //val listOfDicts = mutableListOf<MutableMap<String, MutableList<String>>>()
        val listaDicc = mutableListOf<MutableMap<String,Any>>()

        if (Files.notExists(fichero)){
            println("No existe fichero seleccionado")
        } else{
            val br: BufferedReader = Files.newBufferedReader(fichero)
            br.use {flujo ->

                // Obtener cabecera
                val cabecera = flujo.readLine().split(";")

                // Resto de lineas
                flujo.forEachLine { linea ->
                    val dicc = mutableMapOf<String,Any>()
                    val listaLinea = linea.replace(",",".").replace("%", "").split(";").toMutableList()

                    listaLinea.forEachIndexed {index, dato ->
                        if (dato.isBlank()) listaLinea[index] = "0"
                    }

                    // Añadimos al diccionario
                    for (i in cabecera.indices){
                        dicc[cabecera[i]] = listaLinea[i]
                    }

                    // Añadimos el diccionario al listado de diccionarios
                    listaDicc.add(dicc)
                }
            }
        }

        // Ordena mediante la clave del primer conjunto key-value que encuentra
        // return listaDicc.sortedBy { it["Apellidos"] as String? }
        // return listaDicc.sortedBy { it.keys.first() }
        return listaDicc.sortedBy { it["Apellidos"].toString() }
    }


    /**
     * Una función que reciba una lista de diccionarios como la que devuelve la función anterior y añada a cada diccionario un nuevo par con la nota final del curso. El peso de cada parcial de teoría en la nota final es de un 30% mientras que el peso del examen de prácticas es de un 40%.
     */
    fun addNota(listaDicc: List<MutableMap<String, Any>>): List<MutableMap<String, Any>> {

        listaDicc.forEach { dict ->
            val parcial1 = dict["Parcial1"].toString().toDouble()
            val parcial2 = dict["Parcial2"].toString().toDouble()
            val practicas = dict["Practicas"].toString().toDouble()

            val nota = ((((parcial1+parcial2)/2) * 0.3) + (practicas*0.4))

            dict["Notafinal"] = nota
        }
        return listaDicc
    }



    /**
     * Una función que reciba una lista de diccionarios como la que devuelve la función anterior y devuelva dos listas, una con los alumnos aprobados y otra con los alumnos suspensos. Para aprobar el curso, la asistencia tiene que ser mayor o igual que el 75%, la nota de los exámenes parciales y de prácticas mayor o igual que 4 y la nota final mayor o igual que 5.
     */
    fun aprobadosSuspensos(listaDicc: List<MutableMap<String, Any>>){

        val notaMin = 4
        val asistenciaMin = 75
        val notaFinMin = 5


        val aprobados = listaDicc.filter { dict ->
            dict["Notafinal"].toString().toDouble() >= notaFinMin
                    && dict["Asistencia"].toString().toInt() >= asistenciaMin
                    && dict["Parcial1"].toString().toDouble() >= notaMin
                    && dict["Parcial2"].toString().toDouble() >= notaMin
                    && dict["Practicas"].toString().toDouble() >= notaMin
        }


        val suspensos = listaDicc.filter { dict ->
            dict["Notafinal"].toString().toDouble() < notaFinMin
                    || dict["Asistencia"].toString().toInt() < asistenciaMin
                    || dict["Parcial1"].toString().toDouble() < notaMin
                    || dict["Parcial2"].toString().toDouble() < notaMin
                    || dict["Practicas"].toString().toDouble() < notaMin
        }


          //val suspensos = listaDicc.minus(aprobados)   ??????????

        println("${aprobados}\n${suspensos}")
    }


}