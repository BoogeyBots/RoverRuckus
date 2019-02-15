package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime

@Autonomous(name = "Test", group = "Rover Ruckus")
class Test : LinearOpMode() {
    val hardware = RoverRuckusHardware()
    val elapsedTime = ElapsedTime()

    override fun runOpMode() {
        hardware.init(hardwareMap)
        waitForStart()

        parkArm()
    }

    private fun parkArm() {
        elapsedTime.reset()
        while (elapsedTime.seconds() < 2.0 && opModeIsActive()) {
            hardware.leftArm.power = -0.4
            hardware.rightArm.power = -0.4
        }
        hardware.leftArm.power = 0.0
        hardware.rightArm.power = 0.0
    }
}