package org.firstinspires.ftc.teamcode.roverruckus

import com.qualcomm.robotcore.util.ElapsedTime

private val elapsedTime = ElapsedTime()

fun RoverRuckusHardware.moveForward(time: Double, motorPower: Double = 1.0) {
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

fun RoverRuckusHardware.rotateLeft(time: Double, motorPower: Double = 0.25) {
    elapsedTime.reset()

    while (elapsedTime.seconds() < time) {
        this.leftMotorPower  = -motorPower
        this.rightMotorPower = motorPower
    }
}

fun RoverRuckusHardware.rotateRight(time: Double, motorPower: Double = 0.3) {
    elapsedTime.reset()

    while (elapsedTime.seconds() < time) {
        this.leftMotorPower  = motorPower
        this.rightMotorPower = -motorPower
    }
}

fun RoverRuckusHardware.resetMotors() {
    this.leftMotorPower = 0.0
    this.rightMotorPower = 0.0
}