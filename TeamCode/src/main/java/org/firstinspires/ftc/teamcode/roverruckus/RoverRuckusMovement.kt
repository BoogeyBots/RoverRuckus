package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.util.ElapsedTime

private val elapsedTime = ElapsedTime()

fun RoverRuckusHardware.moveForward(time: Double, motorPower: Double = 0.95) {
    elapsedTime.reset()

    while (elapsedTime.seconds() < time) {
        this.leftMotorPower  = motorPower
        this.rightMotorPower = motorPower
    }
}

fun RoverRuckusHardware.moveBack(time: Double, motorPower: Double = 1.0) {
    elapsedTime.reset()

    while (elapsedTime.seconds() < time) {
        this.leftMotorPower  = -motorPower
        this.rightMotorPower = -motorPower
    }
}

fun RoverRuckusHardware.rotateLeft(laps: Double = 1.0) {
    elapsedTime.reset()

    while (elapsedTime.seconds() < 3.35 * laps) {
        this.leftMotorPower  = -0.95
        this.rightMotorPower = 0.95
    }
}

fun RoverRuckusHardware.rotateRight(laps: Double = 1.0) {
    elapsedTime.reset()

    while (elapsedTime.seconds() < 3.35 * laps) {
        this.leftMotorPower  = 0.95
        this.rightMotorPower = -0.95
    }
}

fun RoverRuckusHardware.resetMotors() {
    this.leftMotorPower = 0.0
    this.rightMotorPower = 0.0
}