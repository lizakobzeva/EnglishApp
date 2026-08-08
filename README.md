# EnglishApp

Android-приложение для изучения английских слов.

Приложение позволяет создавать собственный словарь, редактировать слова и повторять их в режиме обучения.

## ✨ Возможности

* 📚 просмотр добавленных слов;
* ➕ добавление новых слов в словарь;
* ✏️ редактирование существующих слов;
* 🧠 режим изучения и повторения слов;
* 🗂️ разделение функциональности приложения на независимые feature-модули;
* 🎨 интерфейс разработан на основе макетов в Figma.

## 🏗️ Архитектура

Проект построен как многомодульное Android-приложение.

Основная логика разделена на feature-модули, каждый из которых содержит публичный API и отдельную реализацию:

```text
EnglishApp/
├── app/
│
├── features/
│   ├── main/
│   │   ├── api/
│   │   └── impl/
│   │
│   ├── addword/
│   │   ├── api/
│   │   └── impl/
│   │
│   ├── editword/
│   │   ├── api/
│   │   └── impl/
│   │
│   └── wordsstudy/
│       ├── api/
│       └── impl/
│
├── libs/
│   ├── di/
│   └── imageloader/
│       └── coil/
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

### Feature-модули

| Модуль       | Назначение                           |
| ------------ | ------------------------------------ |
| `main`       | Главный экран приложения и навигация |
| `addword`    | Добавление нового слова              |
| `editword`   | Редактирование слова                 |
| `wordsstudy` | Изучение и повторение слов           |

Каждый feature разделён на:

* `api` — публичный контракт модуля;
* `impl` — внутренняя реализация функциональности.

Такое разделение позволяет уменьшить связанность между модулями и упрощает дальнейшее развитие проекта.

### Общие библиотеки

В директории `libs` находятся переиспользуемые модули проекта:

* `di` — общая инфраструктура dependency injection;
* `imageloader/coil` — загрузка и отображение изображений с использованием Coil.

## 🛠️ Технологии

* **Kotlin**
* **Android SDK**
* **Gradle Kotlin DSL**
* **AndroidX**
* **Material Components**
* **ConstraintLayout**
* **Coil**
* **JUnit**
* **Espresso**

Проект использует Java 11 / JVM target 11.

### Android SDK

```text
minSdk    = 24
targetSdk = 36
compileSdk = 36
```

## 🚀 Запуск проекта

### Требования

Перед запуском убедитесь, что установлены:

* Android Studio;
* Android SDK;
* JDK 11;
* Git.

### Клонирование

```bash
git clone https://github.com/lizakobzeva/EnglishApp.git
cd EnglishApp
```

### Запуск

Откройте проект в Android Studio и дождитесь завершения синхронизации Gradle.

После этого выберите Android Emulator или подключённое устройство и запустите конфигурацию `app`.

Также проект можно собрать из командной строки:

```bash
./gradlew assembleDebug
```

Для Windows:

```bash
gradlew.bat assembleDebug
```

## 🧪 Тестирование

Для запуска unit-тестов:

```bash
./gradlew test
```

Для запуска Android instrumentation tests:

```bash
./gradlew connectedAndroidTest
```

## 🎨 Дизайн

Макеты приложения доступны в Figma:

[EnglishApp — Figma Design](https://www.figma.com/design/u89GzIfoHzUiovyG2QzpAi/English-App?node-id=0-1&p=f&t=KIZs04KjTstuX9p2-0&utm_source=chatgpt.com)

## 📌 Статус проекта

Проект находится в разработке.

Текущая версия: **1.0**

Основные реализованные направления:

* главный экран;
* работа со словарём;
* добавление слов;
* редактирование слов;
* изучение слов.

## 📖 Как устроен проект

Приложение состоит из основного `app`-модуля и набора независимых feature-модулей.

`app` объединяет необходимые функции:

```text
app
 ├── main
 ├── addword
 ├── editword
 └── wordsstudy
```

Feature-модули не хранят всю логику непосредственно в `app`, что позволяет развивать отдельные части приложения независимо друг от друга.

Для общих задач используются модули из `libs`.


<h2> Структура экранов </h2>
<img width="862" height="693" alt="image" src="https://github.com/user-attachments/assets/c877db31-5f1a-4d47-b2e8-66512ac8b68b" />

<h2> Ссылка на фигму </h2>
https://www.figma.com/design/u89GzIfoHzUiovyG2QzpAi/English-App?node-id=0-1&p=f&t=KIZs04KjTstuX9p2-0
