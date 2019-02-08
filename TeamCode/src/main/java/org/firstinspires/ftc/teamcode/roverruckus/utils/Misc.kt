package org.firstinspires.ftc.teamcode.roverruckus.utils

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime

val elapsedTime: ElapsedTime = ElapsedTime()

fun LinearOpMode.waitForSeconds(seconds: Double) {
    elapsedTime.reset()
    while (elapsedTime.seconds() < seconds) { }
}