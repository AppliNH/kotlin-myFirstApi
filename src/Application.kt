package com.ocwebserver

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.css.*
import kotlinx.html.*

fun Course.courseToJSON() = Gson().toJson(this)

val mostDifficultCourse = courses.maxBy { it.level };


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }


    routing {
        get("/") {
            call.respondText("Welcome to Openclassrooms brand new server", contentType = ContentType.Text.Plain)
        }
        get("/course/") {
            if (mostDifficultCourse != null) {
                call.respondHtml {
                    body {
                        h1 { +"Please chose a course" }

                        ul {
                            for (n in courses) {
                                li { +"${n.courseToJSON()}" }
                            }
                        }
                    }
                }
            }
        }
        get("/course/{id}") {
            try {
                var id: Int? = call.parameters["id"]?.toInt()
                var selectedCourse = courses.find { it.id == id }

                if (selectedCourse != null) {
                    call.respondText("${selectedCourse.courseToJSON()}", contentType = ContentType.Text.Plain)
                } else {
                    call.respondText("Aucun cours pour cet id !", contentType = ContentType.Text.Plain)
                }
            } catch (e: Exception) {
                call.respondText("404 not found", contentType = ContentType.Text.Plain)

            }
        }

        get("/course/top") {
            if (mostDifficultCourse != null) {
                call.respondText(mostDifficultCourse.courseToJSON(), contentType = ContentType.Text.Plain)
            }
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
