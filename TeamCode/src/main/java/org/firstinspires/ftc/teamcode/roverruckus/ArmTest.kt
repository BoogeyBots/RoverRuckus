package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.Servo
import java.lang.Thread.sleep

@TeleOp(name = "Arm Test", group = "Rover Ruckus")
@Disabled
class ArmTest : OpMode() {
    lateinit var armServo: Servo

    override fun init() {
        armServo = hardwareMap.get(Servo::class.java, "arm_servo")
    }

    override fun loop() {
        if (gamepad1.x) {
            armServo.position = 0.0
            sleep(500)
        } else if (gamepad1.b) {
            armServo.position = 1.0
            sleep(500)
        }

        printDebugInfo()
    }

    private fun printDebugInfo() {
        telemetry.addData("Servo position:", armServo.position)
    }
}
