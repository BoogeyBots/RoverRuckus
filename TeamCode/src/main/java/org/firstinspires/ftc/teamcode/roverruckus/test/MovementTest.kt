package org.firstinspires.ftc.teamcode.roverruckus.test

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.roverruckus.autonomous.moveForSeconds
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.waitForStartFixed
import org.firstinspires.ftc.teamcode.roverruckus.utils.writeSensorTelemetry

@Autonomous(name = "Movement Test", group = "Test")
class MovementTest : LinearOpMode() {
    val robot = Robot(this)

    override fun runOpMode() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        robot.telemetry = telemetry as MultipleTelemetry

        robot.init()

        waitForStartFixed()

        robot.moveForSeconds(2.0, -0.5)
        robot.moveForSeconds(2.0, 0.5)
    }
}