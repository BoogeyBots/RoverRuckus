package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime

@Autonomous (name="TelemetryTest", group="Rover Ruckus")
class TelemetryTest : OpMode() {
    val elapsedTime: ElapsedTime = ElapsedTime()

    override fun init() {

    }

    override fun start() {
        elapsedTime.reset()
    }

    override fun loop() {
        telemetry.addData("Elapsed Time", "${elapsedTime.seconds()} seconds")
    }

}

