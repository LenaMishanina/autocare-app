# AutoCare 🚗🔧

<img src="https://img.icons8.com/fluency/96/000000/car-service.png" align="right" width="120">

**Мобильное приложение для учета технического обслуживания автомобилей**

## 📌 О проекте

AutoCare помогает автовладельцам:
- 📅 Не забывать о плановом ТО
- 🔔 Получать напоминания о сервисных работах
- 📊 Вести историю обслуживания
- 🛠 Планировать будущие работы

> "Простое решение для сложного учета обслуживания вашего автомобиля"

## 👥 Команда

| Роль | ФИО | Группа |
|------|-----|--------|
| Разработчик | Д.В. Вагина | 5130904/20105 |
| Разработчик | Дорзеа Лиз | 5130904/20105 |
| Разработчик | Е.А. Мишанина | 5130904/20105 |

## ✨ Основные функции

<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem; margin: 1rem 0;">

<div style="border: 1px solid #ddd; border-radius: 8px; padding: 1rem;">
<h4>🚘 Регистрация авто</h4>
<p>Марка, модель, год выпуска, текущий пробег</p>
</div>

<div style="border: 1px solid #ddd; border-radius: 8px; padding: 1rem;">
<h4>📅 Календарь ТО</h4>
<p>Планирование сервисных событий</p>
</div>

<div style="border: 1px solid #ddd; border-radius: 8px; padding: 1rem;">
<h4>🔔 Уведомления</h4>
<p>Напоминания о предстоящем ТО</p>
</div>

<div style="border: 1px solid #ddd; border-radius: 8px; padding: 1rem;">
<h4>📊 История работ</h4>
<p>Фильтрация и сортировка выполненных услуг</p>
</div>

</div>

## 🛠 Технологический стек

<table>
  <tr>
    <td align="center" width="100">
      <img src="https://img.icons8.com/color/48/000000/kotlin.png" width="48" height="48" alt="Kotlin" />
      <br>Kotlin
    </td>
    <td align="center" width="100">
      <img src="https://img.icons8.com/color/48/000000/python.png" width="48" height="48" alt="Python" />
      <br>Python
    </td>
    <td align="center" width="100">
      <img src="https://img.icons8.com/color/48/000000/postgreesql.png" width="48" height="48" alt="PostgreSQL" />
      <br>PostgreSQL
    </td>
    <td align="center" width="100">
      <img src="https://img.icons8.com/color/48/000000/docker.png" width="48" height="48" alt="Docker" />
      <br>Docker
    </td>
  </tr>
</table>

## 🚀 Быстрый старт

### Предварительные требования
- Docker

```bash
# 1. Клонировать репозиторий
git clone https://github.com/LenaMishanina/autocare-app.git
cd ./autocare-app/backend/FastAPIProjectAutoCare

# 2. Подготовка конфигурации
# - Перейдите в файл backend/FastAPIProjectAutoCare/config.py 
# - Обновите следующие параметры: 
DATABASE_USERNAME = os.getenv('DATABASE_USERNAME', 'ваш_логин') 
DATABASE_PASSWORD = os.getenv('DATABASE_PASSWORD', 'ваш_пароль')

# 3. Запустить сервисы
docker-compose up -d --build

# 4. Приложение доступно на:
# - Бэкенд: http://localhost:8000
# - Мобильное приложение запускается на эмуляторе Android или на физическом устройстве 
