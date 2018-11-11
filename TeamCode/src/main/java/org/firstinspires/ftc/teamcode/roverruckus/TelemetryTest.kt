package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime

@Autonomous (name="TelemetryTest", group="Rover Ruckus")
class TelemetryTest : OpMode() {
    val elapsedTime: ElapsedTime = ElapsedTime()

    override fun init() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        elapsedTime.reset()
    }

    override fun loop() {
        telemetry.addData("Elapsed Time", "${elapsedTime.seconds()} seconds")
    }

}

