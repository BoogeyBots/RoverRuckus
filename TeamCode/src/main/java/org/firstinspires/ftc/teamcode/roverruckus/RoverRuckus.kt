package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.Range

@TeleOp(name = "Robot", group = "Rover Ruckus")
class RoverRuckus : OpMode() {
    private val hardware = RoverRuckusHardware()

    override fun init() {
        hardware.init(hardwareMap)
    }

    override fun loop() {
        val throttle : Double = gamepad1.left_trigger.toDouble()
        val brake : Double = gamepad1.right_trigger.toDouble()
        val horizontalMovement : Double = (-gamepad1.left_stick_x).toDouble()

        hardware.leftMotorPower = Range.clip(throttle - brake - horizontalMovement, -1.0, 1.0)
        hardware.rightMotorPower = Range.clip(throttle - brake + horizontalMovement, -1.0, 1.0)

        telemetry.addData("Movement", "Forward ${throttle-brake}, Sideways $horizontalMovement")
    }
}