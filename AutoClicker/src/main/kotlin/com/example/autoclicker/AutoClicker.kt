package com.example.autoclicker

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import java.awt.Robot
import java.awt.event.InputEvent
import java.io.IOException
import kotlin.system.exitProcess


class AutoClicker : Application() {
    // Координаты для хранения смещения курсора мыши, чтобы перетащить окно
    private var xOffset = 0.0
    private var yOffset = 0.0

    override fun start(primaryStage: Stage) {
        try {
            // Загружаем FXML файл
            val fxmlLoader = FXMLLoader(AutoClicker::class.java.getResource("auto-clicker.fxml"))
            val root: Parent = fxmlLoader.load()

            // Устанавливаем заголовок окна
            primaryStage.title = "AutoClicker"
            primaryStage.icons.add(Image(AutoClicker::class.java.getResourceAsStream("/images/icon.png")))

            // Создаем сцену с загруженным корнем
            val scene = Scene(root, 550.0, 300.0)
            root.styleClass.add("root")
            primaryStage.scene = scene
            primaryStage.setOnShown { root.requestFocus() }

            // Устанавливаем стиль для корневого узла
            primaryStage.initStyle(StageStyle.TRANSPARENT) // Делаем рамку окна прозрачной
            primaryStage.isResizable = false  // Блокировка изменения размера окна
            primaryStage.isAlwaysOnTop = true // Делаем окно всегда поверх всех окон

            // Обработка перетаскивания окна
            root.setOnMousePressed { event ->
                xOffset = event.screenX - primaryStage.x
                yOffset = event.screenY - primaryStage.y
            }

            root.setOnMouseDragged { event ->
                primaryStage.x = event.screenX - xOffset
                primaryStage.y = event.screenY - yOffset
            }

            primaryStage.show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


class AutoClickerController {
    private var isClicking = false // Работа автоклика
    private lateinit var timeline: Timeline
    private var clickInterval = 0 // Интервал между кликами в миллисекундах
    private var isButtonDisabled = false // Работа кнопки
    private lateinit var robot: Robot


    @FXML
    private lateinit var intervalInput: TextField // Поле для ввода интервала кликов

    // Добавление изображений
    @FXML
    private lateinit var startImage: ImageView

    @FXML
    private lateinit var stopImage: ImageView

    @FXML
    private lateinit var nameImage: ImageView

    @FXML
    private lateinit var gifImageView: ImageView
    private lateinit var gifImage: Image
    private lateinit var staticImage: Image

    @FXML
    fun initialize() {
        robot = Robot() // Инициализация класса Robot

        // Регистрация ресурсов для интерфейса
        startImage.image = Image(AutoClicker::class.java.getResourceAsStream("/images/play.png"))
        stopImage.image = Image(AutoClicker::class.java.getResourceAsStream("/images/pause.png"))
        nameImage.image = Image(AutoClicker::class.java.getResourceAsStream("/images/name.png"))
        gifImage = Image(AutoClicker::class.java.getResourceAsStream("/images/gifka.gif"))
        staticImage = Image(AutoClicker::class.java.getResourceAsStream("/images/static.png"))
        gifImageView.image = gifImage
        setupAutoClicking()
    }



    private fun setupAutoClicking() {
        // Метод для настройки таймера автоклика.
        gifImageView.image = staticImage
        timeline = Timeline(
            KeyFrame(
                Duration.millis(clickInterval.toDouble()),
                {
                    if (isClicking) {
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK) // Нажатие кнопки мыши
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK) // Отпускание кнопки мыши
                    }
                }
            )
        ).apply {
            cycleCount = Timeline.INDEFINITE // Установка количества циклов в бесконечность
        }
    }


    // Управление интерфейсом приложения
    @FXML
    fun handleSetIntervalButton() {
        val input = intervalInput.text
        clickInterval = input.toIntOrNull() ?: clickInterval // Обновление интервала, если ввод корректен
        timeline.stop() // Остановить текущий таймер
        setupAutoClicking() // Настроить новый таймер с новым интервалом
        startImage.styleClass.remove("buttonGuiOff")
        startImage.styleClass.add("buttonGui")
        println("Click interval updated to $clickInterval ms")
    }

    @FXML
    fun handleStartButton() {
        if (isButtonDisabled) return

        if (clickInterval > 0) {
            if (!isClicking) {
                isClicking = true
                timeline.play()
                gifImageView.image = gifImage
                isButtonDisabled = true

                // Включени и отключение анимации наводки на кнопку
                startImage.styleClass.add("buttonGuiOff")
                startImage.styleClass.remove("buttonGui")
                stopImage.styleClass.add("buttonGui")
                stopImage.styleClass.remove("buttonGuiOff")
                println("AutoClicker started")
            }
        }
    }

    @FXML
    fun handleStopButton() {

        if (isClicking) {
            isClicking = false
            timeline.stop()
            gifImageView.image = staticImage

            // Включени и отключение анимации наводки на кнопку
            stopImage.styleClass.add("buttonGuiOff")
            stopImage.styleClass.remove("buttonGui")
            startImage.styleClass.add("buttonGui")
            startImage.styleClass.remove("buttonGuiOff")
            println("AutoClicker stopped")
        }
    }


    // Управление окном приложения
    @FXML
    fun handleCloseButton() {
        // Закрыть приложение
        Platform.exit()
        exitProcess(0)
    }

    @FXML
    fun handleMinimizeButton() {
        // Свернуть приложение
        val stage = intervalInput.scene.window as Stage
        stage.isIconified = true
    }
}

fun main() {
    Application.launch(AutoClicker::class.java)
}
