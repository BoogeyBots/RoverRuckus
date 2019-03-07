package org.firstinspires.ftc.teamcode.roverruckus.utils

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime

val elapsedTime: ElapsedTime = ElapsedTime()

fun LinearOpMode.wait(seconds: Double) {
    elapsedTime.reset()
    while (elapsedTime.seconds() < seconds && opModeIsActive()) { }
}

fun LinearOpMode.waitForStartFixed() {
    while (!opModeIsActive() && !isStopRequested) {
        telemetry.addData("INIT OVER", "Waiting")
        telemetry.update()
    }
}