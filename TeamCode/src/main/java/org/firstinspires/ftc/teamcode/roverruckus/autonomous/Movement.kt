package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.teamcode.roverruckus.utils.*
import org.firstinspires.ftc.teamcode.roverruckus.utils.Motors.*
import kotlin.math.sqrt

val stopwatch = ElapsedTime()

fun Robot.moveLifting(up: Boolean) {
    /* The hook starts down (near the robot).
    *  Make the lifting move up so the robot goes down.
    *  3.7 (gearbox ration on motor) * 28 (encoder counts per revolution) * rotations
    *  Through experimenting we found out that 70 is the perfect number of rotations on the
    *  lifting motor.
    */
    setMotorMode(Lift, DcMotor.RunMode.STOP_AND_RESET_ENCODER)

    motors[Lift]?.targetPosition = (28 * 3.7 * (if (up) 70 else -70)).toInt()

    motors[Lift]?.power = -0.99
    motors[Lift]?.mode = DcMotor.RunMode.RUN_TO_POSITION

    while (motors[Lift]?.isBusy!! && opModeIsActive) { writeMotorsTelemetry() }

    motors[Lift]?.power = 0.0
    motors[Lift]?.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
}


fun Robot.extendArm() {
    setMotorMode(IntakeExtension, DcMotor.RunMode.STOP_AND_RESET_ENCODER)

    setMotorTargetPos(IntakeExtension, 1440.0 * 0.2)
    setMotorPower(IntakeExtension, Robot.DEFAULT_MOTOR_POWER)
    while (isMotorBusy(IntakeExtension) && opModeIsActive) {
        writeMotorsTelemetry()
    }

    setMotorPower(IntakeExtension, 0.0)
    setMotorMode(IntakeExtension, DcMotor.RunMode.RUN_WITHOUT_ENCODER)
}

fun Robot.moveByDistance(centimeters: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    val target = centimeters.toInches() / (Robot.WHEEL_CIRCUMFERENCE * sqrt(2.0)) * Robot.MOVEMENT_MOTOR_TICK_COUNT

    setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, LF, RF, LB, RB)

    setMotorsTargetPos(target, LF, RF, LB, RB)
    setMotorsPower(power, RF, RB)
    setMotorsPower(-power, LF, LB)
    setMotorsMode(DcMotor.RunMode.RUN_TO_POSITION, LF, RF, LB, RB)

    while (areAllMotorsBusy(LF, RF, LB, RB)) {
        writeMotorsTelemetry()
    }

    setMotorsPower(0.0, LF, RF, LB, RB)
    setMotorsMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER, LF, RF, LB, RB)
}

fun Robot.strafeByDistance(centimeters: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    val target = centimeters / (Robot.WHEEL_CIRCUMFERENCE * sqrt(2.0)) * Robot.MOVEMENT_MOTOR_TICK_COUNT

    setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, LF, RF, LB, RB)

    setMotorsTargetPos(target, LF, RB)
    setMotorsTargetPos(-target, RF, LB)
    setMotorsPower(power, RF, RB)
    setMotorsPower(-power, LF, LB)

    setMotorsMode(DcMotor.RunMode.RUN_TO_POSITION, LF, RF, LB, RB)

    while (areAllMotorsBusy(LF, RF, LB, RB)) {  }

    setMotorsPower(0.0, LF, RF, LB, RB)
    setMotorsMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER, LF, RF, LB, RB)
}

fun Robot.moveForSeconds(time: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    setMotorsPower(power, LF, RF, LB, RB)

    stopwatch.reset()
    while (stopwatch.seconds() < time && opModeIsActive) {
        writeMotorsTelemetry()
    }
}

fun Robot.strafeForSeconds(time: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    setMotorsPower(power, RF, RB)
    setMotorsPower(-power, LF, LB)

    while (stopwatch.seconds() < time && opModeIsActive) {
        writeMotorsTelemetry()
    }
}

private fun Robot.resetAngle() {
    lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)

    globalAngle = 0.0
}

fun Robot.getAngle(): Double {
    val angles: Orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)

    var deltaAngle = angles.firstAngle - lastAngles.firstAngle

    if (deltaAngle < -180) {
        deltaAngle += 360
    } else if (deltaAngle > 180) {
        deltaAngle -= 360
    }

    globalAngle += deltaAngle

    lastAngles = angles

    return globalAngle
}

fun Robot.rotate(degrees: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    if (degrees > 0) {
        while (getAngle() < degrees && opModeIsActive) {
            setMotorsPower(-power, LF, LB)
            setMotorsPower(power, RF, RB)
        }
    } else {
        while (getAngle() > degrees && opModeIsActive) {
            setMotorsPower(power, LF, LB)
            setMotorsPower(-power, RF, RB)
        }
    }

    setMotorsPower(0.0, LF, RF, LB, RB)

    // wait for rotation to stop.
    (opMode as LinearOpMode).wait(1.0)

    // reset angle tracking on new heading.
    resetAngle()
}