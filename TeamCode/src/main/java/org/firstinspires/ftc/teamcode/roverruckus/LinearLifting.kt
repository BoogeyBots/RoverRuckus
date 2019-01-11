package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.Range

@TeleOp(name = "Linear Lifting", group = "Rover Ruckus")
class LinearLifting : OpMode() {
    private val hardware = LiftingHardware()

    override fun init() {
        hardware.init(hardwareMap)
    }

    override fun loop() {
        val lifting: Double = -gamepad1.right_stick_y.toDouble()

        hardware.liftingPower = Range.clip(lifting, -0.6, 0.6)
    }
}