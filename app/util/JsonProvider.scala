package util

import java.io.StringWriter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import play.api.libs.json.JsValue


object JsonProvider {

  //create mapper and register scala module
  private val mapper = new ObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule)


  def toJson(obj: Object): String = {
    val writer = new StringWriter
    mapper.writeValue(writer, obj)
    writer.toString
  }


  def fromJson[T: scala.reflect.Manifest](json: String): T = {
    println(json.length)
    val x = mapper.readValue(json, scala.reflect.classTag[T].runtimeClass).asInstanceOf[T]
    println("DONE WITH => " + x)
    x
  }

  def readFromJsonMap[T](jsonData: Map[String, JsValue], fieldName: String)(implicit fjs: play.api.libs.json.Reads[T]): Option[T] = {
    jsonData.get(fieldName) match {
      case Some(x) => x.asOpt[T]
      case _ => None
    }
  }
}
