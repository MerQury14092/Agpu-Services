# Получение расписания на день

Выполнив GET запрос, вы получите 
[день](#модель-дня-расписания)

Требуемые параметры к URL:
* `groupId` - имя группы
* `date` - дата дня в формате dd.MM.yyyy

URL: ***http://merqury.fun:8080/api/timetableOfDay***

<hr>

# Получение расписания на диапазон дней

Выполнив GET запрос, вы получите массив
[дней](#модель-дня-расписания)

Требуемые параметры URL:
* `groupId` - имя группы
* `startDate` - с какой даты
* `endDate` - по какую дату

Опциональные параметры URL:
* `removeEmptyDays` - удалит те дни, которые не имеют расписания

URL: ***http://merqury.fun:8080/api/timetableOfDays***

<hr>

# Получение списка всех групп

Выполнив GET запрос, вы получите массив из строк - имён всех групп университета

URL: ***http://merqury.fun:8080/api/allGroups***

<hr><hr>

# Модель дисциплины
```
{
    "time": "hh:mm-hh:mm",
    "name": "Имя дисциплины",
    "teacherName": "Преподователь",
    "audienceId": "Идентификатор аудитории",
    "subgroup": 0 // 0 - общая пара, 1,2 - по подгруппам        
    "type": "enum" 
    // lec - лекция
    // prac - практика
    // exam - экзамен
    // lab - лабораторная работа
    // hol - выходной
    // cred - зачёт
    // cons - консультация
    // fepo - федеральный экзамен профессионального образования
    // none - N/A
}
```
<hr>

# Модель дня расписания
```
{
    "date": "dd.MM.yyyy",
    "groupName": "Имя группы",
    "disciplines": [
        // массив дисциплин
    ]
}
```