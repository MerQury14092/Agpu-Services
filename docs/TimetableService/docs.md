# Получение расписания на день

Выполнив GET запрос, вы получите 
[день](dto_models.md#модель-дня-расписания)

Требуемые параметры к URL:
* `id` - имя группы (учителя)
* `date` - дата дня в формате dd.MM.yyyy

URL: ***http://merqury.fun/api/timetable/day***

URL (для учителей): ***http://merqury.fun/api/timetable/teacher/day***


<hr>

# Получение расписания на диапазон дней

Выполнив GET запрос, вы получите массив
[дней](dto_models.md#модель-дня-расписания)

Требуемые параметры URL:
* `id` - имя группы (учителя)
* `startDate` - с какой даты
* `endDate` - по какую дату

Опциональные параметры URL:
* `removeEmptyDays` - удалит те дни, которые не имеют расписания

URL: ***http://merqury.fun/api/timetable/days***

URL (для учителей): ***http://merqury.fun/api/timetable/teacher/days***

<hr>

# Получение списка всех групп

Выполнив GET запрос, вы получите массив 
[групп каждого факультета](dto_models.md#модель-факультета-и-его-групп)

URL: ***http://merqury.fun/api/timetable/groups***

<hr>

# Получение списка недель

Выполнив GET запрос, вы получите массив
[недель](dto_models.md#модель-недели)

URL: ***http://merqury.fun/api/timetable/weeks***
