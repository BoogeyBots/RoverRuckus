package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo

@TeleOp(name = "CRServoTesting", group = "roverruckus")
class CRServoTesting : OpMode() {
    lateinit var crServo: Servo
    val midServo = 0.5
    var servoOffset = 0.0

    override fun init() {
        crServo = hardwareMap.get(Servo::class.java, "servo")
        crServo.position = 0.0
    }

    override fun loop() {
        servoOffset += 0.02 * -gamepad1.left_stick_y
        crServo.position = midServo + servoOffset

    }

}