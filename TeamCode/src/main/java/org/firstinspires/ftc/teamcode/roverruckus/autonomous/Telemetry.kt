package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot

fun Robot.writeMotorsTelemetry() {
    var powerT = ""
    var busyT = ""
    var currentPosT = ""
    var targetPosT = ""
    for ((k, v) in motors) {
        powerT += "${k.name}: ${v.power} | "
        if (v.mode == DcMotor.RunMode.RUN_TO_POSITION) {
            currentPosT += "${k.name}: ${v.currentPosition} | "
            targetPosT += "${k.name}: ${v.targetPosition} | "
            busyT += "${k.name}: ${v.isBusy} | "
        }
    }

    telemetry.addData("POW", powerT)
    telemetry.addData("BUSY", busyT)
    telemetry.addData("CURPOS", currentPosT)
    telemetry.addData("TARPOS", targetPosT)
    telemetry.update()
}