package org.firstinspires.ftc.teamcode.extra.pushbot

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.Range

class Pushbot : OpMode() {
    private val hardware = PushbotHardware()

    override fun init() {
        hardware.init(hardwareMap)
    }

    override fun loop() {
        val throttle: Double = gamepad1.left_trigger.toDouble()
        val brake: Double = gamepad1.left_trigger.toDouble()

        val horizontalMovement: Double = -gamepad1.right_stick_x.toDouble()

        hardware.leftMotorPower = Range.clip(throttle - brake + horizontalMovement, 1.0, -1.0)
        hardware.rightMotorPower = Range.clip(throttle - brake - horizontalMovement, 1.0, -1.0)

        telemetry.addData("Movement:", "Forward: ${throttle + brake}, Sideways: $throttle")
    }
}