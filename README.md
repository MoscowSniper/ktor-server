# Ktor API Server

## Описание
- **Items** (элементы/товары): Поля `id` (Int) и `name` (String).

## Требования
- Kotlin 1.8+.
- Gradle (для сборки и запуска).
- Java 11+ (для Ktor).

## Установка и запуск
1. Клонируйте репозиторий:  git clone https://github.com/MoscowSniper/ktor-server
2. Соберите проект (Зайдите в build.gradle.kts и нажмите на слоника :) )
3. Запустите сервер используя текущий файл ( Не Enginemain )
4. Сервер запустится на `http://localhost:8080`.

## API Документация
1. **Базовый URL**: `http://localhost:8080`

2. **Главная страница**  
**GET** `/`  
HTML-описание API.  
**Response**: 200, HTML.

**Items**  
**GET** `/items`  
**Response**: 200

3. **GET** `/items/{id}`  
Элемент по ID.  
**URL**: `/items/1`  
**Response**: 200

4. **POST** `/items`  
Добавить элемент (уникальный ID).  
**Body**:  
json
{"id":4,"name":"Grape"}
5. **DELETE** `/items/{id}`
Удалить по ID.
URL: /items/3
Response: 200, {"message":"Item deleted"}.
