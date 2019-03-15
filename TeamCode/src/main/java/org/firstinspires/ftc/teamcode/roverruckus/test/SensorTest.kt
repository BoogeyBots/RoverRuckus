package org.firstinspires.ftc.teamcode.roverruckus.test

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.waitForStartFixed
import org.firstinspires.ftc.teamcode.roverruckus.utils.writeSensorTelemetry

@Autonomous(name = "Sensor Test", group = "Test")
class SensorTest : LinearOpMode() {
    val robot = Robot(this)

    override fun runOpMode() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        robot.telemetry = telemetry as MultipleTelemetry

        robot.init()

        waitForStartFixed()

        while (opModeIsActive()) {
            robot.writeSensorTelemetry()

            telemetry.update()
        }
    }
}