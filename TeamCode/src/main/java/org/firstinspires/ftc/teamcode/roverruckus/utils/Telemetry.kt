package org.firstinspires.ftc.teamcode.roverruckus.utils

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot

fun Robot.writeMotorsTelemetry() {
    for ((k, v) in motors) {
        telemetry.addData("POW - ${k.name}", v.power)
    }

    for ((k, v) in motors) {
        if (v.mode == DcMotor.RunMode.RUN_TO_POSITION) {
            telemetry.addData("BUSY - ${k.name}", v.isBusy)
            telemetry.addData("TARPOS - ${k.name}", v.targetPosition)
            telemetry.addData("CURPOS - ${k.name}", v.currentPosition)
        }
    }
}

fun Robot.writeServosTelemetry() {
    for ((k, v) in servos) {
        telemetry.addData("POS - ${k.name}", v.position)
    }
}

fun Robot.writeSensorTelemetry() {
    for ((k, v) in distanceSensors) {
        telemetry.addData("DISTANCE", v.getDistance(DistanceUnit.CM))
    }
}