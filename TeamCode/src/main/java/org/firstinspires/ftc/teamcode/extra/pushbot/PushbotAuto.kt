package org.firstinspires.ftc.teamcode.extra.pushbot

import android.graphics.Color
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode

@Autonomous(name = "Pushbot Auto", group = "PUSHBOT")
@Disabled
class PushbotAuto : OpMode() {
    val hardware: PushbotHardware = PushbotHardware()

    override fun init() {
        hardware.init(hardwareMap)
    }

    override fun loop() {
        val hsv: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
        Color.RGBToHSV(hardware.colorSensor.red(), hardware.colorSensor.green(), hardware.colorSensor.blue(), hsv)

        telemetry.addData("Color sensed: ", "Hue: ${hsv[0]}; Saturation: ${hsv[1]}; Value: ${hsv[2]}")
    }
}