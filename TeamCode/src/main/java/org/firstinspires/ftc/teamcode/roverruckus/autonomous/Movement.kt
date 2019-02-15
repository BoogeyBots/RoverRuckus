package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.DEFAULT_MOTOR_SPEED
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.MOVEMENT_MOTOR_TICK_COUNT
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.RATIO
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot.Companion.WHEEL_CIRCUMFERENCE
import org.firstinspires.ftc.teamcode.roverruckus.utils.toInches
import java.lang.Thread.sleep
import java.util.*


val elapsedTime = ElapsedTime()


fun Robot.moveByInches(inches: Double, power: Double = Robot.DEFAULT_MOTOR_SPEED) {
    setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, leftMotor, rightMotor)

    val rotationsNeeded = inches / (WHEEL_CIRCUMFERENCE * RATIO)
    val drivingTarget = (rotationsNeeded * MOVEMENT_MOTOR_TICK_COUNT).toInt()

    leftMotor.targetPosition = drivingTarget
    rightMotor.targetPosition = drivingTarget

    leftMotor.power = power
    rightMotor.power = -power

    setMotorsMode(DcMotor.RunMode.RUN_TO_POSITION, leftMotor, rightMotor)

    while (leftMotor.isBusy || rightMotor.isBusy) {
        telemetry.addData("CPOS", "LTICKS: ${leftMotor.currentPosition} RTICKS: ${rightMotor.currentPosition}")
        telemetry.addData("TARGETS", "LTARGET: ${leftMotor.targetPosition} RTARGET: ${rightMotor.targetPosition}")
        telemetry.addData("POWER", "POW: $power LMPOW: ${leftMotor.power} RMPOW: ${rightMotor.power}")
        telemetry.update()
    }

    setMotorsPower(0.0, leftMotor, rightMotor)

    setMotorsMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER, leftMotor, rightMotor)
}

fun Robot.moveByCentimeters(centimeters: Double, power: Double = Robot.DEFAULT_MOTOR_SPEED) {
    moveByInches(centimeters.toInches(), power)
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

fun Robot.rotate(degrees: Double, power: Double = Robot.DEFAULT_MOTOR_SPEED) {
    var leftPower: Double
    var rightPower: Double

    when {
        degrees < 0 -> {
            leftPower = power
            rightPower = -power
        }
        degrees > 0 -> {
            leftPower = -power
            rightPower = power
        }
        else -> return
    }

    leftMotor.power = leftPower
    rightMotor.power = rightPower

    val linearOpMode = opMode as LinearOpMode

    if (degrees - getAngle() < 0) {
        while (linearOpMode.opModeIsActive() && getAngle() == 0.0) {
            telemetry.addData("Degrees B1", "Teget: $degrees, Now: ${getAngle()}")
            telemetry.update()
        }
        while (linearOpMode.opModeIsActive() && getAngle() > degrees) {
            telemetry.addData("Degrees B2", "Teget: $degrees, Now: ${getAngle()}")
            telemetry.update()
        }
    } else {
        if (degrees < 0 && getAngle() < 0) {

        }
        else {
            while (linearOpMode.opModeIsActive() && getAngle() < degrees) {
                telemetry.addData("Degrees B3", "Teget: $degrees, Now: ${getAngle()}")
                telemetry.update()
            }
        }
    }

    telemetry.addData("Finished", "rotation")
    telemetry.update()
    setMotorsPower(0.0, leftMotor, rightMotor)

    // wait for rotation to stop.
    sleep(1000)

    // reset angle tracking on new heading.
    resetAngle()
}

fun Robot.rotateTo(angle: Double, power: Double = DEFAULT_MOTOR_SPEED) {
    val offset = 10.0

    val checker = {
        heading: Double ->
        when {
            heading > angle + offset -> "SCAD"
            heading < angle - offset -> "ADUN"
            else -> "OK"
        }
    }

    var check: String = "NU_OK"
    var heading = getHeading()
    while (check != "OK" && opModeIsActive) {
        when (check) {
            "SCAD" -> {
                leftMotor.power = power
                rightMotor.power = -power
            }
            "ADUN" -> {
                leftMotor.power = -power
                rightMotor.power = power
            }
        }

        telemetry.addData("CE TREBUIE SA FAC", "$check la/din $heading sa ajung la $angle")
        telemetry.update()

        check = checker(angles.firstAngle.toDouble())
    }
}

fun Robot.getHeading(): Double {
    val angles: Orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
    return angles.firstAngle.toDouble()
}

fun Robot.moveByCentimetersOnAngle(centimeters: Double, angle: Double, power: Double = DEFAULT_MOTOR_SPEED) {
    moveByInchesOnAngle(centimeters.toInches(), angle, power)
}

fun Robot.moveByInchesOnAngle(inches: Double, angle: Double, power: Double = DEFAULT_MOTOR_SPEED) {
    val leftLimiter: (Double) -> Boolean
    val rightLimiter: (Double) -> Boolean
    val higherPower = { power + if (inches > 0) 0.15 else -0.15 }
    val lowerPower = { power + if (inches > 0) -0.15 else 0.15 }
    if (angle == 180.0) {
        leftLimiter = { rotation -> rotation < 0 && rotation > -177.5 }
        rightLimiter = { rotation -> rotation > 0 && rotation < 177.5 }
    } else {
        leftLimiter = { rotation -> rotation > angle + 2.5 }
        rightLimiter = { rotation -> rotation < angle - 2.5 }
    }

    val linearOpMode = opMode as LinearOpMode

    setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, leftMotor, rightMotor)

    val rotationsNeeded = inches / (WHEEL_CIRCUMFERENCE * RATIO)
    val drivingTarget = (rotationsNeeded * MOVEMENT_MOTOR_TICK_COUNT).toInt()

    leftMotor.targetPosition = drivingTarget
    rightMotor.targetPosition = drivingTarget

    setMotorsMode(DcMotor.RunMode.RUN_TO_POSITION, leftMotor, rightMotor)

    while ((leftMotor.isBusy && rightMotor.isBusy) && linearOpMode.opModeIsActive()) {
        val rotation = angles.firstAngle.toDouble()
        when {
            leftLimiter(rotation) -> {
                leftMotor.power = higherPower()
                rightMotor.power = lowerPower()
            }
            rightLimiter(rotation) -> {
                leftMotor.power = lowerPower()
                rightMotor.power = higherPower()
            }
            else -> {
                leftMotor.power = power
                rightMotor.power = power
            }
        }
        if (power < 0.0) { telemetry.addData("Move", "Backwards") }
        telemetry.addData("Movement", "LM: ${leftMotor.power}; RM: ${rightMotor.power}")
        telemetry.update()
    }

    setMotorsPower(0.0, leftMotor, rightMotor)

    setMotorsMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER, leftMotor, rightMotor)
}

fun Robot.moveForSeconds(time: Double, angle: Double, power: Double = DEFAULT_MOTOR_SPEED) {
    setMotorsMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER, leftMotor, rightMotor)

    elapsedTime.reset()
    val leftLimiter: (Double) -> Boolean
    val rightLimiter: (Double) -> Boolean
    val higherPower = power + 0.25
    val lowerPower = power - 0.25
    if (angle == 180.0) {
        leftLimiter = { rotation -> rotation < 0 && rotation > -177.5 }
        rightLimiter = { rotation -> rotation > 0 && rotation < 177.5 }
    } else {
        leftLimiter = { rotation -> rotation > angle + 2.5 }
        rightLimiter = { rotation -> rotation < angle - 2.5 }
    }
    while (elapsedTime.seconds() < time && opModeIsActive) {
        val rotation = imu
                .getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
                .firstAngle
                .toDouble()
        when {
            leftLimiter(rotation) -> {
                leftMotor.power = higherPower
                rightMotor.power = lowerPower
            }
            rightLimiter(rotation) -> {
                leftMotor.power = lowerPower
                rightMotor.power = higherPower
            }
            else -> {
                leftMotor.power = power
                rightMotor.power = power
            }
        }
        if (power < 0.0) { telemetry.addData("Move", "Backwards") }
        telemetry.addData("Movement", "LM: ${leftMotor.power}; RM: ${rightMotor.power}")
        telemetry.update()
    }

    setMotorsPower(0.0, leftMotor, rightMotor)
}
fun Robot.liftLock() {
    elapsedTime.reset()

    // === RIDIC SI SCOT LOCK
    while (elapsedTime.seconds() < 0.3 && opModeIsActive) {
        leftArm.power = 0.6
        rightArm.power = 0.6

        lockServo.position = Range.clip(lockServo.position - 0.05, 0.0, 0.5)
    }

    setMotorsPower(0.0, leftArm, rightArm)
}

fun Robot.dropDown() {
    elapsedTime.reset()

    markerServo.position = 0.3

    // === LAS USOR IN JOS
    while (elapsedTime.seconds() < 2.8 && opModeIsActive) {
        leftArm.power = 0.08
        rightArm.power = 0.08
    }

    setMotorsPower(0.0, leftArm, rightArm)
}

fun Robot.pushLander() {
    elapsedTime.reset()

    markerServo.position = 0.0
    // === MA IMPING IN LANDER
    while (elapsedTime.seconds() < 0.7 && opModeIsActive) {
        leftArm.power = -0.3
        rightArm.power = -0.3
    }

    setMotorsPower(0.0, leftArm, rightArm)
}

fun Robot.detachHook() {
    elapsedTime.reset()
    // === SCOT HOOK
    while (elapsedTime.seconds() < 1.5 && opModeIsActive) {
        hookServo.position = Range.clip(hookServo.position + 0.05, 0.5, 1.0)
    }
}

fun Robot.moveArm(time: Double = 1.1) {
    elapsedTime.reset()
    while (elapsedTime.seconds() < time && opModeIsActive) {
        setMotorsPower(0.2, leftArm, rightArm)
    }
    setMotorsPower(0.0, leftArm, rightArm)
}

fun Robot.parkArm() {
    elapsedTime.reset()
    while (elapsedTime.seconds() < 0.7 && opModeIsActive) {
        setMotorsPower(-0.5, leftArm, rightArm)
    }
    setMotorsPower(0.0, leftArm, rightArm)
}

fun Robot.dropMarker() {
    markerServo.position = 0.5
    elapsedTime.reset()
    while (elapsedTime.seconds() < 0.5 && opModeIsActive) { }
}

//--------------------------------------------------------------------------------------------------
// TELEMETRY
//--------------------------------------------------------------------------------------------------

internal fun Robot.composeTelemetry() {

    // At the beginning of each telemetry update, grab a bunch of data
    // from the IMU that we will then display in separate lines.
    telemetry.addAction {
        // Acquiring the angles is relatively expensive; we don't want
        // to do that in each of the three items that need that info, as that's
        // three times the necessary expense.
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
        gravity = imu.gravity
    }

    telemetry.addLine()
            .addData("status") { imu.systemStatus.toShortString() }
            .addData("calib") { imu.calibrationStatus.toString() }

    telemetry.addLine()
            .addData("heading") { formatAngle(angles.angleUnit, angles.firstAngle.toDouble()) }
            .addData("Global angle") { globalAngle }

    telemetry.addLine()
            .addData("grvty") { gravity.toString() }
            .addData("mag") {
                String.format(Locale.getDefault(), "%.3f",
                        Math.sqrt(gravity.xAccel * gravity.xAccel
                                + gravity.yAccel * gravity.yAccel
                                + gravity.zAccel * gravity.zAccel))
            }
}

//----------------------------------------------------------------------------------------------
// Formatting
//----------------------------------------------------------------------------------------------

internal fun Robot.formatAngle(angleUnit: AngleUnit, angle: Double): String {
    return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle))
}

internal fun Robot.formatDegrees(degrees: Double): String {
    return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees))
}