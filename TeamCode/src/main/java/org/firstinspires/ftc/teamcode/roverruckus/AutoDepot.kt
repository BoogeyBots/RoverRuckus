package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range

@Autonomous(name = "Dezgatare", group = "Rover Ruckus")
class AutoDepot : LinearOpMode() {
    val hardware = RoverRuckusHardware()
    val elapsedTime = ElapsedTime()

    override fun runOpMode() {
        hardware.init(hardwareMap)

        hardware.lockServoPos = 0.5
        hardware.hookServoPos = 0.5

        waitForStart()

        elapsedTime.reset()

        while (elapsedTime.seconds() < 0.28 && opModeIsActive()) {
            hardware.leftArmPower = 0.6
            hardware.rightArmPower = 0.6

            hardware.lockServoPos = Range.clip(hardware.lockServoPos - 0.05, 0.0, 0.5)
        }

        elapsedTime.reset()

        while (elapsedTime.seconds() < 3.0 && opModeIsActive()) {
            hardware.leftArmPower = 0.08
            hardware.rightArmPower = 0.08
        }

        elapsedTime.reset()

        while (elapsedTime.seconds() < 0.5) {
            hardware.leftArmPower = -0.5
            hardware.rightArmPower = -0.5
        }

        hardware.leftArmPower = 0.0
        hardware.rightArmPower = 0.0

        elapsedTime.reset()

        while (elapsedTime.seconds() < 1.0 && opModeIsActive()) {
            hardware.hookServoPos = Range.clip(hardware.hookServoPos - 0.05, 0.0, 0.5)
        }
    }
}