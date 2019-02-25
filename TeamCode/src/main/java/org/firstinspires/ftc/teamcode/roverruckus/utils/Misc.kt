package org.firstinspires.ftc.teamcode.roverruckus.utils

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime

val elapsedTime: ElapsedTime = ElapsedTime()

fun wait(seconds: Double) {
    elapsedTime.reset()
    while (elapsedTime.seconds() < seconds) { }
}

fun LinearOpMode.waitForStartFixed() {
    while (!opModeIsActive() && !isStopRequested) {
        telemetry.addData("INIT OVER", "Waiting")
        telemetry.update()
    }
}