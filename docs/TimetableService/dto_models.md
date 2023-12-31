# Модель дисциплины
```json
{
    "time": "hh:mm-hh:mm",
    "name": "Имя дисциплины",
    "teacherName": "Преподователь",
    "groupName": "Группа",
    "audienceId": "Аудитория",
    "subgroup": 0,          
    "type": "enum"
}
```
* `subgroup`: 0 - общая пара, 1,2 - по подгруппам
* Перечисление `type`:
```
lec - лекция
prac - практика
exam - экзамен
lab - лабораторная работа
hol - выходной
cred - зачёт
cons - консультация
fepo - федеральный экзамен профессионального образования
none - N/A
```
<hr>

# Модель дня расписания для группы
```json
{
    "date": "dd.MM.yyyy",
    "groupName": "Имя группы",
    "disciplines": []
}
```
* `disciplines` - массив дисциплин

# Модель дня расписания для учителя
```json
{
    "date": "dd.MM.yyyy",
    "teacherName": "Имя учителя",
    "disciplines": []
}
```
* `disciplines` - массив дисциплин
<hr>


# Модель недели
```json
{
    "id": 1,
    "from": "dd.MM.yyyy",
    "to": "dd.MM.yyyy", 
    "dayNames": {
        "dd.MM.yyyy": "Понедельник",
        "dd.MM.yyyy": "Вторник",
        "dd.MM.yyyy": "Среда",
        "dd.MM.yyyy": "Четверг",
        "dd.MM.yyyy": "Пятница",
        "dd.MM.yyyy": "Суббота",
        "dd.MM.yyyy": "Воскресенье"
    }
}
```
* `id` - номер недели
* `from` - с какого дня
* `to` - по какой день
* `dayNames` - словарь сопоставляющий каждый день недели с его названием

<hr>

# Модель факультета и его групп
```json
{
    "facultyName": "Имя факльтета/института",
    "groups": [
        "first group",
        "second group"
    ]
} 
```
* `groups` - массив имён групп, соответствующих данному факультету/институту (значения указанные в модели - для примера)