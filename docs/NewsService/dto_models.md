# Модель новости
```json
{
        "id": 1,
        "title": "Заголовок новости",
        "description": "Краткий текст новости",
        "date": "dd.MM.yyyy",
        "previewImage": "URL до изображения предпросмотра новости"
    }
```
* `id` - идентификатор новости на сайте АГПУ
<hr>

# Модель подробной новости
```json
{
        "id": 1,
        "title": "Заголовок новости",
        "description": "Полный текст новости",
        "date": "dd.MM.yyyy",
        "images": []
    }
```
* `id` - идентификатор новости на сайте АГПУ
* `images` - массив строк содержащий URL до изображений, принадлежащей новости