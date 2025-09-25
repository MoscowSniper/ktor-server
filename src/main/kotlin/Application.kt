import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.CopyOnWriteArrayList

@Serializable
data class Item(val id: Int, val name: String)

@Serializable
data class User(val id: Int, val name: String, val email: String)

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class MessageResponse(val message: String)

fun main() {
    val items = CopyOnWriteArrayList(
        listOf(
            Item(1, "Keyboard"),
            Item(2, "Mouse"),
            Item(3, "Headphones")
        )
    )

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        routing {
            get("/") {
                call.respondText(
                    """
                        <html>
                            <head><title>Ktor API Server</title></head>
                            <body>
                                <h1>Добро пожаловать!</h1>
                                <ul>
                                    <li><strong>Items (Товары/Элементы):</strong></li>
                                    <ul>
                                        <li>GET <a href='/items'>/items</a> - Получить все элементы (фильтр по ?name=)</li>
                                        <li>GET <a href='/items/1'>/items/{id}</a> - Получить элемент по ID</li>
                                        <li>POST <a href='/items'>/items</a> - Добавить новый элемент (JSON: {"id": 4, "name": "Grape"})</li>
                                        <li>DELETE <a href='/items/1'>/items/{id}</a> - Удалить элемент по ID</li>
                                    </ul>
                                </ul>
                                
                                <p><em>Сервер запущен на порту 8080. Используйте Postman для тестирования.</em></p>
                            </body>
                        </html>
                    """.trimIndent(),
                    ContentType.Text.Html
                )
            }

            get("/items") {
                val nameFilter = call.request.queryParameters["name"]
                val result = if (!nameFilter.isNullOrBlank()) {
                    items.filter { it.name.contains(nameFilter, ignoreCase = true) }
                } else {
                    items.toList() // Копируем для безопасности
                }
                call.respond(HttpStatusCode.OK, result)
            }

            get("/items/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    return@get
                }

                val item = items.find { it.id == id }
                if (item == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Item not found"))
                } else {
                    call.respond(HttpStatusCode.OK, item)
                }
            }

            post("/items") {
                val newItem = try {
                    call.receive<Item>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request body"))
                    return@post
                }

                if (items.any { it.id == newItem.id }) {
                    call.respond(HttpStatusCode.Conflict, ErrorResponse("Item with this ID already exists"))
                } else {
                    items.add(newItem)
                    call.respond(HttpStatusCode.Created, newItem)
                }
            }

            delete("/items/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    return@delete
                }

                val removed = items.removeIf { it.id == id }
                if (removed) {
                    call.respond(HttpStatusCode.OK, MessageResponse("Item deleted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Item not found"))
                }
            }
        }
    }.start(wait = true)
}